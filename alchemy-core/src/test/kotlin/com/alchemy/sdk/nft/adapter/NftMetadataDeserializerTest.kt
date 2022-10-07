package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.NftMetadata
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class NftMetadataDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test
    fun `should return metadata from parsed json object`() {
        val json = JsonObject()
        val metadata = mapOf("1" to "2")
        every {
            context.deserialize<Map<String, Any>>(
                json,
                object : TypeToken<Map<String, Any>>() {}.type
            )
        } returns metadata
        NftMetadataDeserializer.deserialize(
            json,
            NftMetadata::class.java,
            context
        ) shouldBeEqualTo NftMetadata(metadata)
    }

    @Test
    fun `should handle null case`() {
        val data = NftMetadataDeserializer.deserialize(
            JsonNull.INSTANCE,
            NftMetadata::class.java,
            context
        )
        data shouldBeEqualTo null
    }
}