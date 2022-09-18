package com.alchemy.sdk.core.api

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.Proof
import com.alchemy.sdk.core.model.StoragePosition
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.Wei
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpcParam

interface CoreApi {

    @JsonRpc("eth_getBalance")
    suspend fun getBalance(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("blockTag", position = 1) blockTag: BlockTag = BlockTag.Latest
    ): Result<Wei>

    @JsonRpc("eth_getCode")
    suspend fun getCode(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("blockTag", position = 1) blockTag: BlockTag = BlockTag.Latest
    ): Result<HexString>

    @JsonRpc("eth_getStorageAt")
    suspend fun getStorageAt(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("blockTag", position = 1) position: StoragePosition,
        @JsonRpcParam("blockTag", position = 2) blockTag: BlockTag = BlockTag.Latest
    ): Result<HexString>

    @JsonRpc("eth_getProof")
    suspend fun getProof(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("keys", position = 1) keys: List<HexString>,
        @JsonRpcParam("blockTag", position = 2) blockTag: BlockTag = BlockTag.Latest
    ): Result<Proof>

    @JsonRpc("eth_protocolVersion")
    suspend fun getProtocolVersion(): Result<HexString>

    @JsonRpc("eth_chainId")
    suspend fun getChainId(): Result<HexString>

    @JsonRpc("net_listening")
    suspend fun getNetListening(): Result<Boolean>

    @JsonRpc("net_version")
    suspend fun getNetVersion(): Result<String>

    @JsonRpc("web3_clientVersion")
    suspend fun getWeb3ClientVersion(): Result<String>

    @JsonRpc("web3_sha3")
    suspend fun getWeb3Sha3(
        @JsonRpcParam("data", position = 0) data: HexString
    ): Result<HexString>

}