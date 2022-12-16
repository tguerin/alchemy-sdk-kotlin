package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class HexStringDeserializerTest {


    @Test(expected = Exception::class)
    fun `should throw exception if value is not a string`() {
        json.decodeFromString<HexString>("2")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if value is not a valid hex value`() {
        json.decodeFromString<HexString>("\"XD\"")
    }

    @Test
    fun `should parse hex value as hex string`() {
        val data = json.decodeFromString<HexString>("\"0x02\"")
        data shouldBeEqualTo "0x02".hexString
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<HexString?>("null")
        data shouldBeEqualTo null
    }
}