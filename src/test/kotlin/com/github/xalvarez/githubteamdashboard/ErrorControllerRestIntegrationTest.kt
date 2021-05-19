package com.github.xalvarez.githubteamdashboard

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import io.micronaut.http.HttpHeaders.CONTENT_TYPE
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.HttpStatus.UNAUTHORIZED
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.http.MediaType.TEXT_HTML_TYPE
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest
internal class ErrorControllerRestIntegrationTest : RestIntegrationTest() {

    @Test
    fun `should show error page if github request fails`() {
        givenUnsuccessfulGitHubRequest()

        val response = client.toBlocking().exchange<HttpResponse<Any>>("/dashboard")

        assertEquals(response.status, OK)
        assertEquals(response.contentType.get(), TEXT_HTML_TYPE)
    }

    private fun givenUnsuccessfulGitHubRequest() {
        wireMockServer.stubFor(
            post(WireMock.urlPathEqualTo("/graphql")).willReturn(
                aResponse()
                    .withStatus(UNAUTHORIZED.code)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
            )
        )
    }
}
