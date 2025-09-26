package com.example.pekomon.minesweeper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pekomon.minesweeper.ui.GameScreen
import com.example.pekomon.minesweeper.ui.theme.MinesweeperTheme
import com.example.pekomon.minesweeper.settings.SettingsRepository
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val settingsRepository = remember { SettingsRepository() }
    App(settingsRepository = settingsRepository)
}

@Composable
fun App(settingsRepository: SettingsRepository) {
    var difficulty by remember { mutableStateOf(settingsRepository.getDifficultyOrDefault()) }

    MinesweeperTheme(useDarkTheme = false) {
        GameScreen(
            difficulty = difficulty,
            onDifficultyChange = { newDifficulty ->
                settingsRepository.setDifficulty(newDifficulty)
                difficulty = newDifficulty
            },
        )
    }
}
