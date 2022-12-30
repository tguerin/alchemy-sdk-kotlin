package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.model.Percentile.Companion.percentile
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith


class PercentileTest {

    @Test
    fun `percentile can't be negative`() {
        assertFailsWith<IllegalStateException> {
            (-1).percentile
        }
    }

    @Test
    fun `percentile can't be more than 100`() {
        assertFailsWith<IllegalStateException> {
            101.percentile
        }
    }

    @Test
    fun `percentile identity when in bounds`() {
        2.percentile.value shouldBeEqualTo 2f
    }
}
