package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.BlockCount
import com.alchemy.sdk.core.model.BlockCount.Companion.blockCount
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class BlockCountDeserializerTest {

    @Test(expected = Exception::class)
    fun `should throw exception if not a string value`() {
        json.decodeFromString<BlockCount>("2")
    }

    @Test
    fun `should convert convert hex string to block count`() {
        json.decodeFromString<BlockCount>("\"0x2\"") shouldBeEqualTo 2.blockCount
    }

    @Test
    fun `should handle null case`() {
        json.decodeFromString<BlockCount?>("null") shouldBeEqualTo null
    }
}