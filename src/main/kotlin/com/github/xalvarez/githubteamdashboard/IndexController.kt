package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.GitHubService
import com.github.xalvarez.githubteamdashboard.github.GithubDashboardData
import com.github.xalvarez.githubteamdashboard.github.models.Member
import com.github.xalvarez.githubteamdashboard.github.models.Team
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Controller
class IndexController(val gitHubService: GitHubService) {

    @View("index")
    @Get
    fun index(): HttpResponse<Any> {
        val githubDashboardData = gitHubService.fetchDashboardData()
        val model = HashMap<String, Any>()
        model["team"] = buildTeam(githubDashboardData)
        return HttpResponse.ok(model)
    }

    private fun buildTeam(githubDashboardData: GithubDashboardData) =
        Team(githubDashboardData.data.organization.team.name, buildMembers(githubDashboardData))

    private fun buildMembers(githubDashboardData: GithubDashboardData) =
        githubDashboardData.data.organization.team.members.nodes.map { Member(it.login) }
}