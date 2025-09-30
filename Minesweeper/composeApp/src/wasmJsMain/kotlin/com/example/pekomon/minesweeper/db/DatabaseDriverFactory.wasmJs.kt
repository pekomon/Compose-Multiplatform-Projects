package com.example.pekomon.minesweeper.db

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        error("SQLDelight does not currently provide a WebAssembly driver.")
}

// WebAssembly builds do not persist history yet, so we expose a no-op repository.
actual fun provideRunHistoryRepository(): RunHistoryRepository = NoOpRunHistoryRepository
