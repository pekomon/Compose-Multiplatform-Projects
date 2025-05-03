package org.example.pekomon.bouncybee

import androidx.compose.ui.window.ComposeUIViewController
import org.example.pekomon.bouncybee.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }