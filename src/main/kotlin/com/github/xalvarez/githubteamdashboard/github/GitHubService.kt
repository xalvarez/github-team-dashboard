package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.context.annotation.Prototype

@Prototype
class GitHubService(private val gitHubClient: GitHubClient, private val gitHubConfiguration: GitHubConfiguration) {

    fun fetchDashboardData(): GithubDashboardData = gitHubClient.fetchDashboardData(buildQuery())

    private fun buildQuery() = """
        {
          organization(login: "${gitHubConfiguration.organization}") {
            team(slug: "${gitHubConfiguration.team}") {
              name
              members(first: 100, orderBy: { field: LOGIN, direction: ASC }) {
                nodes {
                  login
                }
              }
              repositories(first: 100, orderBy: { field: NAME, direction: ASC }) {
                nodes {
                  name
                  url
                  vulnerabilityAlerts(first: 1) {
                    totalCount
                  }
                  pullRequests(states: [OPEN], first: 100) {
                    nodes {
                      url
                      createdAt
                      author {
                        login
                      }
                      title
                      reviews(last: 1, states: [APPROVED, CHANGES_REQUESTED]) {
                        nodes {
                          state
                        }
                      }
                      isDraft
                      commits(last: 1) {
                        nodes {
                          commit {
                            statusCheckRollup {
                              state
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
    """.trimIndent()
}
