package com.example.pekomon.minesweeper.history

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.coroutines.flow.first
import kotlin.jvm.Volatile

private val Context.historyDataStore by preferencesDataStore(name = "minesweeper_history")

private class AndroidHistoryStore(
    private val context: Context,
) : HistoryStore {
    private val keys = Difficulty.values().associateWith { difficulty ->
        stringPreferencesKey(difficulty.historyKey())
    }

    override suspend fun getTop10(difficulty: Difficulty): List<RunRecord> {
        val key = keys.getValue(difficulty)
        val stored = context.historyDataStore.data.first()[key]
        return HistoryJson.decode(stored).normalizeTop10()
    }

    override suspend fun addRun(record: RunRecord) {
        val key = keys.getValue(record.difficulty)
        context.historyDataStore.edit { prefs ->
            val current = HistoryJson.decode(prefs[key])
            val updated = (current + record).normalizeTop10()
            prefs[key] = HistoryJson.encode(updated)
        }
    }

    override suspend fun clearAll() {
        context.historyDataStore.edit { prefs ->
            keys.values.forEach { key ->
                prefs.remove(key)
            }
        }
    }
}

private object AndroidHistoryStoreHolder {
    @Volatile
    private var store: HistoryStore? = null

    fun initialize(context: Context) {
        if (store == null) {
            store = AndroidHistoryStore(context.applicationContext)
        }
    }

    fun get(): HistoryStore =
        store ?: error("HistoryStore not initialized. Call initializeHistoryStore(context) first.")
}

fun initializeHistoryStore(context: Context) {
    AndroidHistoryStoreHolder.initialize(context)
}

actual fun provideHistoryStore(): HistoryStore = AndroidHistoryStoreHolder.get()
