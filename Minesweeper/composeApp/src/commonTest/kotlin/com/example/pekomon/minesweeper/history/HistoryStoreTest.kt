package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class HistoryStoreTest {
    private val store = FakeHistoryStore()

    @Test
    fun addingMoreThanTenRecordsKeepsFastestPerDifficulty() = runTest {
        store.clearAll()

        repeat(15) { index ->
            store.addRun(
                RunRecord(
                    difficulty = Difficulty.EASY,
                    millis = (15 - index) * 100L,
                    epochMillis = index.toLong(),
                ),
            )
            store.addRun(
                RunRecord(
                    difficulty = Difficulty.MEDIUM,
                    millis = (15 - index) * 200L,
                    epochMillis = (100 + index).toLong(),
                ),
            )
        }

        val easyTop = store.getTop10(Difficulty.EASY)
        val mediumTop = store.getTop10(Difficulty.MEDIUM)

        assertEquals(10, easyTop.size, "Easy retains top 10")
        assertEquals(10, mediumTop.size, "Medium retains top 10")
        assertEquals((1L..10L).map { it * 100L }, easyTop.map { it.millis })
        assertEquals((1L..10L).map { it * 200L }, mediumTop.map { it.millis })
    }

    @Test
    fun resultsAreIsolatedPerDifficulty() = runTest {
        store.clearAll()

        val easyRecord = RunRecord(Difficulty.EASY, millis = 1_000L, epochMillis = 1)
        val hardRecord = RunRecord(Difficulty.HARD, millis = 2_000L, epochMillis = 2)

        store.addRun(easyRecord)
        store.addRun(hardRecord)

        assertEquals(listOf(easyRecord), store.getTop10(Difficulty.EASY))
        assertEquals(listOf(hardRecord), store.getTop10(Difficulty.HARD))
        assertTrue(store.getTop10(Difficulty.MEDIUM).isEmpty())
    }

    @Test
    fun tiesAreOrderedByEpoch() = runTest {
        store.clearAll()

        val older = RunRecord(Difficulty.HARD, millis = 500L, epochMillis = 1)
        val newer = RunRecord(Difficulty.HARD, millis = 500L, epochMillis = 2)

        store.addRun(newer)
        store.addRun(older)

        assertEquals(listOf(older, newer), store.getTop10(Difficulty.HARD))
    }
}

private class FakeHistoryStore : HistoryStore {
    private val data = mutableMapOf<Difficulty, MutableList<RunRecord>>()

    override suspend fun getTop10(difficulty: Difficulty): List<RunRecord> =
        data[difficulty].orEmpty()

    override suspend fun addRun(record: RunRecord) {
        val bucket = data.getOrPut(record.difficulty) { mutableListOf() }
        val updated = (bucket + record).normalizeTop10()
        data[record.difficulty] = updated.toMutableList()
    }

    override suspend fun clearAll() {
        data.clear()
    }
}
