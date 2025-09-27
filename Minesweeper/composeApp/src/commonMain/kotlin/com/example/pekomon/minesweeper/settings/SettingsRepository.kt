package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty

/**
 * Stores small app preferences. Currently only selected difficulty.
 * Synchronous and simple by design for this app.
 */
interface SettingsRepository {
    fun getSelectedDifficulty(): Difficulty? // null = not set
    fun setSelectedDifficulty(value: Difficulty)
}

object SettingsKeys {
    const val SELECTED_DIFFICULTY = "selected_difficulty"
}

/** Expect a platform-specific provider so common UI can obtain the repo. */
expect fun provideSettingsRepository(): SettingsRepository
