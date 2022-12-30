package com.alchemy.sdk.util

import com.alchemy.sdk.util.Ether.Companion.ether
import com.alchemy.sdk.util.Ether.Companion.wei
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class EtherTest {

    @Test
    fun `should convert ether with right proportions`() {
        val ether = "0x04".wei
        ether.wei shouldBeEqualTo BigInteger.parseString("4")
        ether.kiloWei shouldBeEqualTo BigDecimal.parseString("0.004")
        ether.megaWei shouldBeEqualTo BigDecimal.parseString("0.000004")
        ether.gigaWei shouldBeEqualTo BigDecimal.parseString("0.000000004")
        ether.microEther shouldBeEqualTo BigDecimal.parseString("0.000000000004")
        ether.milliEther shouldBeEqualTo BigDecimal.parseString("0.000000000000004")
        ether.ether shouldBeEqualTo BigDecimal.parseString("0.000000000000000004")
    }

    @Test
    fun `should convert int or long to wei`() {
        1.wei.wei shouldBeEqualTo BigInteger.fromInt(1)
        1L.wei.wei shouldBeEqualTo BigInteger.fromInt(1)
    }

    @Test
    fun `should convert string to ether`() {
        "1".ether.wei shouldBeEqualTo BigInteger.parseString("1000000000000000000")
    }

    @Test
    fun `should convert int or long to ether`() {
        1.ether.wei shouldBeEqualTo BigInteger.parseString("1000000000000000000")
        1L.ether.wei shouldBeEqualTo BigInteger.parseString("1000000000000000000")
    }

    @Test
    fun `should convert double to ether`() {
        (1.1).ether.wei shouldBeEqualTo BigInteger.parseString("1100000000000000000")
    }

}