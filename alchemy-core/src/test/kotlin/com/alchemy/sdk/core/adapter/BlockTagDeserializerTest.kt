package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class BlockTagDeserializerTest {

    @Test(expected = Exception::class)
    fun `should throw exception if not a string value`() {
        json.decodeFromString<BlockTag>("2")
    }

    @Test
    fun `should convert convert valid value to block tag`() {
        json.decodeFromString<BlockTag>("\"latest\"") shouldBeEqualTo BlockTag.Latest
        json.decodeFromString<BlockTag>("\"safe\"") shouldBeEqualTo BlockTag.Safe
        json.decodeFromString<BlockTag>("\"pending\"") shouldBeEqualTo BlockTag.Pending
        json.decodeFromString<BlockTag>("\"earliest\"") shouldBeEqualTo BlockTag.Earliest
        json.decodeFromString<BlockTag>("\"finalized\"") shouldBeEqualTo BlockTag.Finalized
        json.decodeFromString<BlockTag>("\"0x04\"") shouldBeEqualTo BlockTag.BlockTagNumber(4.hexString)
    }

    @Test
    fun `should handle null case`() {
        json.decodeFromString<BlockTag?>("null") shouldBeEqualTo null
    }
}