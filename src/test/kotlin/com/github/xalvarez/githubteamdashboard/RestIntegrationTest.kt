package com.github.xalvarez.githubteamdashboard

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.micronaut.http.HttpHeaders.CONTENT_TYPE
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
open class RestIntegrationTest {
    protected val client: HttpClient by lazy { HttpClient.create(server.url) }

    @Inject
    private lateinit var server: EmbeddedServer

    @Inject
    protected lateinit var wireMockServer: WireMockServer

    @BeforeAll
    fun startWireMockServer() {
        wireMockServer.start()
    }

    @BeforeEach
    fun resetWireMockRequests() {
        wireMockServer.resetRequests()
    }

    @AfterAll
    fun stopWireMockServer() {
        wireMockServer.stop()
    }

    protected fun givenSuccessfulGitHubRequest() {
        stubTeamRepositories(GITHUB_TEAM_REPOSITORIES_STUB_FILE)
        stubPullRequests()
    }

    protected fun givenPaginatedGitHubRequest() {
        stubTeamRepositories(GITHUB_TEAM_REPOSITORIES_FIRST_PAGE_STUB_FILE)
        wireMockServer.stubFor(
            post(urlPathEqualTo("/graphql"))
                .atPriority(1)
                .withRequestBody(containing("team(slug"))
                .withRequestBody(containing("0123456789"))
                .willReturn(okJson(GITHUB_TEAM_REPOSITORIES_SECOND_PAGE_STUB_FILE)),
        )
        stubPullRequests()
    }

    private fun stubTeamRepositories(stubFile: String) {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/graphql"))
                .atPriority(5)
                .withRequestBody(containing("team(slug"))
                .willReturn(okJson(stubFile)),
        )
    }

    private fun stubPullRequests() {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/graphql"))
                .atPriority(10)
                .withRequestBody(containing("repository(owner"))
                .willReturn(okJson(GITHUB_PULL_REQUESTS_EMPTY_STUB_FILE)),
        )
        stubPullRequestsForRepository("example_repo_3", GITHUB_PULL_REQUESTS_REPO_3_STUB_FILE)
        stubPullRequestsForRepository("example_repo_4", GITHUB_PULL_REQUESTS_REPO_4_STUB_FILE)
    }

    private fun stubPullRequestsForRepository(
        repositoryName: String,
        stubFile: String,
    ) {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/graphql"))
                .atPriority(1)
                .withRequestBody(containing("repository(owner"))
                .withRequestBody(containing("name: \"$repositoryName\""))
                .willReturn(okJson(stubFile)),
        )
    }

    private fun okJson(stubFile: String): ResponseDefinitionBuilder =
        aResponse()
            .withStatus(OK.code)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON)
            .withBodyFile(stubFile)
}

private const val GITHUB_TEAM_REPOSITORIES_STUB_FILE = "post_github_team_repositories.json"
private const val GITHUB_TEAM_REPOSITORIES_FIRST_PAGE_STUB_FILE = "post_github_team_repositories_first_page.json"
private const val GITHUB_TEAM_REPOSITORIES_SECOND_PAGE_STUB_FILE = "post_github_team_repositories_second_page.json"
private const val GITHUB_PULL_REQUESTS_EMPTY_STUB_FILE = "post_github_pull_requests_empty.json"
private const val GITHUB_PULL_REQUESTS_REPO_3_STUB_FILE = "post_github_pull_requests_repo_3.json"
private const val GITHUB_PULL_REQUESTS_REPO_4_STUB_FILE = "post_github_pull_requests_repo_4.json"
