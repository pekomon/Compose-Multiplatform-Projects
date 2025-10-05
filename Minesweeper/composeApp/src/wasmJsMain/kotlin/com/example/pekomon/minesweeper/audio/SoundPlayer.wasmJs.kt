package com.example.pekomon.minesweeper.audio

import androidx.compose.runtime.Composable

@Suppress("ForbiddenComment")
@Composable
actual fun rememberPlatformSoundPlayer(): SoundPlayer {
    // TODO: Implement using Web Audio API via JavaScript interop when available.
    return NullSoundPlayer
}
