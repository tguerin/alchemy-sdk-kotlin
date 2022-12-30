package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.BlockCount
import com.alchemy.sdk.core.model.BlockCount.Companion.blockCount
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KBlockCountSerializer : KSerializer<BlockCount> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BlockCount", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BlockCount {
        return decoder.decodeString().hexString.intValue().blockCount
    }

    override fun serialize(encoder: Encoder, value: BlockCount) {
        encoder.encodeString(value.value.hexString.withoutLeadingZero())
    }

}