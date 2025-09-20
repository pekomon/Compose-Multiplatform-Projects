package com.example.pekomon.minesweeper

import androidx.compose.runtime.Composable
import com.example.pekomon.minesweeper.ui.GameScreen
import com.example.pekomon.minesweeper.ui.theme.MinesweeperTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MinesweeperTheme(useDarkTheme = false) {
        GameScreen()
    }
}
