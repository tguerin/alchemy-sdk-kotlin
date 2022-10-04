package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.util.Ether
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
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