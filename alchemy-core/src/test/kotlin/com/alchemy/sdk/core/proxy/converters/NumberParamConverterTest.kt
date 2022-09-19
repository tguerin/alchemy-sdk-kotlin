package com.alchemy.sdk.core.proxy.converters

import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class NumberParamConverterTest {

    @Test
    fun `should convert int to string without leading zero`() = runTest {
        NumberParameterConverter.convert(2) shouldBeEqualTo "0x2"
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if number is not a int`() = runTest {
        NumberParameterConverter.convert(2.0) shouldBeEqualTo "0x2"
    }
}