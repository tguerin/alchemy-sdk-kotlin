package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.NftExcludeFilter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KNftExcludeFilterSerializer : KSerializer<NftExcludeFilter> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NftExcludeFilter", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): NftExcludeFilter {
        val nftExcludeFilterValue = decoder.decodeString()
        return NftExcludeFilter.values().firstOrNull { it.value == nftExcludeFilterValue }
            ?: error("Unknown nft exclude filter: $nftExcludeFilterValue")
    }

    override fun serialize(encoder: Encoder, value: NftExcludeFilter) {
        encoder.encodeString(value.value)
    }
}