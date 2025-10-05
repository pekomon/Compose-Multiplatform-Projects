package com.example.pekomon.minesweeper.audio

import androidx.compose.runtime.Composable

interface SoundPlayer {
    fun click()

    fun reveal()

    fun win()

    fun lose()
}

object NullSoundPlayer : SoundPlayer {
    override fun click() { /* NOOP */ }

    override fun reveal() { /* NOOP */ }

    override fun win() { /* NOOP */ }

    override fun lose() { /* NOOP */ }
}

@Composable
fun rememberSoundPlayer(enableSounds: Boolean): SoundPlayer =
    if (enableSounds) {
        rememberPlatformSoundPlayer()
    } else {
        NullSoundPlayer
    }

@Composable
expect fun rememberPlatformSoundPlayer(): SoundPlayer
