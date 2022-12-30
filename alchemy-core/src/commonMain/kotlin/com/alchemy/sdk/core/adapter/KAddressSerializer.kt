package com.alchemy.sdk.core.adapter

import com.alchemy.sdk.core.model.Address
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KAddressSerializer : KSerializer<Address> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Address", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Address {
        return Address.from(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Address) {
        encoder.encodeString(value.value.data)
    }
}