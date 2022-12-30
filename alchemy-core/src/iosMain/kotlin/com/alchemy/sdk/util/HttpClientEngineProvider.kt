package com.alchemy.sdk.util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual object HttpClientEngineProvider {
    actual fun provideHttpClientEngine(): HttpClientEngine {
        return Darwin.create()
    }
}