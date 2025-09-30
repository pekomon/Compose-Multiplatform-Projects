package com.example.pekomon.minesweeper.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databaseFile = File(System.getProperty("user.home"), ".minesweeper/run_history.db")
        val isNewDatabase = !databaseFile.exists()
        databaseFile.parentFile?.mkdirs()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        if (isNewDatabase) {
            RunHistoryDatabase.Schema.create(driver)
        }
        return driver
    }
}

private val repository: RunHistoryRepository by lazy {
    createRunHistoryRepository(DatabaseDriverFactory())
}

actual fun provideRunHistoryRepository(): RunHistoryRepository = repository
