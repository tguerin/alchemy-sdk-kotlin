package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.nft.model.FloorPrice
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class FloorPriceDeserializerTest {

    @Test(expected = Exception::class)
    fun `should throw exception if value is not a json object`() {
        json.decodeFromString<FloorPrice>("2")
    }

    @Test
    fun `should parse floor price as a market place floor price`() {
        val expectedFloorPrice = FloorPrice.FloorPriceMarketplace(
            floorPrice = 0.0,
            priceCurrency = "ETH",
            retrievedAt = "2022-09-05T01:52:29.345Z",
            collectionUrl = "https://opensea.io/collection/the-wanderer-lands"
        )

        json.decodeFromString<FloorPrice.FloorPriceMarketplace>(
            "{\"floorPrice\": 0, \"priceCurrency\": \"ETH\", \"retrievedAt\": \"2022-09-05T01:52:29.345Z\", \"collectionUrl\": \"https://opensea.io/collection/the-wanderer-lands\"}"
        ) shouldBeEqualTo expectedFloorPrice
    }

    @Test
    fun `should parse floor price as an error`() {
        val expectedFloorPrice = FloorPrice.FloorPriceError(
            error = "oh oh"
        )
        json.decodeFromString<FloorPrice.FloorPriceError>("{\"error\": \"oh oh\"}") shouldBeEqualTo expectedFloorPrice
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<FloorPrice.FloorPriceMarketplace?>("null")
        data shouldBeEqualTo null
    }
}