package com.example.pekomon.minesweeper.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(RunHistoryDatabase.Schema, DATABASE_NAME)
}

private val repository: RunHistoryRepository by lazy {
    createRunHistoryRepository(DatabaseDriverFactory())
}

actual fun provideRunHistoryRepository(): RunHistoryRepository = repository

private const val DATABASE_NAME = "run_history.db"
