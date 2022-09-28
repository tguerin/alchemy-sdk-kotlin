package com.alchemy.sdk.json.rpc.client.generator

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class IncrementalIdGeneratorTest {

    @Test
    fun `should increment each request id by 1`() {
        val incrementalIdGenerator = IncrementalIdGenerator()
        incrementalIdGenerator.generateId() shouldBeEqualTo "1"
        incrementalIdGenerator.generateId() shouldBeEqualTo "2"
    }
}