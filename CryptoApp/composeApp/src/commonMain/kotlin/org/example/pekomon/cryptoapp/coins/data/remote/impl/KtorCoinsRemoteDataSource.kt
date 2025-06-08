package org.example.pekomon.cryptoapp.coins.data.remote.impl

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinPriceHistoryResponseDto
import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinsDetailsResponseDto
import org.example.pekomon.cryptoapp.coins.data.remote.dto.CoinsResponseDto
import org.example.pekomon.cryptoapp.coins.domain.api.CoinsRemoteDataSource
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.example.pekomon.cryptoapp.core.domain.Result
import org.example.pekomon.cryptoapp.core.logger.Logger
import org.example.pekomon.cryptoapp.core.network.safeCall

// TODO: put this to configuration or consts file
private const val BASE_URL = "https://api.coinranking.com/v2"

class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient
): CoinsRemoteDataSource {
    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
        return safeCall {
            val response = httpClient.get("$BASE_URL/coins")
            Logger.d("CoinsRemoteDataSource", "getListOfCoins status: ${response.status.value}")
            response
        }
    }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId/history")
        }
    }

    override suspend fun getCoinById(coinId: String): Result<CoinsDetailsResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId")
        }
    }
}
