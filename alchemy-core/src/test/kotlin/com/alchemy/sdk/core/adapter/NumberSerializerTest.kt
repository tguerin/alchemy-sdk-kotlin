package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.adapter.core.NumberSerializer
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class NumberSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert int to hex string without leading zero`() = runTest {
        NumberSerializer.serialize(
            2,
            Integer::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("0x2")
    }

    @Test
    fun `should convert long to hex string without leading zero`() = runTest {
        NumberSerializer.serialize(
            2L,
            java.lang.Long::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("0x2")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if number is not a int`() = runTest {
        NumberSerializer.serialize(2.0, java.lang.Float::class.java, context)
    }

    @Test
    fun `should handle null case`() {
        NumberSerializer.serialize(
            null,
            Number::class.java,
            context
        ) shouldBeEqualTo JsonNull.INSTANCE
    }
}