package com.alchemy.sdk.nft.model

enum class NftTokenType(val value: String) {
    Erc721("ERC721"),
    Erc1155("ERC1155"),
    Unknown("UNKNOWN")
}