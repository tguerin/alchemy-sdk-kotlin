package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import kotlinx.serialization.encodeToString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class AddressSerializerTest {

    @Test
    fun `should convert address to string`() {
        json.encodeToString(
            Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153"),
        ) shouldBeEqualTo "\"0x1188aa75c38e1790be3768508743fbe7b50b2153\""
    }

    @Test
    fun `should handle null case`() {
        json.encodeToString<Address?>(
            null,
        ) shouldBeEqualTo "null"
    }
}