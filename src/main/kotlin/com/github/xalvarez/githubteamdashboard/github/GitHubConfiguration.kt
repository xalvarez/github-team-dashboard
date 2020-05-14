package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Context
import javax.validation.constraints.NotEmpty

@ConfigurationProperties(value = GITHUB_PROPERTIES_PREFIX, cliPrefix = [GITHUB_PROPERTIES_PREFIX])
@Context
interface GitHubConfiguration {
    @get:NotEmpty val organization: String
    @get:NotEmpty val team: String
    @get:NotEmpty val token: String
    @get:NotEmpty val userAgent: String
}

const val GITHUB_PROPERTIES_PREFIX = "github"