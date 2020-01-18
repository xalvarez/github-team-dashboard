package com.github.xalvarez.githubteamdashboard

import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import javax.inject.Inject

abstract class AbstractRestIntegrationTest : AbstractWireMockTest() {

    protected val client: RxHttpClient by lazy { RxHttpClient.create(server.url) }

    @Inject
    protected lateinit var server: EmbeddedServer
}