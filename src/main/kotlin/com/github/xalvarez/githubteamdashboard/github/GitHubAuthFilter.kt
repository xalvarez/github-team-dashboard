package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import jakarta.inject.Singleton
import org.reactivestreams.Publisher

@BearerToken
@Singleton
internal class GitHubAuthFilter(
    private val gitHubConfiguration: GitHubConfiguration,
) : HttpClientFilter {
    override fun doFilter(
        request: MutableHttpRequest<*>,
        chain: ClientFilterChain,
    ): Publisher<out HttpResponse<*>> =
        chain.proceed(
            request
                .header("User-Agent", gitHubConfiguration.userAgent)
                .bearerAuth(gitHubConfiguration.token),
        )
}
