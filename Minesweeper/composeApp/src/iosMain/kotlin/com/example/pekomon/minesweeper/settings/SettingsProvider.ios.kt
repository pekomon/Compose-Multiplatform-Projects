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

    override fun isSoundEnabled(): Boolean {
        val value = defaults.objectForKey(SettingsKeys.ENABLE_SOUNDS) as? Boolean
        return value ?: true
    }

    override fun setSoundEnabled(enabled: Boolean) {
        defaults.setBool(enabled, forKey = SettingsKeys.ENABLE_SOUNDS)
    }

    override fun isAnimationEnabled(): Boolean {
        val value = defaults.objectForKey(SettingsKeys.ENABLE_ANIMATIONS) as? Boolean
        return value ?: true
    }

    override fun setAnimationEnabled(enabled: Boolean) {
        defaults.setBool(enabled, forKey = SettingsKeys.ENABLE_ANIMATIONS)
    }
}

private val repository: SettingsRepository by lazy { IosSettingsRepository() }

actual fun provideSettingsRepository(): SettingsRepository = repository
