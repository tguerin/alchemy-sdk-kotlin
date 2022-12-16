package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.ResourceUtils.Companion.json
import com.alchemy.sdk.core.model.Percentile.Companion.percentile
import kotlinx.serialization.encodeToString
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class PercentileSerializerTest {

    @Test
    fun `should convert percentile to float value`() {
        json.encodeToString(20.percentile) shouldBeEqualTo "20.0"
    }

}