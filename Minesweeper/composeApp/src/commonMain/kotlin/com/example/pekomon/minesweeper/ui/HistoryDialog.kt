package com.example.pekomon.minesweeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pekomon.minesweeper.composeapp.generated.resources.Res
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_easy
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_hard
import com.example.pekomon.minesweeper.composeapp.generated.resources.difficulty_medium
import com.example.pekomon.minesweeper.composeapp.generated.resources.history_close
import com.example.pekomon.minesweeper.composeapp.generated.resources.history_no_wins
import com.example.pekomon.minesweeper.composeapp.generated.resources.history_title
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.history.InMemoryHistoryStore
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
    onClose: () -> Unit,
) {
    var selectedDifficulty by remember { mutableStateOf(currentDifficulty) }
    val difficulties = remember { Difficulty.values().toList() }
    val records = InMemoryHistoryStore.top(selectedDifficulty)

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(text = t(Res.string.history_close))
            }
        },
        title = { Text(text = t(Res.string.history_title)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    difficulties.forEach { difficulty ->
                        val selected = selectedDifficulty == difficulty
                        val containerColor =
                            if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        val contentColor =
                            if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }

                        OutlinedButton(
                            onClick = { selectedDifficulty = difficulty },
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor,
                                ),
                        ) {
                            Text(text = difficulty.localizedLabel())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        itemsIndexed(records) { index, record ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "${index + 1}.")
                    Text(text = formatMillisToMmSs(record.elapsedMillis))
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
