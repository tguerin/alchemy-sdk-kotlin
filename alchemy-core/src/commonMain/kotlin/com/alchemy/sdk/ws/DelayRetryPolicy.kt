package com.alchemy.sdk.ws

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DelayRetryPolicy(
    private val delayMs: Duration = 5.seconds
) : RetryPolicy {

    // Use dispatcher
    override suspend fun retryConnection(): Boolean {
        delay(delayMs)
        return true
    }
}