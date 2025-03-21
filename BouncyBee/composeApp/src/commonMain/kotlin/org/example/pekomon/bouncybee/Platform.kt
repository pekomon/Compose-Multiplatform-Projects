package org.example.pekomon.bouncybee

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform