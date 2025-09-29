package com.example.pekomon.minesweeper.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class GameTimerState(
    private val tickInterval: Duration = 100.milliseconds,
    private val coroutineScope: CoroutineScope,
) {
    private val timeSource: TimeSource = TimeSource.Monotonic

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _elapsed = MutableStateFlow(Duration.ZERO)
    val elapsed: StateFlow<Duration> = _elapsed.asStateFlow()

    private var baseMark: TimeMark? = null
    private var accumulated: Duration = Duration.ZERO
    private var tickerJob: Job? = null

    fun start() {
        reset()
        baseMark = timeSource.markNow()
        updateElapsed()
        launchTicker()
    }

    fun pause() {
        if (!_isRunning.value) return

        updateElapsed()
        accumulated = _elapsed.value
        baseMark = null
        stopTicker()
    }

    fun resume() {
        if (_isRunning.value) return

        baseMark = timeSource.markNow()
        updateElapsed()
        launchTicker()
    }

    fun reset() {
        stopTicker()
        accumulated = Duration.ZERO
        baseMark = null
        _elapsed.value = Duration.ZERO
    }

    private fun launchTicker() {
        if (tickerJob?.isActive == true) return

        _isRunning.value = true
        tickerJob =
            coroutineScope.launch {
                while (isActive) {
                    updateElapsed()
                    delay(tickInterval)
                }
            }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
        _isRunning.value = false
    }

    private fun updateElapsed() {
        val elapsedDuration =
            baseMark?.let { mark ->
                accumulated + mark.elapsedNow()
            } ?: accumulated
        _elapsed.value = elapsedDuration
    }
}
