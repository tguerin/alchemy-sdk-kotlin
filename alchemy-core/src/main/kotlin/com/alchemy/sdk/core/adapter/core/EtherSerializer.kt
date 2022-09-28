package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.util.Ether
import com.google.gson.*
import java.lang.reflect.Type

object EtherSerializer : JsonSerializer<Ether?> {
    override fun serialize(
        src: Ether?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(src.weiHexValue.toString())
        } else {
            JsonNull.INSTANCE
        }
    }

}