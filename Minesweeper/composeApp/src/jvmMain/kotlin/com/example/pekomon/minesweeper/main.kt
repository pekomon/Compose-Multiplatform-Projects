@file:Suppress("ktlint:standard:filename")
package com.example.pekomon.minesweeper

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.pekomon.minesweeper.ui.GameScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Minesweeper",
    ) {
        GameScreen()
    }
}
