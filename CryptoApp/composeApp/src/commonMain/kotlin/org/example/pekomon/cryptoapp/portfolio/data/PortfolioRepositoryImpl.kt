package org.example.pekomon.cryptoapp.portfolio.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.internal.NopCollector.emit
import org.example.pekomon.cryptoapp.coins.domain.api.CoinsRemoteDataSource
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.example.pekomon.cryptoapp.core.domain.EmptyResult
import org.example.pekomon.cryptoapp.core.domain.Result
import org.example.pekomon.cryptoapp.core.domain.onError
import org.example.pekomon.cryptoapp.core.domain.onSuccess
import org.example.pekomon.cryptoapp.portfolio.data.local.PortfolioDao
import org.example.pekomon.cryptoapp.portfolio.data.local.UserBalanceDao
import org.example.pekomon.cryptoapp.portfolio.data.local.UserBalanceEntity
import org.example.pekomon.cryptoapp.portfolio.data.mappers.toPortfolioCoinModel
import org.example.pekomon.cryptoapp.portfolio.domain.PortfolioCoinModel
import org.example.pekomon.cryptoapp.portfolio.domain.PortfolioRepository

class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinsRemoteDataSource: CoinsRemoteDataSource
) : PortfolioRepository {
    override suspend fun initializeBalance() {
        val currentBalance = userBalanceDao.getCashBalance()
        if (currentBalance == null) {
            userBalanceDao.insertBalance(UserBalanceEntity(cashBalance = 10000.0))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinEntities ->
            if (portfolioCoinEntities.isEmpty()) {
                flow {
                    emit(Result.Success(emptyList<PortfolioCoinModel>()))

                }
            } else {
                flow {
                    coinsRemoteDataSource.getListOfCoins()
                        .onError { error ->
                            emit(Result.Error(error))
                        }
                        .onSuccess { coinsDto ->
                            val portfolioCoins = portfolioCoinEntities.mapNotNull { portfolioCoinEntity ->
                                val coin = coinsDto.data.coins.find { it.uuid == portfolioCoinEntity.coinId }
                                coin?.let {
                                    portfolioCoinEntity.toPortfolioCoinModel(it.price)
                                }
                            }
                            emit(Result.Success(portfolioCoins))
                        }
                }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        coinsRemoteDataSource.getCoinById(coinId)
            .onError { error ->
                return Result.Error(error)
            }
            .onSuccess { coinDto ->
                val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
                return if (portfolioCoinEntity != null) {
                    Result.Success(portfolioCoinEntity.toPortfolioCoinModel(coinDto.data.coin.price))
                } else {
                    Result.Success(null)
                }
            }
        return Result.Error(DataError.Remote.UNKNOWN)
    }
    
    override suspend fun savePortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        TODO("Not yet implemented")
    }

    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        TODO("Not yet implemented")
    }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
        TODO("Not yet implemented")
    }

    override fun cashBalanceFlow(): Flow<Double> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCashBalance(newCashBalance: Double) {
        TODO("Not yet implemented")
    }
}