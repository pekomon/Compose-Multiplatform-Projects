package com.example.pekomon.minesweeper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
