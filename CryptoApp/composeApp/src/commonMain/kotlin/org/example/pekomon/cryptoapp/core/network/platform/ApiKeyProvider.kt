package org.example.pekomon.cryptoapp.core.network.platform

expect object ApiKeyProvider {
    fun getApiKey(): String
}