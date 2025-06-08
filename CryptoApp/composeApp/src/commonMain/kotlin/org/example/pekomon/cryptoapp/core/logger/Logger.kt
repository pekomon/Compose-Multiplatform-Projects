package org.example.pekomon.cryptoapp.core.logger

expect object Logger {
    fun d(tag: String, message: String)
}