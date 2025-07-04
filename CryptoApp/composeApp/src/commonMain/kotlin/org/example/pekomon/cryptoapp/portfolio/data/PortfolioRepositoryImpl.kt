package org.example.pekomon.cryptoapp.portfolio.data

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
import org.example.pekomon.cryptoapp.portfolio.data.mappers.toPortfolioCoinEntity
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
       try {
           portfolioDao.insert(portfolioCoinModel.toPortfolioCoinEntity())
           return Result.Success(Unit)
       } catch (e: SQLiteException) {
           return Result.Error(DataError.Local.DISK_FULL)
       }
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deletePortfolioItem(coinId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinEntities ->
            if (portfolioCoinEntities.isEmpty()) {
                flow {
                    emit(Result.Success(0.0))
                }
            } else {
                flow {
                    val apiResult = coinsRemoteDataSource.getListOfCoins()
                    apiResult.onError { error ->
                        emit(Result.Error(error))
                    }.onSuccess { coinsDto ->
                        val totalValue = portfolioCoinEntities.sumOf { owndeCoin ->
                            val coinPrice = coinsDto.data.coins.find { it.uuid == owndeCoin.coinId }?.price ?: 0.0
                            owndeCoin.amountOwned * coinPrice
                        }
                        emit(Result.Success(totalValue))
                    }
                }
            }
        }.catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
        return combine(
            cashBalanceFlow(),
            calculateTotalPortfolioValue()
        ) { cashBalance, portfolioValue ->
            when (portfolioValue) {
                is Result.Success -> {
                    Result.Success(cashBalance + portfolioValue.data)
                }
                is Result.Error -> {
                    Result.Error(portfolioValue.error)
                }
            }
        }
    }

    override fun cashBalanceFlow(): Flow<Double> {
        return flow {
            emit(userBalanceDao.getCashBalance() ?: 0.0)
        }
    }

    override suspend fun updateCashBalance(newCashBalance: Double) {
        userBalanceDao.updateCashBalance(newCashBalance)
    }
}
