package com.github.xalvarez.githubteamdashboard

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.micronaut.http.MediaType.APPLICATION_JSON
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import javax.inject.Inject

@TestInstance(PER_CLASS)
abstract class AbstractWireMockTest {

    @Inject
    protected lateinit var wireMockServer: WireMockServer

    @BeforeAll
    fun startWireMockServer() {
        wireMockServer.start()
    }

    @BeforeEach
    fun resetWireMockRequests() {
        wireMockServer.resetRequests()
    }

    @AfterAll
    fun stopWireMockServer() {
        wireMockServer.stop()
    }

    protected fun givenSuccessfulGitHubRequest() {
        wireMockServer.stubFor(
            post(urlPathEqualTo("/graphql")).willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON)
                    .withBodyFile(GITHUB_SUCCESSFUL_REQUEST_STUB_FILE)
            )
        )
    }
}

private const val GITHUB_SUCCESSFUL_REQUEST_STUB_FILE = "post_github_successful.json"