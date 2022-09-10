package com.alchemy.sdk.core.util

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class WeiTest {

    @Test
    fun `should convert wei with right proportions`() {
        val wei = Wei(HexString.from("0x04"))
        wei.toWei() shouldBeEqualTo BigInteger("4")
        wei.toKiloWei() shouldBeEqualTo BigDecimal("0.004")
        wei.toMegaWei() shouldBeEqualTo BigDecimal("0.000004")
        wei.toGigaWei() shouldBeEqualTo BigDecimal("0.000000004")
        wei.toMicroEther() shouldBeEqualTo BigDecimal("0.000000000004")
        wei.toMilliEther() shouldBeEqualTo BigDecimal("0.000000000000004")
        wei.toEther() shouldBeEqualTo BigDecimal("0.000000000000000004")
    }

}