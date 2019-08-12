package com.github.xalvarez.githubteamdashboard.github

data class GithubDashboardData(
    val data: Data
)

class Data(
    val organization: Organization
)

class Organization(
    val team: Team
)

class Team(
    val name: String,
    val members: Members
)

class Members(
    val nodes: List<MembersNode>
)

class MembersNode(
    val login: String
)