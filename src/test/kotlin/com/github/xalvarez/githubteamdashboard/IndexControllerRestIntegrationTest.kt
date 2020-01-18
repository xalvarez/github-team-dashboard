package com.github.xalvarez.githubteamdashboard

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.MediaType.TEXT_HTML_TYPE
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@MicronautTest
internal class IndexControllerRestIntegrationTest : AbstractRestIntegrationTest() {

    @Test
    fun `should build index`() {
        givenSuccessfulGitHubRequest()

        val response = client.toBlocking().exchange<HttpResponse<Any>>("/dashboard")

        assertEquals(response.status, OK)
        assertEquals(response.contentType.get(), TEXT_HTML_TYPE)
    }

    @Test
    fun `should build dashboard`() {
        givenSuccessfulGitHubRequest()

        val response = client.toBlocking().exchange<HttpResponse<Any>>("/dashboard")

        assertEquals(response.status, OK)
        assertEquals(response.contentType.get(), TEXT_HTML_TYPE)
    }
}
