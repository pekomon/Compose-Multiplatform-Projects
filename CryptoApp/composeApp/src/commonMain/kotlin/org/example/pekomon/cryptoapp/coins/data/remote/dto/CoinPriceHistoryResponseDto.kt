package org.example.pekomon.cryptoapp.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceHistoryResponseDto(
    val data: CoinsPriceHistoryDto
)

@Serializable
data class CoinsPriceHistoryDto(
    val history: List<CoinPriceDto>
)

@Serializable
data class CoinPriceDto(
    val price: Double?,
    val timestamp: Long
)