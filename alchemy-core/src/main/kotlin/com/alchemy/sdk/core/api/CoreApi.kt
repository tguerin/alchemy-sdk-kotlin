package com.alchemy.sdk.core.api

import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.Block
import com.alchemy.sdk.core.model.BlockCount
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.core.model.FeeHistory
import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.Percentile
import com.alchemy.sdk.core.model.Proof
import com.alchemy.sdk.core.model.TransactionReceipt
import com.alchemy.sdk.core.model.UncleBlock
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpcParam

interface CoreApi {

    @JsonRpc("eth_getBalance")
    suspend fun getBalance(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("blockTag", position = 1) blockTag: BlockTag = BlockTag.Latest
    ): Result<Ether>

    @JsonRpc("eth_getCode")
    suspend fun getCode(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("blockTag", position = 1) blockTag: BlockTag = BlockTag.Latest
    ): Result<HexString>

    @JsonRpc("eth_getStorageAt")
    suspend fun getStorageAt(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("position", position = 1) index: Index,
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

    @JsonRpc("eth_blockNumber")
    suspend fun getBlockNumber(): Result<HexString>

    @JsonRpc("eth_getBlockByNumber")
    suspend fun getBlockByNumber(
        @JsonRpcParam("blockTag", position = 0) blockTag: BlockTag,
        @JsonRpcParam("fullTransactions", position = 1) fullTransactions: Boolean = false
    ): Result<Block>

    @JsonRpc("eth_getBlockByHash")
    suspend fun getBlockByHash(
        @JsonRpcParam("blockHash", position = 0) blockHash: HexString,
        @JsonRpcParam("fullTransactions", position = 1) fullTransactions: Boolean = false
    ): Result<Block>

    @JsonRpc("eth_getBlockTransactionCountByNumber")
    suspend fun getBlockTransactionCountByNumber(
        @JsonRpcParam("blockTag", position = 0) blockTag: BlockTag
    ): Result<HexString>

    @JsonRpc("eth_getBlockTransactionCountByHash")
    suspend fun getBlockTransactionCountByHash(
        @JsonRpcParam("blockHash", position = 0) blockHash: HexString
    ): Result<HexString>

    @JsonRpc("eth_getUncleByBlockNumberAndIndex")
    suspend fun getUncleByBlockNumberAndIndex(
        @JsonRpcParam("blockTag", position = 0) blockTag: BlockTag,
        @JsonRpcParam("index", position = 1) index: Index
    ): Result<UncleBlock>

    @JsonRpc("eth_getUncleByBlockHashAndIndex")
    suspend fun getUncleByBlockHashAndIndex(
        @JsonRpcParam("blockHash", position = 0) blockHash: HexString,
        @JsonRpcParam("index", position = 1) index: Index
    ): Result<UncleBlock>

    @JsonRpc("eth_getUncleCountByBlockNumber")
    suspend fun getUncleCountByBlockNumber(
        @JsonRpcParam("blockTag", position = 0) blockTag: BlockTag
    ): Result<HexString>

    @JsonRpc("eth_getUncleCountByBlockHash")
    suspend fun getUncleCountByBlockHash(
        @JsonRpcParam("blockHash", position = 0) blockHash: HexString
    ): Result<HexString>

    @JsonRpc("eth_getTransactionByBlockNumberAndIndex")
    suspend fun getTransactionByBlockNumberAndIndex(
        @JsonRpcParam("blockTag", position = 0) blockTag: BlockTag,
        @JsonRpcParam("index", position = 1) index: Index,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionByBlockHashAndIndex")
    suspend fun getTransactionByBlockHashAndIndex(
        @JsonRpcParam("blockHash", position = 0) blockHash: HexString,
        @JsonRpcParam("index", position = 1) index: Index,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionCount")
    suspend fun getTransactionCount(
        @JsonRpcParam("address", position = 0) address: Address,
        @JsonRpcParam("blockTag", position = 1) blockTag: BlockTag = BlockTag.Latest,
    ): Result<HexString>

    @JsonRpc("eth_getTransactionByHash")
    suspend fun getTransactionByHash(
        @JsonRpcParam("transactionHash", position = 0) transactionHash: HexString,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionReceipt")
    suspend fun getTransactionReceipt(
        @JsonRpcParam("transactionHash", position = 0) transactionHash: HexString,
    ): Result<TransactionReceipt>

    @JsonRpc("eth_gasPrice")
    suspend fun getGasPrice(): Result<Ether>

    @JsonRpc("eth_maxPriorityFeePerGas")
    suspend fun getMaxPriorityFeePerGas(): Result<Ether>

    @JsonRpc("eth_feeHistory")
    suspend fun getFeeHistory(
        @JsonRpcParam("transactionHash", position = 0) blockCount: BlockCount,
        @JsonRpcParam("newestBlock", position = 1) blockTag: BlockTag,
        @JsonRpcParam(
            "rewardPercentiles",
            position = 2
        ) rewardPercentiles: List<Percentile>? = null,
    ): Result<FeeHistory>

}