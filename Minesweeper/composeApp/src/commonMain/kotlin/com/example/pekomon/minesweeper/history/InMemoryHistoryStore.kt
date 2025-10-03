package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryHistoryStore : HistoryStore {
    private val mutex = Mutex()
    private val records: MutableMap<Difficulty, MutableList<RunRecord>> =
        Difficulty.values().associateWith { mutableListOf<RunRecord>() }.toMutableMap()

    override suspend fun getTop10(difficulty: Difficulty): List<RunRecord> =
        mutex.withLock { records[difficulty].orEmpty().normalizeTop10() }

    override suspend fun addRun(record: RunRecord) {
        mutex.withLock {
            val bucket = records.getOrPut(record.difficulty) { mutableListOf() }
            val updated = (bucket + record).normalizeTop10()
            bucket.clear()
            bucket.addAll(updated)
        }
    }

    override suspend fun clearAll() {
        mutex.withLock {
            records.values.forEach { it.clear() }
        }
    }
}
