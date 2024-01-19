package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.Commit
import com.github.xalvarez.githubteamdashboard.github.GitHubService
import com.github.xalvarez.githubteamdashboard.github.GithubDashboardData
import com.github.xalvarez.githubteamdashboard.github.Review
import com.github.xalvarez.githubteamdashboard.github.models.CheckState
import com.github.xalvarez.githubteamdashboard.github.models.Member
import com.github.xalvarez.githubteamdashboard.github.models.PullRequestModel
import com.github.xalvarez.githubteamdashboard.github.models.ReviewState
import com.github.xalvarez.githubteamdashboard.github.models.ReviewState.APPROVED
import com.github.xalvarez.githubteamdashboard.github.models.ReviewState.CHANGES_REQUESTED
import com.github.xalvarez.githubteamdashboard.github.models.ReviewState.PENDING
import com.github.xalvarez.githubteamdashboard.github.models.SecurityAlert
import com.github.xalvarez.githubteamdashboard.github.models.TeamModel
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import reactor.core.publisher.Mono
import java.time.ZoneId.systemDefault
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Controller
class IndexController(private val gitHubService: GitHubService) {
    @Get
    @View("index")
    fun index(): Mono<HttpResponse<Any>> = Mono.just(HttpResponse.ok())

    @Get("/dashboard")
    @View("dashboard")
    fun dashboard(): Mono<HttpResponse<Any>> =
        buildDashboardModel()
            .map { model -> HttpResponse.ok(model) }

    private fun buildDashboardModel(): Mono<Map<String, Any>> =
        gitHubService.fetchDashboardData()
            .map { githubDashboardData ->
                mapOf(
                    Pair("team", buildTeam(githubDashboardData)),
                    Pair("pullRequests", buildPullRequests(githubDashboardData)),
                    Pair("securityAlerts", buildSecurityAlerts(githubDashboardData)),
                )
            }

    private fun buildTeam(githubDashboardData: GithubDashboardData) =
        TeamModel(githubDashboardData.data.organization.team.name, buildMembers(githubDashboardData))

    private fun buildMembers(githubDashboardData: GithubDashboardData) =
        githubDashboardData.data.organization.team.members.nodes.map { Member(it.login) }

    private fun buildPullRequests(githubDashboardData: GithubDashboardData) =
        githubDashboardData.data.organization.team.repositories.nodes
            .filterNot { repository -> repository.pullRequests.nodes.isEmpty() }
            .flatMap { repository ->
                repository.pullRequests.nodes.map {
                    PullRequestModel(
                        it.url,
                        repository.url,
                        toHumanReadableDatetime(it.createdAt),
                        it.author.login,
                        it.title,
                        repository.name,
                        toReviewState(it.reviews),
                        toCheckState(it.isDraft, it.commits.nodes.first().commit),
                    )
                }
            }
            .sortedBy { it.createdAt }

    private fun buildSecurityAlerts(githubDashboardData: GithubDashboardData) =
        githubDashboardData.data.organization.team.repositories.nodes
            .filter { it.vulnerabilityAlerts.arePresent }
            .map { SecurityAlert(it.name, it.alertsUrl) }
            .sortedBy { it.repository }

    private fun toHumanReadableDatetime(datetime: ZonedDateTime) =
        datetime.withZoneSameInstant(systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    private fun toReviewState(reviews: Review): ReviewState {
        val state =
            reviews.nodes
                .map { it.state }
                .find { CHANGES_REQUESTED.name == it || APPROVED.name == it }
                .orEmpty()

        return when (state) {
            CHANGES_REQUESTED.name -> CHANGES_REQUESTED
            APPROVED.name -> APPROVED
            else -> PENDING
        }
    }

    private fun toCheckState(
        isDraft: Boolean,
        commit: Commit,
    ): CheckState {
        if (isDraft) {
            return CheckState.DRAFT
        }

        return when (commit.statusCheckRollup?.state) {
            CheckState.ERROR.name -> CheckState.ERROR
            CheckState.EXPECTED.name -> CheckState.EXPECTED
            CheckState.FAILURE.name -> CheckState.FAILURE
            CheckState.PENDING.name -> CheckState.PENDING
            CheckState.SUCCESS.name -> CheckState.SUCCESS
            else -> CheckState.NONE
        }
    }
}
