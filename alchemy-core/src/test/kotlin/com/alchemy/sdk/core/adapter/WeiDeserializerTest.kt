package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.Wei
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonPrimitive
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class WeiDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a string`() {
        WeiDeserializer.deserialize(
            JsonPrimitive(2),
            Wei::class.java,
            context
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if value is not a valid hex value`() {
        WeiDeserializer.deserialize(
            JsonPrimitive("XD"),
            Wei::class.java,
            context
        )
    }

    @Test
    fun `should parse hex value as wei`() {
        val data = WeiDeserializer.deserialize(
            JsonPrimitive("0x02"),
            Wei::class.java,
            context
        )
        data shouldBeEqualTo Wei(HexString.from("0x02"))
    }

}