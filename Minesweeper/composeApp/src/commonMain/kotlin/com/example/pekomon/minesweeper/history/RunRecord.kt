package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty

data class RunRecord(
    val difficulty: Difficulty,
    val elapsedMillis: Long,
    val epochMillis: Long,
)
