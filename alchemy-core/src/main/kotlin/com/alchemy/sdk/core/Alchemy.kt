package com.alchemy.sdk.core

import com.alchemy.sdk.core.adapter.AddressDeserializer
import com.alchemy.sdk.core.adapter.EtherDeserializer
import com.alchemy.sdk.core.adapter.HexStringDeserializer
import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.Percentile
import com.alchemy.sdk.core.proxy.AlchemyProxy
import com.alchemy.sdk.core.proxy.ParameterConverter
import com.alchemy.sdk.core.proxy.converters.AddressParamConverter
import com.alchemy.sdk.core.proxy.converters.BlockTagParameterConverter
import com.alchemy.sdk.core.proxy.converters.HexStringParameterConverter
import com.alchemy.sdk.core.proxy.converters.NumberParameterConverter
import com.alchemy.sdk.core.proxy.converters.PercentileParameterConverter
import com.alchemy.sdk.core.util.Constants
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.json.rpc.client.generator.IncrementalIdGenerator
import com.alchemy.sdk.json.rpc.client.http.HttpJsonRpcClient
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import java.lang.reflect.Type

@Suppress("UNCHECKED_CAST")
class Alchemy private constructor(alchemySettings: AlchemySettings) {

    val core: Core

    init {
        val alchemyUrl =
            Constants.getAlchemyHttpUrl(alchemySettings.network, alchemySettings.apiKey)
        val gson = GsonBuilder()
            .registerTypeAdapter(Address::class.java, object : InstanceCreator<Address> {
                override fun createInstance(type: Type?): Address {
                    return Address.from("0x")
                }
            })
            .registerTypeAdapter(Address::class.java, AddressDeserializer)
            .registerTypeAdapter(HexString::class.java, HexStringDeserializer)
            .registerTypeAdapter(Ether::class.java, EtherDeserializer)
            .create()
        val alchemyProxy =
            AlchemyProxy(
                idGenerator = IncrementalIdGenerator(),
                jsonRpcClient = HttpJsonRpcClient(
                    alchemyUrl,
                    OkHttpClient(),
                    gson
                ),
                parameterConverters = hashMapOf(
                    Address::class.java to AddressParamConverter as ParameterConverter<Any, Any>,
                    BlockTag::class.java to BlockTagParameterConverter as ParameterConverter<Any, Any>,
                    HexString::class.java to HexStringParameterConverter as ParameterConverter<Any, Any>,
                    Number::class.java to NumberParameterConverter as ParameterConverter<Any, Any>,
                    Percentile::class.java to PercentileParameterConverter as ParameterConverter<Any, Any>,
                )
            )
        core = Core(alchemyProxy.createProxy(CoreApi::class.java))
    }


    companion object {
        fun with(alchemySettings: AlchemySettings) = Alchemy(alchemySettings)
        fun asyncWith(alchemySettings: AlchemySettings) = callbackFlow<Alchemy> {
            trySendBlocking(Alchemy(alchemySettings))
            channel.close()
        }
    }
}