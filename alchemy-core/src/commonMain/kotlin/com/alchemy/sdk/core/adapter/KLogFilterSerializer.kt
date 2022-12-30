package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.LogFilter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object KLogFilterSerializer : JsonContentPolymorphicSerializer<LogFilter>(LogFilter::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out LogFilter> {
        return if ("blockHash" in element.jsonObject) {
            LogFilter.BlockHashFilter.serializer()
        } else {
            LogFilter.BlockRangeFilter.serializer()
        }
    }

}