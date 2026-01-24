package com.pekomon.pdfforge

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    System.setProperty("apple.awt.application.name", "PdfForge")
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PdfForge")
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "PdfForge",
        ) {
            App()
        }
    }
}
