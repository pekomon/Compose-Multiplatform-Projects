package com.example.pekomon.minesweeper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pekomon.minesweeper.ui.GameScreen
import com.example.pekomon.minesweeper.ui.theme.MinesweeperTheme
import com.example.pekomon.minesweeper.i18n.AppLocales
import com.example.pekomon.minesweeper.i18n.LocalAppLocale
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var locale by remember { mutableStateOf(AppLocales.Default) }
    MinesweeperTheme(useDarkTheme = false) {
        CompositionLocalProvider(LocalAppLocale provides locale) {
            GameScreen(
                availableLocales = AppLocales.supported,
                onLocaleChange = { locale = it },
            )
        }
    }
}
