package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class AddressSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert address to string`() = runTest {
        AddressSerializer.serialize(
            Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153"),
            Address.EthereumAddress::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive("0x1188aa75c38e1790be3768508743fbe7b50b2153")
    }
}