package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.util.Ether.Companion.ether
import com.alchemy.sdk.core.util.Ether.Companion.wei
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class EtherTest {

    @Test
    fun `should convert ether with right proportions`() {
        val ether = "0x04".wei
        ether.wei shouldBeEqualTo BigInteger("4")
        ether.kiloWei shouldBeEqualTo BigDecimal("0.004")
        ether.megaWei shouldBeEqualTo BigDecimal("0.000004")
        ether.gigaWei shouldBeEqualTo BigDecimal("0.000000004")
        ether.microEther shouldBeEqualTo BigDecimal("0.000000000004")
        ether.milliEther shouldBeEqualTo BigDecimal("0.000000000000004")
        ether.ether shouldBeEqualTo BigDecimal("0.000000000000000004")
    }

    @Test
    fun `should convert int or long to wei`() {
        1.wei.wei shouldBeEqualTo BigInteger.valueOf(1)
        1L.wei.wei shouldBeEqualTo BigInteger.valueOf(1)
    }

    @Test
    fun `should convert string to ether`() {
        "1".ether.wei shouldBeEqualTo BigInteger("1000000000000000000")
    }

    @Test
    fun `should convert int or long to ether`() {
        1.ether.wei shouldBeEqualTo BigInteger("1000000000000000000")
        1L.ether.wei shouldBeEqualTo BigInteger("1000000000000000000")
    }

    @Test
    fun `should convert double to ether`() {
        (1.1).ether.wei shouldBeEqualTo BigInteger("1100000000000000000")
    }

}