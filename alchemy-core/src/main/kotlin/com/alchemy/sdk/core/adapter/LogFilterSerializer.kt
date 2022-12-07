package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.LogFilter
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

object LogFilterSerializer : JsonSerializer<LogFilter> {
    override fun serialize(
        src: LogFilter?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return when (src) {
            is LogFilter.BlockHashFilter -> {
                val jsonObject = JsonObject()
                jsonObject.add("blockHash", JsonPrimitive(src.blockHash.data))
                if (src.addresses.isNotEmpty()) {
                    val jsonArray = JsonArray()
                    src.addresses.forEach { jsonArray.add(it.value.data) }
                    jsonObject.add("addresses", jsonArray)
                }
                jsonObject
            }

            is LogFilter.BlockRangeFilter -> {
                val jsonObject = JsonObject()
                jsonObject.add("from", JsonPrimitive(src.from.value))
                jsonObject.add("to", JsonPrimitive(src.to.value))
                if (src.addresses.isNotEmpty()) {
                    val jsonArray = JsonArray()
                    src.addresses.forEach { jsonArray.add(it.value.data) }
                    jsonObject.add("addresses", jsonArray)
                }
                jsonObject
            }

            null -> JsonNull.INSTANCE
        }
    }
}