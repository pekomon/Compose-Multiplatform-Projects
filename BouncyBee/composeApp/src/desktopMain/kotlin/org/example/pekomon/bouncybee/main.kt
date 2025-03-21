package org.example.pekomon.bouncybee

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bouncy Bee",
    ) {
        App()
    }
}