package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import kotlinx.serialization.decodeFromString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AddressDeserializerTest {

    @Test
    fun `should throw exception if value is not a string`() {
        assertFailsWith<Exception> {
            json.decodeFromString<Address>("2")
        }

    }

    @Test
    fun `should throw exception if value is not a valid address value`() {
        assertFailsWith<IllegalArgumentException> {
            json.decodeFromString<Address>("\"0x00\"")
        }
    }

    @Test
    fun `should parse hex value as an address`() {
        val data = json.decodeFromString<Address>("\"0x1188aa75C38E1790bE3768508743FBE7b50b2153\"")
        data shouldBeEqualTo Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153")
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<Address?>("null")
        data shouldBeEqualTo null
    }
}