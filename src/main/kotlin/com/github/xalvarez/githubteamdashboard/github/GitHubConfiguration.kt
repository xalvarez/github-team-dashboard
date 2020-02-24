package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(value = GITHUB_PROPERTIES_PREFIX, cliPrefix = [GITHUB_PROPERTIES_PREFIX])
interface GitHubConfiguration {
    val organization: String
    val team: String
    val token: String
    val userAgent: String
}

const val GITHUB_PROPERTIES_PREFIX = "github"