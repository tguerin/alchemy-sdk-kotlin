package com.alchemy.sdk.tipsmydev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.alchemy.sdk.tipsmydev.ui.home.Home
import com.alchemy.sdk.tipsmydev.ui.theme.TipsMyDevTheme
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
        TipsMyDevTheme {
            Home()
        }
    }

}