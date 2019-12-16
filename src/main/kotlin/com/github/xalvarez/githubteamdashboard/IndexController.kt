package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.GitHubService
import com.github.xalvarez.githubteamdashboard.github.GithubDashboardData
import com.github.xalvarez.githubteamdashboard.github.models.Member
import com.github.xalvarez.githubteamdashboard.github.models.PullRequest
import com.github.xalvarez.githubteamdashboard.github.models.Team
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class IndexController(private val gitHubService: GitHubService) {

    @View("index")
    @Get
    fun index(): HttpResponse<Any> {
        val githubDashboardData = gitHubService.fetchDashboardData()
        val model = HashMap<String, Any>()
        model["team"] = buildTeam(githubDashboardData)
        model["pullRequests"] = buildPullRequests((githubDashboardData))
        return HttpResponse.ok(model)
    }

    private fun buildTeam(githubDashboardData: GithubDashboardData) =
        Team(githubDashboardData.data.organization.team.name, buildMembers(githubDashboardData))

    private fun buildMembers(githubDashboardData: GithubDashboardData) =
        githubDashboardData.data.organization.team.members.nodes.map { Member(it.login) }

    private fun buildPullRequests(githubDashboardData: GithubDashboardData) =
        githubDashboardData.data.organization.team.repositories.nodes
            .filterNot { repository -> repository.pullRequests.nodes.isEmpty() }
            .flatMap { repository ->
                repository.pullRequests.nodes.map {
                    PullRequest(it.url, toHumanReadableDatetime(it.createdAt), it.author.login, it.title, repository.name)
                }
            }

    private fun toHumanReadableDatetime(datetime: LocalDateTime) =
        datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}