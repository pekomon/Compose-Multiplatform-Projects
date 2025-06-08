package org.example.pekomon.cryptoapp.core.logger

import android.util.Log

actual object Logger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}
