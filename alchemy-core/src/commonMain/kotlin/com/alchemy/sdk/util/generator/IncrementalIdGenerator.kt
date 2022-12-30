package com.alchemy.sdk.util.generator

import co.touchlab.stately.concurrency.AtomicLong

class IncrementalIdGenerator : IdGenerator {

    private val currentId = AtomicLong(0)

    override fun generateId() = currentId.incrementAndGet().toString()
}