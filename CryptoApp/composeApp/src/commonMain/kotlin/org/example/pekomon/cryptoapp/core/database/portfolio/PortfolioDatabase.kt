package org.example.pekomon.cryptoapp.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.pekomon.cryptoapp.portfolio.data.local.PortfolioCoinEntity
import org.example.pekomon.cryptoapp.portfolio.data.local.PortfolioDao

@Database(entities = [PortfolioCoinEntity::class], version = 1)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}