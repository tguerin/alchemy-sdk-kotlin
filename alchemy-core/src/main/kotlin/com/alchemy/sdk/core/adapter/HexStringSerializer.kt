package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.util.HexString
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object HexStringSerializer : JsonSerializer<HexString> {
    override fun serialize(
        src: HexString?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(src.toString())
        } else {
            JsonNull.INSTANCE
        }
    }
}