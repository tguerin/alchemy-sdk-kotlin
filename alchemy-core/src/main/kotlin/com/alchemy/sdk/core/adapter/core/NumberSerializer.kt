package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.*
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