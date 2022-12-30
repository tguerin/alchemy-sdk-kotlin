package com.alchemy.sdk

expect object Resource {
    fun readText(resourceName: String): String
}
