package com.alchemy.sdk.ws.adapter

import com.alchemy.sdk.util.HexString.Companion.hexString
import com.alchemy.sdk.ws.model.PendingTransaction
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KHashOnlySerializer : KSerializer<PendingTransaction.HashOnly> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PendingTransaction.HashOnly", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PendingTransaction.HashOnly {
        return PendingTransaction.HashOnly(decoder.decodeString().hexString)
    }

    override fun serialize(encoder: Encoder, value: PendingTransaction.HashOnly) {
        encoder.encodeString(value.hash.data)
    }
}