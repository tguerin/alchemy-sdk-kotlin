@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.alchemy.sdk.util

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
import com.alchemy.sdk.nft.adapter.FloorPriceDeserializer
import com.alchemy.sdk.nft.adapter.NftContractDeserializer
import com.alchemy.sdk.nft.adapter.NftDeserializer
import com.alchemy.sdk.nft.adapter.NftMetadataDeserializer
import com.alchemy.sdk.nft.adapter.NftTokenTypeDeserializer
import com.alchemy.sdk.nft.adapter.OwnedNftDeserializer
import com.alchemy.sdk.nft.adapter.OwnedNftsResponseDeserializer
import com.alchemy.sdk.nft.adapter.RefreshStateDeserializer
import com.alchemy.sdk.nft.model.FloorPrice
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.model.NftContract
import com.alchemy.sdk.nft.model.NftId
import com.alchemy.sdk.nft.model.NftMetadata
import com.alchemy.sdk.nft.model.NftTokenType
import com.alchemy.sdk.nft.model.OwnedNft
import com.alchemy.sdk.nft.model.OwnedNftsResponse
import com.alchemy.sdk.nft.model.RefreshState
import com.alchemy.sdk.nft.model.TokenMetadata
import com.alchemy.sdk.util.HexString.Companion.hexString
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import java.lang.Double
import java.lang.Float
import java.lang.Long
import java.lang.reflect.Type

internal class GsonUtil {

    companion object {
        val gson: Gson by lazy {
            GsonBuilder()
                .registerTypeAdapter(Address::class.java, object : InstanceCreator<Address> {
                    override fun createInstance(type: Type): Address {
                        return Address.from("0x")
                    }
                })
                .registerTypeAdapter(
                    BlockTransaction::class.java,
                    object : InstanceCreator<BlockTransaction> {
                        override fun createInstance(type: Type): BlockTransaction {
                            return BlockTransaction.Unknown
                        }
                    })
                .registerTypeAdapter(Address::class.java, AddressDeserializer)
                .registerTypeAdapter(Address.ContractAddress::class.java, AddressDeserializer)
                .registerTypeAdapter(Address.EthereumAddress::class.java, AddressDeserializer)
                .registerTypeAdapter(Address::class.java, AddressSerializer)
                .registerTypeAdapter(Address.EthereumAddress::class.java, AddressSerializer)
                .registerTypeAdapter(Address.ContractAddress::class.java, AddressSerializer)
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

        val nftGson: Gson by lazy {
            GsonBuilder()
                .registerTypeAdapter(Address::class.java, object : InstanceCreator<Address> {
                    override fun createInstance(type: Type): Address {
                        return Address.from("0x")
                    }
                })
                .registerTypeAdapter(
                    OwnedNftsResponse::class.java,
                    object : InstanceCreator<OwnedNftsResponse> {
                        override fun createInstance(type: Type): OwnedNftsResponse {
                            return OwnedNftsResponse.OwnedBaseNftsResponse(
                                ownedNfts = emptyList(),
                                pageKey = null,
                                totalCount = 0
                            )
                        }
                    })
                .registerTypeAdapter(
                    OwnedNft::class.java,
                    object : InstanceCreator<OwnedNft> {
                        override fun createInstance(type: Type): OwnedNft {
                            return OwnedNft.OwnedBaseNft(
                                0L,
                                contract = NftContract.BaseNftContract(Address.ContractAddress("0x0".hexString)),
                                id = NftId("0".hexString, TokenMetadata(NftTokenType.Unknown)),
                            )
                        }
                    })
                .registerTypeAdapter(
                    NftContract::class.java,
                    object : InstanceCreator<NftContract> {
                        override fun createInstance(type: Type): NftContract {
                            return NftContract.BaseNftContract(Address.ContractAddress("0x0".hexString))
                        }
                    })
                .registerTypeAdapter(Address::class.java, AddressDeserializer)
                .registerTypeAdapter(Address.ContractAddress::class.java, AddressDeserializer)
                .registerTypeAdapter(Address.EthereumAddress::class.java, AddressDeserializer)
                .registerTypeAdapter(Address::class.java, AddressSerializer)
                .registerTypeAdapter(Address.EthereumAddress::class.java, AddressSerializer)
                .registerTypeAdapter(Address.ContractAddress::class.java, AddressSerializer)
                .registerTypeAdapter(Ether::class.java, EtherDeserializer)
                .registerTypeAdapter(Ether::class.java, EtherSerializer)
                .registerTypeAdapter(HexString::class.java, HexStringSerializer)
                .registerTypeAdapter(HexString::class.java, HexStringDeserializer)
                .registerTypeAdapter(OwnedNftsResponse::class.java, OwnedNftsResponseDeserializer)
                .registerTypeAdapter(OwnedNft::class.java, OwnedNftDeserializer)
                .registerTypeAdapter(NftContract::class.java, NftContractDeserializer)
                .registerTypeAdapter(NftTokenType::class.java, NftTokenTypeDeserializer)
                .registerTypeAdapter(NftMetadata::class.java, NftMetadataDeserializer)
                .registerTypeAdapter(Nft::class.java, NftDeserializer)
                .registerTypeAdapter(FloorPrice::class.java, FloorPriceDeserializer)
                .registerTypeAdapter(RefreshState::class.java, RefreshStateDeserializer)
                .create()
        }
    }
}