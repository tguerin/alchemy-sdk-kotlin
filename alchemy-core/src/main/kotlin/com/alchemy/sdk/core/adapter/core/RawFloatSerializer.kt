package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.RawFloat
import com.google.gson.*
import java.lang.reflect.Type

object RawFloatSerializer : JsonSerializer<RawFloat?> {
    override fun serialize(
        src: RawFloat?,
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