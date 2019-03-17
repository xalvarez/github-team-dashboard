package com.github.xalvarez.githubteamdashboard

import com.github.xalvarez.githubteamdashboard.github.GitHubService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Controller
class IndexController(val gitHubService: GitHubService) {

    @View("index")
    @Get
    fun index(): HttpResponse<Any> {
        val response = gitHubService.fetchDashboardData()
        // TODO: Do something with response
        return HttpResponse.ok()
    }
}