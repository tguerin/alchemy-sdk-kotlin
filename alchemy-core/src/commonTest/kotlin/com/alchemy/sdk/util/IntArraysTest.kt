package com.alchemy.sdk.util

import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith

class IntArraysTest {

    @Test
    fun `hex concat can't support integer greater than 255`() {
        assertFailsWith<IllegalArgumentException> {
            intArrayOf(256).hexConcat()
        }
    }

    @Test
    fun `should concat int array as hex`() {
        intArrayOf(255, 255).hexConcat() shouldBeEqualTo "0xffff".hexString
    }

    @Test
    fun `should set array inside greater array`() {
        intArrayOf(0, 0, 0, 0).set(intArrayOf(255, 254)) shouldBeEqualTo intArrayOf(255, 254, 0, 0)
    }

    @Test
    fun `should set array inside greater array with offset`() {
        intArrayOf(0, 0, 0, 0).set(intArrayOf(255, 254), 2) shouldBeEqualTo intArrayOf(
            0, 0, 255, 254
        )
    }

    @Test
    fun `should throw exception if input array size is less than the wrapping array`() {
        assertFailsWith<IllegalArgumentException> {
            intArrayOf(0).set(intArrayOf(255, 254), 2) shouldBeEqualTo intArrayOf(0, 0, 255, 254)
        }
    }

    @Test
    fun `should throw exception if offset is greater or equal than the wrapping array`() {
        assertFailsWith<IllegalArgumentException> {
            intArrayOf(0, 0, 0, 0).set(intArrayOf(255, 254), 4)
        }
    }
}