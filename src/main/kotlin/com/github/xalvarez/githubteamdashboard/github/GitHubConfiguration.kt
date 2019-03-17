package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.ConfigurationProperties
import javax.inject.Singleton

@ConfigurationProperties(value = GITHUB_PROPERTIES_PREFIX, cliPrefix = [GITHUB_PROPERTIES_PREFIX])
@Singleton
class GitHubConfiguration {
    lateinit var organization: String
    lateinit var team: String
    lateinit var token: String
    lateinit var userAgent: String
}

const val GITHUB_PROPERTIES_PREFIX = "github"