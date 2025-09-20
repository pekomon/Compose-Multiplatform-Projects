package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryHistoryStoreTest {
    @BeforeTest
    fun setup() {
        InMemoryHistoryStore.clear()
    }

    @Test
    fun `adding more than ten records keeps only top ten`() {
        repeat(15) { index ->
            InMemoryHistoryStore.add(
                RunRecord(
                    difficulty = Difficulty.EASY,
                    elapsedMillis = (15 - index).toLong() * 1000L,
                    epochMillis = index.toLong(),
                ),
            )
        }

        val records = InMemoryHistoryStore.top(Difficulty.EASY)

        assertEquals(10, records.size)
        assertTrue(records.all { it.difficulty == Difficulty.EASY })
        assertEquals((1L..10L).map { it * 1000L }, records.map { it.elapsedMillis })
    }

    @Test
    fun `records are returned in ascending order of elapsed time`() {
        listOf(5000L, 1000L, 3000L, 2000L).forEachIndexed { index, duration ->
            InMemoryHistoryStore.add(
                RunRecord(
                    difficulty = Difficulty.MEDIUM,
                    elapsedMillis = duration,
                    epochMillis = index.toLong(),
                ),
            )
        }

        val records = InMemoryHistoryStore.top(Difficulty.MEDIUM)

        assertEquals(listOf(1000L, 2000L, 3000L, 5000L), records.map { it.elapsedMillis })
    }

    @Test
    fun `records are filtered per difficulty`() {
        val easyRecord = RunRecord(Difficulty.EASY, elapsedMillis = 1000L, epochMillis = 1)
        val hardRecord = RunRecord(Difficulty.HARD, elapsedMillis = 2000L, epochMillis = 2)

        InMemoryHistoryStore.add(easyRecord)
        InMemoryHistoryStore.add(hardRecord)

        assertEquals(listOf(easyRecord), InMemoryHistoryStore.top(Difficulty.EASY))
        assertEquals(listOf(hardRecord), InMemoryHistoryStore.top(Difficulty.HARD))
        assertTrue(InMemoryHistoryStore.top(Difficulty.MEDIUM).isEmpty())
    }
}
