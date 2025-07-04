package org.example.pekomon.cryptoapp.portfolio.presentation

import org.jetbrains.compose.resources.StringResource

data class PortfolioState(
    val portfolioValue: String = "",
    val cashBalance: String = "",
    val showBuyButton: Boolean = false,
    val isLoaded: Boolean = false,
    val error: StringResource? = null,
    val coins: List<UiPortfolioCoinItem> = emptyList()
)
