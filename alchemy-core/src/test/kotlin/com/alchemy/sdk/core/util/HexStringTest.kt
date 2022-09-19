package com.alchemy.sdk.core.util

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.math.BigInteger

class HexStringTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if not a valid hex value`() {
        HexString.from("XD")
    }

    @Test
    fun `should parse valid hex value and add missing prefix`() {
        HexString.from("04BC").toString() shouldBeEqualTo "0x04bc"
    }

    @Test
    fun `should parse valid hex value and lowercase it`() {
        HexString.from("0x04BC").toString() shouldBeEqualTo "0x04bc"
    }

    @Test
    fun `should parse valid hex value and give right decimal value`() {
        HexString.from("0x04BC").decimalValue() shouldBeEqualTo BigInteger.valueOf(1212)
    }

    @Test
    fun `should parse valid hex value and give byte array`() {
        val byteArray = HexString.from("0x04BC").toByteArray()
        byteArray.size shouldBeEqualTo 2
        byteArray[0].compareTo("04".toInt(16).toByte()) shouldBeEqualTo 0
        byteArray[1].compareTo("bc".toInt(16).toByte()) shouldBeEqualTo 0
    }

    @Test
    fun `should concat hex values`() {
        HexString.from("0x04") + HexString.from("0xBC") shouldBeEqualTo HexString.from("0x04bc")
    }

    @Test
    fun `should be able to cast as an integer`() {
        HexString.from("0x02").intValue() shouldBeEqualTo 2
    }

}