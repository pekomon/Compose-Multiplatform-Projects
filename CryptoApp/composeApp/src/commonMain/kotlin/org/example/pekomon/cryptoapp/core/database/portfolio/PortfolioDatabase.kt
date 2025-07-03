package org.example.pekomon.cryptoapp.core.database.portfolio

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.pekomon.cryptoapp.portfolio.data.local.PortfolioCoinEntity
import org.example.pekomon.cryptoapp.portfolio.data.local.PortfolioDao
import org.example.pekomon.cryptoapp.portfolio.data.local.UserBalanceDao
import org.example.pekomon.cryptoapp.portfolio.data.local.UserBalanceEntity

@Database(
    entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 2)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): UserBalanceDao

}