package com.alchemy.sdk.ws.adapter

import com.alchemy.sdk.ws.model.PendingTransaction
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

object KPendingTransactionSerializer : JsonContentPolymorphicSerializer<PendingTransaction>(PendingTransaction::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out PendingTransaction> {
        return if (element is JsonPrimitive) {
            PendingTransaction.HashOnly.serializer()
        } else {
            PendingTransaction.FullPendingTransaction.serializer()
        }
    }

}