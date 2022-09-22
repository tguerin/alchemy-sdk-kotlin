@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.adapter.AddressDeserializer
import com.alchemy.sdk.core.adapter.AddressSerializer
import com.alchemy.sdk.core.adapter.BlockTagSerializer
import com.alchemy.sdk.core.adapter.BlockTransactionDeserializer
import com.alchemy.sdk.core.adapter.EtherDeserializer
import com.alchemy.sdk.core.adapter.EtherSerializer
import com.alchemy.sdk.core.adapter.HexStringDeserializer
import com.alchemy.sdk.core.adapter.HexStringSerializer
import com.alchemy.sdk.core.adapter.LogFilterSerializer
import com.alchemy.sdk.core.adapter.NumberSerializer
import com.alchemy.sdk.core.adapter.PercentileSerializer
import com.alchemy.sdk.core.adapter.RawFloatSerializer
import com.alchemy.sdk.core.adapter.RawIntSerializer
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.LogFilter
import com.alchemy.sdk.core.model.Percentile
import com.alchemy.sdk.core.model.RawFloat
import com.alchemy.sdk.core.model.RawInt
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import java.lang.Double
import java.lang.Float
import java.lang.Long
import java.lang.reflect.Type

class GsonUtil {
    companion object {
        fun get(): Gson = GsonBuilder()
            .registerTypeAdapter(Address::class.java, object : InstanceCreator<Address> {
                override fun createInstance(type: Type?): Address {
                    return Address.from("0x")
                }
            })
            .registerTypeAdapter(
                BlockTransaction::class.java,
                object : InstanceCreator<BlockTransaction> {
                    override fun createInstance(type: Type?): BlockTransaction {
                        return BlockTransaction.Unknown
                    }
                })
            .registerTypeAdapter(Address::class.java, AddressDeserializer)
            .registerTypeAdapter(Address.EthereumAddress::class.java, AddressSerializer)
            .registerTypeAdapter(Address.IcapAddress::class.java, AddressSerializer)
            .registerTypeAdapter(Address.NameHashAddress::class.java, AddressSerializer)
            .registerTypeAdapter(BlockTag.BlockTagNumber::class.java, BlockTagSerializer)
            .registerTypeAdapter(BlockTag.Earliest::class.java, BlockTagSerializer)
            .registerTypeAdapter(BlockTag.Latest::class.java, BlockTagSerializer)
            .registerTypeAdapter(BlockTag.Pending::class.java, BlockTagSerializer)
            .registerTypeAdapter(BlockTag.Safe::class.java, BlockTagSerializer)
            .registerTypeAdapter(BlockTag.Finalized::class.java, BlockTagSerializer)
            .registerTypeAdapter(BlockTransaction::class.java, BlockTransactionDeserializer)
            .registerTypeAdapter(Ether::class.java, EtherDeserializer)
            .registerTypeAdapter(Ether::class.java, EtherSerializer)
            .registerTypeAdapter(HexString::class.java, HexStringSerializer)
            .registerTypeAdapter(HexString::class.java, HexStringDeserializer)
            .registerTypeAdapter(Integer::class.java, NumberSerializer)
            .registerTypeAdapter(Long::class.java, NumberSerializer)
            .registerTypeAdapter(Float::class.java, NumberSerializer)
            .registerTypeAdapter(Double::class.java, NumberSerializer)
            .registerTypeAdapter(LogFilter.BlockHashFilter::class.java, LogFilterSerializer)
            .registerTypeAdapter(LogFilter.BlockRangeFilter::class.java, LogFilterSerializer)
            .registerTypeAdapter(RawFloat::class.java, RawFloatSerializer)
            .registerTypeAdapter(RawInt::class.java, RawIntSerializer)
            .registerTypeAdapter(Index::class.java, RawFloatSerializer)
            .registerTypeAdapter(Percentile::class.java, PercentileSerializer)
            .create()
    }
}