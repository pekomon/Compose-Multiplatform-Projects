package com.example.pekomon.minesweeper.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(RunHistoryDatabase.Schema, context, DATABASE_NAME)
}

private object AndroidRunHistoryRepositoryHolder {
    @Volatile
    private var repository: RunHistoryRepository? = null

    fun initialize(factory: DatabaseDriverFactory) {
        if (repository == null) {
            synchronized(this) {
                if (repository == null) {
                    repository = createRunHistoryRepository(factory)
                }
            }
        }
    }

    fun repository(): RunHistoryRepository =
        repository ?: error("RunHistoryRepository not initialized. Call initializeRunHistoryRepository(context) first.")
}

fun initializeRunHistoryRepository(context: Context) {
    AndroidRunHistoryRepositoryHolder.initialize(DatabaseDriverFactory(context.applicationContext))
}

actual fun provideRunHistoryRepository(): RunHistoryRepository =
    AndroidRunHistoryRepositoryHolder.repository()

private const val DATABASE_NAME = "run_history.db"
