package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.Ether.Companion.wei
import kotlinx.serialization.decodeFromString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class EtherDeserializerTest {

    @Test
    fun `should throw exception if value is not a string`() {
        assertFailsWith<Exception> {
            json.decodeFromString<Ether>("2")
        }
    }

    @Test
    fun `should throw exception if value is not a valid hex value`() {
        assertFailsWith<IllegalArgumentException> {
            json.decodeFromString<Ether>("\"XD\"")
        }
    }

    @Test
    fun `should parse hex value as ether`() {
        val data = json.decodeFromString<Ether>("\"0x02\"")
        data shouldBeEqualTo "0x02".wei
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<Ether?>("null")
        data shouldBeEqualTo null
    }

}