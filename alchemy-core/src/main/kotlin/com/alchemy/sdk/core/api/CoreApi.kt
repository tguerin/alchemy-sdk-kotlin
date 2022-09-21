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
import com.alchemy.sdk.core.model.Validator
import com.alchemy.sdk.core.util.Ether
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpc
import com.alchemy.sdk.json.rpc.client.annotation.JsonRpcParam

interface CoreApi {

    @JsonRpc("eth_getBalance")
    suspend fun getBalance(
        @JsonRpcParam("address") address: Address,
        @JsonRpcParam("blockTag") blockTag: BlockTag = BlockTag.Latest
    ): Result<Ether>

    @JsonRpc("eth_getCode")
    suspend fun getCode(
        @JsonRpcParam("address") address: Address,
        @JsonRpcParam("blockTag") blockTag: BlockTag = BlockTag.Latest
    ): Result<HexString>

    @JsonRpc("eth_getStorageAt")
    suspend fun getStorageAt(
        @JsonRpcParam("address") address: Address,
        @JsonRpcParam("position") index: Index,
        @JsonRpcParam("blockTag") blockTag: BlockTag = BlockTag.Latest
    ): Result<HexString>

    @JsonRpc("eth_getProof")
    suspend fun getProof(
        @JsonRpcParam("address") address: Address,
        @JsonRpcParam("keys") keys: List<HexString>,
        @JsonRpcParam("blockTag") blockTag: BlockTag = BlockTag.Latest
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
        @JsonRpcParam("data") data: HexString
    ): Result<HexString>

    @JsonRpc("eth_blockNumber")
    suspend fun getBlockNumber(): Result<HexString>

    @JsonRpc("eth_getBlockByNumber")
    suspend fun getBlockByNumber(
        @JsonRpcParam("blockTag") blockTag: BlockTag,
        @JsonRpcParam("fullTransactions") fullTransactions: Boolean = false
    ): Result<Block>

    @JsonRpc("eth_getBlockByHash")
    suspend fun getBlockByHash(
        @JsonRpcParam("blockHash") blockHash: HexString,
        @JsonRpcParam("fullTransactions") fullTransactions: Boolean = false
    ): Result<Block>

    @JsonRpc("eth_getBlockTransactionCountByNumber")
    suspend fun getBlockTransactionCountByNumber(
        @JsonRpcParam("blockTag") blockTag: BlockTag
    ): Result<HexString>

    @JsonRpc("eth_getBlockTransactionCountByHash")
    suspend fun getBlockTransactionCountByHash(
        @JsonRpcParam("blockHash") blockHash: HexString
    ): Result<HexString>

    @JsonRpc("eth_getUncleByBlockNumberAndIndex")
    suspend fun getUncleByBlockNumberAndIndex(
        @JsonRpcParam("blockTag") blockTag: BlockTag,
        @JsonRpcParam("index") index: Index
    ): Result<UncleBlock>

    @JsonRpc("eth_getUncleByBlockHashAndIndex")
    suspend fun getUncleByBlockHashAndIndex(
        @JsonRpcParam("blockHash") blockHash: HexString,
        @JsonRpcParam("index") index: Index
    ): Result<UncleBlock>

    @JsonRpc("eth_getUncleCountByBlockNumber")
    suspend fun getUncleCountByBlockNumber(
        @JsonRpcParam("blockTag") blockTag: BlockTag
    ): Result<HexString>

    @JsonRpc("eth_getUncleCountByBlockHash")
    suspend fun getUncleCountByBlockHash(
        @JsonRpcParam("blockHash") blockHash: HexString
    ): Result<HexString>

    @JsonRpc("eth_getTransactionByBlockNumberAndIndex")
    suspend fun getTransactionByBlockNumberAndIndex(
        @JsonRpcParam("blockTag") blockTag: BlockTag,
        @JsonRpcParam("index") index: Index,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionByBlockHashAndIndex")
    suspend fun getTransactionByBlockHashAndIndex(
        @JsonRpcParam("blockHash") blockHash: HexString,
        @JsonRpcParam("index") index: Index,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionCount")
    suspend fun getTransactionCount(
        @JsonRpcParam("address") address: Address,
        @JsonRpcParam("blockTag") blockTag: BlockTag = BlockTag.Latest,
    ): Result<HexString>

    @JsonRpc("eth_getTransactionByHash")
    suspend fun getTransactionByHash(
        @JsonRpcParam("transactionHash") transactionHash: HexString,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionReceipt")
    suspend fun getTransactionReceipt(
        @JsonRpcParam("transactionHash") transactionHash: HexString,
    ): Result<TransactionReceipt>

    @JsonRpc("eth_gasPrice")
    suspend fun getGasPrice(): Result<Ether>

    @JsonRpc("eth_maxPriorityFeePerGas")
    suspend fun getMaxPriorityFeePerGas(): Result<Ether>

    @JsonRpc("eth_feeHistory")
    suspend fun getFeeHistory(
        @JsonRpcParam("transactionHash") blockCount: BlockCount,
        @JsonRpcParam("newestBlock") blockTag: BlockTag,
        @JsonRpcParam("rewardPercentiles") rewardPercentiles: List<Percentile>? = null,
    ): Result<FeeHistory>

    // Polygon specific
    @JsonRpc("eth_getSignersAtHash")
    suspend fun getSignersAtHash(@JsonRpcParam("blockHash") blockHash: HexString): Result<HexString>

    @JsonRpc("bor_getAuthor")
    suspend fun getAuthor(): Result<HexString>

    @JsonRpc("bor_getCurrentProposer")
    suspend fun getCurrentProposer(): Result<HexString>

    @JsonRpc("bor_getRootHash")
    suspend fun getRootHash(
        @JsonRpcParam("from", useRawValue = true) from: Int,
        @JsonRpcParam("from", useRawValue = true) to: Int
    ): Result<HexString>

    @JsonRpc("bor_getCurrentValidators")
    suspend fun getCurrentValidators(): Result<List<Validator>>

}