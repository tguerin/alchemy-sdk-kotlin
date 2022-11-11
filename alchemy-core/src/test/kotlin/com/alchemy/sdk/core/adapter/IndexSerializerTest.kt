package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.Index.Companion.index
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class IndexSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert index to hex string without leading zero`() = runTest {
        IndexSerializer.serialize(
            2.index,
            Index::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("0x2")
    }

    @Test
    fun `should handle null case`() {
        IndexSerializer.serialize(
            null,
            Index::class.java,
            context
        ) shouldBeEqualTo JsonNull.INSTANCE
    }
}