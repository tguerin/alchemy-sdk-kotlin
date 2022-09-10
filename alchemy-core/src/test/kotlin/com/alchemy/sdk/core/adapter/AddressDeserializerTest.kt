package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonPrimitive
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class AddressDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a string`() {
        AddressDeserializer.deserialize(
            JsonPrimitive(2),
            Address::class.java,
            context
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if value is not a valid address value`() {
        AddressDeserializer.deserialize(
            JsonPrimitive("0x00"),
            Address::class.java,
            context
        )
    }

    @Test
    fun `should parse hex value as an address`() {
        val data = AddressDeserializer.deserialize(
            JsonPrimitive("0x1188aa75C38E1790bE3768508743FBE7b50b2153"),
            Address::class.java,
            context
        )
        data shouldBeEqualTo Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153")
    }
}