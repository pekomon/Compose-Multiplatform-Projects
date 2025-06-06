package org.example.pekomon.cryptoapp.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinsResponseDto(
    val data: CoinsListDto
)

@Serializable
data class CoinsListDto(
    val coins: List<CoinItemDto>
)

@Serializable
data class CoinItemDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val price: Double, // Double may lose precision but KMP has no native support so living with Double now
    val rank: Int,
    val change: Double
)
