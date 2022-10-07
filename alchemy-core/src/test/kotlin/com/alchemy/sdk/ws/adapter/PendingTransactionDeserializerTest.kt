package com.alchemy.sdk.ws.adapter

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether.Companion.ether
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.ws.model.PendingTransaction
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class PendingTransactionDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test
    fun `should deserialize String as hash only`() {
        PendingTransactionDeserializer.deserialize(
            JsonPrimitive("0x02"),
            PendingTransaction::class.java,
            context
        ) shouldBeEqualTo PendingTransaction.HashOnly("0x02".hexString)
    }

    @Test
    fun `should deserialize Object as FullPendingTransaction`() {
        val pendingTransaction = PendingTransaction.FullPendingTransaction(
            "0x01".hexString,
            "0x02".hexString,
            Address.from("vitalik.eth"),
            "0x04".hexString,
            "0x05".ether,
            "0x06".hexString,
            "0x07".hexString,
            "0x08".hexString,
            "0x09".hexString,
            "0x01".hexString,
            "0x02".hexString,
            "0x03".hexString,
            "0x04".hexString,
            "0x04".hexString,
            "0x05".hexString,
            "0x06".hexString,
        )
        val jsonTree = Gson().toJsonTree(pendingTransaction)
        every {
            context.deserialize<PendingTransaction.FullPendingTransaction>(
                jsonTree,
                PendingTransaction.FullPendingTransaction::class.java
            )
        } returns pendingTransaction
        PendingTransactionDeserializer.deserialize(
            jsonTree,
            PendingTransaction::class.java,
            context
        ) shouldBeEqualTo pendingTransaction
    }

    @Test
    fun `should handle null case`() {
        val data = PendingTransactionDeserializer.deserialize(
            JsonNull.INSTANCE,
            PendingTransaction::class.java,
            context
        )
        data shouldBeEqualTo null
    }
}