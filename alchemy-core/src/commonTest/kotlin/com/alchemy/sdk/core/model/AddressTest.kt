package com.alchemy.sdk.core.model

import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AddressTest {

    @Test
    fun `should throw exception if not a valid address`() {
        assertFailsWith<IllegalArgumentException> {
            Address.from("1")
        }
    }

    @Test
    fun `should parse valid eoa address as an EthereumAddress`() {
        val address = Address.from("0xd8da6bf26964af9d7eed9e03e53415d37aa96045")
        address::class shouldBeEqualTo Address.EthereumAddress::class
        address.value.data shouldBeEqualTo "0xd8da6bf26964af9d7eed9e03e53415d37aa96045"
    }

    @Test
    fun `should throw exception if invalid checksum`() {
        assertFailsWith<IllegalArgumentException> {
            Address.from("0xa54D3c09E34aC96807c1CC397404bF2B98DC4eFb")
        }
    }

    @Test
    fun `should parse ens as an EnsAddress with right name hash`() {
        val address = Address.from("vitalik.eth")
        address::class shouldBeEqualTo Address.EnsAddress::class
        address.value.data shouldBeEqualTo "0xee6c4522aab0003e8d14cd40a6af439055fd2577951148c14b6cea9a53475835"
    }
}