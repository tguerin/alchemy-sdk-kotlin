package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.model.core.BlockCount.Companion.blockCount
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test


class BlockCountTest {

    @Test(expected = IllegalStateException::class)
    fun `block count can't be negative`() {
        (-1).blockCount
    }

    @Test(expected = IllegalStateException::class)
    fun `block count must be greater than 0`() {
        0.blockCount
    }

    @Test
    fun `block count identity when greater than 0`() {
        2.blockCount.value shouldBeEqualTo 2
    }
}