package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.Index.Companion.index
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KIndexSerializer : KSerializer<Index> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Index", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Index {
        return decoder.decodeString().hexString.intValue().index
    }

    override fun serialize(encoder: Encoder, value: Index) {
        encoder.encodeString(value.value.hexString.withoutLeadingZero())
    }

}