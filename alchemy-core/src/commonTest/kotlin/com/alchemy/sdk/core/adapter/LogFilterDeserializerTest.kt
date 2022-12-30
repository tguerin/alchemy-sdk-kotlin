package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.LogFilter
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class LogFilterDeserializerTest {

    @Test
    fun `should convert json object to block hash filter without addresses`() = runTest {
        json.decodeFromString<LogFilter>(
            "{\"blockHash\":\"0x02\",\"addresses\":[],\"topics\":null}"
        ) shouldBeEqualTo LogFilter.BlockHashFilter(
            "0x02".hexString,
            emptyList()
        )
    }

    @Test
    fun `should convert json object to block hash filter with addresses`() = runTest {
        json.decodeFromString<LogFilter>(
            "{\"blockHash\":\"0x02\",\"addresses\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"],\"topics\":null}"
        ) shouldBeEqualTo LogFilter.BlockHashFilter(
            "0x02".hexString,
            listOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
        )
    }

    @Test
    fun `should convert json object filter to block range without addresses`() = runTest {
        json.decodeFromString<LogFilter>(
            "{\"from\":\"latest\",\"to\":\"latest\",\"addresses\":[],\"topics\":null}"
        ) shouldBeEqualTo LogFilter.BlockRangeFilter()
    }

    @Test
    fun `should convert json object filter to block range with addresses`() = runTest {
        json.decodeFromString<LogFilter>(
            "{\"from\":\"0x02\",\"to\":\"0x02\",\"addresses\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"],\"topics\":null}"
        ) shouldBeEqualTo LogFilter.BlockRangeFilter(
            from = BlockTag.BlockTagNumber("0x02".hexString),
            to = BlockTag.BlockTagNumber("0x02".hexString),
            addresses = listOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
        )
    }
}