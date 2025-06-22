package org.example.pekomon.cryptoapp.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.example.pekomon.cryptoapp.core.database.portfolio.PortfolioDatabase
import platform.Foundation.NSHomeDirectory

fun getPortfolioDatabaseBuilder(): RoomDatabase.Builder<PortfolioDatabase> {
    val dbFile = NSHomeDirectory() + "/portfolio.db"
    return Room.databaseBuilder<PortfolioDatabase>(
        name = dbFile
    )
}