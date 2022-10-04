package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class BlockTagSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert block tag to string`() = runTest {
        BlockTagSerializer.serialize(
            BlockTag.Latest,
            BlockTag.Latest::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("latest")
        BlockTagSerializer.serialize(
            BlockTag.Safe,
            BlockTag.Safe::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("safe")
        BlockTagSerializer.serialize(
            BlockTag.Pending,
            BlockTag.Pending::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("pending")
        BlockTagSerializer.serialize(
            BlockTag.Finalized,
            BlockTag.Finalized::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("finalized")
        BlockTagSerializer.serialize(
            BlockTag.BlockTagNumber("0x04bc".hexString),
            BlockTag.BlockTagNumber::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("0x04bc")
    }

    @Test
    fun `should handle null case`() {
        BlockTagSerializer.serialize(
            null,
            BlockTag::class.java,
            context
        ) shouldBeEqualTo JsonNull.INSTANCE
    }
}