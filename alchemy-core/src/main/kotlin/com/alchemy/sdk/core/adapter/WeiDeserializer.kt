package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.Wei
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object WeiDeserializer : JsonDeserializer<Wei> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Wei {
        check(json is JsonPrimitive && json.isString)
        return Wei(HexString.from(json.asString))
    }
}