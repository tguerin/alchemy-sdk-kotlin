package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.model.Index.Companion.index
import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test
import kotlin.test.assertFailsWith


class IndexTest {

    @Test
    fun `storage position can't be negative`() {
        assertFailsWith<IllegalStateException> {
        (-1).index
        }
    }

    @Test
    fun `storage position identity when positive`() {
        2.index.value shouldBeEqualTo 2
    }
}