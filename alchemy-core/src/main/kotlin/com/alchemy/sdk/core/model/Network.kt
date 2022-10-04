package com.alchemy.sdk.core.model

import com.alchemy.sdk.util.HexString.Companion.hexString

/**
 * The supported networks by Alchemy. Note that some functions are not available
 * on all networks. Please refer to the Alchemy documentation for which APIs are
 * available on which networks
 * {@link https://docs.alchemy.com/alchemy/apis/feature-support-by-chain}
 *
 */
enum class Network(
    val networkId: String,
    val ensAddress: Address.ContractAddress? = null
) {
    ETH_MAINNET(
        "eth-mainnet",
        Address.ContractAddress("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e".hexString)
    ),
    ETH_ROPSTEN(
        "eth-ropsten",
        Address.ContractAddress("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e".hexString)
    ),
    ETH_GOERLI(
        "eth-goerli",
        Address.ContractAddress("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e".hexString)
    ),
    ETH_KOVAN("eth-kovan"),
    ETH_RINKEBY(
        "eth-rinkeby",
        Address.ContractAddress("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e".hexString)
    ),
    OPT_MAINNET("opt-mainnet"),
    OPT_KOVAN("opt-kovan"),
    OPT_GOERLI("opt-goerli"),
    ARB_MAINNET("arb-mainnet"),
    ARB_RINKEBY("arb-rinkeby"),
    ARB_GOERLI("arb-goerli"),
    MATIC_MAINNET("polygon-mainnet"),
    MATIC_MUMBAI("polygon-mumbai"),
    ASTAR_MAINNET("astar-mainnet")
}