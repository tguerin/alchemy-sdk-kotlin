package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.util.HexString
import com.google.gson.*
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