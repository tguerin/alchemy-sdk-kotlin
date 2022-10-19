package com.alchemy.sdk.ws

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DelayRetryPolicy(
    private val delayMs: Long = TimeUnit.SECONDS.toMillis(5L)
) : RetryPolicy {
    override suspend fun retryConnection(): Boolean = withContext(Dispatchers.IO) {
        delay(delayMs)
        true
    }
}