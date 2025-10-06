package com.example.pekomon.minesweeper.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.useUnmergedTree
import androidx.compose.ui.test.waitForIdle
import androidx.compose.ui.test.waitUntil
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pekomon.minesweeper.game.Difficulty
import java.io.File

internal data class RevealResult(
    val tag: String,
    val before: ImageBitmap,
    val after: ImageBitmap,
)

private const val CELL_TAG_PREFIX = "cell-"

private val easyCellCount = Difficulty.EASY.width * Difficulty.EASY.height
private val easyRows = Difficulty.EASY.height
private val easyCols = Difficulty.EASY.width

internal fun AndroidComposeTestRule<*, *>.waitForBoard(expectedCells: Int = easyCellCount) {
    waitUntil(timeoutMillis = 5_000) { cellCount() == expectedCells }
    waitForIdle()
}

internal fun AndroidComposeTestRule<*, *>.cellCount(): Int =
    onAllNodes(hasTestTagPrefix(CELL_TAG_PREFIX), useUnmergedTree = true).fetchSemanticsNodes().size

internal fun AndroidComposeTestRule<*, *>.setDifficulty(difficulty: Difficulty) {
    onNodeWithTag(TestTags.BTN_DIFFICULTY, useUnmergedTree = true).performClick()
    onNodeWithText(difficultyLabel(difficulty), useUnmergedTree = true).performClick()
    waitForBoard(difficulty.width * difficulty.height)
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

internal fun AndroidComposeTestRule<*, *>.resetGame(expectedCells: Int = easyCellCount) {
    onNodeWithTag(TestTags.BTN_RESET, useUnmergedTree = true).performClick()
    waitForIdle()
    waitForBoard(expectedCells)
    waitUntil(timeoutMillis = 5_000) { !isGameLost() && getTimerText().contains("0s") }
}

internal fun AndroidComposeTestRule<*, *>.getTimerText(): String {
    val node = onNodeWithTag(TestTags.TXT_TIMER, useUnmergedTree = true)
    val semantics = node.fetchSemanticsNode().config
    val text = semantics.getOrNull(SemanticsProperties.Text) ?: return ""
    return text.joinToString(separator = "") { it.text }
}

internal fun AndroidComposeTestRule<*, *>.isGameLost(): Boolean = getTimerText().contains("💥")

internal fun AndroidComposeTestRule<*, *>.saveScreenshot(image: ImageBitmap, fileName: String): File {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val context = instrumentation.targetContext
    val outputDir = File(context.getExternalFilesDir(null), "androidTest-screenshots").apply { mkdirs() }
    val outputFile = File(outputDir, fileName)
    outputFile.outputStream().use { stream ->
        image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    return outputFile
}

internal fun hasTestTagPrefix(prefix: String): SemanticsMatcher =
    SemanticsMatcher("TestTag starts with $prefix") { semanticsNode ->
        val tag = semanticsNode.config.getOrNull(SemanticsProperties.TestTag)
        tag?.startsWith(prefix) == true
    }

internal fun ImageBitmap.centerPixel(): Int {
    val pixels = toPixelMap()
    return pixels[pixels.width / 2, pixels.height / 2]
}

private fun difficultyLabel(difficulty: Difficulty): String =
    when (difficulty) {
        Difficulty.EASY -> "Easy"
        Difficulty.MEDIUM -> "Medium"
        Difficulty.HARD -> "Hard"
    }
