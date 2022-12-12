package com.alchemy.sdk.util

import com.google.gson.Gson

class GsonStringConverter(private val gson: Gson) {
    fun convert(value: Any?): String {
        val jsonValue = gson.toJson(value)
        return if (jsonValue.startsWith("\"") && jsonValue.endsWith("\"")) {
            jsonValue.substring(1, jsonValue.length - 1)
        } else {
            jsonValue
        }
    }
}