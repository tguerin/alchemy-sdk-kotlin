package com.alchemy.sdk.ws.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.shouldBeEqualTo
import com.alchemy.sdk.util.Ether.Companion.ether
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.ws.model.PendingTransaction
import kotlinx.serialization.decodeFromString
import kotlin.test.Test

class PendingTransactionDeserializerTest {

    @Test
    fun `should deserialize String as hash only`() {
        json.decodeFromString<PendingTransaction>("\"0x02\"") shouldBeEqualTo PendingTransaction.HashOnly("0x02".hexString)
    }

    @Test
    fun `should deserialize Object as FullPendingTransaction`() {
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

        json.decodeFromString<PendingTransaction.FullPendingTransaction>(
            "{\"blockHash\":\"0x01\",\"blockNumber\":\"0x02\",\"from\":\"0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d\",\"gas\":\"0x04\",\"gasPrice\":\"0x4563918244f40000\",\"hash\":\"0x06\",\"input\":\"0x07\",\"nonce\":\"0x08\",\"to\":\"0x09\",\"transactionIndex\":\"0x01\",\"value\":\"0x02\",\"type\":\"0x03\",\"chainId\":\"0x04\",\"v\":\"0x04\",\"r\":\"0x05\",\"s\":\"0x06\"}"
        ) shouldBeEqualTo pendingTransaction
    }

}