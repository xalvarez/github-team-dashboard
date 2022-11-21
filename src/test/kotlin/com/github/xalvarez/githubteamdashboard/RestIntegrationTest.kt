package com.github.xalvarez.githubteamdashboard

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.micronaut.http.HttpHeaders.CONTENT_TYPE
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
open class RestIntegrationTest {

    protected val client: HttpClient by lazy { HttpClient.create(server.url) }

    @Inject
    private lateinit var server: EmbeddedServer

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
                    .withStatus(OK.code)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .withBodyFile(GITHUB_SUCCESSFUL_REQUEST_STUB_FILE)
            )
        )
    }
}

private const val GITHUB_SUCCESSFUL_REQUEST_STUB_FILE = "post_github_successful.json"
