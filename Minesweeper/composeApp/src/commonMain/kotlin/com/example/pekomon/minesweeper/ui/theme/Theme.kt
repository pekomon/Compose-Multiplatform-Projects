package com.example.pekomon.minesweeper.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0061A3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF4A5F71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5E4F8),
    onSecondaryContainer = Color(0xFF071C2D),
    tertiary = Color(0xFF146C2E),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA3F5A6),
    onTertiaryContainer = Color(0xFF002107),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDEE3EA),
    onSurfaceVariant = Color(0xFF42474E),
    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC2C7CF),
    scrim = Color.Black,
    inverseSurface = Color(0xFF2F3133),
    inverseOnSurface = Color(0xFFF1F0F4),
    inversePrimary = Color(0xFF9FCAFF),
    surfaceTint = Color(0xFF0061A3),
)

internal val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9FCAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497E),
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = Color(0xFFBAC8D6),
    onSecondary = Color(0xFF21323F),
    secondaryContainer = Color(0xFF384956),
    onSecondaryContainer = Color(0xFFD5E4F8),
    tertiary = Color(0xFF88D98C),
    onTertiary = Color(0xFF003911),
    tertiaryContainer = Color(0xFF00531F),
    onTertiaryContainer = Color(0xFFA3F5A6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE1E2E6),
    surface = Color(0xFF101418),
    onSurface = Color(0xFFE1E2E6),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC2C7CF),
    outline = Color(0xFF8C9199),
    outlineVariant = Color(0xFF42474E),
    scrim = Color.Black,
    inverseSurface = Color(0xFFE1E2E6),
    inverseOnSurface = Color(0xFF2F3133),
    inversePrimary = Color(0xFF0061A3),
    surfaceTint = Color(0xFF9FCAFF),
)

private val LocalOverrideColorScheme = staticCompositionLocalOf<ColorScheme?> { null }

private val MinesweeperTypography = Typography()
private val MinesweeperShapes = Shapes()

@Composable
fun MinesweeperTheme(
    useDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = LocalOverrideColorScheme.current ?: if (useDarkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MinesweeperTypography,
        shapes = MinesweeperShapes,
        content = content,
    )
}

@Composable
internal fun MinesweeperThemeWithDynamicColors(
    colorScheme: ColorScheme,
    useDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOverrideColorScheme provides colorScheme) {
        MinesweeperTheme(useDarkTheme = useDarkTheme, content = content)
    }
}
