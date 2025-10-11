package com.example.pekomon.minesweeper.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Dp.Companion.Infinity
import androidx.compose.ui.unit.Dp.Companion.Unspecified
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.pekomon.minesweeper.audio.SoundPlayer
import com.example.pekomon.minesweeper.audio.rememberSoundPlayer
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
import com.example.pekomon.minesweeper.composeapp.generated.resources.settings_animations
import com.example.pekomon.minesweeper.composeapp.generated.resources.settings_sounds
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
import com.example.pekomon.minesweeper.ui.theme.flaggedCellColor
import com.example.pekomon.minesweeper.ui.theme.hiddenCellColor
import com.example.pekomon.minesweeper.ui.theme.revealedCellColor
import com.example.pekomon.minesweeper.util.formatMillisToMmSs
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
    soundsEnabled: Boolean,
    animationsEnabled: Boolean,
    onDifficultyChanged: (Difficulty) -> Unit = {},
    onSoundsEnabledChange: (Boolean) -> Unit = {},
    onAnimationsEnabledChange: (Boolean) -> Unit = {},
) {
    CompositionLocalProvider(LocalReducedMotion provides !animationsEnabled) {
        GameScreenContent(
            modifier = modifier,
            initialDifficulty = initialDifficulty,
            historyStore = historyStore,
            soundsEnabled = soundsEnabled,
            animationsEnabled = animationsEnabled,
            onDifficultyChanged = onDifficultyChanged,
            onSoundsEnabledChange = onSoundsEnabledChange,
            onAnimationsEnabledChange = onAnimationsEnabledChange,
        )
    }
}

@Composable
private fun GameScreenContent(
    modifier: Modifier = Modifier,
    initialDifficulty: Difficulty = Difficulty.EASY,
    historyStore: HistoryStore,
    soundsEnabled: Boolean,
    animationsEnabled: Boolean,
    onDifficultyChanged: (Difficulty) -> Unit,
    onSoundsEnabledChange: (Boolean) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit,
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
    val soundPlayer = rememberSoundPlayer(soundsEnabled)

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
        soundPlayer = soundPlayer,
    )

    AutoDismissCelebration(celebration = celebration) {
        celebration = null
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize().testTag(TestTags.ROOT),
        color = MaterialTheme.colorScheme.surface,
    ) {
        val safePadding = WindowInsets.safeDrawing.asPaddingValues()
        val contentPadding = 16.dp
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(safePadding)
                    .padding(horizontal = contentPadding, vertical = contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GameHud(
                modifier = Modifier.fillMaxWidth(),
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
                animationsEnabled = animationsEnabled,
                soundsEnabled = soundsEnabled,
                onSoundsEnabledChange = onSoundsEnabledChange,
                onAnimationsEnabledChange = onAnimationsEnabledChange,
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
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
                                    soundPlayer.reveal()
                                    refreshBoard()
                                }
                            },
                            onToggleFlag = { x, y ->
                                if (board.status == GameStatus.IN_PROGRESS) {
                                    api.onToggleFlag(x, y)
                                    soundPlayer.click()
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
                val reducedMotionEnabled = LocalReducedMotion.current
                if (celebrationState != null && !reducedMotionEnabled) {
                    ConfettiOverlay(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                                .zIndex(0.5f),
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .align(Alignment.TopCenter)
                            .zIndex(1f),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = celebrationState != null,
                        enter = fadeIn(),
                        exit = fadeOut(),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameHud(
    difficulty: Difficulty,
    onDifficultyClick: () -> Unit,
    difficultyMenuExpanded: Boolean,
    onDifficultyDismiss: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onReset: () -> Unit,
    elapsedSeconds: Int,
    statusEmoji: String,
    onHistoryClick: () -> Unit,
    animationsEnabled: Boolean,
    soundsEnabled: Boolean,
    onSoundsEnabledChange: (Boolean) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val difficulties = remember { Difficulty.values().toList() }

    val timerDescription = t(Res.string.timer_label, statusEmoji, elapsedSeconds)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = timerDescription,
            style = MaterialTheme.typography.titleLarge,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.TXT_TIMER)
                    .semantics {
                        contentDescription = timerDescription
                    },
            textAlign = TextAlign.Center,
        )

        val soundsLabel = t(Res.string.settings_sounds)
        val animationsLabel = t(Res.string.settings_animations)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconToggleButton(
                checked = soundsEnabled,
                onCheckedChange = onSoundsEnabledChange,
                modifier =
                    Modifier
                        .size(48.dp)
                        .semantics {
                            contentDescription = soundsLabel
                        },
            ) {
                val soundTint =
                    if (soundsEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                Text(
                    text = if (soundsEnabled) "🔊" else "🔇",
                    style = MaterialTheme.typography.titleLarge,
                    color = soundTint,
                )
            }

            IconToggleButton(
                checked = animationsEnabled,
                onCheckedChange = onAnimationsEnabledChange,
                modifier =
                    Modifier
                        .size(48.dp)
                        .semantics {
                            contentDescription = animationsLabel
                        },
            ) {
                val animationTint =
                    if (animationsEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                Text(
                    text = if (animationsEnabled) "✨" else "🚫✨",
                    style = MaterialTheme.typography.titleLarge,
                    color = animationTint,
                )
            }

            DifficultyButton(
                onClick = onDifficultyClick,
                expanded = difficultyMenuExpanded,
                onDismissRequest = onDifficultyDismiss,
                difficulties = difficulties,
                onSelected = onDifficultySelected,
                difficulty = difficulty,
                animationsEnabled = animationsEnabled,
            )

            OutlinedButton(
                onClick = onHistoryClick,
                modifier =
                    Modifier
                        .testTag(TestTags.BTN_HISTORY)
                        .heightIn(min = 48.dp),
            ) {
                Text(
                    text = t(Res.string.history_button),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            val resetInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = onReset,
                modifier =
                    Modifier
                        .testTag(TestTags.BTN_RESET)
                        .heightIn(min = 48.dp)
                        .pressScale(
                            interactionSource = resetInteraction,
                            animationsEnabled = animationsEnabled,
                            label = "resetPress",
                        ),
                interactionSource = resetInteraction,
            ) {
                Text(
                    text = t(Res.string.reset_button),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun DifficultyButton(
    onClick: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    difficulties: List<Difficulty>,
    onSelected: (Difficulty) -> Unit,
    difficulty: Difficulty,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        FilledTonalButton(
            onClick = onClick,
            modifier =
                Modifier
                    .testTag(TestTags.BTN_DIFFICULTY)
                    .heightIn(min = 48.dp)
                    .pressScale(
                        interactionSource = interactionSource,
                        animationsEnabled = animationsEnabled,
                        label = "difficultyPress",
                    ),
            interactionSource = interactionSource,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
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
    soundPlayer: SoundPlayer,
) {
    LaunchedEffect(board.status, historyStore, difficulty) {
        if (board.status == GameStatus.WON && !winRecorded) {
            soundPlayer.win()
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
            soundPlayer.lose()
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
    val reducedMotion = LocalReducedMotion.current
    val animationEnabled = !reducedMotion
    val targetBackground =
        when (cell.state) {
            CellState.HIDDEN -> hiddenCellColor()
            CellState.REVEALED -> revealedCellColor()
            CellState.FLAGGED -> flaggedCellColor()
        }
    val backgroundColor by animateColorAsState(
        targetValue = targetBackground,
        animationSpec = if (animationEnabled) tween(durationMillis = 220) else snap(),
        label = "cellBackground",
    )
    val revealScaleRaw by animateFloatAsState(
        targetValue =
            when {
                !animationEnabled -> 1f
                cell.state == CellState.REVEALED -> 1f
                else -> 0.95f
            },
        animationSpec =
            if (animationEnabled) {
                tween(durationMillis = 220, easing = FastOutSlowInEasing)
            } else {
                snap()
            },
        label = "cellRevealScale",
    )
    val revealScale = if (animationEnabled && cell.state == CellState.REVEALED) revealScaleRaw else 1f
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
        modifier =
            modifier
                .testTag(TestTags.cell(cell.y, cell.x))
                .graphicsLayer {
                    scaleX = revealScale
                    scaleY = revealScale
                },
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

@Composable
private fun Modifier.pressScale(
    interactionSource: MutableInteractionSource,
    animationsEnabled: Boolean,
    label: String,
    pressedScale: Float = 0.98f,
): Modifier {
    val reducedMotion = LocalReducedMotion.current
    val shouldAnimate = animationsEnabled && !reducedMotion
    val isPressed by interactionSource.collectIsPressedAsState()
    val targetScale = if (isPressed) pressedScale else 1f
    val animationSpec = if (shouldAnimate) tween<Float>(durationMillis = 120, easing = FastOutSlowInEasing) else snap()
    val scale by animateFloatAsState(
        targetValue = if (shouldAnimate) targetScale else 1f,
        animationSpec = animationSpec,
        label = label,
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
