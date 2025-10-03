package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import platform.Foundation.NSUserDefaults

private class IosHistoryStore : HistoryStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun getTop10(difficulty: Difficulty): List<RunRecord> {
        val stored = defaults.stringForKey(difficulty.historyKey())
        return HistoryJson.decode(stored).normalizeTop10()
    }

    override suspend fun addRun(record: RunRecord) {
        val key = record.difficulty.historyKey()
        val current = HistoryJson.decode(defaults.stringForKey(key))
        val updated = (current + record).normalizeTop10()
        defaults.setObject(HistoryJson.encode(updated), forKey = key)
    }

    override suspend fun clearAll() {
        defaults.removeObjectForKey(HistoryKeys.EASY)
        defaults.removeObjectForKey(HistoryKeys.MEDIUM)
        defaults.removeObjectForKey(HistoryKeys.HARD)
    }
}

private val store: HistoryStore by lazy { IosHistoryStore() }

actual fun provideHistoryStore(): HistoryStore = store
