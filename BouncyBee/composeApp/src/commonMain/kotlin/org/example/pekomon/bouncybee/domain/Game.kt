package org.example.pekomon.bouncybee.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Game(
    val screenWidth: Int,
    val screenHeight: Int,
    val gravity: Float = 0.7f,
    val beeJumpImpulse: Float = -12f,
    val beeMaxVelocity: Float = 25f
) {
    var status by mutableStateOf(GameStatus.Idle)
        private set

    var beeVelocity by mutableStateOf(0f)
        private set

    var _bee by mutableStateOf(
        Bee(
            x = screenWidth / 4f,
            y = screenHeight / 2f
        )
    )
    var bee: Bee
        get() = _bee
    private set(value) {
        _bee = value
    }

    fun start() {
        status = GameStatus.Started
    }

    fun gameOver() {
        status = GameStatus.Over
    }

    fun jump() {
        beeVelocity = beeJumpImpulse
    }

    fun updateGameProgress() {
        beeVelocity = (beeVelocity + gravity)
            .coerceIn(-beeMaxVelocity, beeMaxVelocity)
        val newBee = bee.copy(
            y = bee.y + beeVelocity
        )
        bee = newBee
    }
}