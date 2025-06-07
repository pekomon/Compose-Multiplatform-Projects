package org.example.pekomon.cryptoapp.coins.domain.api

import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinPriceHistoryResponseDto
import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinsDetailsResponseDto
import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinsResponseDto
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.example.pekomon.cryptoapp.core.domain.Result

interface CoinsRemoteDataSource {
    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>
    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>
    suspend fun getCoinById(coinId: String): Result<CoinsDetailsResponseDto, DataError.Remote>
}