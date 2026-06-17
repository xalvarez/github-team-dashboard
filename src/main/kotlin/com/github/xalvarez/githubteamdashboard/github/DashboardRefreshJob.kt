package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

/**
 * Periodically refreshes the cached dashboard data in the background so that the load time of the
 * dashboard is decoupled from the (potentially slow) GitHub requests.
 *
 * Disabled in tests via the `github.scheduled-refresh.enabled` property.
 */
@Singleton
@Requires(property = "github.scheduled-refresh.enabled", value = "true", defaultValue = "true")
class DashboardRefreshJob(
    private val dashboardService: DashboardService,
) {
    private val logger = LoggerFactory.getLogger(DashboardRefreshJob::class.java)

    @Scheduled(fixedDelay = "\${github.refresh-interval:10m}", initialDelay = "0s")
    fun refresh() {
        dashboardService
            .refresh()
            .subscribe({}, { error -> logger.error("Scheduled dashboard refresh failed: ${error.message}") })
    }
}
