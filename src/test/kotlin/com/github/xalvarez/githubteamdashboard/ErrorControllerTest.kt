package com.github.xalvarez.githubteamdashboard

import io.micronaut.http.HttpStatus.OK
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@MicronautTest
@ExtendWith(MockKExtension::class)
internal class ErrorControllerTest {
    @InjectMockKs
    lateinit var errorController: ErrorController

    @Test
    fun `should build model`() {
        val response = errorController.configurationError().block()
        val model = response?.body() as HashMap<*, *>
        val errorModel = model["error"] as ErrorModel

        assertEquals(response.status, OK)
        assertNotNull(errorModel.title)
        assertNotNull(errorModel.message)
    }
}
