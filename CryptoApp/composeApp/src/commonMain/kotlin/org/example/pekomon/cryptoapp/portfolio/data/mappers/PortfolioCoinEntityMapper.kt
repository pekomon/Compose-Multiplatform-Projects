package org.example.pekomon.cryptoapp.portfolio.data.mappers

import kotlinx.datetime.Clock
import org.example.pekomon.cryptoapp.core.domain.Coin.Coin
import org.example.pekomon.cryptoapp.portfolio.data.local.PortfolioCoinEntity
import org.example.pekomon.cryptoapp.portfolio.domain.PortfolioCoinModel

fun PortfolioCoinEntity.toPortfolioCoinModel(
    currentPrice: Double
) : PortfolioCoinModel {
    return PortfolioCoinModel(
        coin = Coin(
            id = coinId,
            name = name,
            symbol = symbol,
            iconUrl = iconUrl
        ),
        performancePercent = ((currentPrice - averagePurchasePrice) / averagePurchasePrice) * 100,
        averagePurchasePrice = averagePurchasePrice,
        ownedAmountInUnit = amountOwned,
        ownedAmountInFiat = amountOwned * currentPrice
    )
}

fun PortfolioCoinModel.toPortfolioCoinEntity() : PortfolioCoinEntity {
    return PortfolioCoinEntity(
        coinId = coin.id,
        name = coin.name,
        symbol = coin.symbol,
        iconUrl = coin.iconUrl,
        amountOwned = ownedAmountInUnit,
        averagePurchasePrice = averagePurchasePrice,
        timestamp = Clock.System.now().toEpochMilliseconds()
    )

}