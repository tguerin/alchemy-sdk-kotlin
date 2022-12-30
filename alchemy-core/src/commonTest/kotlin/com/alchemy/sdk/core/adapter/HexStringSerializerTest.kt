package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class HexStringSerializerTest {

    @Test
    fun `should convert hex string to string hex representation`() = runTest {
        json.encodeToString("0x02".hexString) shouldBeEqualTo "\"0x02\""
    }

    @Test
    fun `should handle null case`() {
        json.encodeToString<HexString?>(null) shouldBeEqualTo "null"
    }
}