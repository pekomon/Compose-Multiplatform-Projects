package org.example.pekomon.cryptoapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform