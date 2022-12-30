package com.alchemy.sdk.util

import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.util.HexString.Companion.id
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class HexStringTest {

    @Test
    fun `should throw exception if not a valid hex value`() {
        assertFailsWith<IllegalArgumentException> {
            "XD".hexString
        }
    }

    @Test
    fun `should parse valid hex value and add missing prefix`() {
        "04BC".hexString.data shouldBeEqualTo "0x04bc"
    }

    @Test
    fun `should parse valid hex value and lowercase it`() {
        "0x04BC".hexString.data shouldBeEqualTo "0x04bc"
    }

    @Test
    fun `should parse valid hex value and give right decimal value`() {
        "0x04BC".hexString.decimalValue() shouldBeEqualTo BigInteger.fromInt(1212)
    }

    @Test
    fun `should parse byte array as hex string`() {
        val byteArray = "0x4BC".hexString.toByteArray()
        byteArray.hexString shouldBeEqualTo "0x04bc".hexString
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
        2.hexString.data shouldBeEqualTo "0x02"
    }

    @Test
    fun `should be able to parse Long`() {
        2L.hexString.data shouldBeEqualTo "0x02"
    }

    @Test
    fun `should be able to parse big integer`() {
        BigInteger.fromInt(2).hexString.data shouldBeEqualTo "0x02"
    }

    @Test
    fun `should be able to parse integer part of big decimal`() {
        BigDecimal.fromDouble(2.0).hexString.data shouldBeEqualTo "0x02"
    }

    @Test
    fun `should return length of the hexstring`() {
        "0x2".hexString.length() shouldBeEqualTo 1
    }

    @Test
    fun `should check if hexstring has the right length`() {
        "0x9202".hexString.hasLength(2) shouldBeEqualTo true
        "0x9202".hexString.hasLength(1) shouldBeEqualTo false
    }

    @Test
    fun `should slice the hexstring with the right offset`() {
        "0x0002".hexString.slice(1) shouldBeEqualTo "0x02".hexString
    }

    @Test
    fun `should throw exception if start index is invalid for slice`() {
        assertFailsWith<IllegalArgumentException> {
            "0x0002".hexString.slice(-1)
        }
    }

    @Test
    fun `should throw exception if end index is invalid for slice`() {
        assertFailsWith<IllegalArgumentException> {
            "0x0002".hexString.slice(100)
        }
    }

    @Test
    fun `should throw exception if end index is lesser than start index for slice`() {
        assertFailsWith<IllegalArgumentException> {
            "0x0002".hexString.slice(2, 1)
        }
    }

    @Test
    fun `should return null if start index is invalid for parse string`() {
        "0x0002".hexString.parseString(1000) shouldBeEqualTo null
    }

    @Test
    fun `should return null for parsing 0x hexstring`() {
        "0x".hexString.parseString(0) shouldBeEqualTo null
    }

    @Test
    fun `should convert hexstring to int array`() {
        "0x0102".hexString.toIntArray() shouldBeEqualTo intArrayOf(1, 2)
    }

    @Test
    fun `should keccak 256 the string as an id`() {
        "124".id shouldBeEqualTo "0xac09810740600c31fa69f9db79ed6fc3e3281f758a950fe1fb254a3a3ae571b6".hexString
    }

}