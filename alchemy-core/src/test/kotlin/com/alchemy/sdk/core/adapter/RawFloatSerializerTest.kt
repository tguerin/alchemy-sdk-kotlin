package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.RawFloat.Companion.raw
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class RawFloatSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert raw float to float primitive`() = runTest {
        RawFloatSerializer.serialize(
            1f.raw,
            RawFloatSerializer::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive(1f)
    }
}