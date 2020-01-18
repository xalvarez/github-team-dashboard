package com.github.xalvarez.githubteamdashboard.github

import com.github.xalvarez.githubteamdashboard.AbstractWireMockTest
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@MicronautTest
internal class GitHubServiceTest : AbstractWireMockTest() {

    @Inject
    private lateinit var gitHubService: GitHubService

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
                        it.url.isNotEmpty() && it.author.login.isNotEmpty() && it.title.isNotEmpty()
                    }
                }
        }
    }

}