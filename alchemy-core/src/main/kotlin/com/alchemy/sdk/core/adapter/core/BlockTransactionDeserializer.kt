package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.BlockTransaction
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.*
import java.lang.reflect.Type

object BlockTransactionDeserializer : JsonDeserializer<BlockTransaction?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BlockTransaction? {
        return when {
            json == JsonNull.INSTANCE -> null
            json is JsonPrimitive && json.isString -> BlockTransaction.SimpleBlockTransaction(json.asString.hexString)
            json is JsonObject -> context.deserialize(
                json,
                BlockTransaction.FullBlockTransaction::class.java
            )
            else -> BlockTransaction.Unknown
        }
    }
}