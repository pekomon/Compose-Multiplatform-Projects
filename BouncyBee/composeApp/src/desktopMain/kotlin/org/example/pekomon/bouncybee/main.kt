package org.example.pekomon.bouncybee

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.pekomon.bouncybee.di.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bouncy Bee",
    ) {
        App()
    }
}