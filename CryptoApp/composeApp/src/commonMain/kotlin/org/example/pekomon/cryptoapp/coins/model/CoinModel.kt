package org.example.pekomon.cryptoapp.coins.model

import org.example.pekomon.cryptoapp.core.domain.Coin.Coin

data class CoinModel(
    val coin: Coin,
    val price: Double, // Double may lose precision but KMP has no native support so living with Double now
    val change: Double
)