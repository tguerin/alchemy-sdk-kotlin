package com.alchemy.sdk.core.model

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test


class StoragePositionTest {

    @Test(expected = IllegalStateException::class)
    fun `storage position can't be negative`() {
        StoragePosition.from(-1)
    }

    @Test
    fun `storage position identity when positive`() {
        StoragePosition.from(2).position shouldBeEqualTo 2
    }
}