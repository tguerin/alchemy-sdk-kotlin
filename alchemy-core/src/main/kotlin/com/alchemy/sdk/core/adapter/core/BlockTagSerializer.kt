package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.BlockTag
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object BlockTagSerializer : JsonSerializer<BlockTag?> {
    override fun serialize(
        src: BlockTag?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(src.value)
        } else {
            JsonNull.INSTANCE
        }
    }
}