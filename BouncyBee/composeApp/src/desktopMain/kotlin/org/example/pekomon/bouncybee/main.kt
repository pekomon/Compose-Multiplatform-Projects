package org.example.pekomon.bouncybee

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.example.pekomon.bouncybee.di.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bouncy Bee",
        state = WindowState(
            width = 1200.dp,
            height = 800.dp
        ),
        resizable = false
    ) {
        App()
    }
}