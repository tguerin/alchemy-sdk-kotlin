package com.alchemy.sdk.ws

interface RetryPolicy {
    /**
     * Indicates that a retry connection is possible. This function should suspend until a retry
     * is possible. If no retry is desired just return false.
     *
     * @return true if a reconnection should be retried, false if we should stop retrying
     */
    suspend fun retryConnection(): Boolean
}