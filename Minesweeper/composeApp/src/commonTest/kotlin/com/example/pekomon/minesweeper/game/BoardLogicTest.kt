package com.example.pekomon.minesweeper.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoardLogicTest {
    @Test
    fun boardGenerationHasCorrectSizeAndMines() {
        val board = generateBoard(9, 9, 10, seed = 0)
        assertEquals(9, board.width)
        assertEquals(9, board.height)
        assertEquals(10, board.cells.count { it.isMine })
    }

    @Test
    fun revealZeroFloodsArea() {
        val board = generateBoard(3, 3, 0, seed = 0)
        val result = reveal(board, 1, 1)
        assertTrue(result.revealedCount > 1)
    }

    @Test
    fun toggleFlagUpdatesStateAndCount() {
        var board = generateBoard(2, 2, 0, seed = 0)
        board = toggleFlag(board, 0, 0)
        assertEquals(CellState.FLAGGED, board.cellAt(0, 0).state)
        assertEquals(1, board.flaggedCount)
        board = toggleFlag(board, 0, 0)
        assertEquals(CellState.HIDDEN, board.cellAt(0, 0).state)
        assertEquals(0, board.flaggedCount)
    }

    @Test
    fun revealMineLosesGame() {
        val board = generateBoard(1, 1, 1, seed = 0)
        val result = reveal(board, 0, 0)
        assertEquals(GameStatus.LOST, result.status)
        assertEquals(CellState.REVEALED, result.cellAt(0, 0).state)
    }

    @Test
    fun revealingAllNonMinesWinsGame() {
        var board = generateBoard(2, 1, 1, seed = 0)
        val nonMineIndex = board.cells.indexOfFirst { !it.isMine }
        val x = nonMineIndex % board.width
        val y = nonMineIndex / board.width
        board = reveal(board, x, y)
        assertEquals(GameStatus.WON, board.status)
    }
}
