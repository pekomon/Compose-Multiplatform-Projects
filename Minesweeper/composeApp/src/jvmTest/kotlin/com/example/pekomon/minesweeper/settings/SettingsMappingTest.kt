package com.example.pekomon.minesweeper.settings

import com.example.pekomon.minesweeper.game.Difficulty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingsMappingTest {
    @Test
    fun `difficulty enum names map back to enum`() {
        Difficulty.entries.forEach { difficulty ->
            val stored = difficulty.name
            val restored = runCatching { Difficulty.valueOf(stored) }.getOrNull()
            assertEquals(difficulty, restored)
        }
    }

    @Test
    fun `invalid difficulty string returns null`() {
        val restored = runCatching { Difficulty.valueOf("invalid") }.getOrNull()
        assertNull(restored)
    }
}
