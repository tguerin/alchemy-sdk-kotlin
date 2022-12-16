package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.FloorPrice
import kotlinx.serialization.encodeToString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class FloorPriceSerializerTest {

    @Test
    fun `should serialize market place floor price`() {
        val floorPrice = FloorPrice.FloorPriceMarketplace(
            floorPrice = 0.0,
            priceCurrency = "ETH",
            retrievedAt = "2022-09-05T01:52:29.345Z",
            collectionUrl = "https://opensea.io/collection/the-wanderer-lands"
        )

        json.encodeToString(
            floorPrice
        ) shouldBeEqualTo "{\"floorPrice\":0.0,\"priceCurrency\":\"ETH\",\"collectionUrl\":\"https://opensea.io/collection/the-wanderer-lands\",\"retrievedAt\":\"2022-09-05T01:52:29.345Z\"}"
    }

    @Test
    fun `should serialize floor price error`() {
        val floorPriceError = FloorPrice.FloorPriceError(
            error = "oh oh"
        )
        json.encodeToString(floorPriceError) shouldBeEqualTo "{\"error\":\"oh oh\"}"
    }

}