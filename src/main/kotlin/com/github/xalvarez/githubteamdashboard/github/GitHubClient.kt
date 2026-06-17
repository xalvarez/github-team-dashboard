package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@BearerToken
@Client("\${github.apiUrl}")
interface GitHubClient {
    @Post
    fun fetchTeamRepositories(query: String): Mono<TeamRepositoriesResponse>

    @Post
    fun fetchRepositoryPullRequests(query: String): Mono<RepositoryPullRequestsResponse>
}
