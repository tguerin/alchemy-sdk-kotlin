package com.alchemy.sdk.core.proxy.converters

import com.alchemy.sdk.core.model.Address
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class AddressParamConverterTest {

    @Test
    fun `should convert address to string`() = runTest {
        AddressParamConverter.convert(
            Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153")
        ) shouldBeEqualTo "0x1188aa75c38e1790be3768508743fbe7b50b2153"
    }
}