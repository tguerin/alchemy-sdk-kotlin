package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Percentile
import com.alchemy.sdk.core.model.Percentile.Companion.percentile
import kotlinx.serialization.decodeFromString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class PercentileDeserializerTest {

    @Test(expected = Exception::class)
    fun `should throw exception if not a float value`() {
        json.decodeFromString<Percentile>("\"x\"")
    }

    @Test
    fun `should convert convert float to percentile`() {
        json.decodeFromString<Percentile>("20.0") shouldBeEqualTo 20f.percentile
    }

    @Test
    fun `should handle null case`() {
        json.decodeFromString<Percentile?>("null") shouldBeEqualTo null
    }
}