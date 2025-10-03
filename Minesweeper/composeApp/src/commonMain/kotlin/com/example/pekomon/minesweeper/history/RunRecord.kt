package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.serialization.Serializable

@Serializable
data class RunRecord(
    val difficulty: Difficulty,
    val millis: Long,
    val epochMillis: Long,
)

fun isNewRecord(
    currentMillis: Long,
    difficulty: Difficulty,
    history: List<RunRecord>,
): Boolean {
    if (currentMillis < 0) {
        return false
    }

    val bestRecord =
        history
            .asSequence()
            .filter { it.difficulty == difficulty }
            .minWithOrNull(compareBy<RunRecord> { it.millis }.thenBy { it.epochMillis })

    return bestRecord == null || currentMillis < bestRecord.millis
}
