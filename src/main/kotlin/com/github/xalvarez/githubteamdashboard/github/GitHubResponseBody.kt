package com.github.xalvarez.githubteamdashboard.github

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
    val members: Members
)

data class Members(
    val nodes: List<MembersNode>
)

data class MembersNode(
    val login: String
)