package com.alchemy.sdk.core.model.nft

enum class NftTokenType(val value: String) {
    Erc721("ERC721"),
    Erc1155("ERC1155"),
    Unknown("UNKNOWN")
}