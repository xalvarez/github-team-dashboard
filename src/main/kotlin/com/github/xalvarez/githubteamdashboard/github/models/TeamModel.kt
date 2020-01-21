package com.github.xalvarez.githubteamdashboard.github.models

data class TeamModel(
    val name: String,
    val members: List<Member>
)

data class Member(
    val username: String
)