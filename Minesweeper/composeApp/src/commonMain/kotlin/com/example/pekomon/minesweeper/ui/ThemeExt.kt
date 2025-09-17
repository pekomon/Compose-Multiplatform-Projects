package com.example.pekomon.minesweeper.ui

import androidx.compose.ui.graphics.Color

internal val HiddenCellColor = Color(0xFFB0BEC5)
internal val RevealedCellColor = Color(0xFFF5F5F5)
internal val FlaggedCellColor = Color(0xFFFFF59D)
internal val CellBorderColor = Color(0xFF90A4AE)

private val numberPalette = mapOf(
    1 to Color(0xFF1976D2),
    2 to Color(0xFF388E3C),
    3 to Color(0xFFD32F2F),
    4 to Color(0xFF512DA8),
    5 to Color(0xFFF57C00),
    6 to Color(0xFF00796B),
    7 to Color(0xFF455A64),
    8 to Color(0xFF795548),
)

internal fun numberColor(count: Int): Color = numberPalette[count] ?: Color(0xFF263238)
