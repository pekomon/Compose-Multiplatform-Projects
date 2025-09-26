package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty
import com.russhwolf.settings.Settings

/**
 * Stores small user preferences (KMP).
 * Keep keys stable for future migrations.
 */
class SettingsRepository(
    private val settings: Settings = Settings(),
) {
    private companion object {
        const val KEY_DIFFICULTY = "difficulty"
    }

    fun getDifficultyOrDefault(default: Difficulty = Difficulty.EASY): Difficulty {
        val name = settings.getStringOrNull(KEY_DIFFICULTY) ?: return default
        return runCatching { Difficulty.valueOf(name) }.getOrDefault(default)
    }

    fun setDifficulty(value: Difficulty) {
        settings.putString(KEY_DIFFICULTY, value.name)
    }
}
