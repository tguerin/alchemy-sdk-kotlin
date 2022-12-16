package com.alchemy.sdk.nft.model

import kotlinx.serialization.Serializable

@Serializable
data class FloorPriceResponse(
    val openSea: FloorPrice,
    val looksRare: FloorPrice
)