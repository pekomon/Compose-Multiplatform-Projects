package com.example.pekomon.minesweeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pekomon.minesweeper.composeapp.generated.resources.Res
import com.example.pekomon.minesweeper.composeapp.generated.resources.settings_animations
import com.example.pekomon.minesweeper.composeapp.generated.resources.settings_sounds
import com.example.pekomon.minesweeper.composeapp.generated.resources.settings_title
import com.example.pekomon.minesweeper.i18n.t

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsDialog(
    soundsEnabled: Boolean,
    animationsEnabled: Boolean,
    onSoundsEnabledChange: (Boolean) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {},
        title = {
            Text(
                text = t(Res.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SettingsSwitchRow(
                    label = t(Res.string.settings_sounds),
                    checked = soundsEnabled,
                    onCheckedChange = onSoundsEnabledChange,
                )
                SettingsSwitchRow(
                    label = t(Res.string.settings_animations),
                    checked = animationsEnabled,
                    onCheckedChange = onAnimationsEnabledChange,
                )
            }
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        },
        modifier = Modifier.padding(horizontal = 8.dp),
    )
}
