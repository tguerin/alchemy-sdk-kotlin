package com.alchemy.sdk.nft.explorer.ui.nft

import com.alchemy.sdk.nft.model.Nft

data class NftExplorerState(
    val address: String = "0xBC4CA0EdA7647A8aB7C2061c2E118A18a936f13D", // Bored ape contract
    val ownedNfts: List<Nft> = emptyList(),
    val errorMessage: String? = null,
    val loading: Boolean = false
)