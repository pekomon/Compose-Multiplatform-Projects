package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.serialization.Serializable

@Serializable
data class RunRecord(
    val difficulty: Difficulty,
    val millis: Long,
    val epochMillis: Long,
)
