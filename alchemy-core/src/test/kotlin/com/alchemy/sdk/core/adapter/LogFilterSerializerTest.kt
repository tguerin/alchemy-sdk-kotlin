package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.LogFilter
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class LogFilterSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert block hash filter to json object without addresses`() = runTest {
        LogFilterSerializer.serialize(
            LogFilter.BlockHashFilter(
                "0x02".hexString,
                emptyList()
            ),
            LogFilter.BlockHashFilter::class.java,
            context
        ) shouldBeEqualTo JsonObject().apply {
            add("blockHash", JsonPrimitive("0x02"))
        }
    }

    @Test
    fun `should convert block hash filter to json object with addresses`() = runTest {
        LogFilterSerializer.serialize(
            LogFilter.BlockHashFilter(
                "0x02".hexString,
                listOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
            ),
            LogFilter.BlockHashFilter::class.java,
            context
        ) shouldBeEqualTo JsonObject().apply {
            add("blockHash", JsonPrimitive("0x02"))
            add(
                "addresses",
                JsonArray().apply { add("0x1188aa75c38e1790be3768508743fbe7b50b2153") })
        }
    }

    @Test
    fun `should convert block range filter to json object without addresses`() = runTest {
        LogFilterSerializer.serialize(
            LogFilter.BlockRangeFilter(),
            LogFilter.BlockHashFilter::class.java,
            context
        ) shouldBeEqualTo JsonObject().apply {
            add("from", JsonPrimitive("latest"))
            add("to", JsonPrimitive("latest"))
        }
    }

    @Test
    fun `should convert block range filter to json object with addresses`() = runTest {
        LogFilterSerializer.serialize(
            LogFilter.BlockRangeFilter(
                from = BlockTag.BlockTagNumber("0x02".hexString),
                to = BlockTag.BlockTagNumber("0x02".hexString),
                addresses = listOf(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
            ),
            LogFilter.BlockHashFilter::class.java,
            context
        ) shouldBeEqualTo JsonObject().apply {
            add("from", JsonPrimitive("0x02"))
            add("to", JsonPrimitive("0x02"))
            add(
                "addresses",
                JsonArray().apply { add("0x1188aa75c38e1790be3768508743fbe7b50b2153") })
        }
    }
}