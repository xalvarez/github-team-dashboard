package com.github.xalvarez.githubteamdashboard.github.models

data class PullRequest(
    val url: String,
    val createdAt: String,
    val author: String,
    val title: String,
    val repositoryName: String
)