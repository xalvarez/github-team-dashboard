package com.github.xalvarez.githubteamdashboard

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Value
import javax.inject.Singleton

@Factory
class WireMockFactory {

    @Value("\${wiremock.port}")
    private lateinit var wireMockPort: String

    @Singleton
    fun wireMockServer(): WireMockServer {
        val wireMockConfiguration = WireMockConfiguration
            .options()
            .port(wireMockPort.toInt())
            .usingFilesUnderClasspath(WIREMOCK_CLASSPATH)

        return WireMockServer(wireMockConfiguration)
    }
}

private const val WIREMOCK_CLASSPATH = "wiremock"
