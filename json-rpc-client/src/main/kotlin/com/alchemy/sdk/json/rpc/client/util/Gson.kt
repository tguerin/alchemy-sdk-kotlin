package com.alchemy.sdk.json.rpc.client.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import java.io.Reader
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
fun <T> Gson.parseContent(
    wrapperType: Class<*>,
    returnType: Type,
    reader: Reader
): Pair<T?, Exception?> {
    val jsonReader = newJsonReader(reader)
    val typeToken =
        TypeToken.getParameterized(
            wrapperType,
            returnType
        )
    val adapter = getAdapter(typeToken)
    var dataRead: Any?
    var exception: Exception? = null
    try {
        dataRead = adapter.read(jsonReader)
        if (jsonReader.peek() !== JsonToken.END_DOCUMENT) {
            dataRead = null
        }
    } catch (e: Exception) {
        exception = e
        dataRead = null
    }
    return dataRead as T to exception
}

