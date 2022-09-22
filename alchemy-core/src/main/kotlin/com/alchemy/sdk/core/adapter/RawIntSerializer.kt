package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.RawInt
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object RawIntSerializer : JsonSerializer<RawInt> {
    override fun serialize(
        src: RawInt?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(src.value)
        } else {
            JsonNull.INSTANCE
        }
    }
}