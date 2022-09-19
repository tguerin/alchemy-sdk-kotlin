package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.Index.Companion.index
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class IndexParamConverterTest {

    @Test
    fun `should convert storage position to string without leading zero`() = runTest {
        IndexParameterConverter.convert(2.index) shouldBeEqualTo "0x2"
    }
}