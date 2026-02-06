package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.Prototype
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Prototype
class GitHubService(
    private val gitHubClient: GitHubClient,
    private val gitHubConfiguration: GitHubConfiguration,
) {
    private val logger = LoggerFactory.getLogger(GitHubService::class.java)

    fun fetchDashboardData(): Mono<GithubDashboardData> =
        gitHubClient
            .fetchDashboardData(buildQuery(""))
            .doOnError { error -> logger.error("Error fetching data from GitHub: ${error.message}") }
            .flatMap { response ->
                val organization = response.data.organization
                val initialRepositories = organization.team.repositories.nodes
                val pageInfo = organization.team.repositories.pageInfo
                val additionalRepositoriesFlux =
                    if (pageInfo.hasNextPage) {
                        fetchRepositories(pageInfo.endCursor)
                    } else {
                        Flux.empty()
                    }

                Flux
                    .concat(Flux.fromIterable(initialRepositories), additionalRepositoriesFlux)
                    .filter { !it.isArchived }
                    .collectList()
                    .map { allRepositories ->
                        GithubDashboardData(
                            data =
                                Data(
                                    organization =
                                        Organization(
                                            team =
                                                Team(
                                                    name = organization.team.name,
                                                    members = organization.team.members,
                                                    repositories =
                                                        Repositories(
                                                            nodes = allRepositories,
                                                            pageInfo = PageInfo("", false),
                                                        ),
                                                ),
                                        ),
                                ),
                        )
                    }
            }

    private fun fetchRepositories(cursor: String): Flux<Repository> =
        gitHubClient
            .fetchDashboardData(buildQuery(cursor))
            .flatMapMany { response ->
                val repositories = response.data.organization.team.repositories.nodes
                val pageInfo = response.data.organization.team.repositories.pageInfo

                Flux.fromIterable(repositories).concatWith(
                    if (pageInfo.hasNextPage) {
                        fetchRepositories(pageInfo.endCursor)
                    } else {
                        Flux.empty()
                    },
                )
            }

    private fun buildQuery(cursor: String = ""): String {
        val afterClause = if (cursor.isEmpty()) "null" else "\"$cursor\""
        return """
            {
              organization(login: "${gitHubConfiguration.organization}") {
                team(slug: "${gitHubConfiguration.team}") {
                  name
                  members(first: 50, orderBy: { field: LOGIN, direction: ASC }) {
                    nodes {
                      login
                    }
                  }
                  repositories(first: 40, orderBy: { field: NAME, direction: ASC }, after: $afterClause) {
                    nodes {
                      name
                      url
                      isArchived
                      vulnerabilityAlerts(first: 1, states: [OPEN]) {
                        totalCount
                      }
                      pullRequests(states: [OPEN], first: 20, orderBy: { field: CREATED_AT, direction: ASC }) {
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
}
