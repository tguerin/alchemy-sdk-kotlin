package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.Ether.Companion.wei
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonPrimitive
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class EtherDeserializerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonDeserializationContext

    @Test(expected = IllegalStateException::class)
    fun `should throw exception if value is not a string`() {
        EtherDeserializer.deserialize(
            JsonPrimitive(2),
            Ether::class.java,
            context
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception if value is not a valid hex value`() {
        EtherDeserializer.deserialize(
            JsonPrimitive("XD"),
            Ether::class.java,
            context
        )
    }

    @Test
    fun `should parse hex value as ether`() {
        val data = EtherDeserializer.deserialize(
            JsonPrimitive("0x02"),
            Ether::class.java,
            context
        )
        data shouldBeEqualTo "0x02".wei
    }

}