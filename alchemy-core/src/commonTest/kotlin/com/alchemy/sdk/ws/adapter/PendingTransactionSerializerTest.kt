package com.alchemy.sdk.ws.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.Ether.Companion.ether
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.ws.model.PendingTransaction
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class PendingTransactionSerializerTest {

    @Test
    fun `should serialize hash only as String`() {
        json.encodeToString<PendingTransaction>(PendingTransaction.HashOnly("0x02".hexString)) shouldBeEqualTo "\"0x02\""
    }

    @Test
    fun `should serialize FullPendingTransaction as JsonObject`() {
        val pendingTransaction = PendingTransaction.FullPendingTransaction(
            "0x01".hexString,
            "0x02".hexString,
            Address.from("0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d"),
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
        json.encodeToString(
            pendingTransaction
        ) shouldBeEqualTo "{\"blockHash\":\"0x01\",\"blockNumber\":\"0x02\",\"from\":\"0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d\",\"gas\":\"0x04\",\"gasPrice\":\"0x4563918244f40000\",\"hash\":\"0x06\",\"input\":\"0x07\",\"nonce\":\"0x08\",\"to\":\"0x09\",\"transactionIndex\":\"0x01\",\"value\":\"0x02\",\"type\":\"0x03\",\"chainId\":\"0x04\",\"v\":\"0x04\",\"r\":\"0x05\",\"s\":\"0x06\"}"
    }

}