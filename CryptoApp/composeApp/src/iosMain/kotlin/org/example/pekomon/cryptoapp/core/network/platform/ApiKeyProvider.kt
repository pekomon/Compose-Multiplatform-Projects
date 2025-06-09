package org.example.pekomon.cryptoapp.core.network.platform

import platform.Foundation.NSBundle
import platform.Foundation.NSLog


actual object ApiKeyProvider {

    private const val API_KEY_INFO_PLIST_KEY = "COINRANKING_API_KEY"

    actual fun getApiKey(): String {

        val apiKey = NSBundle.mainBundle.objectForInfoDictionaryKey(API_KEY_INFO_PLIST_KEY) as? String
        return if (apiKey.isNullOrBlank()) {
            // Log a warning if the key is not found or is empty.
            // Consider using a more robust KMP logging solution here if you have one.
            NSLog("WARNING: API Key '$API_KEY_INFO_PLIST_KEY' not found in Info.plist or is empty. " +
                    "Please ensure it's configured correctly in your Xcode project. " +
                    "Using a default/placeholder value.")

            "not-found"

        } else {
            // Key found, return it.
            apiKey
        }
    }
}