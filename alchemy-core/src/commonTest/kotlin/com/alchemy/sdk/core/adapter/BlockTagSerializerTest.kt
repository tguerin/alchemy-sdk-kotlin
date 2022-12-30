package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.BlockTag.Earliest.blockTag
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class BlockTagSerializerTest {

    @Test
    fun `should convert block tag to string`() {
        json.encodeToString<BlockTag>(
            BlockTag.Latest
        ) shouldBeEqualTo "\"latest\""
        json.encodeToString<BlockTag>(
            BlockTag.Safe
        ) shouldBeEqualTo "\"safe\""
        json.encodeToString<BlockTag>(
            BlockTag.Pending
        ) shouldBeEqualTo "\"pending\""
        json.encodeToString<BlockTag>(
            BlockTag.Finalized
        ) shouldBeEqualTo "\"finalized\""
        json.encodeToString<BlockTag>(
            BlockTag.Earliest
        ) shouldBeEqualTo "\"earliest\""
        json.encodeToString(
            "0x04bc".hexString.blockTag
        ) shouldBeEqualTo "\"0x04bc\""
    }

    @Test
    fun `should handle null case`() {
        json.encodeToString<BlockTag?>(
            null
        ) shouldBeEqualTo "null"
    }
}