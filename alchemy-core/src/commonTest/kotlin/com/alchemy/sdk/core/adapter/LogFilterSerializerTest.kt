package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.LogFilter
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class LogFilterSerializerTest {

    @Test
    fun `should convert block hash filter to json object without addresses`() = runTest {
        json.encodeToString(
            LogFilter.BlockHashFilter(
                "0x02".hexString,
                emptyList()
            )
        ) shouldBeEqualTo "{\"blockHash\":\"0x02\",\"addresses\":[],\"topics\":null}"
    }

    @Test
    fun `should convert block hash filter to json object with addresses`() = runTest {
        json.encodeToString(
            LogFilter.BlockHashFilter(
                "0x02".hexString,
                listOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
            )
        ) shouldBeEqualTo "{\"blockHash\":\"0x02\",\"addresses\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"],\"topics\":null}"
    }

    @Test
    fun `should convert block range filter to json object without addresses`() = runTest {
        json.encodeToString(
            LogFilter.BlockRangeFilter()
        ) shouldBeEqualTo "{\"from\":\"latest\",\"to\":\"latest\",\"addresses\":[],\"topics\":null}"
    }

    @Test
    fun `should convert block range filter to json object with addresses`() = runTest {
        json.encodeToString(
            LogFilter.BlockRangeFilter(
                from = BlockTag.BlockTagNumber("0x02".hexString),
                to = BlockTag.BlockTagNumber("0x02".hexString),
                addresses = listOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
            )
        ) shouldBeEqualTo "{\"from\":\"0x02\",\"to\":\"0x02\",\"addresses\":[\"0x1188aa75c38e1790be3768508743fbe7b50b2153\"],\"topics\":null}"
    }
}