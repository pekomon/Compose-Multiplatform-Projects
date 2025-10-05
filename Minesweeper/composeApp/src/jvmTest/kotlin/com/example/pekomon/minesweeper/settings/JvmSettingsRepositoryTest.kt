package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty
import java.util.UUID
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JvmSettingsRepositoryTest {
    private lateinit var preferences: Preferences
    private lateinit var repository: JvmSettingsRepository

    @BeforeTest
    fun setUp() {
        val nodeName = "minesweeper-test-${UUID.randomUUID()}"
        preferences = Preferences.userRoot().node(nodeName)
        repository = JvmSettingsRepository(preferences)
    }

    @AfterTest
    fun tearDown() {
        try {
            preferences.removeNode()
        } catch (_: BackingStoreException) {
            // ignore cleanup failure
        }
    }

    @Test
    fun `round trips difficulty`() {
        assertNull(repository.getSelectedDifficulty())

        repository.setSelectedDifficulty(Difficulty.MEDIUM)

        assertEquals(Difficulty.MEDIUM, repository.getSelectedDifficulty())
    }

    @Test
    fun `round trips sound preference`() {
        assertEquals(true, repository.isSoundEnabled())

        repository.setSoundEnabled(false)

        assertEquals(false, repository.isSoundEnabled())
    }

    @Test
    fun `round trips animation preference`() {
        assertEquals(true, repository.isAnimationEnabled())

        repository.setAnimationEnabled(false)

        assertEquals(false, repository.isAnimationEnabled())
    }
}
