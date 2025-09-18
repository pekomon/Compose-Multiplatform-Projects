@file:Suppress("FunctionNaming", "ktlint:standard:function-naming")

package com.example.pekomon.minesweeper

import androidx.compose.ui.window.ComposeUIViewController
import com.example.pekomon.minesweeper.ui.GameScreen
import com.example.pekomon.minesweeper.ui.theme.MinesweeperTheme

fun MainViewController() = ComposeUIViewController {
    MinesweeperTheme(useDarkTheme = false) {
        GameScreen()
    }
}
