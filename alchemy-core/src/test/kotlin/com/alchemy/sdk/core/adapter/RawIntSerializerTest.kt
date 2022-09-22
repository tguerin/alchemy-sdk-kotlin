package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.RawInt
import com.alchemy.sdk.core.model.RawInt.Companion.raw
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.Ether.Companion.ether
import com.alchemy.sdk.core.util.HexString.Companion.hexString
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
}