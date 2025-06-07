package org.example.pekomon.cryptoapp.coins.domain

import org.example.pekomon.cryptoapp.coins.data.mappers.toPriceModel
import org.example.pekomon.cryptoapp.coins.domain.api.CoinsRemoteDataSource
import org.example.pekomon.cryptoapp.coins.domain.model.PriceModel
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.example.pekomon.cryptoapp.core.domain.Result
import org.example.pekomon.cryptoapp.core.domain.map

class GetCoinPriceHistoryUseCase(
    private val client: CoinsRemoteDataSource
) {
    suspend operator fun invoke(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return client.getPriceHistory(coinId).map { dto ->
            dto.data.history.map { gg ->
                gg.toPriceModel()
            }
        }
    }
}