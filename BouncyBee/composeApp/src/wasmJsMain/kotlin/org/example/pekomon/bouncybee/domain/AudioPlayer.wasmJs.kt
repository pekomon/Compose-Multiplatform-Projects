package org.example.pekomon.bouncybee.domain

import org.w3c.dom.Audio

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {

    private val audioElements = mutableMapOf<String, Audio>()

    actual fun playGameOverSound() {
        stopFallingSound()
        playSound(fileName = "game_over.wav")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound(fileName = "jump.wav")
    }

    actual fun playFallingSound() {
        playSound(fileName = "falling.wav")
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
        stopSound(fileName = "game_sound.wav")
    }

    actual fun release() {
        stopAllSounds()
        audioElements.clear()
    }

    private fun stopSound(fileName: String) {
        audioElements[fileName]?.let {
            it.pause()
            it.currentTime = 0.0
        }
    }

    private fun stopAllSounds() {
        audioElements.values.forEach {
            it.pause()
            it.currentTime = 0.0
        }
    }

    private fun playSound(
        fileName: String,
        playInLoop: Boolean = false
    ) {
        val audio = audioElements[fileName] ?: createAudioElement(fileName).also { audioElements[fileName] = it }
        audio.loop = playInLoop
        audio.play().catch {
            println("Error playing sound: $fileName")
            it
        }
    }


    private fun createAudioElement(filename: String): Audio {
        val path = "src/commonMain/composeResources/files/$filename"
        return Audio(path).apply {
            onerror = { _: JsAny?, _: String, _: Int, _: Int, _: JsAny? ->
                println("Error loading audio file: $path")
                null
            }
        }
    }
}