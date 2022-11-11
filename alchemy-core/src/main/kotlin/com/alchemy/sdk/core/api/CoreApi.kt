package com.alchemy.sdk.core.api

import com.alchemy.sdk.annotations.JsonRpc
import com.alchemy.sdk.annotations.JsonRpcService
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.Block
import com.alchemy.sdk.core.model.BlockCount
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.core.model.CancelPrivateTransactionRequest
import com.alchemy.sdk.core.model.FeeHistory
import com.alchemy.sdk.core.model.Index
import com.alchemy.sdk.core.model.Log
import com.alchemy.sdk.core.model.LogFilter
import com.alchemy.sdk.core.model.Percentile
import com.alchemy.sdk.core.model.PrivateTransactionCall
import com.alchemy.sdk.core.model.Proof
import com.alchemy.sdk.core.model.RawInt
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.core.model.TransactionReceipt
import com.alchemy.sdk.core.model.UncleBlock
import com.alchemy.sdk.core.model.Validator
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString

@JsonRpcService
interface CoreApi {

    @JsonRpc("eth_getBalance")
    suspend fun getBalance(address: Address, blockTag: BlockTag = BlockTag.Latest): Result<Ether>

    @JsonRpc("eth_getCode")
    suspend fun getCode(address: Address, blockTag: BlockTag = BlockTag.Latest): Result<HexString>

    @JsonRpc("eth_getStorageAt")
    suspend fun getStorageAt(
        address: Address,
        index: Index,
        blockTag: BlockTag = BlockTag.Latest
    ): Result<HexString>

    @JsonRpc("eth_getProof")
    suspend fun getProof(
        address: Address,
        keys: List<HexString>,
        blockTag: BlockTag = BlockTag.Latest
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
    suspend fun getWeb3Sha3(data: HexString): Result<HexString>

    @JsonRpc("eth_blockNumber")
    suspend fun getBlockNumber(): Result<HexString>

    @JsonRpc("eth_getBlockByNumber")
    suspend fun getBlockByNumber(
        blockTag: BlockTag,
        fullTransactions: Boolean = false
    ): Result<Block>

    @JsonRpc("eth_getBlockByHash")
    suspend fun getBlockByHash(
        blockHash: HexString,
        fullTransactions: Boolean = false
    ): Result<Block>

    @JsonRpc("eth_getBlockTransactionCountByNumber")
    suspend fun getBlockTransactionCountByNumber(blockTag: BlockTag): Result<HexString>

    @JsonRpc("eth_getBlockTransactionCountByHash")
    suspend fun getBlockTransactionCountByHash(blockHash: HexString): Result<HexString>

    @JsonRpc("eth_getUncleByBlockNumberAndIndex")
    suspend fun getUncleByBlockNumberAndIndex(blockTag: BlockTag, index: Index): Result<UncleBlock>

    @JsonRpc("eth_getUncleByBlockHashAndIndex")
    suspend fun getUncleByBlockHashAndIndex(blockHash: HexString, index: Index): Result<UncleBlock>

    @JsonRpc("eth_getUncleCountByBlockNumber")
    suspend fun getUncleCountByBlockNumber(blockTag: BlockTag): Result<HexString>

    @JsonRpc("eth_getUncleCountByBlockHash")
    suspend fun getUncleCountByBlockHash(
        blockHash: HexString
    ): Result<HexString>

    @JsonRpc("eth_getTransactionByBlockNumberAndIndex")
    suspend fun getTransactionByBlockNumberAndIndex(
        blockTag: BlockTag,
        index: Index,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionByBlockHashAndIndex")
    suspend fun getTransactionByBlockHashAndIndex(
        blockHash: HexString,
        index: Index,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionCount")
    suspend fun getTransactionCount(
        address: Address,
        blockTag: BlockTag = BlockTag.Latest,
    ): Result<HexString>

    @JsonRpc("eth_getTransactionByHash")
    suspend fun getTransactionByHash(
        transactionHash: HexString,
    ): Result<BlockTransaction>

    @JsonRpc("eth_getTransactionReceipt")
    suspend fun getTransactionReceipt(
        transactionHash: HexString,
    ): Result<TransactionReceipt?>

    @JsonRpc("eth_call")
    suspend fun call(
        transactionCall: TransactionCall,
        blockTag: BlockTag = BlockTag.Latest
    ): Result<HexString>

    @JsonRpc("eth_sendRawTransaction")
    suspend fun sendRawTransaction(signedTransaction: HexString): Result<HexString>

    @JsonRpc("eth_sendPrivateTransaction")
    suspend fun sendPrivateTransaction(privateTransactionCall: PrivateTransactionCall): Result<HexString>

    @JsonRpc("eth_cancelPrivateTransaction")
    suspend fun cancelPrivateTransaction(
        cancelPrivateTransactionRequest: CancelPrivateTransactionRequest
    ): Result<Boolean>

    @JsonRpc("eth_estimateGas")
    suspend fun estimateGas(transactionCall: TransactionCall): Result<HexString>

    @JsonRpc("eth_estimateGas")
    suspend fun estimateGas(blockTag: BlockTag): Result<HexString>

    @JsonRpc("eth_gasPrice")
    suspend fun getGasPrice(): Result<Ether>

    @JsonRpc("eth_maxPriorityFeePerGas")
    suspend fun getMaxPriorityFeePerGas(): Result<Ether>

    @JsonRpc("eth_feeHistory")
    suspend fun getFeeHistory(
        blockCount: BlockCount,
        blockTag: BlockTag,
        rewardPercentiles: List<Percentile>? = null,
    ): Result<FeeHistory>

    @JsonRpc("eth_getLogs")
    suspend fun getLogs(filter: LogFilter): Result<List<Log>>

    @JsonRpc("eth_newFilter")
    suspend fun newFilter(filter: LogFilter): Result<HexString>

    @JsonRpc("eth_newPendingTransactionFilter")
    suspend fun newPendingTransactionFilter(): Result<HexString>

    @JsonRpc("eth_newBlockFilter")
    suspend fun newBlockFilter(): Result<HexString>

    @JsonRpc("eth_getFilterChanges")
    suspend fun getFilterChanges(filterId: HexString): Result<List<HexString>>

    @JsonRpc("eth_getFilterLogs")
    suspend fun getFilterLogs(): Result<List<HexString>>

    @JsonRpc("eth_uninstallFilter")
    suspend fun uninstallFilter(filterId: HexString): Result<Boolean>

    // Polygon specific
    @JsonRpc("eth_getSignersAtHash")
    suspend fun getSignersAtHash(blockHash: HexString): Result<HexString>

    @JsonRpc("bor_getAuthor")
    suspend fun getAuthor(): Result<HexString>

    @JsonRpc("bor_getCurrentProposer")
    suspend fun getCurrentProposer(): Result<HexString>

    @JsonRpc("bor_getRootHash")
    suspend fun getRootHash(from: RawInt, to: RawInt): Result<HexString>

    @JsonRpc("bor_getCurrentValidators")
    suspend fun getCurrentValidators(): Result<List<Validator>>

}