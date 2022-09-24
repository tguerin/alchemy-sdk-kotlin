package com.alchemy.sdk.core.model

import com.alchemy.sdk.core.model.core.Index.Companion.index
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test


class IndexTest {

    @Test(expected = IllegalStateException::class)
    fun `storage position can't be negative`() {
        (-1).index
    }

    @Test
    fun `storage position identity when positive`() {
        2.index.value shouldBeEqualTo 2
    }
}