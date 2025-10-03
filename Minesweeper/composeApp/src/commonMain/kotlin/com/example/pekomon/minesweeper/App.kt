package com.example.pekomon.minesweeper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.history.HistoryStore
import com.example.pekomon.minesweeper.history.InMemoryHistoryStore
import com.example.pekomon.minesweeper.history.provideHistoryStore
import com.example.pekomon.minesweeper.settings.SettingsRepository
import com.example.pekomon.minesweeper.settings.provideSettingsRepository
import com.example.pekomon.minesweeper.ui.GameScreen
import com.example.pekomon.minesweeper.ui.theme.MinesweeperTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MinesweeperTheme(useDarkTheme = false) {
        MinesweeperContent()
    }
}

@Composable
fun MinesweeperContent() {
    val settingsRepository =
        remember {
            runCatching { provideSettingsRepository() }.getOrElse { InMemorySettingsRepository() }
        }
    val historyStore: HistoryStore =
        remember {
            runCatching { provideHistoryStore() }.getOrElse { InMemoryHistoryStore() }
        }
    var storedDifficulty by remember { mutableStateOf(settingsRepository.getSelectedDifficulty()) }
    val initialDifficulty = storedDifficulty ?: Difficulty.EASY

    GameScreen(
        initialDifficulty = initialDifficulty,
        historyStore = historyStore,
        onDifficultyChanged = {
            storedDifficulty = it
            settingsRepository.setSelectedDifficulty(it)
        },
    )
}

private class InMemorySettingsRepository : SettingsRepository {
    private var difficulty: Difficulty? = null

    override fun getSelectedDifficulty(): Difficulty? = difficulty

    override fun setSelectedDifficulty(value: Difficulty) {
        difficulty = value
    }
}
