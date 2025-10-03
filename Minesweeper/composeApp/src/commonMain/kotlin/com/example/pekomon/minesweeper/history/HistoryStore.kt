package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty

interface HistoryStore {
    suspend fun getTop10(difficulty: Difficulty): List<RunRecord>

    suspend fun addRun(record: RunRecord)

    suspend fun clearAll()
}

expect fun provideHistoryStore(): HistoryStore
