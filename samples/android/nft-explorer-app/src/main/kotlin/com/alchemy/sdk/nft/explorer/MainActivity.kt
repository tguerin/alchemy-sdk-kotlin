package com.alchemy.sdk.nft.explorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.alchemy.sdk.nft.explorer.ui.nft.NftExplorer
import com.alchemy.sdk.nft.explorer.ui.theme.NftExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreen() {
        NftExplorerTheme {
            NftExplorer()
        }
    }

}