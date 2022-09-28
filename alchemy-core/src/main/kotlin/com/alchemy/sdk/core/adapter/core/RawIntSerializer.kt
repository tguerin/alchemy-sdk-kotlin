package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.RawInt
import com.google.gson.*
import java.lang.reflect.Type

object RawIntSerializer : JsonSerializer<RawInt?> {
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