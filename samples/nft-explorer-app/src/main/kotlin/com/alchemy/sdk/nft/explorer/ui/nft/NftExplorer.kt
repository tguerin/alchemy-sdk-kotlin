package com.alchemy.sdk.nft.explorer.ui.nft

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alchemy.sdk.nft.model.Nft
import com.alchemy.sdk.nft.explorer.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun NftExplorer() {
    NftExplorer(viewModel())
}

@Composable
@OptIn(ExperimentalLifecycleComposeApi::class)
internal fun NftExplorer(
    viewModel: NftExplorerViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NftExplorer(
        state = state,
        onContractAddressEntered = { contractAddress ->
            viewModel.onContractAddressEntered(contractAddress)
        },
        fetchMoreItems = {
            viewModel.fetchMoreItems()
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun NftExplorer(
    state: NftExplorerState,
    onContractAddressEntered: (String) -> Unit = {},
    fetchMoreItems: () -> Unit = {}
) {

    val snackbarHostState = remember { SnackbarHostState() }
    state.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                NftExplorerSearch(state, onContractAddressEntered)
                NftExplorerList(state, fetchMoreItems)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NftExplorerSearch(
    state: NftExplorerState,
    onContractAddressEntered: (String) -> Unit
) {
    var address by remember { mutableStateOf(TextFieldValue(state.address)) }
    var buttonWidth by remember { mutableStateOf(0.dp) }
    val focusManager = LocalFocusManager.current
    val localDensity = LocalDensity.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = address,
            label = {
                Text(text = LocalContext.current.getString(R.string.contract_address))
            },
            onValueChange = { newValue ->
                address = newValue
            },
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minWidth = 120.dp)
                .padding(end = 8.dp)
        )
        if (state.loading) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(buttonWidth)
            ) {
                CircularProgressIndicator()
            }
        } else {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onContractAddressEntered(address.text)
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier.onSizeChanged {
                    with(localDensity) {
                        buttonWidth = it.width.toDp()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Text(text = LocalContext.current.getString(R.string.search))
            }
        }
    }
}

@Composable
private fun NftExplorerList(
    state: NftExplorerState,
    fetchMoreItems: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        items(state.ownedNfts) {
            NftExploreItem(nft = it)
        }
    }
    lazyListState.OnBottomReached(buffer = 20) {
        fetchMoreItems()
    }
}

@Composable
fun NftExploreItem(nft: Nft) {
    when (nft) {
        is Nft.AlchemyNft -> {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlideImage(
                        imageModel = nft.media[0].gateway,
                        modifier = Modifier.size(160.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = nft.title.ifBlank {
                                "NFT #${nft.tokenId.intValue() + 1}"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = nft.metadata?.attributes?.joinToString(separator = "\n") {
                                it["trait_type"].toString() + ": " + it["value"]
                            } ?: "no attributes",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        is Nft.BaseNft -> {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = nft.tokenId.toString())
            }
        }
    }
}

@Composable
fun LazyListState.OnBottomReached(
    buffer: Int = 0,
    loadMore: () -> Unit
) {
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

    val shouldLoadMore = remember {
        derivedStateOf {
            // get last visible item
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            // Check if last visible item is the last item in the list
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}