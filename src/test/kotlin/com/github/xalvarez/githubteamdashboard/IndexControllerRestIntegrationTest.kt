package com.github.xalvarez.githubteamdashboard

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.MediaType.TEXT_HTML_TYPE
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject
import kotlin.test.assertEquals

@MicronautTest
class IndexControllerRestIntegrationTest : AbstractWireMockTest() {

    private val client: RxHttpClient by lazy { RxHttpClient.create(server.url) }

    @Inject
    private lateinit var server: EmbeddedServer

    @Test
    fun `should build index page`() {
        givenSuccessfulGitHubRequest()

        val response = client.toBlocking().exchange<HttpResponse<Any>>("")

        assertEquals(response.status, OK)
        assertEquals(response.contentType.get(), TEXT_HTML_TYPE)
    }
}
