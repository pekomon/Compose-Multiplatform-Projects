package org.example.pekomon.bouncybee

enum class Platform {
    Android,
    iOS,
    Desktop,
    Web
}

expect fun getPlatform(): Platform