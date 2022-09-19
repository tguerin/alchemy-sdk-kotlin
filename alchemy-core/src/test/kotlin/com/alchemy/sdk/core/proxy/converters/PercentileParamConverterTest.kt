package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.Percentile.Companion.percentile
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class PercentileParamConverterTest {

    @Test
    fun `should convert int to string without leading zero`() = runTest {
        PercentileParameterConverter.convert(20.percentile) shouldBeEqualTo 20f
    }

}