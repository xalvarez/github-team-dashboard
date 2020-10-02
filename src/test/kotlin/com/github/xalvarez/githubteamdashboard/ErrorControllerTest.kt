package com.github.xalvarez.githubteamdashboard

import io.micronaut.http.HttpStatus.OK
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@MicronautTest
@ExtendWith(MockitoExtension::class)
internal class ErrorControllerTest {

    @InjectMocks
    lateinit var errorController: ErrorController

    @Test
    fun `should build model`() {
        val response = errorController.configurationError()
        val model = response.body() as HashMap<*, *>
        val errorModel = model["error"] as ErrorModel

        assertEquals(response.status, OK)
        assertNotNull(errorModel.title)
        assertNotNull(errorModel.message)
    }
}
