package com.example.pekomon.minesweeper.history

import com.example.pekomon.minesweeper.game.Difficulty
import java.util.prefs.Preferences

private class JvmHistoryStore : HistoryStore {
    private val preferences = Preferences.userRoot().node("com.example.pekomon.minesweeper.history")

    override suspend fun getTop10(difficulty: Difficulty): List<RunRecord> {
        val stored = preferences.get(difficulty.historyKey(), null)
        return HistoryJson.decode(stored).normalizeTop10()
    }

    override suspend fun addRun(record: RunRecord) {
        val key = record.difficulty.historyKey()
        val current = HistoryJson.decode(preferences.get(key, null))
        val updated = (current + record).normalizeTop10()
        preferences.put(key, HistoryJson.encode(updated))
    }

    override suspend fun clearAll() {
        preferences.remove(HistoryKeys.EASY)
        preferences.remove(HistoryKeys.MEDIUM)
        preferences.remove(HistoryKeys.HARD)
    }
}

private val store: HistoryStore by lazy { JvmHistoryStore() }

actual fun provideHistoryStore(): HistoryStore = store
