package org.example.pekomon.cryptoapp.core.logger

actual object Logger {
    actual fun d(tag: String, message: String) {
        println("[$tag] $message")
    }
}