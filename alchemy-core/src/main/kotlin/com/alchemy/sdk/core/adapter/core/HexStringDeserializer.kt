package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object HexStringDeserializer : JsonDeserializer<HexString?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): HexString? {
        return if (json == JsonNull.INSTANCE) {
            null
        } else {
            check(json is JsonPrimitive && json.isString)
            json.asString.hexString
        }
    }
}