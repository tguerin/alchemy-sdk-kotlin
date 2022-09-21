package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.Ether.Companion.wei
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object EtherDeserializer : JsonDeserializer<Ether> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Ether {
        check(json is JsonPrimitive && json.isString)
        return json.asString.wei
    }
}