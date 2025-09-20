package com.example.pekomon.minesweeper.util

/** Formats [millis] to a mm:ss string, clamping negative inputs to zero. */
internal fun formatMillisToMmSs(millis: Long): String {
    val totalSeconds = (millis / 1_000).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return buildString {
        append(minutes)
        append(':')
        if (seconds < 10) {
            append('0')
        }
        append(seconds)
    }
}
