package com.alchemy.sdk.util

expect class System {
    companion object {
        fun getenv(variable: String): String?
    }
}