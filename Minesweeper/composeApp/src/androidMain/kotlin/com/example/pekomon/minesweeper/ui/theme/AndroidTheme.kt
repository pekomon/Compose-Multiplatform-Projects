package com.example.pekomon.minesweeper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun AndroidMinesweeperTheme(content: @Composable () -> Unit) {
    val useDarkTheme = isSystemInDarkTheme()
    MinesweeperTheme(useDarkTheme = useDarkTheme, content = content)
}
