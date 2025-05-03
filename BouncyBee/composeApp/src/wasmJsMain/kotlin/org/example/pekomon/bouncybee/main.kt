package org.example.pekomon.bouncybee

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.example.pekomon.bouncybee.di.initializeKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()
    ComposeViewport(document.body!!) {
        App()
    }
}