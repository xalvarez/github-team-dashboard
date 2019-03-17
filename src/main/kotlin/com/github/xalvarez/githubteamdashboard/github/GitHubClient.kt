package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(GITHUB_API_URL)
interface GitHubClient {

    @Post
    fun fetchDashboardData(
        @Header("Authorization") authorization: String, @Header(value = "User-Agent") userAgent: String, query: String
    ): String
}

const val GITHUB_API_URL = "https://api.github.com/graphql"