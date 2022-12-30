package com.alchemy.sdk.util

import kotlinx.coroutines.CoroutineDispatcher

expect class Dispatchers() {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
}