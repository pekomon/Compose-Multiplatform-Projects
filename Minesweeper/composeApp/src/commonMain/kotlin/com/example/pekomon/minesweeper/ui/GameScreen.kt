package com.example.pekomon.minesweeper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pekomon.minesweeper.game.Board
import com.example.pekomon.minesweeper.game.Cell
import com.example.pekomon.minesweeper.game.CellState
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.game.GameApi
import com.example.pekomon.minesweeper.game.GameStatus
import com.example.pekomon.minesweeper.history.InMemoryHistoryStore
import com.example.pekomon.minesweeper.history.RunRecord
import com.example.pekomon.minesweeper.ui.theme.cellBorderColor
import com.example.pekomon.minesweeper.ui.theme.flaggedCellColor
import com.example.pekomon.minesweeper.ui.theme.hiddenCellColor
import com.example.pekomon.minesweeper.ui.theme.numberColor
import com.example.pekomon.minesweeper.ui.theme.revealedCellColor
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val api = remember { GameApi(Difficulty.EASY) }
    var difficulty by remember { mutableStateOf(Difficulty.EASY) }
    var board by remember { mutableStateOf(api.board) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }
    var difficultyMenuExpanded by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var historyVersion by remember { mutableStateOf(0) }
    var winRecorded by remember { mutableStateOf(false) }

    fun refreshBoard() {
        board = api.board
    }

    fun resetGame(newDifficulty: Difficulty = difficulty) {
        api.reset(newDifficulty)
        board = api.board
        difficulty = newDifficulty
        elapsedSeconds = 0
        timerRunning = false
    }

    val statusEmoji = when (board.status) {
        GameStatus.IN_PROGRESS -> "⏳"
        GameStatus.WON -> "🏆"
        GameStatus.LOST -> "💥"
    }

    LaunchedEffect(board.status, board.revealedCount) {
        when {
            board.status != GameStatus.IN_PROGRESS -> timerRunning = false
            board.revealedCount == 0 -> {
                elapsedSeconds = 0
                timerRunning = false
            }
            else -> timerRunning = true
        }
    }

    LaunchedEffect(timerRunning) {
        while (timerRunning) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    LaunchedEffect(board.status) {
        if (board.status == GameStatus.WON && !winRecorded) {
            val elapsedMillis = elapsedSeconds * 1000L
            InMemoryHistoryStore.add(
                RunRecord(
                    difficulty = difficulty,
                    elapsedMillis = elapsedMillis,
                    epochMillis = Clock.System.now().toEpochMilliseconds(),
                ),
            )
            historyVersion += 1
            winRecorded = true
        }
        if (board.status == GameStatus.IN_PROGRESS) {
            winRecorded = false
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            TopBar(
                difficulty = difficulty,
                onDifficultyClick = { difficultyMenuExpanded = true },
                difficultyMenuExpanded = difficultyMenuExpanded,
                onDifficultyDismiss = { difficultyMenuExpanded = false },
                onDifficultySelected = {
                    difficultyMenuExpanded = false
                    resetGame(it)
                },
                onReset = { resetGame(difficulty) },
                elapsedSeconds = elapsedSeconds,
                statusEmoji = statusEmoji,
                onHistoryClick = { showHistoryDialog = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            BoardView(
                board = board,
                onReveal = { x, y ->
                    if (board.status == GameStatus.IN_PROGRESS) {
                        api.onReveal(x, y)
                        refreshBoard()
                    }
                },
                onToggleFlag = { x, y ->
                    if (board.status == GameStatus.IN_PROGRESS) {
                        api.onToggleFlag(x, y)
                        refreshBoard()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
                cellSpacing = 4.dp,
            )
        }
    }

    if (showHistoryDialog) {
        key(historyVersion) {
            HistoryDialog(
                currentDifficulty = difficulty,
                onClose = { showHistoryDialog = false },
            )
        }
    }
}

@Composable
private fun TopBar(
    difficulty: Difficulty,
    onDifficultyClick: () -> Unit,
    difficultyMenuExpanded: Boolean,
    onDifficultyDismiss: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onReset: () -> Unit,
    elapsedSeconds: Int,
    statusEmoji: String,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val difficulties = remember { Difficulty.values().toList() }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Button(onClick = onDifficultyClick) {
                Text(text = difficulty.toDisplayName())
            }
            DropdownMenu(
                expanded = difficultyMenuExpanded,
                onDismissRequest = onDifficultyDismiss,
            ) {
                difficulties.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.toDisplayName()) },
                        onClick = { onDifficultySelected(option) },
                    )
                }
            }
        }

        Text(text = "$statusEmoji ${elapsedSeconds}s")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = onHistoryClick) {
                Text(text = "History")
            }

            Button(onClick = onReset) {
                Text(text = "Reset")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoardView(
    board: Board,
    onReveal: (Int, Int) -> Unit,
    onToggleFlag: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    cellSpacing: Dp = 2.dp,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(board.width),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(cellSpacing),
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        items(board.cells, key = { it.y * board.width + it.x }) { cell ->
            CellView(
                cell = cell,
                onReveal = { onReveal(cell.x, cell.y) },
                onToggleFlag = { onToggleFlag(cell.x, cell.y) },
                boardStatus = board.status,
            )
        }
    }
}

@Suppress("CyclomaticComplexMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CellView(
    cell: Cell,
    onReveal: () -> Unit,
    onToggleFlag: () -> Unit,
    boardStatus: GameStatus,
    modifier: Modifier = Modifier,
) {
    val cornerRadius = 6.dp
    val updatedReveal by rememberUpdatedState(onReveal)
    val updatedToggle by rememberUpdatedState(onToggleFlag)
    val backgroundColor = when (cell.state) {
        CellState.HIDDEN -> hiddenCellColor()
        CellState.REVEALED -> revealedCellColor()
        CellState.FLAGGED -> flaggedCellColor()
    }
    val textColor = when {
        cell.state == CellState.REVEALED && cell.isMine -> MaterialTheme.colorScheme.error
        cell.state == CellState.REVEALED && cell.adjacentMines > 0 -> numberColor(cell.adjacentMines)
        else -> MaterialTheme.colorScheme.onSurface
    }
    val content = when {
        cell.state == CellState.FLAGGED -> "🚩"
        cell.state == CellState.REVEALED && cell.isMine -> "💣"
        cell.state == CellState.REVEALED && cell.adjacentMines > 0 -> cell.adjacentMines.toString()
        else -> ""
    }
    val revealEnabled = boardStatus == GameStatus.IN_PROGRESS && cell.state == CellState.HIDDEN

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .border(1.dp, cellBorderColor(), RoundedCornerShape(cornerRadius))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (revealEnabled) {
                            updatedReveal()
                        }
                    },
                    onLongPress = {
                        if (cell.state != CellState.REVEALED) {
                            updatedToggle()
                        }
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        if (content.isNotEmpty()) {
            Text(
                text = content,
                color = textColor,
                fontWeight = if (cell.state == CellState.REVEALED && cell.adjacentMines > 0) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun Difficulty.toDisplayName(): String {
    val name = name.lowercase()
    return name.replaceFirstChar { it.titlecase() }
}
