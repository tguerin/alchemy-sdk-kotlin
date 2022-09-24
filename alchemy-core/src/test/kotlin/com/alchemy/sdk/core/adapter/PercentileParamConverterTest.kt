package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.adapter.core.PercentileSerializer
import com.alchemy.sdk.core.model.core.Percentile
import com.alchemy.sdk.core.model.core.Percentile.Companion.percentile
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test

class PercentileParamConverterTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: JsonSerializationContext

    @Test
    fun `should convert percentile to float value`() = runTest {
        PercentileSerializer.serialize(
            20.percentile,
            Percentile::class.java,
            context
        ) shouldBeEqualTo JsonPrimitive(20f)
    }

}