package com.github.xalvarez.githubteamdashboard.github

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicReference

/**
 * Caches the assembled dashboard data so that incoming HTTP requests are served from an in-memory
 * snapshot instead of triggering a (potentially slow) call to GitHub on every request.
 *
 * The snapshot is refreshed in the background by [DashboardRefreshJob]. The very first request
 * (before any snapshot exists) falls back to fetching the data on demand so the dashboard still
 * works immediately after startup.
 */
@Singleton
open class DashboardService(
    private val gitHubService: GitHubService,
) {
    private val logger = LoggerFactory.getLogger(DashboardService::class.java)
    private val cachedData = AtomicReference<GithubDashboardData?>()
    private val lastUpdatedAt = AtomicReference<ZonedDateTime?>()

    open fun fetchDashboardData(): Mono<GithubDashboardData> = cachedData.get()?.let { Mono.just(it) } ?: refresh()

    /**
     * Returns the instant at which the cached dashboard data was last refreshed from GitHub, or
     * `null` if no successful refresh has happened yet.
     */
    open fun getLastUpdatedAt(): ZonedDateTime? = lastUpdatedAt.get()

    open fun refresh(): Mono<GithubDashboardData> =
        gitHubService
            .fetchDashboardData()
            .doOnNext { data ->
                cachedData.set(data)
                lastUpdatedAt.set(ZonedDateTime.now())
                logger.info("Dashboard data refreshed from GitHub")
            }.doOnError { error -> logger.error("Failed to refresh dashboard data: ${error.message}") }
}
