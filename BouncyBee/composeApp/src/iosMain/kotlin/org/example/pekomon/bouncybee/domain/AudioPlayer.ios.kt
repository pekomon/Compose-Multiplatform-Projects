package org.example.pekomon.bouncybee.domain

import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.fileURLWithPath

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@OptIn(kotlinx. cinterop. ExperimentalForeignApi::class)
actual class AudioPlayer {

    private var audioPlayers = mutableMapOf<String, AVAudioPlayer>()

    // falling sound needs own player because it can be stopped
    private var fallingSoundPlayer: AVAudioPlayer? = null

    init {
        // Configuration for audio session for playback
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, null)
    }

    actual fun playGameOverSound() {
        stopFallingSound()
        playSound("game_over")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound("jump")
    }

    actual fun playFallingSound() {
        fallingSoundPlayer = playSound("falling")
    }

    actual fun stopFallingSound() {
        fallingSoundPlayer?.stop()
        fallingSoundPlayer = null
    }

    actual fun playBackgroundMusic() {
        getSoundUrl("game_sound")?.let { url ->
            val player = AVAudioPlayer(url, null)
            player.numberOfLoops = -1 // infinite
            player.prepareToPlay()
            player.play()
            audioPlayers["game_sound"] = player
        }
    }

    actual fun stopBackgroundMusic() {
        playGameOverSound()
        audioPlayers["game_sound"]?.stop()
        audioPlayers.remove("game_sound")
    }

    actual fun release() {
        audioPlayers.values.forEach { it.stop() }
        audioPlayers.clear()
        fallingSoundPlayer?.stop()
        fallingSoundPlayer = null
    }

    private fun playSound(soundName: String): AVAudioPlayer? {
        val url = getSoundUrl(soundName) ?: return null
        val player = AVAudioPlayer(url, null)
        player.prepareToPlay()
        player.play()

        audioPlayers[soundName] = player
        return player
    }

    private fun getSoundUrl(resourceName: String): NSURL? {
        val bundle = NSBundle.mainBundle()
        val path = bundle.pathForResource(resourceName, "wav")
        return path?.let { fileURLWithPath(it) }
    }
}