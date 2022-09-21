package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.core.util.Ether.Companion.wei
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonPrimitive
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class BlockTransactionDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test
    fun `should deserialize String as SimpleBlockTransaction`() {
        BlockTransactionDeserializer.deserialize(
            JsonPrimitive("0x02"),
            BlockTransaction::class.java,
            context
        ) shouldBeEqualTo BlockTransaction.SimpleBlockTransaction("0x02".hexString)
    }

    @Test
    fun `should deserialize Object as FullBlockTransaction`() {
        val blockTransaction = BlockTransaction.FullBlockTransaction(
            "0x01".hexString,
            "0x02".hexString,
            "0x03".hexString,
            listOf("0x02".hexString),
            "0x04".hexString,
            "0x05".hexString,
            "0x06".hexString,
            "0x07".wei,
            "0x08".hexString,
            "0x09".wei,
            "0x01".wei,
            "0x02".hexString,
            "0x03".hexString,
            "0x04".hexString,
            Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153"),
            "0x05".hexString,
            "0x06".hexString,
            "0x07".hexString,
            "0x08".hexString,
        )
        val jsonTree = Gson().toJsonTree(blockTransaction)
        every { context.deserialize<BlockTransaction.FullBlockTransaction>(jsonTree, BlockTransaction.FullBlockTransaction::class.java) } returns blockTransaction
        BlockTransactionDeserializer.deserialize(
            jsonTree,
            BlockTransaction::class.java,
            context
        ) shouldBeEqualTo blockTransaction
    }
}