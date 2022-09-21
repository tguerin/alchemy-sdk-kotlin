package com.alchemy.sdk.core

import com.alchemy.sdk.core.adapter.AddressDeserializer
import com.alchemy.sdk.core.adapter.BlockTransactionDeserializer
import com.alchemy.sdk.core.adapter.EtherDeserializer
import com.alchemy.sdk.core.adapter.HexStringDeserializer
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import java.lang.reflect.Type

class GsonUtil {
    companion object {
        fun get(): Gson = GsonBuilder()
            .registerTypeAdapter(Address::class.java, object : InstanceCreator<Address> {
                override fun createInstance(type: Type?): Address {
                    return Address.from("0x")
                }
            })
            .registerTypeAdapter(Address::class.java, AddressDeserializer)
            .registerTypeAdapter(BlockTransaction::class.java, BlockTransactionDeserializer)
            .registerTypeAdapter(Ether::class.java, EtherDeserializer)
            .registerTypeAdapter(HexString::class.java, HexStringDeserializer)
            .create()
    }
}