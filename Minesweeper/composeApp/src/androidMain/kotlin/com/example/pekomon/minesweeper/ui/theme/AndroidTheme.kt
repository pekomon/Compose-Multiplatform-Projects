package com.example.pekomon.minesweeper.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AndroidMinesweeperTheme(content: @Composable () -> Unit) {
    val useDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MinesweeperThemeWithDynamicColors(
        colorScheme = colorScheme,
        useDarkTheme = useDarkTheme,
        content = content,
    )
}
