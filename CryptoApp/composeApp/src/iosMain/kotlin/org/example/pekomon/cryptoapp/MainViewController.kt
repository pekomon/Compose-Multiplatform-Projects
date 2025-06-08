package org.example.pekomon.cryptoapp

import androidx.compose.ui.window.ComposeUIViewController
import org.example.pekomon.cryptoapp.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }