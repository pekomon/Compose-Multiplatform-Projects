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
    var soundsEnabled by remember { mutableStateOf(settingsRepository.isSoundEnabled()) }
    var animationsEnabled by remember { mutableStateOf(settingsRepository.isAnimationEnabled()) }
    val initialDifficulty = storedDifficulty ?: Difficulty.EASY

    GameScreen(
        initialDifficulty = initialDifficulty,
        historyStore = historyStore,
        soundsEnabled = soundsEnabled,
        animationsEnabled = animationsEnabled,
        onDifficultyChanged = {
            storedDifficulty = it
            settingsRepository.setSelectedDifficulty(it)
        },
        onSoundsEnabledChange = { enabled ->
            soundsEnabled = enabled
            settingsRepository.setSoundEnabled(enabled)
        },
        onAnimationsEnabledChange = { enabled ->
            animationsEnabled = enabled
            settingsRepository.setAnimationEnabled(enabled)
        },
    )
}

private class InMemorySettingsRepository : SettingsRepository {
    private var difficulty: Difficulty? = null
    private var soundEnabled: Boolean = true
    private var animationEnabled: Boolean = true

    override fun getSelectedDifficulty(): Difficulty? = difficulty

    override fun setSelectedDifficulty(value: Difficulty) {
        difficulty = value
    }

    override fun isSoundEnabled(): Boolean = soundEnabled

    override fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    override fun isAnimationEnabled(): Boolean = animationEnabled

    override fun setAnimationEnabled(enabled: Boolean) {
        animationEnabled = enabled
    }
}
