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

    override fun isSoundEnabled(): Boolean =
        storage
            .getItem(SettingsKeys.ENABLE_SOUNDS)
            ?.toBoolean()
            ?: true

    override fun setSoundEnabled(enabled: Boolean) {
        storage.setItem(SettingsKeys.ENABLE_SOUNDS, enabled.toString())
    }

    override fun isAnimationEnabled(): Boolean =
        storage
            .getItem(SettingsKeys.ENABLE_ANIMATIONS)
            ?.toBoolean()
            ?: true

    override fun setAnimationEnabled(enabled: Boolean) {
        storage.setItem(SettingsKeys.ENABLE_ANIMATIONS, enabled.toString())
    }
}

private val repository: SettingsRepository by lazy { WasmSettingsRepository() }

actual fun provideSettingsRepository(): SettingsRepository = repository
