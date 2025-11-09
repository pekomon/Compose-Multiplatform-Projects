package org.example.pekomon.cryptoapp.trade.domain

import kotlinx.coroutines.flow.first
import org.example.pekomon.cryptoapp.core.domain.Coin.Coin
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.example.pekomon.cryptoapp.core.domain.EmptyResult
import org.example.pekomon.cryptoapp.core.domain.Result
import org.example.pekomon.cryptoapp.portfolio.domain.PortfolioRepository

class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend fun sellCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double
    ): EmptyResult<DataError> {
        // After selling the specified amount of coins,
        // if remaining assets of that coin is less than 1$,
        // => Remove 'dust' from repo
        // Yeah, not 'bestest' but makes things a lot simpler
        // to not have 0.000000000001 coins.
        // Downsize: you may lose a bunch of Pepe's etc ;)
        val sellAllThreshold = 1
        when (val existingCoinResponse = portfolioRepository.getPortfolioCoin(coin.id)) {
            is Result.Success -> {
                val existingCoin = existingCoinResponse.data
                val sellAmountUnit = amountInFiat / price

                val balance = portfolioRepository.cashBalanceFlow().first()
                if (existingCoin == null || existingCoin.ownedAmountInUnit < sellAmountUnit) {
                    return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
                }
                val remainingAmountInFiat = existingCoin.ownedAmountInFiat
                val remainingAmountUnit = existingCoin.ownedAmountInUnit - sellAmountUnit
                if (remainingAmountInFiat < sellAllThreshold) {
                    portfolioRepository.removeCoinFromPortfolio(coin.id)
                } else {
                    portfolioRepository.savePortfolioCoin(
                        existingCoin.copy(
                            ownedAmountInUnit = remainingAmountUnit,
                            ownedAmountInFiat = remainingAmountInFiat
                        )
                    )
                }
                portfolioRepository.updateCashBalance(balance + amountInFiat)
                return Result.Success(Unit)
            }
            is Result.Error -> {
                return existingCoinResponse
            }
        }
    }

}