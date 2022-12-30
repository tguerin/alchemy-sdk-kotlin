package com.alchemy.sdk.util

import com.alchemy.sdk.shouldBeEqualTo
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlin.test.Test

class BytesTest {

    @Test
    fun `should mimic ethers arraify`() {
        1.arrayify() shouldBeEqualTo intArrayOf(1)
        10000000.arrayify() shouldBeEqualTo intArrayOf(152, 150, 128)
        0.arrayify() shouldBeEqualTo intArrayOf(0)
    }

    @Test
    fun `should mimic ethers numpad`() {
        1.numPad() shouldBeEqualTo intArrayOf(
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1
        )
        10000000.numPad() shouldBeEqualTo intArrayOf(
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 152, 150, 128
        )
    }

    @Test
    fun `should mimic ethers bytes pad`() {
        val bytesPad = "0x01310f6f6666636861696e6578616d706c650365746800".hexString.bytesPad()
        bytesPad shouldBeEqualTo intArrayOf(
            1, 49, 15, 111, 102, 102,
            99, 104, 97, 105, 110, 101,
            120, 97, 109, 112, 108, 101,
            3, 101, 116, 104, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0
        )
    }

    @Test
    fun `should mimic ethers encode bytes`() {
        encodeBytes(
            "0x01310f6f6666636861696e6578616d706c650365746800".hexString,
            "0x3b3b57de1c9fb8c1fe76f464ccec6d2c003169598fdfcbcb6bbddf6af9c097a39fa0048c".hexString
        ) shouldBeEqualTo "0x00000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000001701310f6f6666636861696e6578616d706c65036574680000000000000000000000000000000000000000000000000000000000000000000000000000000000243b3b57de1c9fb8c1fe76f464ccec6d2c003169598fdfcbcb6bbddf6af9c097a39fa0048c00000000000000000000000000000000000000000000000000000000".hexString
    }

}