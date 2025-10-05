package com.example.pekomon.minesweeper.audio

import androidx.compose.runtime.Composable

@Composable
actual fun rememberPlatformSoundPlayer(): SoundPlayer {
    // TODO: Implement using AVFoundation for richer native sounds.
    return NullSoundPlayer
}
