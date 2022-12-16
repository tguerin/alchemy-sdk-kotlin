package com.alchemy.sdk

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

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
            return File("src/test/resources/$fileName").useLines {
                it.joinToString("")
            }
        }
    }
}