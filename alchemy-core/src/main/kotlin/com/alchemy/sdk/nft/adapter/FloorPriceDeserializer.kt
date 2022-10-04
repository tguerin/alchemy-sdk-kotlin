package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.FloorPrice
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import java.lang.reflect.Type

object FloorPriceDeserializer : JsonDeserializer<FloorPrice?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): FloorPrice? {
        return when {
            json == JsonNull.INSTANCE -> null
            typeOfT == FloorPrice.FloorPriceMarketplace::class.java || (json is JsonObject && containsFloorPrice(
                json
            )) -> context.deserialize(
                json,
                FloorPrice.FloorPriceMarketplace::class.java
            )
            typeOfT == FloorPrice.FloorPriceError::class.java || (json is JsonObject && containsError(
                json
            )) -> context.deserialize(
                json,
                FloorPrice.FloorPriceError::class.java
            )
            else -> throw IllegalStateException("Unknown Nft type")
        }
    }

    private fun containsFloorPrice(json: JsonObject): Boolean = json.has("floorPrice")

    private fun containsError(json: JsonObject): Boolean = json.has("error")
}