package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.util.Ether.Companion.wei
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.encodeToString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class BlockTransactionSerializerTest {
    @Test
    fun `should serialize SimpleBlockTransaction as String`() {
        json.encodeToString(BlockTransaction.SimpleBlockTransaction("0x02".hexString)) shouldBeEqualTo "\"0x02\""
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
        json.encodeToString(blockTransaction) shouldBeEqualTo "{\"blockHash\":\"0x01\",\"blockNumber\":\"0x02\",\"hash\":\"0x03\",\"accessList\":[\"0x02\"],\"chainId\":\"0x04\",\"from\":\"0x05\",\"gas\":\"0x06\",\"gasPrice\":\"0x07\",\"input\":\"0x08\",\"maxFeePerGas\":\"0x09\",\"maxPriorityFeePerGas\":\"0x01\",\"nonce\":\"0x02\",\"r\":\"0x03\",\"s\":\"0x04\",\"to\":\"0x1188aa75c38e1790be3768508743fbe7b50b2153\",\"transactionIndex\":\"0x05\",\"type\":\"0x06\",\"v\":\"0x07\",\"value\":\"0x08\"}"
    }

    @Test
    fun `should handle null case`() {
        val data = json.encodeToString<BlockTransaction?>(null)
        data shouldBeEqualTo "null"
    }
}