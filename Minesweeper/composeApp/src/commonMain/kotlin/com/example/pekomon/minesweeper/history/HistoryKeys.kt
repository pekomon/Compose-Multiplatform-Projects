package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty

internal object HistoryKeys {
    const val EASY = "history_easy"
    const val MEDIUM = "history_medium"
    const val HARD = "history_hard"
}

internal fun Difficulty.historyKey(): String =
    when (this) {
        Difficulty.EASY -> HistoryKeys.EASY
        Difficulty.MEDIUM -> HistoryKeys.MEDIUM
        Difficulty.HARD -> HistoryKeys.HARD
    }
