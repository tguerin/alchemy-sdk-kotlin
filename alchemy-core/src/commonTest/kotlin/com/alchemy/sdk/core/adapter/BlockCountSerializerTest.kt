package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.BlockCount
import com.alchemy.sdk.core.model.BlockCount.Companion.blockCount
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class BlockCountSerializerTest {

    @Test
    fun `should convert block count to hex string without leading zero`() {
        json.encodeToString(
            2.blockCount
        ) shouldBeEqualTo "\"0x2\""
    }

    @Test
    fun `should handle null case`() {
        json.encodeToString<BlockCount?>(
            null
        ) shouldBeEqualTo "null"
    }
}