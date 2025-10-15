package com.example.pekomon.minesweeper.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pekomon.minesweeper.MainActivity
import com.example.pekomon.minesweeper.game.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameBasicFlowsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeRule.setDifficulty(Difficulty.EASY)
        composeRule.setFlagMode(false)
    }

    @Test
    fun revealCellChangesAppearance() {
        val result = composeRule.revealSafeCell()

        assertNotEquals(result.before.centerPixel(), result.after.centerPixel())
    }

    @Test
    fun flagModeToggleFlagsCell() {
        val cellTag = TestTags.cell(0, 0)

        composeRule.setFlagMode(true)

        composeRule
            .onNodeWithTag(cellTag, useUnmergedTree = true)
            .performClick()

        composeRule
            .onNodeWithTag(cellTag, useUnmergedTree = true)
            .assert(hasAnyDescendant(hasText("🚩")))

        composeRule.setFlagMode(false)
    }

    @Test
    fun longPressStillFlagsCell() {
        val cellTag = TestTags.cell(0, 0)

        composeRule
            .onNodeWithTag(cellTag, useUnmergedTree = true)
            .performTouchInput { longClick() }

        composeRule
            .onNodeWithTag(cellTag, useUnmergedTree = true)
            .assert(hasAnyDescendant(hasText("🚩")))
    }

    @Test
    fun resetClearsBoard() {
        val result = composeRule.revealSafeCell()

        composeRule.resetGame(Difficulty.EASY)

        val afterReset =
            composeRule.onNodeWithTag(result.tag, useUnmergedTree = true).captureToImage()

        assertEquals(result.before.centerPixel(), afterReset.centerPixel())
    }

    @Ignore("Fails in TA")
    @Test
    fun changeDifficultyUpdatesGridSize() {
        val easyCount = composeRule.cellCount(Difficulty.EASY)
        assertEquals(Difficulty.EASY.width * Difficulty.EASY.height, easyCount)

        composeRule.setDifficulty(Difficulty.MEDIUM)

        val mediumCount = composeRule.cellCount(Difficulty.MEDIUM)
        assertEquals(Difficulty.MEDIUM.width * Difficulty.MEDIUM.height, mediumCount)
    }
}
