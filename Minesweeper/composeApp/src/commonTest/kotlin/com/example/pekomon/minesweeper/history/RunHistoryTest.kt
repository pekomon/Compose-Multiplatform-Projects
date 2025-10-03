package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RunHistoryTest {
    @Test
    fun `returns true when history empty`() {
        val isRecord = isNewRecord(currentMillis = 1_000L, difficulty = Difficulty.EASY, history = emptyList())

        assertTrue(isRecord)
    }

    @Test
    fun `returns true when faster than best`() {
        val history =
            listOf(
                RunRecord(difficulty = Difficulty.EASY, millis = 5_000L, epochMillis = 1L),
                RunRecord(difficulty = Difficulty.EASY, millis = 6_000L, epochMillis = 2L),
            )

        val isRecord = isNewRecord(currentMillis = 4_000L, difficulty = Difficulty.EASY, history = history)

        assertTrue(isRecord)
    }

    @Test
    fun `returns false when slower than best`() {
        val history =
            listOf(
                RunRecord(difficulty = Difficulty.EASY, millis = 3_000L, epochMillis = 1L),
                RunRecord(difficulty = Difficulty.EASY, millis = 4_000L, epochMillis = 2L),
            )

        val isRecord = isNewRecord(currentMillis = 5_000L, difficulty = Difficulty.EASY, history = history)

        assertFalse(isRecord)
    }

    @Test
    fun `returns false when time matches best`() {
        val history =
            listOf(
                RunRecord(difficulty = Difficulty.EASY, millis = 3_000L, epochMillis = 1L),
                RunRecord(difficulty = Difficulty.EASY, millis = 4_000L, epochMillis = 2L),
            )

        val isRecord = isNewRecord(currentMillis = 3_000L, difficulty = Difficulty.EASY, history = history)

        assertFalse(isRecord)
    }

    @Test
    fun `ignores runs from other difficulties`() {
        val history =
            listOf(
                RunRecord(difficulty = Difficulty.MEDIUM, millis = 2_000L, epochMillis = 1L),
                RunRecord(difficulty = Difficulty.HARD, millis = 1_500L, epochMillis = 2L),
            )

        val isRecord = isNewRecord(currentMillis = 4_000L, difficulty = Difficulty.EASY, history = history)

        assertTrue(isRecord)
    }

    @Test
    fun `returns false for negative durations`() {
        val history = listOf(RunRecord(difficulty = Difficulty.EASY, millis = 2_000L, epochMillis = 1L))

        val isRecord = isNewRecord(currentMillis = -100L, difficulty = Difficulty.EASY, history = history)

        assertFalse(isRecord)
    }
}
