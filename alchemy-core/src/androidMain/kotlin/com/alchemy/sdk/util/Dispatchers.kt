package com.alchemy.sdk.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class Dispatchers(
    actual val io: CoroutineDispatcher = Dispatchers.IO,
    actual val main: CoroutineDispatcher = Dispatchers.Main
) {
    actual constructor() : this(
        Dispatchers.IO,
        Dispatchers.Main,
    )
}