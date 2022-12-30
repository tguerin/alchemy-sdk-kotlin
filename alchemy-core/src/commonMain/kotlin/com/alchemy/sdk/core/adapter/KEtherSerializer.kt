package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.Ether.Companion.wei
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KEtherSerializer : KSerializer<Ether> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Ether", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Ether {
        return decoder.decodeString().wei
    }

    override fun serialize(encoder: Encoder, value: Ether) {
        encoder.encodeString(value.weiHexValue.data)
    }
}