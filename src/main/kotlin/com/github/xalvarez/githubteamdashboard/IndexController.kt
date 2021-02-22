package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.Commit
import com.github.xalvarez.githubteamdashboard.github.GitHubService
import com.github.xalvarez.githubteamdashboard.github.GithubDashboardData
import com.github.xalvarez.githubteamdashboard.github.Review
import com.github.xalvarez.githubteamdashboard.github.models.*
import com.github.xalvarez.githubteamdashboard.github.models.ReviewState.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class IndexController(private val gitHubService: GitHubService) {

    @Get
    @View("index")
    fun index(): HttpResponse<Any> = HttpResponse.ok()

    @Get("/dashboard")
    @View("dashboard")
    fun dashboard(): HttpResponse<Any> = HttpResponse.ok(buildDashboardModel())

    private fun buildDashboardModel(): Map<String, Any> = gitHubService.fetchDashboardData().let {
        mapOf(
            Pair("team", buildTeam(it)),
            Pair("pullRequests", buildPullRequests(it)),
            Pair("securityAlerts", buildSecurityAlerts(it)))
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
                        toHumanReadableDatetime(it.createdAt),
                        it.author.login,
                        it.title,
                        repository.name,
                        toReviewState(it.reviews),
                        toCheckState(it.isDraft, it.commits.nodes.first().commit)
                    )
                }
            }
            .sortedBy { it.createdAt }

    private fun buildSecurityAlerts(githubDashboardData: GithubDashboardData) =
        githubDashboardData.data.organization.team.repositories.nodes
            .filter { it.vulnerabilityAlerts.arePresent }
            .map { SecurityAlert(it.name, it.alertsUrl) }
            .sortedBy { it.repository }

    private fun toHumanReadableDatetime(datetime: LocalDateTime) =
        datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    private fun toReviewState(reviews: Review): ReviewState {
        reviews.nodes
            .forEach {
                if (it.state == CHANGES_REQUESTED.name) {
                    return CHANGES_REQUESTED
                }
                else if (it.state == APPROVED.name) {
                    return APPROVED
                }
            }

        return PENDING
    }

    private fun toCheckState(isDraft: Boolean, commit: Commit): CheckState {
        if (isDraft) {
            return CheckState.DRAFT
        }
        commit.statusCheckRollup ?: return CheckState.NONE

        return when (commit.statusCheckRollup.state) {
            CheckState.ERROR.name -> CheckState.ERROR
            CheckState.EXPECTED.name -> CheckState.EXPECTED
            CheckState.FAILURE.name -> CheckState.FAILURE
            CheckState.PENDING.name -> CheckState.PENDING
            CheckState.SUCCESS.name -> CheckState.SUCCESS
            else -> CheckState.NONE
        }
    }
}
