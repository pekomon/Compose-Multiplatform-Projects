package org.example.pekomon.cryptoapp.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinsDetailsResponseDto(
    val data: CoinResponseDto
)

@Serializable
data class CoinResponseDto(
    val coin: CoinItemDto
)

