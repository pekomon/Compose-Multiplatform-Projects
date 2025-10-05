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

    override fun isSoundEnabled(): Boolean = preferences.getBoolean(SettingsKeys.ENABLE_SOUNDS, true)

    override fun setSoundEnabled(enabled: Boolean) {
        preferences.putBoolean(SettingsKeys.ENABLE_SOUNDS, enabled)
    }

    override fun isAnimationEnabled(): Boolean = preferences.getBoolean(SettingsKeys.ENABLE_ANIMATIONS, true)

    override fun setAnimationEnabled(enabled: Boolean) {
        preferences.putBoolean(SettingsKeys.ENABLE_ANIMATIONS, enabled)
    }
}

private val repository: SettingsRepository by lazy {
    val node = Preferences.userRoot().node("minesweeper")
    JvmSettingsRepository(node)
}

actual fun provideSettingsRepository(): SettingsRepository = repository
