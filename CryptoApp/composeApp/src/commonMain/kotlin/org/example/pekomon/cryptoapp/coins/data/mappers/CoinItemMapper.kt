package org.example.pekomon.cryptoapp.coins.data.mappers

import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinItemDto
import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinPriceDto
import org.example.pekomon.cryptoapp.coins.model.CoinModel
import org.example.pekomon.cryptoapp.coins.model.PriceModel
import org.example.pekomon.cryptoapp.core.domain.Coin.Coin

fun CoinItemDto.toCoinModel() = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl
    ),
    price = price,
    change = change
)

fun CoinPriceDto.toPriceModel() = PriceModel(
    price = price ?: 0.0,
    timestamp = timestamp
)