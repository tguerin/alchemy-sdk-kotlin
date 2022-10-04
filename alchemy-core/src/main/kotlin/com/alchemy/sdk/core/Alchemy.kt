package com.alchemy.sdk.core

import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.api.NftApi
import com.alchemy.sdk.core.ccip.CcipReadFetcher
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.proxy.AlchemyProxy
import com.alchemy.sdk.core.util.AlchemyVersionInterceptor
import com.alchemy.sdk.core.util.Constants
import com.alchemy.sdk.core.util.GsonStringConverter
import com.alchemy.sdk.core.util.GsonUtil.Companion.gson
import com.alchemy.sdk.core.util.GsonUtil.Companion.nftGson
import com.alchemy.sdk.core.util.ResultCallAdapter
import com.alchemy.sdk.json.rpc.client.generator.IncrementalIdGenerator
import com.alchemy.sdk.json.rpc.client.http.HttpJsonRpcClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Suppress("UNCHECKED_CAST")
class Alchemy private constructor(alchemySettings: AlchemySettings) {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AlchemyVersionInterceptor)
        .build()

    private val idGenerator = IncrementalIdGenerator()

    val core by lazy {
        setupCore(alchemySettings)
    }

    val nft by lazy {
        setupNft(alchemySettings)
    }

    val transact by lazy {
        Transact(core)
    }

    val ws by lazy {
        Websocket(
            idGenerator = idGenerator,
            gson = gson,
            websocketUrl = Constants.getAlchemyWebsocketUrl(
                alchemySettings.network,
                alchemySettings.apiKey
            ),
            okHttpClientBuilder = OkHttpClient.Builder()
        )
    }

    private fun setupCore(alchemySettings: AlchemySettings): Core {
        val alchemyUrl =
            Constants.getAlchemyHttpUrl(alchemySettings.network, alchemySettings.apiKey)
        val alchemyProxy =
            AlchemyProxy(
                idGenerator = idGenerator,
                jsonRpcClient = HttpJsonRpcClient(
                    alchemyUrl,
                    okHttpClient,
                    gson
                )
            )
        return Core(
            alchemySettings.network,
            CcipReadFetcher(okHttpClient, gson),
            alchemyProxy.createProxy(CoreApi::class.java)
        )
    }

    private fun setupNft(alchemySettings: AlchemySettings): Nft {
        val alchemyUrl =
            Constants.getAlchemyNftUrl(alchemySettings.network, alchemySettings.apiKey)
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(alchemyUrl)
            .addCallAdapterFactory(ResultCallAdapter)
            .addConverterFactory(GsonConverterFactory.create(nftGson))
            .addConverterFactory(GsonStringConverter(nftGson))
            .build()
        return Nft(core, retrofit.create(NftApi::class.java))
    }

    companion object {
        fun with(alchemySettings: AlchemySettings) = Alchemy(alchemySettings)
    }
}