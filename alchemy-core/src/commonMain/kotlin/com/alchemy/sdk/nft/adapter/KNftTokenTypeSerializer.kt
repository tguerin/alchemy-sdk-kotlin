package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.NftTokenType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KNftTokenTypeSerializer : KSerializer<NftTokenType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NftTokenType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): NftTokenType {
        val nftTokenTypeValue = decoder.decodeString()
        return NftTokenType.values().firstOrNull { it.value.equals(nftTokenTypeValue, ignoreCase = true) }
            ?: NftTokenType.Unknown
    }

    override fun serialize(encoder: Encoder, value: NftTokenType) {
        encoder.encodeString(value.value)
    }
}