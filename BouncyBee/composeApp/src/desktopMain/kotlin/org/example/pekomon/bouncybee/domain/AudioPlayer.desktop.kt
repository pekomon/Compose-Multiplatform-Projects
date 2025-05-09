package org.example.pekomon.bouncybee.domain

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {

    private val audioCache = mutableMapOf<String, ByteArray>()
    private val playingLines = mutableMapOf<String, SourceDataLine>()

    actual fun playGameOverSound() {
        stopFallingSound()
        playSound("game_over.wav")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound("jump.wav")
    }

    actual fun playFallingSound() {
        playSound("falling.wav")
    }

    actual fun stopFallingSound() {
        stopSound("falling.wav")
    }

    actual fun playBackgroundMusic() {
        playSound(
            fileName = "game_sound.wav",
            playInLoop = true
        )
    }

    actual fun stopBackgroundMusic() {
        playGameOverSound()
        stopSound("game_sound.wav")
    }

    actual fun release() {
        audioCache.clear()
        stopAllSounds()
    }

    private fun playSound(
        fileName: String,
        playInLoop: Boolean = false
    ) {
        thread {
            try {
                val audioData = audioCache[fileName] ?: loadAudioFile(fileName).also {
                    audioCache[fileName] = it
                }
                val inputStream = AudioSystem.getAudioInputStream(audioData.inputStream())
                val format = inputStream.format
                val info = DataLine.Info(SourceDataLine::class.java, format)
                val line = AudioSystem.getLine(info) as SourceDataLine

                line.open(format)
                line.start()

                synchronized(playingLines) {
                    playingLines[fileName] = line
                }

                val buffer = ByteArray(4096)
                var bytesRead = 0
                var shouldContinue = true

                if (playInLoop) {
                    inputStream.reset()
                    while (shouldContinue && inputStream.read(buffer).also { bytesRead = it } != -1 ) {
                        synchronized(playingLines) {
                            shouldContinue = playingLines.containsKey(fileName)
                        }
                        if (shouldContinue) {
                            line.write(buffer, 0, bytesRead)
                        }
                    }
                } else {
                    while (shouldContinue && inputStream.read(buffer).also { bytesRead = it } != -1 ) {
                        synchronized(playingLines) {
                            shouldContinue = playingLines.containsKey(fileName)
                        }
                        if (shouldContinue) {
                            line.write(buffer, 0, bytesRead)
                        }
                    }

                    line.drain()
                    line.close()
                    synchronized(playingLines) {
                        playingLines.remove(fileName)
                    }
                }

            } catch (e: Exception) {
                println("Error playing sound: $e")
            }
        }
    }

    private fun stopAllSounds() {
        synchronized(playingLines) {
            playingLines.values.forEach {
                it.stop()
                it.close()
            }
            playingLines.clear()
        }
    }

    private fun stopSound(fileName: String) {
        synchronized(playingLines) {
            playingLines[fileName]?.let { line ->
                line.stop()
                line.close()
                playingLines.remove(fileName)
            }
        }
    }

    private fun loadAudioFile(fileName: String): ByteArray {
        val resourcePath = Paths.get("src/common/composeResources/files/$fileName")
        if (Files.exists(resourcePath).not()) {
            throw FileNotFoundException("Audio file not found: $fileName")
        }
        return FileInputStream(resourcePath.toFile()).use { it.readBytes() }
    }
}