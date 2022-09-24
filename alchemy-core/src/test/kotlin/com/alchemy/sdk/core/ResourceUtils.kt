package com.alchemy.sdk.core

import com.alchemy.sdk.core.util.GsonUtil.Companion.gson
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class ResourceUtils {

    companion object {

        fun <T> parseFile(fileName: String, type: Class<T>): T {
            return gson.fromJson(
                jsonReaderFromFileName(fileName),
                type
            )
        }

        private fun jsonReaderFromFileName(fileName: String): JsonReader {
            return JsonReader(
                InputStreamReader(
                    FileInputStream(
                        File("src/test/resources/$fileName")
                    )
                )
            )
        }
    }
}