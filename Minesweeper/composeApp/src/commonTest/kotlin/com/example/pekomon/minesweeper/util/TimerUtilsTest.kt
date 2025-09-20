package com.example.pekomon.minesweeper.util

import kotlin.test.Test
import kotlin.test.assertEquals

class TimerUtilsTest {
    @Test
    fun formatsUnderTenSecondsWithLeadingZero() {
        assertEquals("0:07", formatMillisToMmSs(7_000))
    }

    @Test
    fun formatsMinutesAndSeconds() {
        assertEquals("1:05", formatMillisToMmSs(65_000))
        assertEquals("12:34", formatMillisToMmSs(754_000))
    }

    @Test
    fun negativeValuesClampToZero() {
        assertEquals("0:00", formatMillisToMmSs(-1_000))
    }
}
