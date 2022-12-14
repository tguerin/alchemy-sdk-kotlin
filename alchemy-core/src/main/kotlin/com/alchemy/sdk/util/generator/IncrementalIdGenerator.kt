package com.alchemy.sdk.util.generator

import java.util.concurrent.atomic.AtomicLong

class IncrementalIdGenerator : IdGenerator {

    private val currentId = AtomicLong(1)

    override fun generateId() = currentId.getAndIncrement().toString()
}