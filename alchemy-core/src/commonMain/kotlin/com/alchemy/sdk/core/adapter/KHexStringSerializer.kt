package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KHexStringSerializer : KSerializer<HexString> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HexString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): HexString {
        return decoder.decodeString().hexString
    }

    override fun serialize(encoder: Encoder, value: HexString) {
        encoder.encodeString(value.data)
    }
}