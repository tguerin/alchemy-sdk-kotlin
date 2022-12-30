package com.alchemy.sdk.util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual object HttpClientEngineProvider {
    actual fun provideHttpClientEngine(): HttpClientEngine {
        return OkHttp.create()
    }
}