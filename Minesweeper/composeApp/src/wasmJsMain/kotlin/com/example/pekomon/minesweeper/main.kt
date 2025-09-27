package com.example.pekomon.minesweeper

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val loading = document.getElementById("loading")
    ComposeViewport(document.body!!) {
        LaunchedEffect(Unit) {
            loading?.parentNode?.removeChild(loading)
        }
        App()
    }
}
