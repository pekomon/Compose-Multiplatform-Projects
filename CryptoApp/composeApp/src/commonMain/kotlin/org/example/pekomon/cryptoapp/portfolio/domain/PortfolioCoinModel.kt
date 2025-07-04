package org.example.pekomon.cryptoapp.portfolio.domain

import org.example.pekomon.cryptoapp.core.domain.Coin.Coin

data class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: Double,
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val ownedAmountInFiat: Double
)