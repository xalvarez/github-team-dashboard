package com.github.xalvarez.githubteamdashboard.github.models

data class PullRequestModel(
    val url: String,
    val createdAt: String,
    val author: String,
    val title: String,
    val repositoryName: String,
    val state: ReviewState,
    val checkState: CheckState
)