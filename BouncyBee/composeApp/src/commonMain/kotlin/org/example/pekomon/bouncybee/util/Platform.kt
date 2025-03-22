package org.example.pekomon.bouncybee.util

enum class Platform {
    Android,
    iOS,
    Desktop,
    Web
}

expect fun getPlatform(): Platform