package com.alchemy.sdk.ws

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class DelayRetryPolicy(
    private val delayMs: Long = TimeUnit.SECONDS.toMillis(5L)
) : RetryPolicy {
    override suspend fun retryConnection(): Boolean = with(Dispatchers.IO) {
        delay(delayMs)
        return true
    }
}