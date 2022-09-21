package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object BlockTransactionDeserializer : JsonDeserializer<BlockTransaction> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BlockTransaction {
        return when {
            json is JsonPrimitive && json.isString -> BlockTransaction.SimpleBlockTransaction(json.asString.hexString)
            json is JsonObject -> context.deserialize(
                json,
                BlockTransaction.FullBlockTransaction::class.java
            )
            else -> BlockTransaction.Unknown
        }
    }
}