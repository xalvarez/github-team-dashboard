package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.core.type.Argument
import io.micronaut.serde.Decoder
import io.micronaut.serde.Deserializer
import io.micronaut.serde.Encoder
import io.micronaut.serde.Serializer
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class TotalCountSerdeTest {

    @InjectMockKs
    lateinit var totalCountSerde: TotalCountSerde

    @MockK
    lateinit var decoder: Decoder

    @MockK
    lateinit var encoder: Encoder

    @MockK
    lateinit var decoderContext: Deserializer.DecoderContext

    @MockK
    lateinit var encoderContext: Serializer.EncoderContext

    @MockK
    lateinit var deserializeType: Argument<in Boolean>

    @MockK
    lateinit var serializeType: Argument<out Boolean>

    @Test
    fun `should deserialize positive integer as boolean true`() {
        every { decoder.decodeInt() } returns 1

        val result = totalCountSerde.deserialize(decoder, decoderContext, deserializeType)

        assertTrue(result)
    }

    @Test
    fun `should deserialize non positive integer as boolean false`() {
        every { decoder.decodeInt() } returns 0

        val result = totalCountSerde.deserialize(decoder, decoderContext, deserializeType)

        assertFalse(result)
    }

    @Test
    fun `should do nothing when calling serialize`() {
        totalCountSerde.serialize(encoder, encoderContext, serializeType, true)

        verify { encoder wasNot Called }
    }
}
