package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty

/**
 * Stores small app preferences. Currently only selected difficulty.
 * Synchronous and simple by design for this app.
 */
interface SettingsRepository {
    fun getSelectedDifficulty(): Difficulty? // null = not set

    fun setSelectedDifficulty(value: Difficulty)

    fun isSoundEnabled(): Boolean

    fun setSoundEnabled(enabled: Boolean)

    fun isAnimationEnabled(): Boolean

    fun setAnimationEnabled(enabled: Boolean)
}

object SettingsKeys {
    const val SELECTED_DIFFICULTY = "selected_difficulty"
    const val ENABLE_SOUNDS = "enable_sounds"
    const val ENABLE_ANIMATIONS = "enable_animations"
}

/** Expect a platform-specific provider so common UI can obtain the repo. */
expect fun provideSettingsRepository(): SettingsRepository
