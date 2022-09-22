package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class HexStringSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert hex string to string hex representation`() = runTest {
        HexStringSerializer.serialize(
            "0x02".hexString,
            HexString::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("0x02")
    }
}