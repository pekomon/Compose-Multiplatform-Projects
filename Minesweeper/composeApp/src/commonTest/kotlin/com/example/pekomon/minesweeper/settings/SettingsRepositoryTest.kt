package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRepositoryTest {
    @Test
    fun returnsDefaultWhenEmpty() {
        val repository = SettingsRepository(settings = MapSettings())

        assertEquals(Difficulty.EASY, repository.getDifficultyOrDefault())
    }

    @Test
    fun persistsAndReadsDifficulty() {
        val mapSettings = MapSettings()
        val repository = SettingsRepository(settings = mapSettings)

        repository.setDifficulty(Difficulty.HARD)

        assertEquals(Difficulty.HARD, repository.getDifficultyOrDefault())
    }

    @Test
    fun fallsBackWhenStoredValueUnknown() {
        val mapSettings = MapSettings()
        mapSettings.putString("difficulty", "UNKNOWN")
        val repository = SettingsRepository(settings = mapSettings)

        assertEquals(Difficulty.EASY, repository.getDifficultyOrDefault())
    }
}
