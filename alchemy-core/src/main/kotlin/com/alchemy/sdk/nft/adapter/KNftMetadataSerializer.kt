package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.NftMetadata
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

object KNftMetadataSerializer : KSerializer<NftMetadata> {
    override val descriptor: SerialDescriptor = MapSerializer(String.serializer(), JsonElement.serializer()).descriptor

    override fun deserialize(decoder: Decoder): NftMetadata {
        return NftMetadata(
            decoder.decodeSerializableValue(
                MapSerializer(
                    String.serializer(),
                    JsonElement.serializer()
                )
            )
        )
    }

    override fun serialize(encoder: Encoder, value: NftMetadata) {
        encoder.encodeSerializableValue(MapSerializer(String.serializer(), JsonElement.serializer()), value.metadata)
    }
}