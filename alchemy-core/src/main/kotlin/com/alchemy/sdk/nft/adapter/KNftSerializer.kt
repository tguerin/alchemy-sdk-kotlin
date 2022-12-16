package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.Nft
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object KNftSerializer : JsonContentPolymorphicSerializer<Nft>(Nft::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Nft> {
        return if ("title" in element.jsonObject) {
            Nft.AlchemyNft.serializer()
        } else {
            Nft.BaseNft.serializer()
        }
    }

}