package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.browser.window

private class WasmHistoryStore : HistoryStore {
    private val storage get() = window.localStorage

    override suspend fun getTop10(difficulty: Difficulty): List<RunRecord> {
        val stored = storage.getItem(difficulty.historyKey())
        return HistoryJson.decode(stored).normalizeTop10()
    }

    override suspend fun addRun(record: RunRecord) {
        val key = record.difficulty.historyKey()
        val current = HistoryJson.decode(storage.getItem(key))
        val updated = (current + record).normalizeTop10()
        storage.setItem(key, HistoryJson.encode(updated))
    }

    override suspend fun clearAll() {
        storage.removeItem(HistoryKeys.EASY)
        storage.removeItem(HistoryKeys.MEDIUM)
        storage.removeItem(HistoryKeys.HARD)
    }
}

private val store: HistoryStore by lazy { WasmHistoryStore() }

actual fun provideHistoryStore(): HistoryStore = store
