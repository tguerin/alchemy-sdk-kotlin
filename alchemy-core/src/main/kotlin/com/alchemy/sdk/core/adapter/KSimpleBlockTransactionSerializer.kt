package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.util.HexString.Companion.hexString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KSimpleBlockTransactionSerializer : KSerializer<BlockTransaction.SimpleBlockTransaction> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BlockTransaction.SimpleBlockTransaction", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BlockTransaction.SimpleBlockTransaction {
        return BlockTransaction.SimpleBlockTransaction(decoder.decodeString().hexString)
    }

    override fun serialize(encoder: Encoder, value: BlockTransaction.SimpleBlockTransaction) {
        encoder.encodeString(value.hash.data)
    }

}