package com.github.xalvarez.githubteamdashboard

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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME

@Controller
class IndexController(private val gitHubService: GitHubService) {

    @Get
    @View("index")
    fun index(): HttpResponse<Any> {
        return HttpResponse.ok()
    }

    @Get("/dashboard")
    @View("dashboard")
    fun dashboard(): HttpResponse<Any> {
        return HttpResponse.ok(buildDashboardModel())
    }

    private fun buildDashboardModel(): HashMap<String, Any> {
        val githubDashboardData = gitHubService.fetchDashboardData()
        val model = HashMap<String, Any>()
        model["team"] = buildTeam(githubDashboardData)
        model["pullRequests"] = buildPullRequests(githubDashboardData)
        model["securityAlerts"] = buildSecurityAlerts(githubDashboardData)
        model["lastUpdate"] = ZonedDateTime.now().format(ISO_ZONED_DATE_TIME)
        return model
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
                        toReviewState(it.reviews)
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
        var reviewState = PENDING

        reviews.nodes
            .forEach {
                if (it.state == CHANGES_REQUESTED.name) {
                    return CHANGES_REQUESTED
                }
                else if (it.state == APPROVED.name) {
                    reviewState = APPROVED
                }
            }
        return reviewState
    }
}