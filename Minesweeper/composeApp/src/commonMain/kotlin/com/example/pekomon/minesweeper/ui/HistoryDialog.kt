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
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pekomon.minesweeper.game.Difficulty
import com.example.pekomon.minesweeper.history.InMemoryHistoryStore
import com.example.pekomon.minesweeper.history.RunRecord

@OptIn(ExperimentalMaterial3Api::class)
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
                Text(text = "Close")
            }
        },
        title = { Text(text = "History") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    difficulties.forEach { difficulty ->
                        ElevatedFilterChip(
                            selected = selectedDifficulty == difficulty,
                            onClick = { selectedDifficulty = difficulty },
                            label = { Text(text = difficulty.toDisplayName()) },
                            colors = ButtonDefaults.elevatedFilterChipColors(),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (records.isEmpty()) {
                    Text(
                        text = "No wins recorded for ${selectedDifficulty.toDisplayName()} yet.",
                        style = MaterialTheme.typography.bodyMedium,
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
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = (ROW_HEIGHT * records.size).coerceAtMost(ROW_HEIGHT * MAX_VISIBLE_ROWS))
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
                    Text(text = formatElapsed(record.elapsedMillis))
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

private fun formatElapsed(elapsedMillis: Long): String {
    val totalSeconds = (elapsedMillis / 1000).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val minutePart = minutes.toString().padStart(2, '0')
    val secondPart = seconds.toString().padStart(2, '0')
    return "$minutePart:$secondPart"
}

private fun formatTimestamp(epochMillis: Long): String {
    val parts = epochMillis.toUtcParts()
    return buildString {
        append(parts.year.toString().padStart(4, '0'))
        append('-')
        append(parts.month.toString().padStart(2, '0'))
        append('-')
        append(parts.day.toString().padStart(2, '0'))
        append(' ')
        append(parts.hour.toString().padStart(2, '0'))
        append(':')
        append(parts.minute.toString().padStart(2, '0'))
    }
}

private data class UtcDateTimeParts(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
)

private fun Long.toUtcParts(): UtcDateTimeParts {
    val epochSecond = floorDiv(this, MILLIS_PER_SECOND)
    val epochDay = floorDiv(epochSecond, SECONDS_PER_DAY)
    val secondsOfDay = floorMod(epochSecond, SECONDS_PER_DAY).toInt()
    val hour = secondsOfDay / SECONDS_PER_HOUR
    val minute = (secondsOfDay % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val (year, month, day) = epochDayToDate(epochDay)
    return UtcDateTimeParts(year = year, month = month, day = day, hour = hour, minute = minute)
}

private fun epochDayToDate(epochDay: Long): Triple<Int, Int, Int> {
    var zeroDay = epochDay + DAYS_0000_TO_1970 - 60
    var adjust: Long = 0
    if (zeroDay < 0) {
        val adjustCycles = (zeroDay + 1) / DAYS_PER_CYCLE - 1
        adjust = adjustCycles * 400
        zeroDay -= adjustCycles * DAYS_PER_CYCLE
    }
    var yearEst = (400 * zeroDay + 591) / DAYS_PER_CYCLE
    var doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400)
    if (doyEst < 0) {
        yearEst -= 1
        doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400)
    }
    val marchDoy0 = doyEst.toInt()
    val marchMonth0 = (marchDoy0 * 5 + 2) / 153
    val month = (marchMonth0 + 2) % 12 + 1
    val day = marchDoy0 - (marchMonth0 * 153 + 2) / 5 + 1
    val year = (yearEst + adjust + marchMonth0 / 10).toInt()
    return Triple(year, month, day)
}

private fun floorDiv(x: Long, y: Long): Long {
    var result = x / y
    if ((x xor y) < 0 && x % y != 0L) {
        result -= 1
    }
    return result
}

private fun floorMod(x: Long, y: Long): Long = x - floorDiv(x, y) * y

private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60
private const val SECONDS_PER_DAY = SECONDS_PER_HOUR * 24
private const val DAYS_PER_CYCLE = 146097L
private const val DAYS_0000_TO_1970 = 719528L

private val ROW_HEIGHT = 32.dp
private const val MAX_VISIBLE_ROWS = 10

private fun Difficulty.toDisplayName(): String {
    val name = name.lowercase()
    return name.replaceFirstChar { it.titlecase() }
}
