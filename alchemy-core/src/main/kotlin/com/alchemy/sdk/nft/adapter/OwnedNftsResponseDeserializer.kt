package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.Nft.Companion.alchemyNftSpecificPropertyNames
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

object OwnedNftsResponseDeserializer : JsonDeserializer<OwnedNftsResponse> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): OwnedNftsResponse {
        check(json is JsonObject)
        return when {
            isAlchemyNft(json) -> context.deserialize<OwnedNftsResponse.OwnedAlchemyNftsResponse>(
                json,
                OwnedNftsResponse.OwnedAlchemyNftsResponse::class.java
            )
            else -> context.deserialize<OwnedNftsResponse.OwnedBaseNftsResponse>(
                json,
                OwnedNftsResponse.OwnedBaseNftsResponse::class.java
            )
        }
    }

    private fun isAlchemyNft(json: JsonObject): Boolean {
        val nftsArray = json.getAsJsonArray("ownedNfts")
        return !nftsArray.isEmpty && run {
            val nftObject = nftsArray.get(0).asJsonObject
            alchemyNftSpecificPropertyNames.any { property ->
                nftObject.has(property)
            }
        }
    }

}