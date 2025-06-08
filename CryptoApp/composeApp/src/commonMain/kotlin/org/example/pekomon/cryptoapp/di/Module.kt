package org.example.pekomon.cryptoapp.di

import io.ktor.client.HttpClient
import org.example.pekomon.cryptoapp.coins.data.remote.impl.KtorCoinsRemoteDataSource
import org.example.pekomon.cryptoapp.coins.domain.GetCoinDetailsUseCase
import org.example.pekomon.cryptoapp.coins.domain.GetCoinsListUseCase
import org.example.pekomon.cryptoapp.coins.domain.api.CoinsRemoteDataSource
import org.example.pekomon.cryptoapp.coins.presentation.CoinsListViewModel
import org.example.pekomon.cryptoapp.core.network.HttpClientFactory
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

    // coins list
    viewModel { CoinsListViewModel(get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinDetailsUseCase)
}