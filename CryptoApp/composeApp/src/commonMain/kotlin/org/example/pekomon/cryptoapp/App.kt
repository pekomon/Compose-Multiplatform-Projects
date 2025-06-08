package org.example.pekomon.cryptoapp

import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.example.pekomon.cryptoapp.coins.presentation.CoinsListScreen
import org.example.pekomon.cryptoapp.theme.CryptoAppTheme

@Composable
@Preview
fun App() {
    CryptoAppTheme {
        CoinsListScreen(
            onCoinClicked = {}
        )
    }
}