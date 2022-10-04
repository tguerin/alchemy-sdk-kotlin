package com.alchemy.sdk

import com.alchemy.sdk.util.GsonUtil
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class ResourceUtils {

    companion object {

        fun <T> parseFile(fileName: String, type: Class<T>, gson: Gson = GsonUtil.gson): T {
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