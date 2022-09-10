package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object AddressDeserializer : JsonDeserializer<Address> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Address {
        check(json is JsonPrimitive && json.isString)
        return Address.from(json.asString)
    }
}