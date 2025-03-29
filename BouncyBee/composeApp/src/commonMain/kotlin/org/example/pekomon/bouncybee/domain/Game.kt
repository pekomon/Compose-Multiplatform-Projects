package org.example.pekomon.bouncybee.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

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
) {
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

    fun start() {
        status = GameStatus.Started
    }

    fun gameOver() {
        status = GameStatus.Over
    }

    fun jump() {
        beeVelocity = beeJumpImpulse
    }

    fun restart() {
        resetBeePosition()
        removePipes()
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

    fun updateGameProgress() {
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

    fun stopBee() {
        beeVelocity = 0f
        bee = bee.copy(y = 0f)
    }
}