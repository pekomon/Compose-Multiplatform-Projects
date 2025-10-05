package com.example.pekomon.minesweeper.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
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

    val reducedMotion = LocalReducedMotion.current
    val shouldAnimate = !reducedMotion
    val flagScaleRaw by animateFloatAsState(
        targetValue =
            when {
                !shouldAnimate -> 1f
                state == CellState.FLAGGED -> 1f
                else -> 0.9f
            },
        animationSpec =
            if (shouldAnimate) {
                tween(durationMillis = 120, easing = FastOutSlowInEasing)
            } else {
                snap()
            },
        label = "flagPop",
    )
    val scaleModifier =
        if (state == CellState.FLAGGED && shouldAnimate) {
            modifier.graphicsLayer {
                val scale = flagScaleRaw
                scaleX = scale
                scaleY = scale
            }
        } else {
            modifier
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
        style = MaterialTheme.typography.bodyLarge,
        modifier = scaleModifier.clearAndSetSemantics { },
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

@Composable
private fun cellContentColor(
    state: CellState,
    isMine: Boolean,
    adjacentMines: Int,
) = when {
    state == CellState.REVEALED && isMine -> MaterialTheme.colorScheme.error
    state == CellState.REVEALED && adjacentMines > 0 -> numberColor(adjacentMines)
    else -> MaterialTheme.colorScheme.onSurface
}
