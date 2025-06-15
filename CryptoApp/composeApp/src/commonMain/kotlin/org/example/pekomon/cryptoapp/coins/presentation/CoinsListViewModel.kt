package org.example.pekomon.cryptoapp.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.pekomon.cryptoapp.coins.domain.GetCoinPriceHistoryUseCase
import org.example.pekomon.cryptoapp.coins.domain.GetCoinsListUseCase
import org.example.pekomon.cryptoapp.core.domain.Result
import org.example.pekomon.cryptoapp.core.util.formatFiat
import org.example.pekomon.cryptoapp.core.util.formatPercentage
import org.example.pekomon.cryptoapp.core.util.toUiText

class CoinsListViewModel(
    private val getCoinsUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart {
            getAllCoins()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState()
        )

    private suspend fun getAllCoins() {
        when (val coinsResponse = getCoinsUseCase()) {
            is Result.Success -> {
                _state.update {
                    CoinsState(
                        coins = coinsResponse.data.map { item ->
                            UiCoinListItem(
                                id = item.coin.id,
                                name = item.coin.name,
                                iconUrl = item.coin.iconUrl,
                                symbol = item.coin.symbol,
                                formattedPrice = formatFiat(item.price),
                                formattedChange = formatPercentage(item.change),
                                isPositive = item.change >= 0
                            )
                        }
                    )
                }
            }

            is Result.Error-> {
                _state.update {
                    CoinsState(
                        error = coinsResponse.error.toUiText(),
                        coins = emptyList()
                    )
                }
            }
        }
    }

    fun onCoinLongPressed(coinId: String) {
        _state.update {
            it.copy(
                chartState = UiChartState(
                    sparkLine = emptyList(),
                    isLoading = true,
                    coinName = ""
                )
            )
        }

        viewModelScope.launch {
            when (val priceHistory = getCoinPriceHistoryUseCase(coinId)) {
                is Result.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            chartState = UiChartState(
                                sparkLine = priceHistory.data.sortedBy { it.timestamp }.map { it.price },
                                isLoading = false,
                                coinName = currentState.coins.firstOrNull { it.id == coinId }?.name ?: ""
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            chartState = UiChartState(
                                sparkLine = emptyList(),
                                isLoading = false,
                                coinName = ""
                            )
                        )
                    }
                }
            }
        }
    }

    fun dismissChart() {
        _state.update {
            it.copy(
                chartState = null
            )
        }
    }
}

