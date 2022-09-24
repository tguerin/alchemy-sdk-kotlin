package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.adapter.core.EtherSerializer
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.Ether.Companion.ether
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class EtherSerializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert ether to string hex representation of its wei value`() = runTest {
        EtherSerializer.serialize(
            1.ether,
            Ether::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive(1.ether.wei.hexString.toString())
    }

    @Test
    fun `should handle null case`() {
        EtherSerializer.serialize(
            null,
            Ether::class.java,
            context
        ) shouldBeEqualTo JsonNull.INSTANCE
    }

}