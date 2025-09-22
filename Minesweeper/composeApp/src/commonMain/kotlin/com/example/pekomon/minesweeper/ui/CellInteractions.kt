package com.example.pekomon.minesweeper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalFoundationApi::class)
internal fun Modifier.cellInteractions(
    revealEnabled: Boolean,
    toggleEnabled: Boolean,
    onReveal: () -> Unit,
    onToggleFlag: () -> Unit,
): Modifier =
    composed {
        val currentReveal by rememberUpdatedState(onReveal)
        val currentToggle by rememberUpdatedState(onToggleFlag)

        pointerInput(revealEnabled, toggleEnabled) {
            detectTapGestures(
                onTap = {
                    if (revealEnabled) {
                        currentReveal()
                    }
                },
                onLongPress = {
                    if (toggleEnabled) {
                        currentToggle()
                    }
                },
            )
        }
    }
