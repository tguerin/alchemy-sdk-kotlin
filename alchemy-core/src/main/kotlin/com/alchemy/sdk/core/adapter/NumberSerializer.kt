package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.util.HexString.Companion.hexString
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object NumberSerializer : JsonSerializer<Number?> {
    override fun serialize(
        src: Number?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(src.hexString.withoutLeadingZero())
        } else {
            JsonNull.INSTANCE
        }
    }
}