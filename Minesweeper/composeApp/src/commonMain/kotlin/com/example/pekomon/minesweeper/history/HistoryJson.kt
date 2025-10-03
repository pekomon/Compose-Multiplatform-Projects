package com.example.pekomon.minesweeper.history

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal object HistoryJson {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun encode(records: List<RunRecord>): String = json.encodeToString(records)

    fun decode(text: String?): List<RunRecord> =
        if (text.isNullOrBlank()) {
            emptyList()
        } else {
            runCatching { json.decodeFromString<List<RunRecord>>(text) }.getOrElse { emptyList() }
        }
}

internal fun List<RunRecord>.normalizeTop10(): List<RunRecord> =
    sortedWith(compareBy<RunRecord> { it.millis }.thenBy { it.epochMillis }).take(10)
