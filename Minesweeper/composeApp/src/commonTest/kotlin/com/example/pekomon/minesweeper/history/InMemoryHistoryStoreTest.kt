package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryHistoryStoreTest {
    @BeforeTest
    fun setup() {
        InMemoryHistoryStore.clear()
    }

    @AfterTest
    fun tearDown() {
        InMemoryHistoryStore.clear()
    }

    @Test
    fun addingMoreThanTenRecordsKeepsFastestPerDifficulty() {
        repeat(15) { index ->
            InMemoryHistoryStore.add(
                RunRecord(
                    difficulty = Difficulty.EASY,
                    elapsedMillis = (15 - index) * 100L,
                    epochMillis = index.toLong(),
                ),
            )
            InMemoryHistoryStore.add(
                RunRecord(
                    difficulty = Difficulty.MEDIUM,
                    elapsedMillis = (15 - index) * 200L,
                    epochMillis = (100 + index).toLong(),
                ),
            )
        }

        val easyTop = InMemoryHistoryStore.top(Difficulty.EASY)
        val mediumTop = InMemoryHistoryStore.top(Difficulty.MEDIUM)

        assertEquals(10, easyTop.size, "Easy retains top 10")
        assertEquals(10, mediumTop.size, "Medium retains top 10")
        assertEquals((1L..10L).map { it * 100L }, easyTop.map { it.elapsedMillis })
        assertEquals((1L..10L).map { it * 200L }, mediumTop.map { it.elapsedMillis })
    }

    @Test
    fun topListIsFilteredByDifficulty() {
        val easyRecord = RunRecord(Difficulty.EASY, elapsedMillis = 1_000L, epochMillis = 1)
        val hardRecord = RunRecord(Difficulty.HARD, elapsedMillis = 2_000L, epochMillis = 2)

        InMemoryHistoryStore.add(easyRecord)
        InMemoryHistoryStore.add(hardRecord)

        assertEquals(listOf(easyRecord), InMemoryHistoryStore.top(Difficulty.EASY))
        assertEquals(listOf(hardRecord), InMemoryHistoryStore.top(Difficulty.HARD))
        assertTrue(InMemoryHistoryStore.top(Difficulty.MEDIUM).isEmpty())
    }

    @Test
    fun limitParameterTruncatesResults() {
        repeat(5) { index ->
            InMemoryHistoryStore.add(
                RunRecord(
                    difficulty = Difficulty.HARD,
                    elapsedMillis = (index + 1) * 1_000L,
                    epochMillis = index.toLong(),
                ),
            )
        }

        val topThree = InMemoryHistoryStore.top(Difficulty.HARD, limit = 3)
        assertEquals(3, topThree.size)
        assertEquals(listOf(1_000L, 2_000L, 3_000L), topThree.map { it.elapsedMillis })
    }
}
