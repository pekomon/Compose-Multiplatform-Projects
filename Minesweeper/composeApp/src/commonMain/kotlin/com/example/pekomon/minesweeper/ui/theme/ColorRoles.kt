package com.example.pekomon.minesweeper.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun hiddenCellColor(): Color = MaterialTheme.colorScheme.surfaceVariant

@Composable
fun revealedCellColor(): Color = MaterialTheme.colorScheme.surface

@Composable
fun flaggedCellColor(): Color = MaterialTheme.colorScheme.tertiaryContainer

@Composable
fun cellBorderColor(): Color = MaterialTheme.colorScheme.outline

@Composable
fun numberColor(count: Int): Color = numberColor(count, MaterialTheme.colorScheme)

fun numberColor(count: Int, colorScheme: ColorScheme): Color = when (count) {
    1 -> colorScheme.primary
    2 -> colorScheme.secondary
    3 -> colorScheme.tertiary
    4 -> colorScheme.primaryContainer
    5 -> colorScheme.secondaryContainer
    6 -> colorScheme.tertiaryContainer
    7 -> colorScheme.outline
    8 -> colorScheme.onSurfaceVariant
    else -> colorScheme.onSurface
}
