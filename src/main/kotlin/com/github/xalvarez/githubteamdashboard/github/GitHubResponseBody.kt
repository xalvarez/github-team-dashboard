package com.github.xalvarez.githubteamdashboard.github

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GithubDashboardData(
    val data: Data
)

data class Data(
    val organization: Organization
)

data class Organization(
    val team: Team
)

data class Team(
    val name: String,
    val members: Members,
    val repositories: Repositories
)

data class Members(
    val nodes: List<MembersNode>
)

data class MembersNode(
    val login: String
)

data class Repositories(
    val nodes: List<Repository>
)

data class Repository(
    val name: String,
    val pullRequests: PullRequests,
    val url: String,
    val vulnerabilityAlerts: VulnerabilityAlerts
) {
    val alertsUrl: String by lazy { url +  ALERTS_URL_SUFFIX }
}

data class VulnerabilityAlerts(
    @JsonProperty("totalCount")
    @JsonFormat(shape = NUMBER)
    val arePresent: Boolean
)

data class PullRequests(
    val nodes: List<PullRequestNode>
)

data class PullRequestNode(
    val url: String,
    val createdAt: LocalDateTime,
    val author: Author,
    val title: String,
    val reviews: Review,
    val isDraft: Boolean,
    val commits: Commits
)

data class Author(
    val login: String
)

data class Review(
    val nodes: List<ReviewNode>
)

data class ReviewNode(
    val state: String
)

data class Commits(
     val nodes: List<CommitNode>
)

data class CommitNode(
      val commit: Commit
)

data class Commit(
      val statusCheckRollup: CommitStatusCheckState?
)

data class CommitStatusCheckState(
      val state: String
)

const val ALERTS_URL_SUFFIX = "/network/alerts"
