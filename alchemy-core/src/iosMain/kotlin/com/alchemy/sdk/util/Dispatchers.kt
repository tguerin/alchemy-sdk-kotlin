package com.alchemy.sdk.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext

actual class Dispatchers(
    actual val io: CoroutineDispatcher = newFixedThreadPoolContext(64, "IO Dispatcher").limitedParallelism(64),
    actual val main: CoroutineDispatcher = Dispatchers.Main,
) {
    actual constructor() : this(
        newFixedThreadPoolContext(64, "IO Dispatcher").limitedParallelism(64),
        Dispatchers.Main,
    )
}