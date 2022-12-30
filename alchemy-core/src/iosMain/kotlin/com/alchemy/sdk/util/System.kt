package com.alchemy.sdk.util

import kotlinx.cinterop.toKString

actual class System {
    actual companion object {
        actual fun getenv(variable: String): String? {
            return platform.posix.getenv("$$variable")?.toKString()
        }
    }
}