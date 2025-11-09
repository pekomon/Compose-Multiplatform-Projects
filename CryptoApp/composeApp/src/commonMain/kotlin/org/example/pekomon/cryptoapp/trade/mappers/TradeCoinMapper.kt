package org.example.pekomon.cryptoapp.trade.mappers

import org.example.pekomon.cryptoapp.core.domain.Coin.Coin
import org.example.pekomon.cryptoapp.trade.presentation.common.UITradeCoinItem

fun UITradeCoinItem.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl
)