package com.alchemy.sdk.nft.explorer.ui.nft

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alchemy.sdk.core.model.core.Address
import com.alchemy.sdk.core.model.nft.GetNftsForContractOptions
import com.alchemy.sdk.core.model.nft.Nft
import com.alchemy.sdk.core.util.HexString
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.alchemy.sdk.nft.explorer.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.alchemy.sdk.core.Nft as NftService

@HiltViewModel
class NftExplorerViewModel @Inject constructor(
    private val nftService: NftService,
    application: Application
) : AndroidViewModel(application) {
    private val addressState = MutableStateFlow("")
    private val nftsState = MutableStateFlow(emptyList<Nft>())
    private val loadingState = MutableStateFlow(false)
    private val errorMessageState = MutableStateFlow<String?>(null)

    private var nextToken: HexString? = null

    val state: StateFlow<NftExplorerState> = combine(
        addressState,
        nftsState,
        errorMessageState,
        loadingState,
        ::NftExplorerState
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NftExplorerState()
        )

    fun onContractAddressEntered(contractAddress: String) {
        viewModelScope.launch {
            nextToken = null
            loadingState.emit(true)
            addressState.emit(contractAddress)
            val nftResult =
                nftService.getNftsForContract(Address.ContractAddress(contractAddress.hexString))
            if (nftResult.isFailure) {
                errorMessageState.emit(getApplication<Application>().getString(R.string.nfts_loading_failed))
            } else {
                val nftContractNftsResponse = nftResult.getOrThrow()
                nextToken = nftContractNftsResponse.nextToken
                nftsState.emit(nftContractNftsResponse.nfts)
            }
            loadingState.emit(false)
        }
    }

    fun fetchMoreItems() {
        if (nextToken == null) return
        viewModelScope.launch {
            val nftResult = nftService.getNftsForContract(
                Address.ContractAddress(addressState.value.hexString),
                GetNftsForContractOptions(
                    startToken = nextToken
                )
            )
            if (nftResult.isFailure) {
                errorMessageState.emit(getApplication<Application>().getString(R.string.nfts_loading_failed))
            } else {
                nftsState.emit(nftsState.value + nftResult.getOrThrow().nfts)
            }
        }
    }
}