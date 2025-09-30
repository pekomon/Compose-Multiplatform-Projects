package com.example.pekomon.minesweeper.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.pekomon.minesweeper.game.Difficulty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class RunHistoryRepositoryTest {
    @Test
    fun top10ReturnsSortedAscending() = runTest {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        RunHistoryDatabase.Schema.create(driver)
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = createRunHistoryRepository(driver, dispatcher)

        val easyRuns = listOf(
            900L to 12_000L,
            1_200L to 12_100L,
            500L to 11_000L,
            500L to 10_000L,
            700L to 12_200L,
            800L to 12_300L,
            1_000L to 12_400L,
            1_100L to 12_500L,
            1_300L to 12_600L,
            1_400L to 12_700L,
            1_500L to 12_800L,
            1_600L to 12_900L,
        )

        easyRuns.forEach { (elapsedMillis, finishedAt) ->
            repository.insert(
                difficulty = Difficulty.EASY,
                elapsedMillis = elapsedMillis,
                finishedAt = finishedAt,
            )
        }

        repository.insert(
            difficulty = Difficulty.HARD,
            elapsedMillis = 1L,
            finishedAt = 1L,
        )

        val records = repository.top10(Difficulty.EASY)

        assertEquals(10, records.size)
        records.forEach { record ->
            assertEquals(Difficulty.EASY, record.difficulty)
        }

        val expectedOrder =
            easyRuns
                .sortedWith(compareBy<Pair<Long, Long>> { it.first }.thenBy { it.second })
                .take(10)

        assertEquals(expectedOrder.map { it.first }, records.map { it.elapsedMillis })
        assertEquals(expectedOrder.map { it.second }, records.map { it.finishedAt })
    }
}
