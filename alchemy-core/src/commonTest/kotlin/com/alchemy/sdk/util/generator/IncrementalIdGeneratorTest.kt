package com.alchemy.sdk.util.generator

import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class IncrementalIdGeneratorTest {

    @Test
    fun `should increment each request id by 1`() {
        val incrementalIdGenerator = IncrementalIdGenerator()
        incrementalIdGenerator.generateId() shouldBeEqualTo "1"
        incrementalIdGenerator.generateId() shouldBeEqualTo "2"
    }
}