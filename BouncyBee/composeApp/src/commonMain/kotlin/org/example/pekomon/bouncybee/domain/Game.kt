package org.example.pekomon.bouncybee.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ObservableSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

private const val SETTINGS_KEY_HIGH_SCORE = "high_score"

class Game(
    val screenWidth: Int,
    val screenHeight: Int,
    val gravity: Float = 0.7f,
    val beeRadius: Float = 30f,
    val beeJumpImpulse: Float = -12f,
    val beeMaxVelocity: Float = 25f,
    val pipeWidth: Float = 120f,
    val pipeVelocity: Float = 5f,
    val pipeGapSize: Float = 260f,
) : KoinComponent {
    private val audioPlayer: AudioPlayer by inject()
    private val settings: ObservableSettings by inject()

    var status by mutableStateOf(GameStatus.Idle)
        private set

    var beeVelocity by mutableStateOf(0f)
        private set

    private var _bee by mutableStateOf(
        Bee(
            x = screenWidth / 4f,
            y = screenHeight / 2f,
            radius = beeRadius
        )
    )
    var bee: Bee
        get() = _bee
    private set(value) {
        _bee = value
    }

    var pipePairs = mutableStateListOf<PipePair>()

    var currentScore by mutableStateOf(0)
        private set

    var highScore by mutableStateOf(0)
        private set

    init {
        highScore = settings.getInt(
            key = SETTINGS_KEY_HIGH_SCORE,
            defaultValue = 0
        )
        settings.addIntListener(
            key = SETTINGS_KEY_HIGH_SCORE,
            defaultValue = 0,
            callback = {
                highScore = it
            }
        )
    }

    fun start() {
        status = GameStatus.Started
        audioPlayer.playBackgroundMusic()
    }

    fun gameOver() {
        status = GameStatus.Over
        audioPlayer.stopBackgroundMusic()
        saveScore()
    }

    private fun saveScore() {
        if (currentScore > highScore) {
            settings.putInt(SETTINGS_KEY_HIGH_SCORE, currentScore)
            //highScore = currentScore
        }
    }

    fun jump() {
        beeVelocity = beeJumpImpulse
        audioPlayer.playJumpSound()
    }

    fun restart() {
        resetBeePosition()
        removePipes()
        resetScore()
        start()
    }

    private fun resetBeePosition() {
        bee = bee.copy(
            y = (screenHeight / 2f)
        )
        beeVelocity = 0f
    }

    private fun removePipes() {
        pipePairs.clear()
    }

    private fun resetScore() {
        currentScore = 0
    }

    fun updateGameProgress() {

        pipePairs.forEach { pipePair ->
            if (isCollision(pipePair = pipePair)) {
                gameOver()
                return
            }

            if (!pipePair.scored && bee.x > pipePair.x + pipeWidth / 2) {
                pipePair.scored = true
                currentScore++
            }
        }

        if (bee.y < 0) {
            stopBee()
            return
        } else if (bee.y > screenHeight) {
            gameOver()
            return
        }

        beeVelocity = (beeVelocity + gravity)
            .coerceIn(-beeMaxVelocity, beeMaxVelocity)
        val newBee = bee.copy(
            y = bee.y + beeVelocity
        )
        bee = newBee

        spawnPipes()
    }

    private fun spawnPipes() {
        pipePairs.forEach { it.x -= pipeVelocity }
        pipePairs.removeAll { it.x + pipeWidth < 0 }

        if (pipePairs.isEmpty() || pipePairs.last().x < screenWidth /2) {
            val initialPipeX = screenWidth.toFloat() + pipeWidth
            val topHeight = Random.nextFloat() * (screenHeight / 2)
            val bottomHeight = screenHeight - topHeight - pipeGapSize
            val newPipePair = PipePair(
                x = initialPipeX,
                y = topHeight + pipeGapSize / 2,
                topHeight = topHeight,
                bottomHeight = bottomHeight
            )
            pipePairs.add(newPipePair)

        }
    }

    fun isCollision(pipePair: PipePair): Boolean {
        // Horizontal collision check
        val beeRightEdge = bee.x + bee.radius
        val beeLeftEdge = bee.x - bee.radius
        val pipeLeftEdge = pipePair.x - pipeWidth / 2
        val pipeRightEdge = pipePair.x + pipeWidth / 2
        val horizontalCollision = beeRightEdge > pipeLeftEdge && beeLeftEdge < pipeRightEdge

        // Check if bee is betbeen the gap (Not collided with pipe gaps)
        val beeTopEdge = bee.y - bee.radius
        val beeBottomEdge = bee.y + bee.radius
        val gapTopEdge = pipePair.y - pipeGapSize / 2
        val gapBottomEdge = pipePair.y + pipeGapSize / 2
        val beeInGap = beeTopEdge > gapTopEdge && beeBottomEdge < gapBottomEdge

        return horizontalCollision && !beeInGap
    }

    fun stopBee() {
        beeVelocity = 0f
        bee = bee.copy(y = 0f)
    }

    fun cleanUp() {
        audioPlayer.release()
    }
}