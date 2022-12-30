package com.alchemy.sdk.core.model

/**
 * The supported networks by Alchemy. Note that some functions are not available
 * on all networks. Please refer to the Alchemy documentation for which APIs are
 * available on which networks
 * {@link https://docs.alchemy.com/alchemy/apis/feature-support-by-chain}
 *
 */
enum class Network(
    val networkId: String,
    val ensAddress: Address? = null
) {
    ETH_MAINNET(
        "eth-mainnet",
        Address.from("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e")
    ),
    ETH_ROPSTEN(
        "eth-ropsten",
        Address.from("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e")
    ),
    ETH_GOERLI(
        "eth-goerli",
        Address.from("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e")
    ),
    ETH_KOVAN("eth-kovan"),
    ETH_RINKEBY(
        "eth-rinkeby",
        Address.from("0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e")
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