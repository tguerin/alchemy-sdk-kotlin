package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.model.Percentile.Companion.percentile
import com.alchemy.sdk.core.model.RawFloat.Companion.raw
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test


class PercentileTest {

    @Test(expected = IllegalStateException::class)
    fun `percentile can't be negative`() {
        (-1).percentile
    }

    @Test(expected = IllegalStateException::class)
    fun `percentile can't be more than 100`() {
        101.percentile
    }

    @Test
    fun `percentile identity when in bounds`() {
        2.percentile.value shouldBeEqualTo 2f.raw
    }
}
