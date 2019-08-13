package com.github.xalvarez.githubteamdashboard.github.models

data class Team(
    val name: String,
    val members: List<Member>
)

data class Member(
    val username: String
)