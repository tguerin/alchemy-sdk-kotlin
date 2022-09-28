package com.alchemy.sdk.core.adapter.nft

import com.alchemy.sdk.core.model.nft.RefreshState
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