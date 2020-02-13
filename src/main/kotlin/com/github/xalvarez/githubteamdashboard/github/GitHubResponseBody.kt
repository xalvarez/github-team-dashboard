package com.github.xalvarez.githubteamdashboard.github

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
    val pullRequests: PullRequests
)

data class PullRequests(
    val nodes: List<PullRequestNode>
)

data class PullRequestNode(
    val url: String,
    val createdAt: LocalDateTime,
    val author: Author,
    val title: String,
    val reviews: Review
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