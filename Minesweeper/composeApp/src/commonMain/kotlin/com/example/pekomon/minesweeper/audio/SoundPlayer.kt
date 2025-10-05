package com.example.pekomon.minesweeper.audio

import androidx.compose.runtime.Composable

interface SoundPlayer {
    fun click()

    fun reveal()

    fun win()

    fun lose()
}

object NullSoundPlayer : SoundPlayer {
    override fun click() {}

    override fun reveal() {}

    override fun win() {}

    override fun lose() {}
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
