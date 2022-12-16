package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.OwnedNftsResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object KOwnedNftsResponseSerializer : JsonContentPolymorphicSerializer<OwnedNftsResponse>(OwnedNftsResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out OwnedNftsResponse> {
        return if ((element.jsonObject["ownedNfts"] as JsonArray).any { "title" in (it as JsonObject) }) {
            OwnedNftsResponse.OwnedAlchemyNftsResponse.serializer()
        } else {
            OwnedNftsResponse.OwnedBaseNftsResponse.serializer()
        }
    }

}