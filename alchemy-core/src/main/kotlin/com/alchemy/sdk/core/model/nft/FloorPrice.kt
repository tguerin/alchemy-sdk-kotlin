package com.alchemy.sdk.core.model.nft

sealed interface FloorPrice {
    data class FloorPriceError(val error: String) : FloorPrice
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