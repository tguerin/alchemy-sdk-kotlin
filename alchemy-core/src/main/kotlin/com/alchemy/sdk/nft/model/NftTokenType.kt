package com.alchemy.sdk.nft.model

import com.alchemy.sdk.nft.adapter.KNftTokenTypeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = KNftTokenTypeSerializer::class)
enum class NftTokenType(val value: String) {
    Erc721("ERC721"),
    Erc1155("ERC1155"),
    Unknown("UNKNOWN")
}