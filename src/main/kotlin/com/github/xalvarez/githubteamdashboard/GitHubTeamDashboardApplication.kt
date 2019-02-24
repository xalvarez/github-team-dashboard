package com.github.xalvarez.githubteamdashboard

import io.micronaut.runtime.Micronaut

object GitHubTeamDashboardApplication {

    @JvmStatic
    fun main() {
        Micronaut.build()
            .packages(GitHubTeamDashboardApplication::class.java.`package`.toString())
            .mainClass(GitHubTeamDashboardApplication::class.java)
            .start()
    }
}