package com.alchemy.sdk.core.util

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class GsonStringConverter(private val gson: Gson) : Converter.Factory() {

    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String> {
        val typeAdapter = gson.getAdapter(TypeToken.get(type))
        return StringConverter(typeAdapter)
    }

    class StringConverter<T>(private val typeAdapter: TypeAdapter<T>) : Converter<T, String> {

        override fun convert(value: T): String {
            val jsonValue = typeAdapter.toJson(value)
            return if (jsonValue.startsWith("\"") && jsonValue.endsWith("\"")) {
                jsonValue.substring(1, jsonValue.length - 1)
            } else {
                jsonValue
            }
        }
    }
}