package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.Percentile
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object PercentileSerializer : JsonSerializer<Percentile> {
    override fun serialize(
        src: Percentile,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val rawInt = src.value
        return JsonPrimitive(rawInt.value)
    }
}