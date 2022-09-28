package com.alchemy.sdk.core.adapter.core

import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.*
import java.lang.reflect.Type

object AddressDeserializer : JsonDeserializer<Address?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Address? {
        if (json == JsonNull.INSTANCE) {
            return null
        }
        check(json is JsonPrimitive && json.isString)
        return when (typeOfT) {
            Address.ContractAddress::class.java -> {
                Address.ContractAddress(json.asString.hexString)
            }
            else -> {
                Address.from(json.asString)
            }
        }
    }
}