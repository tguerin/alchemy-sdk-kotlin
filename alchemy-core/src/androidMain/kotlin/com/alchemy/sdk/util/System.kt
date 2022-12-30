package com.alchemy.sdk.util

import java.lang.System

actual class System {
    actual companion object {
        actual fun getenv(variable: String): String? {
           return System.getenv(variable)
        }
    }
}