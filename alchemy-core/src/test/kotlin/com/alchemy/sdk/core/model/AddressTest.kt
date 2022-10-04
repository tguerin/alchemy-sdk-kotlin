package com.alchemy.sdk.core.model

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

class AddressTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if not a valid address`() {
        Address.from("1")
    }

    @Test
    fun `should parse valid eoa address as an EthereumAddress`() {
        val address = Address.from("0xd8da6bf26964af9d7eed9e03e53415d37aa96045")
        address shouldBeInstanceOf Address.EthereumAddress::class
        address.value.data shouldBeEqualTo "0xd8da6bf26964af9d7eed9e03e53415d37aa96045"
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if invalid checksum`() {
        Address.from("0xa54D3c09E34aC96807c1CC397404bF2B98DC4eFb")
    }

    @Test
    fun `should parse ens as an EnsAddress with right name hash`() {
        val address = Address.from("vitalik.eth")
        address shouldBeInstanceOf Address.EnsAddress::class
        address.value.data shouldBeEqualTo "0xee6c4522aab0003e8d14cd40a6af439055fd2577951148c14b6cea9a53475835"
    }
}