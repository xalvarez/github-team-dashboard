package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.Prototype

@Prototype
class GitHubService(private val gitHubClient: GitHubClient, private val gitHubConfiguration: GitHubConfiguration) {

    fun fetchDashboardData(): GithubDashboardData {
        val authorization = "Bearer ${gitHubConfiguration.token}"
        return gitHubClient.fetchDashboardData(authorization, gitHubConfiguration.userAgent, buildQuery()).blockingGet()

    }

    private fun buildQuery() = """
        {
          organization(login: "${gitHubConfiguration.organization}") {
            team(slug: "${gitHubConfiguration.team}") {
              members(first: 100) {
                nodes {
                  login
                }
              }
              repositories(first: 100) {
                nodes {
                  name
                  pullRequests(states: [OPEN], first: 100) {
                    nodes {
                      url
                    }
                  }
                }
              }
            }
          }
        }
    """.trimIndent()
}