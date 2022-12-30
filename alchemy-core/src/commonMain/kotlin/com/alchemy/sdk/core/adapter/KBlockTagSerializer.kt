package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.BlockTag.Safe.blockTag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KBlockTagSerializer : KSerializer<BlockTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BlockTag", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BlockTag {
        return decoder.decodeString().blockTag
    }

    override fun serialize(encoder: Encoder, value: BlockTag) {
        encoder.encodeString(value.value)
    }

}