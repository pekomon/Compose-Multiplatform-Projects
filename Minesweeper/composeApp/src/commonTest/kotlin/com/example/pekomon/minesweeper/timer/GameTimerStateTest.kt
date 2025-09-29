package com.example.pekomon.minesweeper.timer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class GameTimerStateTest {
    @Test
    fun pauseStopsAdvancing() = runBlocking {
        val timer = GameTimerState(tickInterval = 10.milliseconds, coroutineScope = this)

        timer.start()
        delay(50)
        timer.pause()

        val pausedDuration = timer.elapsed.value
        assertTrue(pausedDuration > Duration.ZERO)

        delay(50)

        assertEquals(pausedDuration, timer.elapsed.value)
    }
}
