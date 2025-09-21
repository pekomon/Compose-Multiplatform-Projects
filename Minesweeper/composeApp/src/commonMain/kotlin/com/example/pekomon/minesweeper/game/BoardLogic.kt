package com.example.pekomon.minesweeper.game

import kotlin.random.Random

/** Creates a new [Board] for the given [difficulty]. */
fun newGame(difficulty: Difficulty, seed: Long? = null): Board = generateBoard(difficulty.width, difficulty.height, difficulty.mines, seed)

/**
 * Generates a new [Board] with [width] x [height] and [mineCount] mines.
 * Mines are placed by shuffling indices using [seed] when provided.
 */
fun generateBoard(width: Int, height: Int, mineCount: Int, seed: Long? = null): Board {
    val total = width * height
    val random = seed?.let { Random(it) } ?: Random
    val indices = (0 until total).shuffled(random)
    val mines = indices.take(mineCount).toSet()

    fun neighbors(x: Int, y: Int): List<Int> {
        val list = mutableListOf<Int>()
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = x + dx
                val ny = y + dy
                if (nx in 0 until width && ny in 0 until height) {
                    list += ny * width + nx
                }
            }
        }
        return list
    }

    val cells = List(total) { idx ->
        val x = idx % width
        val y = idx / width
        val isMine = mines.contains(idx)
        val adjacent = if (isMine) 0 else neighbors(x, y).count { mines.contains(it) }
        Cell(x, y, isMine, adjacent, CellState.HIDDEN)
    }

    return Board(width, height, cells)
}

/** Toggles a flag on the cell at [x], [y] if it is hidden or flagged. */
@Suppress("ReturnCount")
fun toggleFlag(board: Board, x: Int, y: Int): Board {
    if (!board.inBounds(x, y)) return board
    val index = board.indexOf(x, y)
    val cell = board.cells[index]
    if (cell.state == CellState.REVEALED) return board

    val newState = if (cell.state == CellState.FLAGGED) CellState.HIDDEN else CellState.FLAGGED
    val newCell = cell.copy(state = newState)
    val newCells = board.cells.toMutableList().apply { this[index] = newCell }
    val delta = if (newState == CellState.FLAGGED) 1 else -1
    return board.copy(cells = newCells, flaggedCount = board.flaggedCount + delta)
}

/** Reveals the cell at [x], [y]. Handles flood fill for zero-value cells and win/lose states. */
@Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount")
fun reveal(board: Board, x: Int, y: Int): Board {
    if (!board.inBounds(x, y)) return board
    if (board.status != GameStatus.IN_PROGRESS) return board
    val index = board.indexOf(x, y)
    val cell = board.cells[index]
    if (cell.state != CellState.HIDDEN) return board

    if (cell.isMine) {
        val newCell = cell.copy(state = CellState.REVEALED)
        val newCells = board.cells.toMutableList().apply { this[index] = newCell }
        return board.copy(
            cells = newCells,
            status = GameStatus.LOST,
            revealedCount = board.revealedCount + 1,
        )
    }

    val total = board.width * board.height
    val newCells = board.cells.toMutableList()
    var revealed = board.revealedCount
    val toVisit = ArrayDeque<Int>()
    val visited = mutableSetOf<Int>()

    fun enqueue(idx: Int) {
        if (idx in visited) return
        visited += idx
        toVisit += idx
    }

    enqueue(index)
    while (toVisit.isNotEmpty()) {
        val idx = toVisit.removeFirst()
        val c = newCells[idx]
        if (c.state != CellState.HIDDEN) continue
        newCells[idx] = c.copy(state = CellState.REVEALED)
        revealed++
        if (c.adjacentMines == 0) {
            val cx = idx % board.width
            val cy = idx / board.width
            for (dy in -1..1) {
                for (dx in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val nx = cx + dx
                    val ny = cy + dy
                    if (nx in 0 until board.width && ny in 0 until board.height) {
                        enqueue(board.indexOf(nx, ny))
                    }
                }
            }
        }
    }

    val nonMineTotal = total - newCells.count { it.isMine }
    val newStatus = if (revealed == nonMineTotal) GameStatus.WON else board.status
    return board.copy(cells = newCells, revealedCount = revealed, status = newStatus)
}
