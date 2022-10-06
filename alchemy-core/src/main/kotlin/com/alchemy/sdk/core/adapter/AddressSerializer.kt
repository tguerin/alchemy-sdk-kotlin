package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object AddressSerializer : JsonSerializer<Address?> {
    override fun serialize(
        src: Address?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(src.value.toString())
        } else {
            JsonNull.INSTANCE
        }
    }

}