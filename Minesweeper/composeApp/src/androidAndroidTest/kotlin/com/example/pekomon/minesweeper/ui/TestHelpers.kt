package com.example.pekomon.minesweeper.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pekomon.minesweeper.game.Difficulty
import java.io.File

internal data class RevealResult(
    val tag: String,
    val before: ImageBitmap,
    val after: ImageBitmap,
)

private val easyRows = Difficulty.EASY.height
private val easyCols = Difficulty.EASY.width

@OptIn(ExperimentalTestApi::class)
internal fun AndroidComposeTestRule<*, *>.waitForBoard(difficulty: Difficulty = Difficulty.EASY) {
    waitUntil(timeoutMillis = 5_000) { boardReady(difficulty) }
    waitForIdle()
}

internal fun AndroidComposeTestRule<*, *>.cellCount(difficulty: Difficulty = Difficulty.EASY): Int =
    (0 until difficulty.height).sumOf { row ->
        (0 until difficulty.width).count { col -> cellExists(TestTags.cell(row, col)) }
    }

internal fun AndroidComposeTestRule<*, *>.setDifficulty(difficulty: Difficulty) {
    onNodeWithTag(TestTags.BTN_DIFFICULTY, useUnmergedTree = true).performClick()
    onNodeWithText(difficultyLabel(difficulty), useUnmergedTree = true).performClick()
    waitForBoard(difficulty)
}

internal fun AndroidComposeTestRule<*, *>.revealSafeCell(): RevealResult {
    waitForBoard()
    for (row in 0 until easyRows) {
        for (col in 0 until easyCols) {
            val tag = TestTags.cell(row, col)
            val node = onNodeWithTag(tag, useUnmergedTree = true)
            val before = node.captureToImage()
            node.performClick()
            waitForIdle()
            if (!isGameLost()) {
                val after = onNodeWithTag(tag, useUnmergedTree = true).captureToImage()
                return RevealResult(tag, before, after)
            }
            resetGame()
        }
    }
    error("Failed to reveal a safe cell")
}

@OptIn(ExperimentalTestApi::class)
internal fun AndroidComposeTestRule<*, *>.resetGame(difficulty: Difficulty = Difficulty.EASY) {
    onNodeWithTag(TestTags.BTN_RESET, useUnmergedTree = true).performClick()
    waitForIdle()
    waitForBoard(difficulty)
    waitUntil(timeoutMillis = 5_000) { !isGameLost() && getTimerText().contains("0s") }
}

internal fun AndroidComposeTestRule<*, *>.getTimerText(): String {
    val node = onNodeWithTag(TestTags.TXT_TIMER, useUnmergedTree = true)
    val semantics = node.fetchSemanticsNode().config
    val text = semantics.getOrNull(SemanticsProperties.Text) ?: return ""
    return text.joinToString(separator = "") { it.text }
}

internal fun AndroidComposeTestRule<*, *>.isGameLost(): Boolean = getTimerText().contains("💥")

internal fun AndroidComposeTestRule<*, *>.saveScreenshot(
    image: ImageBitmap,
    fileName: String,
): File {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val context = instrumentation.targetContext
    val outputDir = File(context.getExternalFilesDir(null), "androidTest-screenshots").apply { mkdirs() }
    val outputFile = File(outputDir, fileName)
    outputFile.outputStream().use { stream ->
        image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    return outputFile
}

internal fun ImageBitmap.centerPixel(): Color {
    val pixels = toPixelMap()
    return pixels[pixels.width / 2, pixels.height / 2]
}

private fun AndroidComposeTestRule<*, *>.boardReady(difficulty: Difficulty): Boolean =
    (0 until difficulty.height).all { row ->
        (0 until difficulty.width).all { col -> cellExists(TestTags.cell(row, col)) }
    }

private fun AndroidComposeTestRule<*, *>.cellExists(tag: String): Boolean =
    runCatching { onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode() }.isSuccess

private fun difficultyLabel(difficulty: Difficulty): String =
    when (difficulty) {
        Difficulty.EASY -> "Easy"
        Difficulty.MEDIUM -> "Medium"
        Difficulty.HARD -> "Hard"
    }
