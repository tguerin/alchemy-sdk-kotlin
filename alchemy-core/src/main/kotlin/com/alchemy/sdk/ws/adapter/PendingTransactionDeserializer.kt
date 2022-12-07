package com.alchemy.sdk.ws.adapter

import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.ws.model.PendingTransaction
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object PendingTransactionDeserializer : JsonDeserializer<PendingTransaction?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PendingTransaction? {
        return when (json) {
            JsonNull.INSTANCE -> {
                null
            }

            is JsonPrimitive -> {
                PendingTransaction.HashOnly(json.asString.hexString)
            }

            else -> {
                context.deserialize(json, PendingTransaction.FullPendingTransaction::class.java)
            }
        }
    }
}