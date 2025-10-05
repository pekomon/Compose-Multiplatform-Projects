package com.example.pekomon.minesweeper.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

private const val SAMPLE_RATE = 44_100
private val AUDIO_FORMAT = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)

private class JvmSoundPlayer : SoundPlayer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun click() {
        playTone(frequencyHz = 420, durationMs = 80)
    }

    override fun reveal() {
        playTone(frequencyHz = 620, durationMs = 100)
    }

    override fun win() {
        playSequence(
            ToneSpec(frequencyHz = 660, durationMs = 120, delayBeforeMs = 0),
            ToneSpec(frequencyHz = 880, durationMs = 130, delayBeforeMs = 120),
            ToneSpec(frequencyHz = 1040, durationMs = 160, delayBeforeMs = 140),
        )
    }

    override fun lose() {
        playTone(frequencyHz = 320, durationMs = 360)
    }

    private fun playTone(frequencyHz: Int, durationMs: Int, delayBeforeMs: Long = 0L) {
        scope.launch {
            if (delayBeforeMs > 0) {
                delay(delayBeforeMs)
            }
            playToneInternal(frequencyHz, durationMs)
        }
    }

    private fun playSequence(vararg tones: ToneSpec) {
        scope.launch {
            tones.forEachIndexed { index, tone ->
                if (tone.delayBeforeMs > 0 && index != 0) {
                    delay(tone.delayBeforeMs)
                }
                playToneInternal(tone.frequencyHz, tone.durationMs)
            }
        }
    }

    private suspend fun playToneInternal(frequencyHz: Int, durationMs: Int) {
        val clip = runCatching { AudioSystem.getClip() }.getOrNull() ?: return
        try {
            val data = generateTone(frequencyHz, durationMs)
            clip.open(AUDIO_FORMAT, data, 0, data.size)
            clip.start()
            delay(durationMs.toLong())
        } catch (_: Exception) {
            // Ignore playback failures on desktop
        } finally {
            runCatching { clip.stop() }
            runCatching { clip.close() }
        }
    }
}

private data class ToneSpec(
    val frequencyHz: Int,
    val durationMs: Int,
    val delayBeforeMs: Long,
)

fun generateTone(frequencyHz: Int, millis: Int): ByteArray {
    val totalSamples = (SAMPLE_RATE * (millis / 1000.0)).roundToInt().coerceAtLeast(1)
    val buffer = ByteArray(totalSamples * 2)
    for (i in 0 until totalSamples) {
        val angle = 2.0 * PI * i * frequencyHz / SAMPLE_RATE
        val amplitude = (sin(angle) * Short.MAX_VALUE).toInt()
        buffer[i * 2] = (amplitude and 0xFF).toByte()
        buffer[i * 2 + 1] = ((amplitude shr 8) and 0xFF).toByte()
    }
    return buffer
}

@Composable
actual fun rememberPlatformSoundPlayer(): SoundPlayer = remember { JvmSoundPlayer() }
