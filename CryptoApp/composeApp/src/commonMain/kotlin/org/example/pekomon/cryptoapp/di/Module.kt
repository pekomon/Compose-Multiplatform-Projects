package org.example.pekomon.cryptoapp.di

import androidx.room.RoomDatabase
import io.ktor.client.HttpClient
import org.example.pekomon.cryptoapp.coins.data.remote.impl.KtorCoinsRemoteDataSource
import org.example.pekomon.cryptoapp.coins.domain.GetCoinDetailsUseCase
import org.example.pekomon.cryptoapp.coins.domain.GetCoinPriceHistoryUseCase
import org.example.pekomon.cryptoapp.coins.domain.GetCoinsListUseCase
import org.example.pekomon.cryptoapp.coins.domain.api.CoinsRemoteDataSource
import org.example.pekomon.cryptoapp.coins.presentation.CoinsListViewModel
import org.example.pekomon.cryptoapp.core.database.portfolio.PortfolioDatabase
import org.example.pekomon.cryptoapp.core.database.portfolio.getPortfolioDatabase
import org.example.pekomon.cryptoapp.core.network.HttpClientFactory
import org.example.pekomon.cryptoapp.portfolio.data.PortfolioRepositoryImpl
import org.example.pekomon.cryptoapp.portfolio.domain.PortfolioRepository
import org.example.pekomon.cryptoapp.portfolio.presentation.PortfolioViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule,
            platformModule
        )
    }

expect val platformModule: Module

val sharedModule = module {
    single<HttpClient> { HttpClientFactory.create(get()) }

    // portfolio
    single { getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>()) }
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()
    single { get<PortfolioDatabase>().portfolioDao() }
    single { get<PortfolioDatabase>().userBalanceDao() }
    viewModel { PortfolioViewModel(get()) }

    // coins list
    viewModel { CoinsListViewModel(get(), get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinDetailsUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)
}