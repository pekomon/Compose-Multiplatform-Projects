package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty
import platform.Foundation.NSUserDefaults

private class IosSettingsRepository : SettingsRepository {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getSelectedDifficulty(): Difficulty? {
        val stored = defaults.stringForKey(SettingsKeys.SELECTED_DIFFICULTY) ?: return null
        return runCatching { Difficulty.valueOf(stored) }.getOrNull()
    }

    override fun setSelectedDifficulty(value: Difficulty) {
        defaults.setObject(value.name, forKey = SettingsKeys.SELECTED_DIFFICULTY)
    }
}

private val repository: SettingsRepository by lazy { IosSettingsRepository() }

actual fun provideSettingsRepository(): SettingsRepository = repository
