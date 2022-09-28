package com.alchemy.sdk.core.model.nft

import com.alchemy.sdk.core.model.core.Address
import com.google.gson.annotations.SerializedName

data class RefreshContractResponse(
    val contractAddress: Address.ContractAddress,
    @SerializedName("reingestionState")
    val refreshState: RefreshState,
    val progress: Int
)