package com.example.pekomon.minesweeper.game

/**
 * Tiny facade to drive the game logic from the UI layer.
 * The [board] is immutable and replaced on each operation.
 */
class GameApi(
    initialDifficulty: Difficulty = Difficulty.EASY,
    seed: Long? = null,
) {
    /** Current board snapshot. */
    var board: Board = newGame(initialDifficulty, seed)
        private set

    /** Resets the game to a new board with [difficulty]. */
    fun reset(
        difficulty: Difficulty = Difficulty.EASY,
        seed: Long? = null,
    ) {
        board = newGame(difficulty, seed)
    }

    /** Reveals the cell at [x], [y]. */
    fun onReveal(
        x: Int,
        y: Int,
    ) {
        board = reveal(board, x, y)
    }

    /** Toggles a flag on the cell at [x], [y]. */
    fun onToggleFlag(
        x: Int,
        y: Int,
    ) {
        board = toggleFlag(board, x, y)
    }
}
