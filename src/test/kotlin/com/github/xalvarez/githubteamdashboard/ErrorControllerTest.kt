package com.github.xalvarez.githubteamdashboard

import io.micronaut.http.HttpStatus.OK
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations.initMocks

@MicronautTest
internal class ErrorControllerTest {

    @InjectMocks
    lateinit var errorController: ErrorController

    @BeforeEach
    fun setup() = initMocks(this)

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