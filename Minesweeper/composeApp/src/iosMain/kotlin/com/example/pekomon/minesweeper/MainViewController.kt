@file:Suppress("FunctionNaming", "ktlint:standard:function-naming")

package com.example.pekomon.minesweeper

import androidx.compose.ui.window.ComposeUIViewController
import com.example.pekomon.minesweeper.ui.GameScreen

fun MainViewController() = ComposeUIViewController { GameScreen() }
