package com.alchemy.sdk.nft.model

import com.alchemy.sdk.core.model.Address
import com.google.gson.annotations.SerializedName

data class RefreshContractResponse(
    val contractAddress: Address.ContractAddress,
    @SerializedName("reingestionState")
    val refreshState: RefreshState,
    val progress: Int
)