package com.example.pekomon.minesweeper.audio

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private object AndroidSoundPlayer : SoundPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun click() {
        playTone(tone = ToneGenerator.TONE_DTMF_1, durationMs = 80)
    }

    override fun reveal() {
        playTone(tone = ToneGenerator.TONE_DTMF_5, durationMs = 90)
    }

    override fun win() {
        playSequence(
            ToneSegment(tone = ToneGenerator.TONE_DTMF_6, durationMs = 110, delayBeforeMs = 0),
            ToneSegment(tone = ToneGenerator.TONE_DTMF_8, durationMs = 120, delayBeforeMs = 120),
            ToneSegment(tone = ToneGenerator.TONE_DTMF_A, durationMs = 150, delayBeforeMs = 140),
        )
    }

    override fun lose() {
        playTone(tone = ToneGenerator.TONE_DTMF_2, durationMs = 320)
    }

    private fun playTone(
        tone: Int,
        durationMs: Int,
        delayBeforeMs: Long = 0L,
    ) {
        scope.launch {
            if (delayBeforeMs > 0) {
                delay(delayBeforeMs)
            }
            toneGenerator.startTone(tone, durationMs)
        }
    }

    private fun playSequence(vararg segments: ToneSegment) {
        scope.launch {
            segments.forEachIndexed { index, segment ->
                if (segment.delayBeforeMs > 0 && index != 0) {
                    delay(segment.delayBeforeMs)
                }
                toneGenerator.startTone(segment.tone, segment.durationMs)
            }
        }
    }
}

private data class ToneSegment(
    val tone: Int,
    val durationMs: Int,
    val delayBeforeMs: Long,
)

@Composable
actual fun rememberPlatformSoundPlayer(): SoundPlayer = remember { AndroidSoundPlayer }
