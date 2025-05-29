package org.example.pekomon.cryptoapp.core.network.platform

actual object ApiKeyProvider {
    actual fun getApiKey(): String {
        return BuildKonfig.COINRANKING_API_KEY
    }
}