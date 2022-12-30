package com.alchemy.sdk.nft.explorer.ui.nft

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alchemy.sdk.Alchemy
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.nft.explorer.R
import com.alchemy.sdk.nft.model.GetNftsForContractOptions
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.util.HexString
import com.alchemy.sdk.util.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NftExplorerViewModel @Inject constructor(
    private val alchemy: Alchemy,
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
        viewModelScope.launch(Dispatchers.IO) {
            nextToken = null
            loadingState.emit(true)
            addressState.emit(contractAddress)
            val nftResult =
                alchemy.nft.getNftsForContract(Address.from(contractAddress))
            if (nftResult.isFailure) {
                errorMessageState.emit(nftResult.exceptionOrNull()!!.message)
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
        viewModelScope.launch(Dispatchers.IO) {
            val nftResult = alchemy.nft.getNftsForContract(
                Address.from(addressState.value),
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