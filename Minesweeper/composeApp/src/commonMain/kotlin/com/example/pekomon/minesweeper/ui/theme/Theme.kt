package com.example.pekomon.minesweeper.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette =
    lightColors(
        primary = Color(0xFF0061A3),
        primaryVariant = Color(0xFF001D36),
        secondary = Color(0xFF4A5F71),
        secondaryVariant = Color(0xFF071C2D),
        background = Color(0xFFFDFCFF),
        surface = Color(0xFFFDFCFF),
        error = Color(0xFFBA1A1A),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF1A1C1E),
        onSurface = Color(0xFF1A1C1E),
        onError = Color.White,
    )

private val DarkColorPalette =
    darkColors(
        primary = Color(0xFF9FCAFF),
        primaryVariant = Color(0xFF003258),
        secondary = Color(0xFFBAC8D6),
        secondaryVariant = Color(0xFF384956),
        background = Color(0xFF101418),
        surface = Color(0xFF101418),
        error = Color(0xFFFFB4AB),
        onPrimary = Color(0xFF003258),
        onSecondary = Color(0xFF21323F),
        onBackground = Color(0xFFE1E2E6),
        onSurface = Color(0xFFE1E2E6),
        onError = Color(0xFF690005),
    )

private val MinesweeperTypography = Typography()
private val MinesweeperShapes = Shapes()

@Composable
fun MinesweeperTheme(
    useDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = if (useDarkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = MinesweeperTypography,
        shapes = MinesweeperShapes,
        content = content,
    )
}

