package com.example.pekomon.minesweeper.db

import app.cash.sqldelight.db.SqlDriver
import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Records completed game runs.
 */
interface RunHistoryRepository {
    suspend fun insert(difficulty: Difficulty, elapsedMillis: Long, finishedAt: Long)

    suspend fun top10(difficulty: Difficulty): List<RunRecord>
}

data class RunRecord(
    val difficulty: Difficulty,
    val elapsedMillis: Long,
    val finishedAt: Long,
)

fun createRunHistoryRepository(
    driver: SqlDriver,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): RunHistoryRepository {
    val database = RunHistoryDatabase(driver)
    return SqlDelightRunHistoryRepository(
        queries = database.runHistoryQueries,
        dispatcher = dispatcher,
    )
}

fun createRunHistoryRepository(
    factory: DatabaseDriverFactory,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): RunHistoryRepository = createRunHistoryRepository(factory.createDriver(), dispatcher)

val NoOpRunHistoryRepository: RunHistoryRepository =
    object : RunHistoryRepository {
        override suspend fun insert(difficulty: Difficulty, elapsedMillis: Long, finishedAt: Long) = Unit

        override suspend fun top10(difficulty: Difficulty): List<RunRecord> = emptyList()
    }

expect fun provideRunHistoryRepository(): RunHistoryRepository

internal class SqlDelightRunHistoryRepository(
    private val queries: RunHistoryQueries,
    private val dispatcher: CoroutineDispatcher,
) : RunHistoryRepository {
    override suspend fun insert(difficulty: Difficulty, elapsedMillis: Long, finishedAt: Long) {
        withContext(dispatcher) {
            queries.insert(
                difficulty = difficulty.name,
                elapsedMillis = elapsedMillis,
                finishedAt = finishedAt,
            )
        }
    }

    override suspend fun top10(difficulty: Difficulty): List<RunRecord> =
        withContext(dispatcher) {
            queries
                .selectAllByDifficulty(difficulty = difficulty.name)
                .executeAsList()
                .map { record ->
                    RunRecord(
                        difficulty = Difficulty.valueOf(record.difficulty),
                        elapsedMillis = record.elapsedMillis,
                        finishedAt = record.finishedAt,
                    )
                }
        }
}
