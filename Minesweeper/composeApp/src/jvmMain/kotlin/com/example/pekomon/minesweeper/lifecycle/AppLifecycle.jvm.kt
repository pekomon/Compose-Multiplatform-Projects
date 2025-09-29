package com.example.pekomon.minesweeper.lifecycle

actual object AppLifecycle {
    actual fun register(observer: AppLifecycleObserver) {
        // No-op for desktop.
    }

    actual fun unregister(observer: AppLifecycleObserver) {
        // No-op for desktop.
    }
}
