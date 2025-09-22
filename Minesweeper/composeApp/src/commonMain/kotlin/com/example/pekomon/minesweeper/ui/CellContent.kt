package com.example.pekomon.minesweeper.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.pekomon.minesweeper.game.CellState
import com.example.pekomon.minesweeper.ui.theme.numberColor

@Composable
internal fun CellContent(
    state: CellState,
    isMine: Boolean,
    adjacentMines: Int,
    modifier: Modifier = Modifier,
) {
    val content = cellContent(state, isMine, adjacentMines)
    if (content.isEmpty()) {
        return
    }

    Text(
        text = content,
        color = cellContentColor(state, isMine, adjacentMines),
        fontWeight =
            if (state == CellState.REVEALED && adjacentMines > 0) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
}

private fun cellContent(
    state: CellState,
    isMine: Boolean,
    adjacentMines: Int,
): String =
    when {
        state == CellState.FLAGGED -> "🚩"
        state == CellState.REVEALED && isMine -> "💣"
        state == CellState.REVEALED && adjacentMines > 0 -> adjacentMines.toString()
        else -> ""
    }

private fun cellContentColor(
    state: CellState,
    isMine: Boolean,
    adjacentMines: Int,
) =
    when {
        state == CellState.REVEALED && isMine -> MaterialTheme.colorScheme.error
        state == CellState.REVEALED && adjacentMines > 0 -> numberColor(adjacentMines)
        else -> MaterialTheme.colorScheme.onSurface
    }
