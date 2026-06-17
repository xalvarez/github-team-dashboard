package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class TeamRepositoriesResponse(
    val data: TeamRepositoriesData? = null,
    val errors: List<GraphQlError>? = null,
)

@Serdeable
data class TeamRepositoriesData(
    val rateLimit: RateLimit? = null,
    val organization: TeamRepositoriesOrganization? = null,
)

@Serdeable
data class TeamRepositoriesOrganization(
    val team: TeamRepositoriesTeam,
)

@Serdeable
data class TeamRepositoriesTeam(
    val name: String,
    val members: Members,
    val repositories: RepositorySummaryConnection,
)

@Serdeable
data class RepositorySummaryConnection(
    val nodes: List<RepositorySummaryNode>,
    val pageInfo: PageInfo,
)

@Serdeable
data class RepositorySummaryNode(
    val name: String,
    val url: String,
    val isArchived: Boolean,
    val vulnerabilityAlerts: VulnerabilityAlerts,
)

@Serdeable
data class RepositoryPullRequestsResponse(
    val data: RepositoryPullRequestsData? = null,
    val errors: List<GraphQlError>? = null,
)

@Serdeable
data class RepositoryPullRequestsData(
    val rateLimit: RateLimit? = null,
    val repository: RepositoryPullRequestsNode? = null,
)

@Serdeable
data class RepositoryPullRequestsNode(
    val pullRequests: PullRequests,
)

@Serdeable
data class RateLimit(
    val cost: Int,
    val remaining: Int,
)

@Serdeable
data class GraphQlError(
    val message: String,
)
