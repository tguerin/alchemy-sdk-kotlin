package com.alchemy.sdk

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class ResourceUtils {

    companion object {

        val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

        internal inline fun <reified T> parseFile(fileName: String): T {
            return json.decodeFromString(readFile(fileName))
        }

        fun readFile(fileName: String): String {
            return Resource.readText(fileName)
        }

    }
}