package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.LogFilter
import com.google.gson.*
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
                jsonObject.add("blockHash", JsonPrimitive(src.blockHash.toString()))
                if (src.addresses.isNotEmpty()) {
                    val jsonArray = JsonArray()
                    src.addresses.forEach { jsonArray.add(it.value.toString()) }
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
                    src.addresses.forEach { jsonArray.add(it.value.toString()) }
                    jsonObject.add("addresses", jsonArray)
                }
                jsonObject
            }
            null -> JsonNull.INSTANCE
        }
    }
}