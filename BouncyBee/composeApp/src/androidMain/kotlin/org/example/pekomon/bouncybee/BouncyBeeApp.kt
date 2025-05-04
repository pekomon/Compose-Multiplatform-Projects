package org.example.pekomon.bouncybee

import android.app.Application
import org.example.pekomon.bouncybee.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class BouncyBeeApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(this@BouncyBeeApp)
        }
    }
}