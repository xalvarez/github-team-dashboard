package com.github.xalvarez.githubteamdashboard.github

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import java.time.ZonedDateTime

@Serdeable
data class GithubDashboardData(
    val data: Data
)

@Serdeable
data class Data(
    val organization: Organization
)

@Serdeable
data class Organization(
    val team: Team
)

@Serdeable
data class Team(
    val name: String,
    val members: Members,
    val repositories: Repositories
)

@Serdeable
data class Members(
    val nodes: List<MembersNode>
)

@Serdeable
data class MembersNode(
    val login: String
)

@Serdeable
data class Repositories(
    val nodes: List<Repository>
)
@Serdeable
data class Repository(
    val name: String,
    val pullRequests: PullRequests,
    val url: String,
    val vulnerabilityAlerts: VulnerabilityAlerts
) {
    val alertsUrl: String by lazy { url +  ALERTS_URL_SUFFIX }
}

@Serdeable
data class VulnerabilityAlerts(
    @JsonProperty("totalCount")
    @Serdeable.Deserializable(using = TotalCountSerde::class)
    val arePresent: Boolean
)

@Serdeable
data class PullRequests(
    val nodes: List<PullRequestNode>
)

@Serdeable
data class PullRequestNode(
    val url: String,
    val createdAt: ZonedDateTime,
    val author: Author,
    val title: String,
    val reviews: Review,
    val isDraft: Boolean,
    val commits: Commits
)

@Serdeable
data class Author(
    val login: String
)

@Serdeable
data class Review(
    val nodes: List<ReviewNode>
)

@Serdeable
data class ReviewNode(
    val state: String
)

@Serdeable
data class Commits(
     val nodes: List<CommitNode>
)

@Serdeable
data class CommitNode(
      val commit: Commit
)
@Serdeable
data class Commit(
      val statusCheckRollup: CommitStatusCheckState?
)

@Serdeable
data class CommitStatusCheckState(
      val state: String
)

const val ALERTS_URL_SUFFIX = "/network/alerts"
