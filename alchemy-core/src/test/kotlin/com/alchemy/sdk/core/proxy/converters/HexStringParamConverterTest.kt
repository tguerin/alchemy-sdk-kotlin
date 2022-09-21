package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.util.HexString.Companion.hexString
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class HexStringParamConverterTest {

    @Test
    fun `should convert hex string to string`() = runTest {
        HexStringParameterConverter.convert(
            "0x1188aa75C38E1790bE3768508743FBE7b50b2153".hexString
        ) shouldBeEqualTo "0x1188aa75c38e1790be3768508743fbe7b50b2153"
    }
}