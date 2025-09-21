package com.example.pekomon.minesweeper.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BoardLogicTest {
    @Test
    fun toggleFlagOnlyAffectsCoveredCells() {
        val board =
            createBoard(
                width = 2,
                height = 1,
                mines = emptySet(),
                stateOverrides = mapOf((1 to 0) to CellState.REVEALED),
            )

        val flaggedBoard = toggleFlag(board, 0, 0)
        assertEquals(CellState.FLAGGED, flaggedBoard.cellAt(0, 0).state)
        assertEquals(1, flaggedBoard.flaggedCount)

        val unflaggedBoard = toggleFlag(flaggedBoard, 0, 0)
        assertEquals(CellState.HIDDEN, unflaggedBoard.cellAt(0, 0).state)
        assertEquals(0, unflaggedBoard.flaggedCount)

        val unchangedBoard = toggleFlag(board, 1, 0)
        assertEquals(CellState.REVEALED, unchangedBoard.cellAt(1, 0).state)
        assertEquals(0, unchangedBoard.flaggedCount)
        assertSame(board, unchangedBoard)
    }

    @Test
    fun revealShowsNumbersAndCascadesZeroCells() {
        val mines = setOf(2 to 2)
        val numberBoard = reveal(createBoard(3, 3, mines), 1, 1)
        val centerCell = numberBoard.cellAt(1, 1)
        assertEquals(CellState.REVEALED, centerCell.state)
        assertEquals(1, centerCell.adjacentMines)

        val cascadedBoard = reveal(createBoard(3, 3, mines), 0, 0)
        val revealedCells = cascadedBoard.cells.filter { it.state == CellState.REVEALED }
        assertEquals(8, revealedCells.size)
        assertEquals(8, cascadedBoard.revealedCount)
        assertTrue(revealedCells.none { it.isMine })
        assertEquals(GameStatus.WON, cascadedBoard.status)
    }

    @Test
    fun winningWhenAllSafeCellsRevealed() {
        val mines = setOf(1 to 1)
        var board = createBoard(2, 2, mines)

        val safeCells = board.cells.filterNot { it.isMine }
        for (cell in safeCells) {
            board = reveal(board, cell.x, cell.y)
        }

        assertEquals(GameStatus.WON, board.status)
        assertEquals(safeCells.size, board.revealedCount)
    }

    @Test
    fun losingWhenMineRevealed() {
        val mines = setOf(1 to 0)
        val result = reveal(createBoard(2, 1, mines), 1, 0)

        assertEquals(GameStatus.LOST, result.status)
        assertEquals(CellState.REVEALED, result.cellAt(1, 0).state)
        assertEquals(CellState.REVEALED, result.cells.first { it.isMine }.state)
    }

    private fun createBoard(
        width: Int,
        height: Int,
        mines: Set<Pair<Int, Int>>,
        stateOverrides: Map<Pair<Int, Int>, CellState> = emptyMap(),
    ): Board {
        fun adjacentCount(
            x: Int,
            y: Int,
        ): Int {
            if (mines.contains(x to y)) return 0
            var count = 0
            for (dy in -1..1) {
                for (dx in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in 0 until width && ny in 0 until height && mines.contains(nx to ny)) {
                        count++
                    }
                }
            }
            return count
        }

        val cells =
            buildList(width * height) {
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val state = stateOverrides[x to y] ?: CellState.HIDDEN
                        add(
                            Cell(
                                x = x,
                                y = y,
                                isMine = mines.contains(x to y),
                                adjacentMines = adjacentCount(x, y),
                                state = state,
                            ),
                        )
                    }
                }
            }

        val revealedCount = cells.count { it.state == CellState.REVEALED }
        val flaggedCount = cells.count { it.state == CellState.FLAGGED }
        return Board(
            width = width,
            height = height,
            cells = cells,
            status = GameStatus.IN_PROGRESS,
            revealedCount = revealedCount,
            flaggedCount = flaggedCount,
        )
    }
}
