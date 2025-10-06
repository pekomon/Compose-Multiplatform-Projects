package com.example.pekomon.minesweeper.ui

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pekomon.minesweeper.MainActivity
import com.example.pekomon.minesweeper.game.Difficulty
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerAndScreenshotTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeRule.setDifficulty(Difficulty.EASY)
    }

    @Test
    fun timerAdvancesAndScreenshotSaved() {
        composeRule.revealSafeCell()

        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.mainClock.advanceTimeBy(1_200)
            composeRule.waitForIdle()
        } finally {
            composeRule.mainClock.autoAdvance = true
        }

        val timerText = composeRule.getTimerText()
        assertTrue("Timer should advance past 1s", timerText.contains("1s") || timerText.contains("2s"))

        val rootImage =
            composeRule.onNodeWithTag(TestTags.ROOT, useUnmergedTree = true).captureToImage()
        val screenshot = composeRule.saveScreenshot(rootImage, "game_basic.png")

        assertTrue("Screenshot file must exist", screenshot.exists())
    }
}
