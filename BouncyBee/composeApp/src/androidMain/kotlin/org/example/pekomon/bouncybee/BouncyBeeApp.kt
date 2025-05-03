package org.example.pekomon.bouncybee

import android.app.Application
import org.example.pekomon.bouncybee.di.initializeKoin

class BouncyBeeApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }
}