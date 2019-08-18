package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.reactivex.Single

@Client("\${github.apiurl}")
interface GitHubClient {

    @Post
    fun fetchDashboardData(
        @Header("Authorization") authorization: String, @Header(value = "User-Agent") userAgent: String, query: String
    ): Single<GithubDashboardData>
}
