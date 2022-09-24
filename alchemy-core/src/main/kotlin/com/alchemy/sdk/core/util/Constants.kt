package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.model.core.Network

internal object Constants {
    const val DEFAULT_CONTRACT_ADDRESSES = "DEFAULT_TOKENS"
    const val DEFAULT_ALCHEMY_API_KEY = "demo"
    const val DEFAULT_MAX_RETRIES = 5
    const val LATEST_BLOCK = "latest"

    val DEFAULT_NETWORK = Network.ETH_MAINNET

    val ETHERS_NETWORK = mapOf(
        Network.ETH_MAINNET to "mainnet",
        Network.ETH_ROPSTEN to "ropsten",
        Network.ETH_GOERLI to "goerli",
        Network.ETH_KOVAN to "kovan",
        Network.ETH_RINKEBY to "rinkeby",
        Network.OPT_MAINNET to "optimism",
        Network.OPT_KOVAN to "optimism-kovan",
        Network.OPT_GOERLI to "optimism-goerli",
        Network.ARB_MAINNET to "arbitrum",
        Network.ARB_RINKEBY to "arbitrum-rinkeby",
        Network.ARB_GOERLI to "arbitrum-goerli",
        Network.MATIC_MAINNET to "matic",
        Network.MATIC_MUMBAI to "maticmum",
        Network.ASTAR_MAINNET to "astar-mainnet"
    )

    fun getAlchemyHttpUrl(network: Network, apiKey: String): String {
        return "https://${network.networkId}.g.alchemy.com/v2/${apiKey}";
    }

    fun getAlchemyNftUrl(network: Network, apiKey: String): String {
        return "https://${network.networkId}.g.alchemy.com/nft/v2/${apiKey}/";
    }

    const val ETH_NULL_VALUE = "0x"
}