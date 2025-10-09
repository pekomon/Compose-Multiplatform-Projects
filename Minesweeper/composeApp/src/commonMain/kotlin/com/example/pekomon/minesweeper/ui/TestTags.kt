package com.example.pekomon.minesweeper.ui

object TestTags {
    const val ROOT = "root-scaffold"
    const val BTN_DIFFICULTY = "btn-difficulty"
    const val BTN_RESET = "btn-reset"
    const val TXT_TIMER = "txt-timer"
    const val TXT_MINES = "txt-mines"

    fun cell(
        row: Int,
        col: Int,
    ) = "cell-$row-$col"

    const val BTN_HISTORY = "btn-history"
    const val DIALOG_HISTORY = "dialog-history"
}
