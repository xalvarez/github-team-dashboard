package com.github.xalvarez.githubteamdashboard.github

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.test.annotation.MicronautTest
import javax.inject.Inject
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@MicronautTest
class GitHubServiceTest {

    @Inject
    private lateinit var gitHubService: GitHubService

    @Inject
    private lateinit var wireMockServer: WireMockServer

    @Test
    fun `should map all data`() {
        givenSuccessfulGitHubRequest()

        val githubDashboardData = gitHubService.fetchDashboardData()

        assertNotNull(githubDashboardData.data.organization.team.name)
        assertTrue { githubDashboardData.data.organization.team.members.nodes.all { it.login.isNotEmpty() } }
        assertTrue { githubDashboardData.data.organization.team.repositories.nodes.all { it.name.isNotEmpty() } }
        assertNotNull(githubDashboardData.data.organization.team.repositories.nodes.map { it.pullRequests.nodes })
        assertTrue {
            githubDashboardData.data.organization.team.repositories.nodes
                .filterNot { repository -> repository.pullRequests.nodes.isEmpty() }
                .all { repository ->
                    repository.pullRequests.nodes.all {
                        it.url.isNotEmpty() && it.author.login.isNotEmpty()
                    }
                }
        }
    }

    private fun givenSuccessfulGitHubRequest() {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/graphql")).willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON)
                    .withBodyFile(GITHUB_SUCCESSFUL_REQUEST_STUB_FILE)
            )
        )
    }
}

private const val GITHUB_SUCCESSFUL_REQUEST_STUB_FILE = "post_github_successful.json"