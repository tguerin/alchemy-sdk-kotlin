package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.RefreshState
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import java.lang.reflect.Type

object RefreshStateDeserializer : JsonDeserializer<RefreshState> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): RefreshState {
        return if (json == JsonNull.INSTANCE) {
            RefreshState.Unknown
        } else {
            RefreshState.values().firstOrNull { it.value == json.asString } ?: RefreshState.Unknown
        }
    }

}