package com.example.pekomon.minesweeper.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.pekomon.minesweeper.game.Difficulty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "minesweeper_settings")

private class AndroidSettingsRepository(
    private val context: Context,
) : SettingsRepository {
    private val difficultyKey = stringPreferencesKey(SettingsKeys.SELECTED_DIFFICULTY)
    private val reducedMotionKey = booleanPreferencesKey(SettingsKeys.REDUCED_MOTION_ENABLED)

    override fun getSelectedDifficulty(): Difficulty? =
        runBlocking {
            val stored = context.dataStore.data.first()[difficultyKey]
            stored?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() }
        }

    override fun setSelectedDifficulty(value: Difficulty) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[difficultyKey] = value.name
            }
        }
    }

    override fun isReducedMotionEnabled(): Boolean =
        runBlocking { context.dataStore.data.first()[reducedMotionKey] ?: false }

    override fun setReducedMotionEnabled(enabled: Boolean) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[reducedMotionKey] = enabled
            }
        }
    }
}

private object AndroidSettingsHolder {
    @Volatile
    private var repository: SettingsRepository? = null

    fun initialize(context: Context) {
        if (repository == null) {
            repository = AndroidSettingsRepository(context.applicationContext)
        }
    }

    fun repository(): SettingsRepository =
        repository ?: error("SettingsRepository not initialized. Call initializeSettingsRepository(context) first.")
}

fun initializeSettingsRepository(context: Context) {
    AndroidSettingsHolder.initialize(context)
}

actual fun provideSettingsRepository(): SettingsRepository = AndroidSettingsHolder.repository()
