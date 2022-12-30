package com.alchemy.sdk.util

import io.ktor.client.engine.HttpClientEngine

expect object HttpClientEngineProvider {
    fun provideHttpClientEngine(): HttpClientEngine
}