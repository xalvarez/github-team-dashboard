package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.http.annotation.FilterMatcher
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@FilterMatcher
@Retention(RUNTIME)
@Target(CLASS)
annotation class BearerToken
