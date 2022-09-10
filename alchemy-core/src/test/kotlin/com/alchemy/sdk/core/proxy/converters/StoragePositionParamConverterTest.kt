package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.StoragePosition
import com.alchemy.sdk.core.util.HexString
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class StoragePositionParamConverterTest {

    @Test
    fun `should convert storage position to string`() = runTest {
        StoragePositionParameterConverter.convert(
            StoragePosition.from(2)
        ) shouldBeEqualTo HexString.from(2).toString()
    }
}