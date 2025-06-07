package org.example.pekomon.cryptoapp.coins.domain

import org.example.pekomon.cryptoapp.coins.data.mappers.toCoinModel
import org.example.pekomon.cryptoapp.coins.domain.api.CoinsRemoteDataSource
import org.example.pekomon.cryptoapp.coins.model.CoinModel
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.example.pekomon.cryptoapp.core.domain.map
import org.example.pekomon.cryptoapp.core.domain.Result

class GetCoinDetailsUseCase(
    private val client: CoinsRemoteDataSource
) {
    suspend operator fun invoke(coinId: String): Result<CoinModel, DataError.Remote> {
        return client.getCoinById(coinId).map { dto ->
            dto.data.coin.toCoinModel()
        }
    }
}