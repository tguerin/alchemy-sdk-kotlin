package com.alchemy.sdk.core.model

import com.alchemy.sdk.shouldBeEqualTo
import kotlin.test.Test

class NetworkTest {

    @Test
    fun `ensure network id`() {
        Network.ETH_MAINNET.networkId shouldBeEqualTo "eth-mainnet"
        Network.ETH_ROPSTEN.networkId shouldBeEqualTo "eth-ropsten"
        Network.ETH_GOERLI.networkId shouldBeEqualTo "eth-goerli"
        Network.ETH_KOVAN.networkId shouldBeEqualTo "eth-kovan"
        Network.ETH_RINKEBY.networkId shouldBeEqualTo "eth-rinkeby"
        Network.OPT_MAINNET.networkId shouldBeEqualTo "opt-mainnet"
        Network.OPT_KOVAN.networkId shouldBeEqualTo "opt-kovan"
        Network.OPT_GOERLI.networkId shouldBeEqualTo "opt-goerli"
        Network.ARB_MAINNET.networkId shouldBeEqualTo "arb-mainnet"
        Network.ARB_RINKEBY.networkId shouldBeEqualTo "arb-rinkeby"
        Network.ARB_GOERLI.networkId shouldBeEqualTo "arb-goerli"
        Network.MATIC_MAINNET.networkId shouldBeEqualTo "polygon-mainnet"
        Network.MATIC_MUMBAI.networkId shouldBeEqualTo "polygon-mumbai"
        Network.ASTAR_MAINNET.networkId shouldBeEqualTo "astar-mainnet"
    }
}