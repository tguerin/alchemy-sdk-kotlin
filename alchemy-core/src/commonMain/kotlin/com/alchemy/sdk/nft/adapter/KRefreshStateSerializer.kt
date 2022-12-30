package com.alchemy.sdk.nft.adapter

import com.alchemy.sdk.nft.model.RefreshState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KRefreshStateSerializer : KSerializer<RefreshState> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RefreshState", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): RefreshState {
        val refreshStateValue = decoder.decodeString()
        return RefreshState.values().firstOrNull { it.value.equals(refreshStateValue, ignoreCase = true) }
            ?: error("Unknown refresh state $refreshStateValue")
    }

    override fun serialize(encoder: Encoder, value: RefreshState) {
        encoder.encodeString(value.value)
    }
}