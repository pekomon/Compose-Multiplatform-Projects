package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty
import java.util.prefs.Preferences

internal class JvmSettingsRepository(
    private val preferences: Preferences,
) : SettingsRepository {
    override fun getSelectedDifficulty(): Difficulty? {
        val stored = preferences.get(SettingsKeys.SELECTED_DIFFICULTY, null) ?: return null
        return runCatching { Difficulty.valueOf(stored) }.getOrNull()
    }

    override fun setSelectedDifficulty(value: Difficulty) {
        preferences.put(SettingsKeys.SELECTED_DIFFICULTY, value.name)
    }

    override fun isReducedMotionEnabled(): Boolean = preferences.getBoolean(SettingsKeys.REDUCED_MOTION_ENABLED, false)

    override fun setReducedMotionEnabled(enabled: Boolean) {
        preferences.putBoolean(SettingsKeys.REDUCED_MOTION_ENABLED, enabled)
    }
}

private val repository: SettingsRepository by lazy {
    val node = Preferences.userRoot().node("minesweeper")
    JvmSettingsRepository(node)
}

actual fun provideSettingsRepository(): SettingsRepository = repository
