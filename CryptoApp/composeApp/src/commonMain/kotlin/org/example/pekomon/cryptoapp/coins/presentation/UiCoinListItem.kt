package org.example.pekomon.cryptoapp.coins.presentation

data class UiCoinListItem(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val formattedPrice: String,
    val formattedChange: String,
    val isPositive: Boolean, // TODO: better having state: neg, pos, neutral
)
