package com.github.xalvarez.githubteamdashboard.github

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.time.Duration

internal class DashboardServiceTest {
    private val gitHubService = mockk<GitHubService>()
    private val dashboardService = DashboardService(gitHubService)

    @Test
    fun `should cache data after first refresh`() {
        val data = mockk<GithubDashboardData>()
        every { gitHubService.fetchDashboardData() } returns Mono.just(data)

        assertSame(data, dashboardService.fetchDashboardData().block())
        assertSame(data, dashboardService.fetchDashboardData().block())

        verify(exactly = 1) { gitHubService.fetchDashboardData() }
    }

    @Test
    fun `should coalesce concurrent refreshes into a single GitHub fetch`() {
        val data = mockk<GithubDashboardData>()
        every { gitHubService.fetchDashboardData() } answers {
            Mono.just(data).delayElement(Duration.ofMillis(200))
        }

        val results =
            Mono
                .zip(dashboardService.refresh(), dashboardService.refresh()) { first, second -> first to second }
                .block()

        assertSame(data, results?.first)
        assertSame(data, results?.second)
        verify(exactly = 1) { gitHubService.fetchDashboardData() }
    }

    @Test
    fun `should fetch again after a refresh completes`() {
        val data = mockk<GithubDashboardData>()
        every { gitHubService.fetchDashboardData() } returns Mono.just(data)

        dashboardService.refresh().block()
        dashboardService.refresh().block()

        verify(exactly = 2) { gitHubService.fetchDashboardData() }
    }
}
