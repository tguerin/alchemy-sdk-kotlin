package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.NftTokenType
import com.alchemy.sdk.core.model.nft.RefreshState
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class RefreshStateDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test
    fun `should return unknown if not a string or null`() {
        RefreshStateDeserializer.deserialize(
            JsonPrimitive(3),
            NftTokenType::class.java,
            context
        ) shouldBeEqualTo RefreshState.Unknown
    }

    @Test
    fun `should parse refresh state base on enum value`() {
        RefreshState.values().forEach { refreshState ->
            RefreshStateDeserializer.deserialize(
                JsonPrimitive(refreshState.value),
                RefreshState::class.java,
                context
            ) shouldBeEqualTo refreshState
        }

    }

    @Test
    fun `should handle null case`() {
        val data = RefreshStateDeserializer.deserialize(
            JsonNull.INSTANCE,
            RefreshState::class.java,
            context
        )
        data shouldBeEqualTo RefreshState.Unknown
    }
}