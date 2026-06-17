package com.github.xalvarez.githubteamdashboard.github

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicReference

@Singleton
open class DashboardService(
    private val gitHubService: GitHubService,
) {
    private val logger = LoggerFactory.getLogger(DashboardService::class.java)
    private val cachedData = AtomicReference<GithubDashboardData?>()
    private val lastUpdatedAt = AtomicReference<ZonedDateTime?>()

    open fun fetchDashboardData(): Mono<GithubDashboardData> = cachedData.get()?.let { Mono.just(it) } ?: refresh()

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
