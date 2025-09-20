package com.example.pekomon.minesweeper.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import minesweeper.composeapp.generated.resources.MR

class LocalizationTest {
    @Test
    fun difficultyEasyIsLocalized() = runTest {
        val english = localizedString(AppLocales.English, MR.strings.difficulty_easy)
        val finnish = localizedString(AppLocales.Finnish, MR.strings.difficulty_easy)

        assertEquals("Easy", english)
        assertEquals("Helppo", finnish)
    }
}
