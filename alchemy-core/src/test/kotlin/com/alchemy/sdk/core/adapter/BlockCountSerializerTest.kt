package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.BlockCount
import com.alchemy.sdk.core.model.BlockCount.Companion.blockCount
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class BlockCountSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert block count to hex string without leading zero`() = runTest {
        BlockCountSerializer.serialize(
            2.blockCount,
            BlockCount::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("0x2")
    }

    @Test
    fun `should handle null case`() {
        BlockCountSerializer.serialize(
            null,
            BlockCount::class.java,
            context
        ) shouldBeEqualTo JsonNull.INSTANCE
    }
}