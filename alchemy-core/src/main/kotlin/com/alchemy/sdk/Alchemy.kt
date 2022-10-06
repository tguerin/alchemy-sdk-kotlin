package com.alchemy.sdk

import com.alchemy.sdk.ccip.CcipReadFetcher
import com.alchemy.sdk.core.Core
import com.alchemy.sdk.core.api.CoreApi
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.json.rpc.client.generator.IncrementalIdGenerator
import com.alchemy.sdk.json.rpc.client.http.HttpJsonRpcClient
import com.alchemy.sdk.nft.Nft
import com.alchemy.sdk.nft.api.NftApi
import com.alchemy.sdk.proxy.AlchemyProxy
import com.alchemy.sdk.transact.Transact
import com.alchemy.sdk.util.AlchemyVersionInterceptor
import com.alchemy.sdk.util.Constants
import com.alchemy.sdk.util.GsonStringConverter
import com.alchemy.sdk.util.GsonUtil.Companion.gson
import com.alchemy.sdk.util.GsonUtil.Companion.nftGson
import com.alchemy.sdk.util.ResultCallAdapter
import com.alchemy.sdk.ws.WebSocket
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class AlchemySettings(
    /** The Alchemy API key that can be found in the Alchemy dashboard. */
    val apiKey: String = Constants.DEFAULT_ALCHEMY_API_KEY,

    /**
     * The name of the network. Once configured, the network cannot be changed. To
     * use a different network, instantiate a new `Alchemy` instance
     */
    val network: Network = Constants.DEFAULT_NETWORK,

    /** The maximum number of retries to attempt if a request fails. Defaults to 5. Not used for now*/
    val maxRetries: Int = Constants.DEFAULT_MAX_RETRIES,

    /**
     * Optional URL endpoint to use for all requests. Setting this field will
     * override the URL generated by the {@link network} and {@link apiKey} fields.
     *
     * This field is useful for testing or for using a custom node endpoint. Note
     * that not all methods will work with custom URLs.
     */
    val url: String? = null
)


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
        WebSocket(
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