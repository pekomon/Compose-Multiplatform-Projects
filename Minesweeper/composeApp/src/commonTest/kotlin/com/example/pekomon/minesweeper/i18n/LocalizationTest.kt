package com.example.pekomon.minesweeper.i18n

import com.example.pekomon.minesweeper.generated.resources.MR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class LocalizationTest {
    @Test
    fun difficultyEasyUsesDefaultLocale() = runTest {
        val text = localizedString(MR.strings.difficulty_easy)

        assertEquals("Easy", text)
    }
}
