package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.FloorPrice
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object KFloorPriceSerializer : JsonContentPolymorphicSerializer<FloorPrice>(FloorPrice::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out FloorPrice> {
        return if ("error" in element.jsonObject) {
            FloorPrice.FloorPriceError.serializer()
        } else {
            FloorPrice.FloorPriceMarketplace.serializer()
        }
    }

}