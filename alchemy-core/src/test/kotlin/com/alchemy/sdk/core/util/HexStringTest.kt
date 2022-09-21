package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.util.HexString.Companion.hexString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class HexStringTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if not a valid hex value`() {
        "XD".hexString
    }

    @Test
    fun `should parse valid hex value and add missing prefix`() {
        "04BC".hexString.toString() shouldBeEqualTo "0x04bc"
    }

    @Test
    fun `should parse valid hex value and lowercase it`() {
        "0x04BC".hexString.toString() shouldBeEqualTo "0x04bc"
    }

    @Test
    fun `should parse valid hex value and give right decimal value`() {
        "0x04BC".hexString.decimalValue() shouldBeEqualTo BigInteger.valueOf(1212)
    }

    @Test
    fun `should parse valid hex value and give byte array`() {
        val byteArray = "0x04BC".hexString.toByteArray()
        byteArray.size shouldBeEqualTo 2
        byteArray[0].compareTo("04".toInt(16).toByte()) shouldBeEqualTo 0
        byteArray[1].compareTo("bc".toInt(16).toByte()) shouldBeEqualTo 0
    }

    @Test
    fun `should concat hex values`() {
        "0x04".hexString + "0xBC".hexString shouldBeEqualTo "0x04bc".hexString
    }

    @Test
    fun `should be able to cast as an integer`() {
        "0x02".hexString.intValue() shouldBeEqualTo 2
    }

    @Test
    fun `should be able to parse integer`() {
        2.hexString.toString() shouldBeEqualTo "0x02"
    }

    @Test
    fun `should be able to parse Long`() {
        2L.hexString.toString() shouldBeEqualTo "0x02"
    }

    @Test
    fun `should be able to parse big integer`() {
        BigInteger.valueOf(2).hexString.toString() shouldBeEqualTo "0x02"
    }

    @Test
    fun `should be able to parse integer part of big decimal`() {
        BigDecimal.valueOf(2.0).hexString.toString() shouldBeEqualTo "0x02"
    }

}