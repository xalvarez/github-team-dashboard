package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.Prototype
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Prototype
class GitHubService(
    private val gitHubClient: GitHubClient,
    private val gitHubConfiguration: GitHubConfiguration,
) {
    private val logger = LoggerFactory.getLogger(GitHubService::class.java)

    /**
     * Fetches all the data required by the dashboard using a two-phase, query-decomposition
     * strategy:
     *
     * 1. A cheap, shallow query lists the team's repositories (paginated). This keeps the GraphQL
     *    complexity low regardless of how many repositories the team owns.
     * 2. For every non-archived repository, a small per-repository query fetches its open pull
     *    requests. These requests run concurrently (with bounded parallelism) so the overall
     *    latency stays low without tripping GitHub's secondary rate limits.
     */
    fun fetchDashboardData(): Mono<GithubDashboardData> =
        fetchTeamWithRepositories()
            .flatMap { team ->
                val activeRepositories = team.repositories.filterNot { it.isArchived }
                Flux
                    .fromIterable(activeRepositories)
                    .flatMap({ summary -> fetchRepository(summary) }, FETCH_CONCURRENCY)
                    .collectList()
                    .map { repositories -> assembleDashboardData(team, repositories.sortedBy { it.name }) }
            }

    private fun fetchTeamWithRepositories(): Mono<TeamSummary> =
        fetchTeamRepositoriesPage(null)
            .expand { response ->
                val pageInfo =
                    response.data
                        ?.organization
                        ?.team
                        ?.repositories
                        ?.pageInfo
                if (pageInfo?.hasNextPage == true) {
                    fetchTeamRepositoriesPage(pageInfo.endCursor)
                } else {
                    Mono.empty()
                }
            }.collectList()
            .map { pages -> toTeamSummary(pages) }

    private fun fetchTeamRepositoriesPage(cursor: String?): Mono<TeamRepositoriesResponse> =
        gitHubClient
            .fetchTeamRepositories(buildTeamRepositoriesQuery(cursor))
            .timeout(REQUEST_TIMEOUT)
            .retryWhen(retrySpecification())
            .doOnNext { response ->
                logRateLimit(response.data?.rateLimit)
                logErrors(response.errors)
            }.doOnError { error -> logger.error("Error fetching team repositories from GitHub: ${error.message}") }

    private fun fetchRepository(summary: RepositorySummaryNode): Mono<Repository> =
        gitHubClient
            .fetchRepositoryPullRequests(buildPullRequestsQuery(summary.name))
            .timeout(REQUEST_TIMEOUT)
            .retryWhen(retrySpecification())
            .doOnNext { response ->
                logRateLimit(response.data?.rateLimit)
                logErrors(response.errors)
            }.map { response -> toRepository(summary, response.data?.repository?.pullRequests) }
            .onErrorResume { error ->
                logger.error("Error fetching pull requests for ${summary.name}: ${error.message}")
                Mono.just(toRepository(summary, null))
            }

    private fun toTeamSummary(pages: List<TeamRepositoriesResponse>): TeamSummary {
        val team =
            pages
                .firstOrNull()
                ?.data
                ?.organization
                ?.team
                ?: throw IllegalStateException("GitHub response did not contain any team data")
        val repositories =
            pages.flatMap {
                it.data
                    ?.organization
                    ?.team
                    ?.repositories
                    ?.nodes ?: emptyList()
            }
        return TeamSummary(team.name, team.members, repositories)
    }

    private fun toRepository(
        summary: RepositorySummaryNode,
        pullRequests: PullRequests?,
    ) = Repository(
        name = summary.name,
        pullRequests = pullRequests ?: PullRequests(emptyList()),
        url = summary.url,
        isArchived = summary.isArchived,
        vulnerabilityAlerts = summary.vulnerabilityAlerts,
    )

    private fun assembleDashboardData(
        team: TeamSummary,
        repositories: List<Repository>,
    ) = GithubDashboardData(
        data =
            Data(
                organization =
                    Organization(
                        team =
                            Team(
                                name = team.name,
                                members = team.members,
                                repositories = Repositories(nodes = repositories, pageInfo = PageInfo("", false)),
                            ),
                    ),
            ),
    )

    private fun retrySpecification(): Retry = Retry.backoff(MAX_RETRIES, RETRY_MIN_BACKOFF)

    private fun logRateLimit(rateLimit: RateLimit?) {
        if (rateLimit != null) {
            logger.debug("GitHub GraphQL query cost: ${rateLimit.cost}, remaining: ${rateLimit.remaining}")
        }
    }

    private fun logErrors(errors: List<GraphQlError>?) {
        if (!errors.isNullOrEmpty()) {
            logger.warn("GitHub GraphQL returned errors: ${errors.joinToString { it.message }}")
        }
    }

    private fun buildTeamRepositoriesQuery(cursor: String?): String {
        val afterClause = if (cursor.isNullOrEmpty()) "null" else "\"$cursor\""
        return """
            {
              rateLimit {
                cost
                remaining
              }
              organization(login: "${gitHubConfiguration.organization}") {
                team(slug: "${gitHubConfiguration.team}") {
                  name
                  members(first: $MEMBERS_PAGE_SIZE, orderBy: { field: LOGIN, direction: ASC }) {
                    nodes {
                      login
                    }
                  }
                  repositories(first: $REPOSITORIES_PAGE_SIZE, orderBy: { field: NAME, direction: ASC }, after: $afterClause) {
                    nodes {
                      name
                      url
                      isArchived
                      vulnerabilityAlerts(first: 1, states: [OPEN]) {
                        totalCount
                      }
                    }
                    pageInfo {
                      endCursor
                      hasNextPage
                    }
                  }
                }
              }
            }
            """.trimIndent()
    }

    private fun buildPullRequestsQuery(repositoryName: String): String =
        """
        {
          rateLimit {
            cost
            remaining
          }
          repository(owner: "${gitHubConfiguration.organization}", name: "$repositoryName") {
            pullRequests(states: [OPEN], first: $PULL_REQUESTS_PAGE_SIZE, orderBy: { field: CREATED_AT, direction: ASC }) {
              nodes {
                url
                createdAt
                author {
                  login
                }
                title
                reviews(last: 1, states: [APPROVED, CHANGES_REQUESTED]) {
                  nodes {
                    state
                  }
                }
                isDraft
                commits(last: 1) {
                  nodes {
                    commit {
                      statusCheckRollup {
                        state
                      }
                    }
                  }
                }
              }
            }
          }
        }
        """.trimIndent()

    private data class TeamSummary(
        val name: String,
        val members: Members,
        val repositories: List<RepositorySummaryNode>,
    )

    private companion object {
        const val MEMBERS_PAGE_SIZE = 50
        const val REPOSITORIES_PAGE_SIZE = 100
        const val PULL_REQUESTS_PAGE_SIZE = 20
        const val FETCH_CONCURRENCY = 8
        const val MAX_RETRIES = 2L
        val REQUEST_TIMEOUT: Duration = Duration.ofSeconds(30)
        val RETRY_MIN_BACKOFF: Duration = Duration.ofMillis(200)
    }
}
