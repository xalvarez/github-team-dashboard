package com.github.xalvarez.githubteamdashboard.github

import io.micronaut.core.type.Argument
import io.micronaut.serde.Decoder
import io.micronaut.serde.Deserializer
import io.micronaut.serde.Encoder
import io.micronaut.serde.Serde
import io.micronaut.serde.Serializer

class TotalCountSerde : Serde<Boolean> {
    @Suppress("EmptyFunctionBlock")
    override fun serialize(
        encoder: Encoder,
        context: Serializer.EncoderContext,
        type: Argument<out Boolean>,
        value: Boolean,
    ) {
    }

    override fun deserialize(
        decoder: Decoder,
        context: Deserializer.DecoderContext,
        type: Argument<in Boolean>,
    ) = decoder.decodeInt() > 0
}
