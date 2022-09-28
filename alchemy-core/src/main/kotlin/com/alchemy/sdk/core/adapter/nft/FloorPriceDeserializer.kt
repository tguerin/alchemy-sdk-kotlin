package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.FloorPrice
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