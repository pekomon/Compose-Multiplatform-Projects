package com.example.pekomon.minesweeper.game

import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyConfigTest {
    @Test
    fun difficultyDimensionsAndMinesMatchRules() {
        val expected = mapOf(
            Difficulty.EASY to Triple(9, 9, 10),
            Difficulty.MEDIUM to Triple(16, 16, 40),
            Difficulty.HARD to Triple(30, 16, 99),
        )

        expected.forEach { (difficulty, spec) ->
            val (width, height, mines) = spec
            assertEquals(width, difficulty.width, "${difficulty.name} width")
            assertEquals(height, difficulty.height, "${difficulty.name} height")
            assertEquals(mines, difficulty.mines, "${difficulty.name} mines")
        }
    }

    @Test
    fun mineDensityStaysWithinClassicRatios() {
        val classicDensity = mapOf(
            Difficulty.EASY to 10.0 / 81.0,
            Difficulty.MEDIUM to 40.0 / 256.0,
            Difficulty.HARD to 99.0 / 480.0,
        )

        classicDensity.forEach { (difficulty, expectedDensity) ->
            val totalCells = difficulty.width * difficulty.height
            val density = difficulty.mines.toDouble() / totalCells
            val roundedPercent = (density * 100).roundToInt()
            val expectedPercent = (expectedDensity * 100).roundToInt()
            assertEquals(expectedPercent, roundedPercent, "${difficulty.name} density")
        }
    }
}
