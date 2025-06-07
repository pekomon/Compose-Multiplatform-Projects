package org.example.pekomon.cryptoapp.core.network.platform

import org.example.pekomon.cryptoapp.BuildConfig

actual object ApiKeyProvider {
    actual fun getApiKey(): String {
        return BuildConfig.COINRANKING_API_KEY
    }
}