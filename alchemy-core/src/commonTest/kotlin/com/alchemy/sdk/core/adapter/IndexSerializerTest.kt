package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.Index.Companion.index
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class IndexSerializerTest {

    @Test
    fun `should convert index to hex string without leading zero`() {
        json.encodeToString(
            2.index
        ) shouldBeEqualTo "\"0x2\""
    }

    @Test
    fun `should handle null case`() {
        json.encodeToString<Index?>(
            null
        ) shouldBeEqualTo "null"
    }
}