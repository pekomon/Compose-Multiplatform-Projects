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

    override fun isReducedMotionEnabled(): Boolean =
        storage.getItem(SettingsKeys.REDUCED_MOTION_ENABLED)?.toBoolean() ?: false

    override fun setReducedMotionEnabled(enabled: Boolean) {
        storage.setItem(SettingsKeys.REDUCED_MOTION_ENABLED, enabled.toString())
    }
}

private val repository: SettingsRepository by lazy { WasmSettingsRepository() }

actual fun provideSettingsRepository(): SettingsRepository = repository
