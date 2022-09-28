package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.NftTokenType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class NftTokenTypeDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test
    fun `should return unknown if not a string or null`() {
        NftTokenTypeDeserializer.deserialize(
            JsonPrimitive(3),
            NftTokenType::class.java,
            context
        ) shouldBeEqualTo NftTokenType.Unknown
    }

    @Test
    fun `should parse token type base on enum value`() {
        NftTokenType.values().forEach { tokenType ->
            NftTokenTypeDeserializer.deserialize(
                JsonPrimitive(tokenType.value),
                NftTokenType::class.java,
                context
            ) shouldBeEqualTo tokenType
        }

    }

    @Test
    fun `should handle null case`() {
        val data = NftTokenTypeDeserializer.deserialize(
            JsonNull.INSTANCE,
            NftTokenType::class.java,
            context
        )
        data shouldBeEqualTo NftTokenType.Unknown
    }
}