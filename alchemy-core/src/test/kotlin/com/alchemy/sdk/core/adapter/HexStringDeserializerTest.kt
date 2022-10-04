package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class HexStringDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a string`() {
        HexStringDeserializer.deserialize(
            JsonPrimitive(2),
            HexString::class.java,
            context
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if value is not a valid hex value`() {
        HexStringDeserializer.deserialize(
            JsonPrimitive("XD"),
            HexString::class.java,
            context
        )
    }

    @Test
    fun `should parse hex value as hex string`() {
        val data = HexStringDeserializer.deserialize(
            JsonPrimitive("0x02"),
            HexString::class.java,
            context
        )
        data shouldBeEqualTo "0x02".hexString
    }

    @Test
    fun `should handle null case`() {
        val data = HexStringDeserializer.deserialize(
            JsonNull.INSTANCE,
            HexString::class.java,
            context
        )
        data shouldBeEqualTo null
    }
}