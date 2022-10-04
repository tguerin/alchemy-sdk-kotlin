package com.alchemy.sdk.util

import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.util.HexString.Companion.hexString

internal object Constants {
    const val DEFAULT_ALCHEMY_API_KEY = "demo"
    const val DEFAULT_MAX_RETRIES = 5
    const val NONTRANSITIONAL_TO_UNICODE = 32

    val DEFAULT_NETWORK = Network.ETH_MAINNET

    const val HASH_ZERO = "0x0000000000000000000000000000000000000000000000000000000000000000"

    val ADDRESS_ZERO = "0x0000000000000000000000000000000000000000".hexString


    fun getAlchemyHttpUrl(network: Network, apiKey: String): String {
        return "https://${network.networkId}.g.alchemy.com/v2/${apiKey}"
    }

    fun getAlchemyNftUrl(network: Network, apiKey: String): String {
        return "https://${network.networkId}.g.alchemy.com/nft/v2/${apiKey}/"
    }

    fun getAlchemyWebsocketUrl(network: Network, apiKey: String): String {
        return "wss://${network.networkId}.g.alchemy.com/v2/$apiKey";
    }
}