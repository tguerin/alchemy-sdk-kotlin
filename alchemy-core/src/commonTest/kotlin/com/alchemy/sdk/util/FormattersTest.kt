package com.alchemy.sdk.util

import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class FormattersTest {

    @Test
    fun `should return null if the hex has not a 32 length`() {
        Formatters.formatCallAddress("0x0".hexString) shouldBeEqualTo null
    }

    @Test
    fun `should return null if the address is ADRESS_ZERO`() {
        Formatters.formatCallAddress("0x0000000000000000000000000000000000000000000000000000000000000000".hexString) shouldBeEqualTo null
    }

    @Test
    fun `should return format valid hex from call`() {
        Formatters.formatCallAddress("0x000000000000000000000000d8da6bf26964af9d7eed9e03e53415d37aa96045".hexString) shouldBeEqualTo "0xd8da6bf26964af9d7eed9e03e53415d37aa96045".hexString.data
    }

}