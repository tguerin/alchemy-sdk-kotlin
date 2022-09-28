package com.alchemy.sdk.core.model.nft

@Suppress("UNCHECKED_CAST")
data class NftMetadata(
    private val metadata: Map<String, Any>
) {

    val date: Long?
        get() = (metadata["date"] as Number?)?.toLong()

    val image: String?
        get() = metadata["image"] as String?

    val dna: String?
        get() = metadata["dna"] as String?

    val name: String?
        get() = metadata["name"] as String?

    val description: String?
        get() = metadata["description"] as String?

    val edition: Double?
        get() = (metadata["edition"] as Number?)?.toDouble()

    val attributes: List<Map<String, Any>>?
        get() = metadata["attributes"] as List<Map<String, Any>>?

}