package com.alchemy.sdk.nft.model

import com.alchemy.sdk.nft.adapter.KFloorPriceSerializer
import kotlinx.serialization.Serializable

@Serializable(with = KFloorPriceSerializer::class)
sealed interface FloorPrice {
    @Serializable
    data class FloorPriceError(val error: String) : FloorPrice

    @Serializable
    data class FloorPriceMarketplace(
        /** The floor price of the collection on the given marketplace */
        val floorPrice: Double,
        /** The currency in which the floor price is denominated */
        val priceCurrency: String,
        /** The link to the collection on the given marketplace */
        val collectionUrl: String,
        /** UTC timestamp of when the floor price was retrieved from the marketplace */
        val retrievedAt: String,
    ) : FloorPrice

}