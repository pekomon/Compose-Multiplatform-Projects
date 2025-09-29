package com.example.pekomon.minesweeper.lifecycle

interface AppLifecycleObserver {
    fun onEnterForeground()
    fun onEnterBackground()
}

expect object AppLifecycle {
    fun register(observer: AppLifecycleObserver)
    fun unregister(observer: AppLifecycleObserver)
}
