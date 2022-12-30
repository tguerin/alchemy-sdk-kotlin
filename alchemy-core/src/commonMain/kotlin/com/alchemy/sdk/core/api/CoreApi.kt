package com.alchemy.sdk.core.api

import com.alchemy.sdk.annotations.JsonRpc
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
import com.alchemy.sdk.core.model.TransactionCall
import com.alchemy.sdk.core.model.TransactionReceipt
import com.alchemy.sdk.core.model.UncleBlock
import com.alchemy.sdk.core.model.Validator
import com.alchemy.sdk.util.Ether
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.SdkResult

interface CoreApi {

    @JsonRpc("eth_getBalance")
    suspend fun getBalance(address: Address, blockTag: BlockTag = BlockTag.Latest): SdkResult<Ether>

    @JsonRpc("eth_getCode")
    suspend fun getCode(address: Address, blockTag: BlockTag = BlockTag.Latest): SdkResult<HexString>

    @JsonRpc("eth_getStorageAt")
    suspend fun getStorageAt(
        address: Address,
        index: Index,
        blockTag: BlockTag = BlockTag.Latest
    ): SdkResult<HexString>

    @JsonRpc("eth_getProof")
    suspend fun getProof(
        address: Address,
        keys: List<HexString>,
        blockTag: BlockTag = BlockTag.Latest
    ): SdkResult<Proof>

    @JsonRpc("eth_protocolVersion")
    suspend fun getProtocolVersion(): SdkResult<HexString>

    @JsonRpc("eth_chainId")
    suspend fun getChainId(): SdkResult<HexString>

    @JsonRpc("net_listening")
    suspend fun getNetListening(): SdkResult<Boolean>

    @JsonRpc("net_version")
    suspend fun getNetVersion(): SdkResult<String>

    @JsonRpc("web3_clientVersion")
    suspend fun getWeb3ClientVersion(): SdkResult<String>

    @JsonRpc("web3_sha3")
    suspend fun getWeb3Sha3(data: HexString): SdkResult<HexString>

    @JsonRpc("eth_blockNumber")
    suspend fun getBlockNumber(): SdkResult<HexString>

    @JsonRpc("eth_getBlockByNumber")
    suspend fun getBlockByNumber(
        blockTag: BlockTag,
        fullTransactions: Boolean = false
    ): SdkResult<Block>

    @JsonRpc("eth_getBlockByHash")
    suspend fun getBlockByHash(
        blockHash: HexString,
        fullTransactions: Boolean = false
    ): SdkResult<Block>

    @JsonRpc("eth_getBlockTransactionCountByNumber")
    suspend fun getBlockTransactionCountByNumber(blockTag: BlockTag): SdkResult<HexString>

    @JsonRpc("eth_getBlockTransactionCountByHash")
    suspend fun getBlockTransactionCountByHash(blockHash: HexString): SdkResult<HexString>

    @JsonRpc("eth_getUncleByBlockNumberAndIndex")
    suspend fun getUncleByBlockNumberAndIndex(blockTag: BlockTag, index: Index): SdkResult<UncleBlock>

    @JsonRpc("eth_getUncleByBlockHashAndIndex")
    suspend fun getUncleByBlockHashAndIndex(blockHash: HexString, index: Index): SdkResult<UncleBlock>

    @JsonRpc("eth_getUncleCountByBlockNumber")
    suspend fun getUncleCountByBlockNumber(blockTag: BlockTag): SdkResult<HexString>

    @JsonRpc("eth_getUncleCountByBlockHash")
    suspend fun getUncleCountByBlockHash(
        blockHash: HexString
    ): SdkResult<HexString>

    @JsonRpc("eth_getTransactionByBlockNumberAndIndex")
    suspend fun getTransactionByBlockNumberAndIndex(
        blockTag: BlockTag,
        index: Index,
    ): SdkResult<BlockTransaction>

    @JsonRpc("eth_getTransactionByBlockHashAndIndex")
    suspend fun getTransactionByBlockHashAndIndex(
        blockHash: HexString,
        index: Index,
    ): SdkResult<BlockTransaction>

    @JsonRpc("eth_getTransactionCount")
    suspend fun getTransactionCount(
        address: Address,
        blockTag: BlockTag = BlockTag.Latest,
    ): SdkResult<HexString>

    @JsonRpc("eth_getTransactionByHash")
    suspend fun getTransactionByHash(
        transactionHash: HexString,
    ): SdkResult<BlockTransaction>

    @JsonRpc("eth_getTransactionReceipt")
    suspend fun getTransactionReceipt(
        transactionHash: HexString,
    ): SdkResult<TransactionReceipt?>

    @JsonRpc("eth_call")
    suspend fun call(
        transactionCall: TransactionCall,
        blockTag: BlockTag = BlockTag.Latest
    ): SdkResult<HexString>

    @JsonRpc("eth_sendRawTransaction")
    suspend fun sendRawTransaction(signedTransaction: HexString): SdkResult<HexString>

    @JsonRpc("eth_sendPrivateTransaction")
    suspend fun sendPrivateTransaction(privateTransactionCall: PrivateTransactionCall): SdkResult<HexString>

    @JsonRpc("eth_cancelPrivateTransaction")
    suspend fun cancelPrivateTransaction(
        cancelPrivateTransactionRequest: CancelPrivateTransactionRequest
    ): SdkResult<Boolean>

    @JsonRpc("eth_estimateGas")
    suspend fun estimateGas(transactionCall: TransactionCall): SdkResult<HexString>

    @JsonRpc("eth_estimateGas")
    suspend fun estimateGas(blockTag: BlockTag): SdkResult<HexString>

    @JsonRpc("eth_gasPrice")
    suspend fun getGasPrice(): SdkResult<Ether>

    @JsonRpc("eth_maxPriorityFeePerGas")
    suspend fun getMaxPriorityFeePerGas(): SdkResult<Ether>

    @JsonRpc("eth_feeHistory")
    suspend fun getFeeHistory(
        blockCount: BlockCount,
        blockTag: BlockTag,
        rewardPercentiles: List<Percentile>? = emptyList(),
    ): SdkResult<FeeHistory>

    @JsonRpc("eth_getLogs")
    suspend fun getLogs(filter: LogFilter): SdkResult<List<Log>>

    @JsonRpc("eth_newFilter")
    suspend fun newFilter(filter: LogFilter): SdkResult<HexString>

    @JsonRpc("eth_newPendingTransactionFilter")
    suspend fun newPendingTransactionFilter(): SdkResult<HexString>

    @JsonRpc("eth_newBlockFilter")
    suspend fun newBlockFilter(): SdkResult<HexString>

    @JsonRpc("eth_getFilterChanges")
    suspend fun getFilterChanges(filterId: HexString): SdkResult<List<HexString>>

    @JsonRpc("eth_getFilterLogs")
    suspend fun getFilterLogs(): SdkResult<List<HexString>>

    @JsonRpc("eth_uninstallFilter")
    suspend fun uninstallFilter(filterId: HexString): SdkResult<Boolean>

    // Polygon specific
    @JsonRpc("eth_getSignersAtHash")
    suspend fun getSignersAtHash(blockHash: HexString): SdkResult<HexString>

    @JsonRpc("bor_getAuthor")
    suspend fun getAuthor(): SdkResult<HexString>

    @JsonRpc("bor_getCurrentProposer")
    suspend fun getCurrentProposer(): SdkResult<HexString>

    @JsonRpc("bor_getRootHash")
    suspend fun getRootHash(from: Int, to: Int): SdkResult<HexString>

    @JsonRpc("bor_getCurrentValidators")
    suspend fun getCurrentValidators(): SdkResult<List<Validator>>

    suspend fun resolveAddress(address: Address): SdkResult<Address> = error("Not implemented")

}