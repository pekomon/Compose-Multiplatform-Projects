package org.example.pekomon.cryptoapp

import android.app.Application
import org.example.pekomon.cryptoapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class CryptoApp : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CryptoApp)
        }
    }
}