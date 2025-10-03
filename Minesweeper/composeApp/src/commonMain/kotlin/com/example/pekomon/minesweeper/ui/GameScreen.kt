package com.example.pekomon.minesweeper.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import com.example.pekomon.minesweeper.composeapp.generated.resources.new_record_difficulty
import com.example.pekomon.minesweeper.composeapp.generated.resources.new_record_time
import com.example.pekomon.minesweeper.composeapp.generated.resources.new_record_title
import com.example.pekomon.minesweeper.composeapp.generated.resources.reset_button
import com.example.pekomon.minesweeper.composeapp.generated.resources.timer_label
import com.example.pekomon.minesweeper.game.Board
import com.example.pekomon.minesweeper.game.Cell
import com.example.pekomon.minesweeper.game.CellState
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.game.GameApi
import com.example.pekomon.minesweeper.game.GameStatus
import com.example.pekomon.minesweeper.history.HistoryStore
import com.example.pekomon.minesweeper.history.RunRecord
import com.example.pekomon.minesweeper.history.isNewRecord
import com.example.pekomon.minesweeper.i18n.t
import com.example.pekomon.minesweeper.lifecycle.AppLifecycle
import com.example.pekomon.minesweeper.lifecycle.AppLifecycleObserver
import com.example.pekomon.minesweeper.timer.GameTimerState
import com.example.pekomon.minesweeper.util.formatMillisToMmSs
import com.example.pekomon.minesweeper.ui.theme.flaggedCellColor
import com.example.pekomon.minesweeper.ui.theme.hiddenCellColor
import com.example.pekomon.minesweeper.ui.theme.revealedCellColor
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    initialDifficulty: Difficulty = Difficulty.EASY,
    historyStore: HistoryStore,
    reducedMotionEnabled: Boolean = false,
    onDifficultyChanged: (Difficulty) -> Unit = {},
) {
    val api = remember { GameApi(initialDifficulty) }
    var difficulty by remember { mutableStateOf(initialDifficulty) }
    var board by remember { mutableStateOf(api.board) }
    val coroutineScope = rememberCoroutineScope()
    val timer = remember(coroutineScope) { GameTimerState(coroutineScope = coroutineScope) }
    val elapsed by timer.elapsed.collectAsState()
    val elapsedSeconds = elapsed.inWholeSeconds.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    var difficultyMenuExpanded by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var historyVersion by remember { mutableStateOf(0) }
    var winRecorded by remember { mutableStateOf(false) }
    var celebration by remember { mutableStateOf<NewRecordCelebration?>(null) }

    fun refreshBoard() {
        board = api.board
    }

    fun resetGame(newDifficulty: Difficulty = difficulty) {
        api.reset(newDifficulty)
        board = api.board
        difficulty = newDifficulty
        timer.reset()
        winRecorded = false
        celebration = null
    }

    val statusEmoji = board.status.asStatusEmoji()

    GameTimerEffects(board = board, timer = timer)

    GameCelebrationEffects(
        board = board,
        difficulty = difficulty,
        elapsed = elapsed,
        historyStore = historyStore,
        winRecorded = winRecorded,
        onWinRecordedChange = { winRecorded = it },
        onHistoryVersionIncrement = { historyVersion += 1 },
        onCelebrationChange = { celebration = it },
    )

    AutoDismissCelebration(celebration = celebration) {
        celebration = null
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
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
            val outerPadding = 16.dp
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = outerPadding, vertical = outerPadding),
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val cellSpacing = 8.dp
                    val columns = board.width.coerceAtLeast(1)
                    val rows = board.height.coerceAtLeast(1)

                    val maxContentWidth = maxWidth
                    val availableCellSpace = maxContentWidth - cellSpacing * (columns - 1)
                    val minCellSize = 28.dp
                    val maxCellSize = 48.dp
                    val desiredCellSize = availableCellSpace / columns
                    val cellSize = desiredCellSize.coerceIn(minCellSize, maxCellSize)
                    val boardWidth = cellSize * columns + cellSpacing * (columns - 1)
                    val boardHeight = cellSize * rows + cellSpacing * (rows - 1)

                    val hasBoundedHeight = maxHeight != Infinity && maxHeight != Unspecified
                    val availableHeight = if (hasBoundedHeight) maxHeight else Infinity
                    val needsScroll = hasBoundedHeight && boardHeight > availableHeight

                    val scrollModifier = if (needsScroll) Modifier.verticalScroll(scrollState) else Modifier

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .then(scrollModifier),
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

                val celebrationState = celebration
                if (celebrationState != null && !reducedMotionEnabled) {
                    ConfettiOverlay(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .align(Alignment.Center)
                                .zIndex(0.5f),
                    )
                }

                AnimatedVisibility(
                    visible = celebrationState != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .zIndex(1f),
                ) {
                    celebrationState?.let {
                        NewRecordBanner(
                            celebration = it,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }

    if (showHistoryDialog) {
        HistoryDialog(
            currentDifficulty = difficulty,
            historyStore = historyStore,
            dataVersion = historyVersion,
            onClose = { showHistoryDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    val actionSpacing = 8.dp
    TopAppBar(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues())
                .padding(horizontal = 16.dp),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
        title = {
            Text(
                text = t(Res.string.timer_label, statusEmoji, elapsedSeconds),
                style = MaterialTheme.typography.titleLarge,
            )
        },
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
                OutlinedButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    Text(
                        text = t(Res.string.history_button),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Button(
                    onClick = onReset,
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    Text(
                        text = t(Res.string.reset_button),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
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
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier.heightIn(min = 48.dp),
        ) {
            Text(
                text = t(Res.string.difficulty, difficulty.localizedLabel()),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
        ) {
            difficulties.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.localizedLabel(),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    onClick = { onSelected(option) },
                )
            }
        }
    }
}

@Composable
private fun NewRecordBanner(
    celebration: NewRecordCelebration,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = t(Res.string.new_record_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = t(Res.string.new_record_time, formatMillisToMmSs(celebration.millis)),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = t(Res.string.new_record_difficulty, celebration.difficulty.localizedLabel()),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun GameTimerEffects(
    board: Board,
    timer: GameTimerState,
) {
    LaunchedEffect(board.status, board.revealedCount) {
        when {
            board.status != GameStatus.IN_PROGRESS -> timer.pause()
            board.revealedCount == 0 -> timer.reset()
            else -> {
                if (!timer.isRunning.value) {
                    if (timer.elapsed.value == Duration.ZERO) {
                        timer.start()
                    } else {
                        timer.resume()
                    }
                }
            }
        }
    }

    val currentStatus = rememberUpdatedState(board.status)
    val currentRevealedCount = rememberUpdatedState(board.revealedCount)

    DisposableEffect(timer) {
        val observer =
            object : AppLifecycleObserver {
                override fun onEnterForeground() {
                    val status = currentStatus.value
                    val revealed = currentRevealedCount.value
                    if (status == GameStatus.IN_PROGRESS && revealed > 0) {
                        timer.resume()
                    }
                }

                override fun onEnterBackground() {
                    timer.pause()
                }
            }

        AppLifecycle.register(observer)

        onDispose {
            AppLifecycle.unregister(observer)
        }
    }
}

@Composable
private fun GameCelebrationEffects(
    board: Board,
    difficulty: Difficulty,
    elapsed: Duration,
    historyStore: HistoryStore,
    winRecorded: Boolean,
    onWinRecordedChange: (Boolean) -> Unit,
    onHistoryVersionIncrement: () -> Unit,
    onCelebrationChange: (NewRecordCelebration?) -> Unit,
) {
    LaunchedEffect(board.status, historyStore, difficulty) {
        if (board.status == GameStatus.WON && !winRecorded) {
            val elapsedMillis = elapsed.inWholeMilliseconds
            val previousRuns =
                runCatching { historyStore.getTop10(difficulty) }.getOrElse { emptyList() }
            val newRecordAchieved =
                isNewRecord(
                    currentMillis = elapsedMillis,
                    difficulty = difficulty,
                    history = previousRuns,
                )
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val result =
                runCatching {
                    historyStore.addRun(
                        RunRecord(
                            difficulty = difficulty,
                            millis = elapsedMillis,
                            epochMillis = timestamp,
                        ),
                    )
                }
            if (result.isSuccess) {
                onHistoryVersionIncrement()
                if (newRecordAchieved) {
                    onCelebrationChange(
                        NewRecordCelebration(
                            millis = elapsedMillis,
                            difficulty = difficulty,
                            token = timestamp,
                        ),
                    )
                }
            }
            onWinRecordedChange(true)
        }
        if (board.status == GameStatus.IN_PROGRESS) {
            onWinRecordedChange(false)
            onCelebrationChange(null)
        }
        if (board.status == GameStatus.LOST) {
            onCelebrationChange(null)
        }
    }
}

@Composable
private fun AutoDismissCelebration(
    celebration: NewRecordCelebration?,
    onTimeout: () -> Unit,
) {
    val latestCelebration = rememberUpdatedState(celebration)
    val latestTimeout = rememberUpdatedState(onTimeout)

    LaunchedEffect(celebration?.token) {
        val activeToken = celebration?.token ?: return@LaunchedEffect
        delay(NEW_RECORD_DISPLAY_DURATION_MS)
        if (latestCelebration.value?.token == activeToken) {
            latestTimeout.value()
        }
    }
}

private fun GameStatus.asStatusEmoji(): String =
    when (this) {
        GameStatus.IN_PROGRESS -> "⏳"
        GameStatus.WON -> "🏆"
        GameStatus.LOST -> "💥"
    }

private data class NewRecordCelebration(
    val millis: Long,
    val difficulty: Difficulty,
    val token: Long,
)

private const val NEW_RECORD_DISPLAY_DURATION_MS = 5_000L

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
