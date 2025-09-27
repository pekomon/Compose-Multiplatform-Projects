@file:Suppress("ktlint:standard:filename")
package com.example.pekomon.minesweeper

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.pekomon.minesweeper.MinesweeperContent
import com.example.pekomon.minesweeper.ui.theme.MinesweeperTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Minesweeper",
    ) {
        MinesweeperTheme(useDarkTheme = false) {
            MinesweeperContent()
        }
    }
}
