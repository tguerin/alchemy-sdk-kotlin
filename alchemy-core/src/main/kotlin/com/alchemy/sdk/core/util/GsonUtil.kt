@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.alchemy.sdk.core.util

import com.alchemy.sdk.core.adapter.core.AddressDeserializer
import com.alchemy.sdk.core.adapter.core.AddressSerializer
import com.alchemy.sdk.core.adapter.core.BlockTagSerializer
import com.alchemy.sdk.core.adapter.core.BlockTransactionDeserializer
import com.alchemy.sdk.core.adapter.core.EtherDeserializer
import com.alchemy.sdk.core.adapter.core.EtherSerializer
import com.alchemy.sdk.core.adapter.core.HexStringDeserializer
import com.alchemy.sdk.core.adapter.core.HexStringSerializer
import com.alchemy.sdk.core.adapter.core.LogFilterSerializer
import com.alchemy.sdk.core.adapter.core.NumberSerializer
import com.alchemy.sdk.core.adapter.core.PercentileSerializer
import com.alchemy.sdk.core.adapter.core.RawFloatSerializer
import com.alchemy.sdk.core.adapter.core.RawIntSerializer
import com.alchemy.sdk.core.adapter.nft.FloorPriceDeserializer
import com.alchemy.sdk.core.adapter.nft.NftContractDeserializer
import com.alchemy.sdk.core.adapter.nft.NftDeserializer
import com.alchemy.sdk.core.adapter.nft.NftMetadataDeserializer
import com.alchemy.sdk.core.adapter.nft.NftTokenTypeDeserializer
import com.alchemy.sdk.core.adapter.nft.OwnedNftDeserializer
import com.alchemy.sdk.core.adapter.nft.OwnedNftsResponseDeserializer
import com.alchemy.sdk.core.adapter.nft.RefreshStateDeserializer
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.core.BlockTag
import com.alchemy.sdk.core.model.core.BlockTransaction
import com.alchemy.sdk.core.model.core.Index
import com.alchemy.sdk.core.model.core.LogFilter
import com.alchemy.sdk.core.model.core.Percentile
import com.alchemy.sdk.core.model.core.RawFloat
import com.alchemy.sdk.core.model.core.RawInt
import com.alchemy.sdk.core.model.nft.FloorPrice
import com.alchemy.sdk.core.model.nft.Nft
import com.alchemy.sdk.core.model.nft.NftContract
import com.alchemy.sdk.core.model.nft.NftExcludeFilter
import com.alchemy.sdk.core.model.nft.NftId
import com.alchemy.sdk.core.model.nft.NftMetadata
import com.alchemy.sdk.core.model.nft.NftTokenType
import com.alchemy.sdk.core.model.nft.OwnedNft
import com.alchemy.sdk.core.model.nft.OwnedNftsResponse
import com.alchemy.sdk.core.model.nft.RefreshState
import com.alchemy.sdk.core.model.nft.TokenMetadata
import com.alchemy.sdk.core.util.HexString.Companion.hexString
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