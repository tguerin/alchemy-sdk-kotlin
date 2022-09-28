package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.Address
import com.google.gson.*
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