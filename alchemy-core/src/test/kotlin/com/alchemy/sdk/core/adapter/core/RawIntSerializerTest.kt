package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.RawInt
import com.alchemy.sdk.core.model.core.RawInt.Companion.raw
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class RawIntSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert raw int to int primitive`() = runTest {
        RawIntSerializer.serialize(
            1.raw,
            RawInt::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive(1)
    }

    @Test
    fun `should handle null case`() {
        RawIntSerializer.serialize(
            null,
            RawInt::class.java,
            context
        ) shouldBeEqualTo JsonNull.INSTANCE
    }
}