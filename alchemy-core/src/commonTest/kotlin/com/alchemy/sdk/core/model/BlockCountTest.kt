package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.model.BlockCount.Companion.blockCount
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith


class BlockCountTest {

    @Test
    fun `block count can't be negative`() {
        assertFailsWith<IllegalStateException> {
            (-1).blockCount
        }
    }

    @Test
    fun `block count must be greater than 0`() {
        assertFailsWith<IllegalStateException> {
            0.blockCount
        }
    }

    @Test
    fun `block count identity when greater than 0`() {
        2.blockCount.value shouldBeEqualTo 2
    }
}