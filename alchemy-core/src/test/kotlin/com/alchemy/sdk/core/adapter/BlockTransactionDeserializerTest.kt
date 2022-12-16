package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.util.Ether.Companion.wei
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class BlockTransactionDeserializerTest {
    @Test
    fun `should deserialize String as SimpleBlockTransaction`() {
        json.decodeFromString<BlockTransaction>("\"0x02\"") shouldBeEqualTo BlockTransaction.SimpleBlockTransaction("0x02".hexString)
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
        val jsonTree = json.encodeToString(BlockTransaction.FullBlockTransaction.serializer(), blockTransaction)
        json.decodeFromString<BlockTransaction>(jsonTree) shouldBeEqualTo blockTransaction
    }

    @Test
    fun `should handle null case`() {
        val data = json.decodeFromString<BlockTransaction?>("null")
        data shouldBeEqualTo null
    }
}