package com.example.pekomon.minesweeper.game

/**
 * The preset difficulties for Minesweeper games.
 *
 * @property width board width in cells
 * @property height board height in cells
 * @property mines number of mines on the board
 */
enum class Difficulty(
    val width: Int,
    val height: Int,
    val mines: Int,
) {
    /** 9x9 board with 10 mines. */
    EASY(9, 9, 10),

    /** 16x16 board with 40 mines. */
    MEDIUM(16, 16, 40),

    /** 30x16 board with 99 mines. */
    HARD(30, 16, 99),
}

/**
 * Represents the visual state of a cell.
 */
enum class CellState {
    /** Cell has not been revealed. */
    HIDDEN,

    /** Cell has been revealed. */
    REVEALED,

    /** Cell has been flagged by the user. */
    FLAGGED,
}

/**
 * A single cell on a Minesweeper [Board].
 *
 * @property x x coordinate starting at 0
 * @property y y coordinate starting at 0
 * @property isMine true if this cell contains a mine
 * @property adjacentMines number of neighboring mines
 * @property state current visual [CellState]
 */
data class Cell(
    val x: Int,
    val y: Int,
    val isMine: Boolean,
    val adjacentMines: Int,
    val state: CellState,
)

/**
 * The current status of a game.
 */
enum class GameStatus {
    /** Game is still in progress. */
    IN_PROGRESS,

    /** All non‑mine cells are revealed. */
    WON,

    /** A mine was revealed. */
    LOST,
}

/**
 * Immutable representation of a Minesweeper board.
 *
 * @property width board width in cells
 * @property height board height in cells
 * @property cells list of all cells row by row
 * @property status current [GameStatus]
 * @property revealedCount number of revealed cells
 * @property flaggedCount number of flagged cells
 */
data class Board(
    val width: Int,
    val height: Int,
    val cells: List<Cell>,
    val status: GameStatus = GameStatus.IN_PROGRESS,
    val revealedCount: Int = 0,
    val flaggedCount: Int = 0,
) {
    /** Returns the index within [cells] for coordinates [x], [y]. */
    fun indexOf(
        x: Int,
        y: Int,
    ): Int = y * width + x

    /** Returns the cell at coordinates [x], [y]. */
    fun cellAt(
        x: Int,
        y: Int,
    ): Cell = cells[indexOf(x, y)]

    /** Returns true if [x], [y] are within board bounds. */
    fun inBounds(
        x: Int,
        y: Int,
    ): Boolean = x in 0 until width && y in 0 until height
}
