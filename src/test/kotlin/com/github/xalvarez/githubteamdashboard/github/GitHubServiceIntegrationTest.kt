package com.github.xalvarez.githubteamdashboard.github

import com.github.xalvarez.githubteamdashboard.RestIntegrationTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@MicronautTest
internal class GitHubServiceIntegrationTest : RestIntegrationTest() {
    @Inject
    private lateinit var gitHubService: GitHubService

    @Test
    fun `should map all data`() {
        givenSuccessfulGitHubRequest()

        val githubDashboardData = gitHubService.fetchDashboardData().block()
        val team = githubDashboardData?.data?.organization?.team
        val members = team?.members?.nodes
        val repositories = team?.repositories?.nodes
        val repositoriesPageInfo = team?.repositories?.pageInfo

        assertAll(
            { assertNotNull(team?.name) },
            { assertTrue { members?.all { it.login.isNotEmpty() } ?: false } },
            { assertTrue { repositories!!.size == 6 } },
            { assertTrue { repositories?.all { it.name.isNotEmpty() } ?: false } },
            { assertTrue { repositories?.all { it.url.isNotEmpty() } ?: false } },
            { assertTrue { repositories?.all { it.alertsUrl.isNotEmpty() } ?: false } },
            { assertNotNull(repositories?.all { it.vulnerabilityAlerts.arePresent }) },
            { assertNotNull(repositories?.map { it.pullRequests.nodes }) },
            {
                assertTrue {
                    repositories
                        ?.filterNot { repository -> repository.pullRequests.nodes.isEmpty() }
                        ?.all { repository ->
                            repository.pullRequests.nodes.all {
                                it.url.isNotEmpty() && it.author.login.isNotEmpty() && it.title.isNotEmpty()
                            }
                        } ?: false
                }
            },
            { assertTrue { repositoriesPageInfo!!.endCursor.isEmpty() } },
            { assertFalse { repositoriesPageInfo!!.hasNextPage } },
        )
    }

    @Test
    fun `should handle multiple pages of repositories`() {
        givenPaginatedGitHubRequest()

        val githubDashboardData = gitHubService.fetchDashboardData().block()
        val repositories =
            githubDashboardData
                ?.data
                ?.organization
                ?.team
                ?.repositories
                ?.nodes

        assertTrue { repositories!!.size == 12 }
    }
}
