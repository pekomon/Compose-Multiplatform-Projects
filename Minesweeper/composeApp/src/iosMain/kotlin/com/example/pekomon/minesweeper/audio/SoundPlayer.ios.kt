package com.example.pekomon.minesweeper.audio

import androidx.compose.runtime.Composable

@Suppress("ForbiddenComment")
@Composable
actual fun rememberPlatformSoundPlayer(): SoundPlayer {
    // TODO: Implement using AVFoundation for richer native sounds.
    return NullSoundPlayer
}
