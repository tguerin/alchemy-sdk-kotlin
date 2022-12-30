package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.BlockTransaction
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

object KBlockTransactionSerializer : JsonContentPolymorphicSerializer<BlockTransaction>(BlockTransaction::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out BlockTransaction> {
        return if (element is JsonPrimitive) {
            BlockTransaction.SimpleBlockTransaction.serializer()
        } else {
            BlockTransaction.FullBlockTransaction.serializer()
        }
    }

}