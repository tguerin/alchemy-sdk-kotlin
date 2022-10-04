package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
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