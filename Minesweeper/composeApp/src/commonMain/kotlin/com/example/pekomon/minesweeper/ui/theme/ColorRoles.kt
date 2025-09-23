package com.example.pekomon.minesweeper.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightSurfaceVariant = Color(0xFFDEE3EA)
private val DarkSurfaceVariant = Color(0xFF42474E)

private val LightTertiaryContainer = Color(0xFFA3F5A6)
private val DarkTertiaryContainer = Color(0xFF00531F)

private val LightOutline = Color(0xFF73777F)
private val DarkOutline = Color(0xFF8C9199)

private val LightPrimary = Color(0xFF0061A3)
private val DarkPrimary = Color(0xFF9FCAFF)

private val LightSecondary = Color(0xFF4A5F71)
private val DarkSecondary = Color(0xFFBAC8D6)

private val LightTertiary = Color(0xFF146C2E)
private val DarkTertiary = Color(0xFF88D98C)

private val LightPrimaryContainer = Color(0xFFD0E4FF)
private val DarkPrimaryContainer = Color(0xFF00497E)

private val LightSecondaryContainer = Color(0xFFD5E4F8)
private val DarkSecondaryContainer = Color(0xFF384956)

private val LightOnSurfaceVariant = Color(0xFF42474E)
private val DarkOnSurfaceVariant = Color(0xFFC2C7CF)

private val LightOnSurface = Color(0xFF1A1C1E)
private val DarkOnSurface = Color(0xFFE1E2E6)

@Composable
fun hiddenCellColor(): Color = if (MaterialTheme.colors.isLight) LightSurfaceVariant else DarkSurfaceVariant

@Composable
fun revealedCellColor(): Color = if (MaterialTheme.colors.isLight) Color(0xFFFDFCFF) else Color(0xFF101418)

@Composable
fun flaggedCellColor(): Color = if (MaterialTheme.colors.isLight) LightTertiaryContainer else DarkTertiaryContainer

@Composable
fun cellBorderColor(): Color = if (MaterialTheme.colors.isLight) LightOutline else DarkOutline

@Composable
fun numberColor(count: Int): Color =
    if (MaterialTheme.colors.isLight) {
        lightNumberColor(count)
    } else {
        darkNumberColor(count)
    }

private fun lightNumberColor(count: Int): Color =
    when (count) {
        1 -> LightPrimary
        2 -> LightSecondary
        3 -> LightTertiary
        4 -> LightPrimaryContainer
        5 -> LightSecondaryContainer
        6 -> LightTertiaryContainer
        7 -> LightOutline
        8 -> LightOnSurfaceVariant
        else -> LightOnSurface
    }

private fun darkNumberColor(count: Int): Color =
    when (count) {
        1 -> DarkPrimary
        2 -> DarkSecondary
        3 -> DarkTertiary
        4 -> DarkPrimaryContainer
        5 -> DarkSecondaryContainer
        6 -> DarkTertiaryContainer
        7 -> DarkOutline
        8 -> DarkOnSurfaceVariant
        else -> DarkOnSurface
    }
