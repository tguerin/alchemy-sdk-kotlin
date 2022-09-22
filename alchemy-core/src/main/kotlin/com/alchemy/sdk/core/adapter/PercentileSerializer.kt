package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Percentile
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object PercentileSerializer : JsonSerializer<Percentile> {
    override fun serialize(
        src: Percentile?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (src != null) {
            val rawInt = src.value
            JsonPrimitive(rawInt.value)
        } else {
            JsonNull.INSTANCE
        }
    }
}