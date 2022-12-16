package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Percentile
import com.alchemy.sdk.core.model.Percentile.Companion.percentile
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KPercentileSerializer : KSerializer<Percentile> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Percentile", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Percentile {
        return decoder.decodeFloat().percentile
    }

    override fun serialize(encoder: Encoder, value: Percentile) {
        encoder.encodeFloat(value.value)
    }

}