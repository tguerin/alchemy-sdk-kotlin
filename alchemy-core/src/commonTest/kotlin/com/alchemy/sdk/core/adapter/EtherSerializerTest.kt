package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.Ether.Companion.ether
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class EtherSerializerTest {

    @Test
    fun `should convert ether to string hex representation of its wei value`() {
        json.encodeToString(
            1.ether
        ) shouldBeEqualTo "\"${1.ether.wei.hexString.data}\""
    }

    @Test
    fun `should handle null case`() {
        json.encodeToString<Ether?>(
            null
        ) shouldBeEqualTo "null"
    }

}