package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.FloorPrice
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class FloorPriceDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a json object`() {
        FloorPriceDeserializer.deserialize(
            JsonPrimitive(2),
            FloorPrice::class.java,
            context
        )
    }

    @Test
    fun `should parse floor price as a market place floor price`() {
        val expectedFloorPrice = FloorPrice.FloorPriceMarketplace(
            floorPrice = 0.0,
            priceCurrency = "ETH",
            retrievedAt = "2022-09-05T01:52:29.345Z",
            collectionUrl = "https://opensea.io/collection/the-wanderer-lands"
        )
        val json = JsonObject().apply {
            add("floorPrice", JsonPrimitive(0))
            add("priceCurrency", JsonPrimitive("ETH"))
            add("retrievedAt", JsonPrimitive("2022-09-05T01:52:29.345Z"))
            add("collectionUrl", JsonPrimitive("https://opensea.io/collection/the-wanderer-lands"))
        }

        every {
            context.deserialize<FloorPrice>(
                json,
                FloorPrice.FloorPriceMarketplace::class.java
            )
        } returns expectedFloorPrice
        FloorPriceDeserializer.deserialize(
            json,
            FloorPrice::class.java,
            context
        ) shouldBeEqualTo expectedFloorPrice
    }

    @Test
    fun `should parse floor price as an error`() {
        val expectedFloorPrice = FloorPrice.FloorPriceError(
            error = "oh oh"
        )
        val json = JsonObject().apply {
            add("error", JsonPrimitive("oh oh"))
        }

        every {
            context.deserialize<FloorPrice>(
                json,
                FloorPrice.FloorPriceError::class.java
            )
        } returns expectedFloorPrice
        FloorPriceDeserializer.deserialize(
            json,
            FloorPrice::class.java,
            context
        ) shouldBeEqualTo expectedFloorPrice
    }

    @Test
    fun `should handle null case`() {
        val data = FloorPriceDeserializer.deserialize(
            JsonNull.INSTANCE,
            FloorPrice::class.java,
            context
        )
        data shouldBeEqualTo null
    }
}