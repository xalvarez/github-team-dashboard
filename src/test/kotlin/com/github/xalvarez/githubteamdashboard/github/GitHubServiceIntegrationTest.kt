package com.github.xalvarez.githubteamdashboard.github

import com.github.xalvarez.githubteamdashboard.RestIntegrationTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
internal class GitHubServiceIntegrationTest : RestIntegrationTest() {

    @Inject
    private lateinit var gitHubService: GitHubService

    @Test
    fun `should map all data`() {
        givenSuccessfulGitHubRequest()

        val githubDashboardData = gitHubService.fetchDashboardData()

        assertNotNull(githubDashboardData.data.organization.team.name)
        assertTrue { githubDashboardData.data.organization.team.members.nodes.all { it.login.isNotEmpty() } }
        assertTrue { githubDashboardData.data.organization.team.repositories.nodes.all { it.name.isNotEmpty() } }
        assertTrue { githubDashboardData.data.organization.team.repositories.nodes.all { it.url.isNotEmpty() }}
        assertTrue { githubDashboardData.data.organization.team.repositories.nodes.all { it.alertsUrl.isNotEmpty() }}
        assertNotNull(githubDashboardData.data.organization.team.repositories.nodes.all
            { it.vulnerabilityAlerts.arePresent})
        assertNotNull(githubDashboardData.data.organization.team.repositories.nodes.map { it.pullRequests.nodes })
        assertTrue {
            githubDashboardData.data.organization.team.repositories.nodes
                .filterNot { repository -> repository.pullRequests.nodes.isEmpty() }
                .all { repository ->
                    repository.pullRequests.nodes.all {
                        it.url.isNotEmpty() && it.author.login.isNotEmpty() && it.title.isNotEmpty()
                    }
                }
        }
    }

}
