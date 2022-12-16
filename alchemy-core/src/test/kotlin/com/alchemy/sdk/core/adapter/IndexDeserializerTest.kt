package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.Index.Companion.index
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class IndexDeserializerTest {

    @Test(expected = Exception::class)
    fun `should throw exception if not a int value`() {
        json.decodeFromString<Index>("\"x\"")
    }

    @Test
    fun `should convert convert int to index`() {
        json.decodeFromString<Index>("\"0x2\"") shouldBeEqualTo 2.index
    }

    @Test
    fun `should handle null case`() {
        json.decodeFromString<Index?>("null") shouldBeEqualTo null
    }
}