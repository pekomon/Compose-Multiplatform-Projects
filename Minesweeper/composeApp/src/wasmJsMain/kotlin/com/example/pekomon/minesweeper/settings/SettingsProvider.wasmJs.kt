package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.browser.window

private class WasmSettingsRepository : SettingsRepository {
    private val storage = window.localStorage

    override fun getSelectedDifficulty(): Difficulty? {
        val stored = storage.getItem(SettingsKeys.SELECTED_DIFFICULTY) ?: return null
        return runCatching { Difficulty.valueOf(stored) }.getOrNull()
    }

    override fun setSelectedDifficulty(value: Difficulty) {
        storage.setItem(SettingsKeys.SELECTED_DIFFICULTY, value.name)
    }
}

private val repository: SettingsRepository by lazy { WasmSettingsRepository() }

actual fun provideSettingsRepository(): SettingsRepository = repository
