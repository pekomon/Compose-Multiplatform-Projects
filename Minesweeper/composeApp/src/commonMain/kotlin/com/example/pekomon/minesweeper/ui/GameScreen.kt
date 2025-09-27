package com.example.pekomon.minesweeper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Dp.Companion.Infinity
import androidx.compose.ui.unit.Dp.Companion.Unspecified
import androidx.compose.ui.unit.dp
import com.example.pekomon.minesweeper.composeapp.generated.resources.Res
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_easy
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_hard
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_medium
import com.example.pekomon.minesweeper.composeapp.generated.resources.history_button
import com.example.pekomon.minesweeper.composeapp.generated.resources.reset_button
import com.example.pekomon.minesweeper.composeapp.generated.resources.timer_label
import com.example.pekomon.minesweeper.game.Board
import com.example.pekomon.minesweeper.game.Cell
import com.example.pekomon.minesweeper.game.CellState
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.game.GameApi
import com.example.pekomon.minesweeper.game.GameStatus
import com.example.pekomon.minesweeper.history.InMemoryHistoryStore
import com.example.pekomon.minesweeper.history.RunRecord
import com.example.pekomon.minesweeper.i18n.t
import com.example.pekomon.minesweeper.ui.theme.flaggedCellColor
import com.example.pekomon.minesweeper.ui.theme.hiddenCellColor
import com.example.pekomon.minesweeper.ui.theme.revealedCellColor
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    initialDifficulty: Difficulty = Difficulty.EASY,
    onDifficultyChanged: (Difficulty) -> Unit = {},
) {
    val api = remember { GameApi(initialDifficulty) }
    var difficulty by remember { mutableStateOf(initialDifficulty) }
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

    val statusEmoji =
        when (board.status) {
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

    val scrollState = rememberScrollState()

    Surface(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                GameTopBar(
                    difficulty = difficulty,
                    onDifficultyClick = { difficultyMenuExpanded = true },
                    difficultyMenuExpanded = difficultyMenuExpanded,
                    onDifficultyDismiss = { difficultyMenuExpanded = false },
                    onDifficultySelected = {
                        difficultyMenuExpanded = false
                        resetGame(it)
                        onDifficultyChanged(it)
                    },
                    onReset = { resetGame(difficulty) },
                    elapsedSeconds = elapsedSeconds,
                    statusEmoji = statusEmoji,
                    onHistoryClick = { showHistoryDialog = true },
                )
            },
        ) { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(WindowInsets.safeDrawing.asPaddingValues()),
                contentAlignment = Alignment.TopCenter,
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val horizontalPadding = 16.dp
                    val verticalPadding = 24.dp
                    val cellSpacing = 8.dp
                    val columns = board.width.coerceAtLeast(1)
                    val rows = board.height.coerceAtLeast(1)

                    val maxContentWidth = maxWidth - horizontalPadding * 2
                    val availableCellSpace = maxContentWidth - cellSpacing * (columns - 1)
                    val minCellSize = 28.dp
                    val maxCellSize = 48.dp
                    val desiredCellSize = availableCellSpace / columns
                    val cellSize = desiredCellSize.coerceIn(minCellSize, maxCellSize)
                    val boardWidth = cellSize * columns + cellSpacing * (columns - 1)
                    val boardHeight = cellSize * rows + cellSpacing * (rows - 1)

                    val hasBoundedHeight = maxHeight != Infinity && maxHeight != Unspecified
                    val availableHeight = if (hasBoundedHeight) maxHeight - verticalPadding * 2 else Infinity
                    val needsScroll = hasBoundedHeight && boardHeight > availableHeight

                    val scrollModifier = if (needsScroll) Modifier.verticalScroll(scrollState) else Modifier

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = horizontalPadding)
                                .then(scrollModifier)
                                .padding(vertical = verticalPadding),
                        contentAlignment = Alignment.TopCenter,
                    ) {
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
                            modifier = Modifier.width(boardWidth).height(boardHeight),
                            cellSpacing = cellSpacing,
                            cellSize = cellSize,
                        )
                    }
                }
            }
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
private fun GameTopBar(
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

    val horizontalPadding = 16.dp
    val actionSpacing = 8.dp
    val topBarBackground = MaterialTheme.colors.surface
    val contentColor = contentColorFor(topBarBackground)

    TopAppBar(
        modifier =
            modifier
                .statusBarsPadding()
                .padding(horizontal = horizontalPadding)
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues()),
        title = { Text(text = t(Res.string.timer_label, statusEmoji, elapsedSeconds)) },
        navigationIcon = {
            DifficultyButton(
                onClick = onDifficultyClick,
                expanded = difficultyMenuExpanded,
                onDismissRequest = onDifficultyDismiss,
                difficulties = difficulties,
                onSelected = onDifficultySelected,
                difficulty = difficulty,
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(actionSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = onHistoryClick) {
                    Text(text = t(Res.string.history_button))
                }

                Button(onClick = onReset) {
                    Text(text = t(Res.string.reset_button))
                }
            }
        },
        backgroundColor = topBarBackground,
        contentColor = contentColor,
        elevation = AppBarDefaults.TopAppBarElevation,
    )
}

@Composable
private fun DifficultyButton(
    onClick: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    difficulties: List<Difficulty>,
    onSelected: (Difficulty) -> Unit,
    difficulty: Difficulty,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Button(onClick = onClick) {
            Text(text = t(Res.string.difficulty, difficulty.localizedLabel()))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
        ) {
            difficulties.forEach { option ->
                DropdownMenuItem(onClick = { onSelected(option) }) {
                    Text(option.localizedLabel())
                }
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
    cellSize: Dp,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(board.width),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(cellSpacing),
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
        userScrollEnabled = false,
    ) {
        items(board.cells, key = { it.y * board.width + it.x }) { cell ->
            CellView(
                cell = cell,
                onReveal = { onReveal(cell.x, cell.y) },
                onToggleFlag = { onToggleFlag(cell.x, cell.y) },
                boardStatus = board.status,
                modifier = Modifier.size(cellSize),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CellView(
    cell: Cell,
    onReveal: () -> Unit,
    onToggleFlag: () -> Unit,
    boardStatus: GameStatus,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when (cell.state) {
            CellState.HIDDEN -> hiddenCellColor()
            CellState.REVEALED -> revealedCellColor()
            CellState.FLAGGED -> flaggedCellColor()
        }
    val interactionModifier =
        Modifier.cellInteractions(
            revealEnabled = boardStatus == GameStatus.IN_PROGRESS && cell.state == CellState.HIDDEN,
            toggleEnabled = cell.state != CellState.REVEALED,
            onReveal = onReveal,
            onToggleFlag = onToggleFlag,
        )

    CellContainer(
        backgroundColor = backgroundColor,
        cornerRadius = 6.dp,
        modifier = modifier,
        interactionModifier = interactionModifier,
    ) {
        CellContent(
            state = cell.state,
            isMine = cell.isMine,
            adjacentMines = cell.adjacentMines,
        )
    }
}

private fun Difficulty.toStringResource(): StringResource =
    when (this) {
        Difficulty.EASY -> Res.string.difficulty_easy
        Difficulty.MEDIUM -> Res.string.difficulty_medium
        Difficulty.HARD -> Res.string.difficulty_hard
    }

@Composable
private fun Difficulty.localizedLabel(): String = stringResource(toStringResource())
