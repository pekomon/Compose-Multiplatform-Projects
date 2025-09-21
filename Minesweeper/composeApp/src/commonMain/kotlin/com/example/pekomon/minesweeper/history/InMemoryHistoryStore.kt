package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty

private const val MAX_RECORDS = 10

object InMemoryHistoryStore {
    private val records: MutableMap<Difficulty, MutableList<RunRecord>> =
        Difficulty.values().associateWith { mutableListOf<RunRecord>() }.toMutableMap()

    fun add(record: RunRecord) {
        val bucket = records.getOrPut(record.difficulty) { mutableListOf() }
        bucket.add(record)
        bucket.sortWith(compareBy<RunRecord> { it.elapsedMillis }.thenBy { it.epochMillis })
        if (bucket.size > MAX_RECORDS) {
            bucket.subList(MAX_RECORDS, bucket.size).clear()
        }
    }

    fun top(
        difficulty: Difficulty,
        limit: Int = MAX_RECORDS,
    ): List<RunRecord> {
        if (limit <= 0) {
            return emptyList()
        }
        val bucket = records[difficulty].orEmpty()
        return bucket.take(limit)
    }

    internal fun clear() {
        records.values.forEach { it.clear() }
    }
}
