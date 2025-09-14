@file:Suppress("ktlint:standard:filename")
package com.example.pekomon.minesweeper

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Minesweeper",
    ) {
        App()
    }
}
