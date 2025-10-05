package com.example.pekomon.minesweeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import com.example.pekomon.minesweeper.composeapp.generated.resources.Res
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_easy
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_hard
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_medium
import com.example.pekomon.minesweeper.composeapp.generated.resources.history_close
import com.example.pekomon.minesweeper.composeapp.generated.resources.history_no_wins
import com.example.pekomon.minesweeper.composeapp.generated.resources.history_title
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.history.HistoryStore
import com.example.pekomon.minesweeper.history.RunRecord
import com.example.pekomon.minesweeper.i18n.t
import com.example.pekomon.minesweeper.util.formatMillisToMmSs
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HistoryDialog(
    currentDifficulty: Difficulty,
    historyStore: HistoryStore,
    dataVersion: Int,
    onClose: () -> Unit,
) {
    var selectedDifficulty by remember(currentDifficulty) { mutableStateOf(currentDifficulty) }
    val difficulties = remember { Difficulty.values().toList() }
    var records by remember { mutableStateOf<List<RunRecord>>(emptyList()) }

    LaunchedEffect(historyStore, selectedDifficulty, dataVersion) {
        records = runCatching { historyStore.getTop10(selectedDifficulty) }.getOrElse { emptyList() }
    }

    val closeLabel = stringResource(Res.string.history_close)
    val titleLabel = stringResource(Res.string.history_title)
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                onClick = onClose,
                modifier =
                    Modifier
                        .minimumInteractiveComponentSize()
                        .semantics {
                            role = Role.Button
                            contentDescription = closeLabel
                        },
            ) {
                Text(
                    text = closeLabel,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        title = {
            Text(
                text = titleLabel,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    difficulties.forEach { difficultyOption ->
                        val isSelected = selectedDifficulty == difficultyOption
                        val description = t(Res.string.difficulty, difficultyOption.localizedLabel())

                        if (isSelected) {
                            FilledTonalButton(
                                onClick = { selectedDifficulty = difficultyOption },
                                modifier =
                                    Modifier
                                        .minimumInteractiveComponentSize()
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = description
                                            stateDescription = description
                                            selected = true
                                        },
                            ) {
                                Text(
                                    text = difficultyOption.localizedLabel(),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = { selectedDifficulty = difficultyOption },
                                modifier =
                                    Modifier
                                        .minimumInteractiveComponentSize()
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = description
                                            selected = false
                                        },
                            ) {
                                Text(
                                    text = difficultyOption.localizedLabel(),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }

                if (records.isEmpty()) {
                    Text(
                        text = t(Res.string.history_no_wins, selectedDifficulty.localizedLabel()),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                } else {
                    HistoryList(records = records)
                }
            }
        },
    )
}

@Composable
private fun HistoryList(records: List<RunRecord>) {
    val maxRows = records.size.coerceAtLeast(1).coerceAtMost(MAX_VISIBLE_ROWS)
    val maxHeight = ROW_HEIGHT * maxRows.toFloat()
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(records) { index, record ->
            val entryDescription =
                t(
                    Res.string.history_entry_a11y,
                    index + 1,
                    formatMillisToMmSs(record.millis),
                    formatTimestamp(record.epochMillis),
                )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clearAndSetSemantics {
                            contentDescription = entryDescription
                        },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = formatMillisToMmSs(record.millis),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = formatTimestamp(record.epochMillis),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun formatTimestamp(epochMillis: Long): String {
    val utcTime = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.UTC)
    return buildString {
        append(utcTime.year.toString().padStart(4, '0'))
        append('-')
        append(utcTime.monthNumber.toString().padStart(2, '0'))
        append('-')
        append(utcTime.dayOfMonth.toString().padStart(2, '0'))
        append(' ')
        append(utcTime.hour.toString().padStart(2, '0'))
        append(':')
        append(utcTime.minute.toString().padStart(2, '0'))
    }
}

private val ROW_HEIGHT = 32.dp
private const val MAX_VISIBLE_ROWS = 10

private fun Difficulty.toStringResource(): StringResource =
    when (this) {
        Difficulty.EASY -> Res.string.difficulty_easy
        Difficulty.MEDIUM -> Res.string.difficulty_medium
        Difficulty.HARD -> Res.string.difficulty_hard
    }

@Composable
private fun Difficulty.localizedLabel(): String = stringResource(toStringResource())
