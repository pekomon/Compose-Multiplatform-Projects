package org.example.pekomon.cryptoapp.portfolio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PortFolioCoinEntity(
    @PrimaryKey
    val coinId: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val averagePurchasePrice: Double,
    val amountOwned: Double,
    val timestamp: Long
)
