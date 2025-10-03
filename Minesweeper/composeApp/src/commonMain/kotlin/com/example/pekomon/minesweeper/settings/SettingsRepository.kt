package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty

/**
 * Stores small app preferences. Currently only selected difficulty.
 * Synchronous and simple by design for this app.
 */
interface SettingsRepository {
    fun getSelectedDifficulty(): Difficulty? // null = not set

    fun setSelectedDifficulty(value: Difficulty)

    fun isReducedMotionEnabled(): Boolean

    fun setReducedMotionEnabled(enabled: Boolean)
}

object SettingsKeys {
    const val SELECTED_DIFFICULTY = "selected_difficulty"
    const val REDUCED_MOTION_ENABLED = "reduced_motion_enabled"
}

/** Expect a platform-specific provider so common UI can obtain the repo. */
expect fun provideSettingsRepository(): SettingsRepository
