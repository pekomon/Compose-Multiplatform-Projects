package org.example.pekomon.cryptoapp.coins.domain

import org.example.pekomon.cryptoapp.coins.data.mappers.toCoinModel
import org.example.pekomon.cryptoapp.coins.domain.api.CoinsRemoteDataSource
import org.example.pekomon.cryptoapp.coins.domain.model.CoinModel
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.example.pekomon.cryptoapp.core.domain.Result
import org.example.pekomon.cryptoapp.core.domain.map
import org.example.pekomon.cryptoapp.core.logger.Logger

class GetCoinsListUseCase(
    private val client: CoinsRemoteDataSource
) {
    suspend operator fun invoke(): Result<List<CoinModel>, DataError.Remote> {
        Logger.d("GetCoinsListUseCase", "invoke()")
        return client.getListOfCoins().map { dto ->
            dto.data.coins.map {
                it.toCoinModel()
            }
        }
    }
}