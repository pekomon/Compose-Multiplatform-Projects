package com.example.pekomon.minesweeper.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner

actual object AppLifecycle {
    private val lifecycle = ProcessLifecycleOwner.get().lifecycle
    private val observers = mutableMapOf<AppLifecycleObserver, LifecycleEventObserver>()

    actual fun register(observer: AppLifecycleObserver) {
        if (observers.containsKey(observer)) return

        val lifecycleObserver =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> observer.onEnterForeground()
                    Lifecycle.Event.ON_STOP -> observer.onEnterBackground()
                    else -> Unit
                }
            }

        observers[observer] = lifecycleObserver
        lifecycle.addObserver(lifecycleObserver)
    }

    actual fun unregister(observer: AppLifecycleObserver) {
        val lifecycleObserver = observers.remove(observer) ?: return
        lifecycle.removeObserver(lifecycleObserver)
    }
}
