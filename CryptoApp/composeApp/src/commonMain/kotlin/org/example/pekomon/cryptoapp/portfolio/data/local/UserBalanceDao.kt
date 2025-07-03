package org.example.pekomon.cryptoapp.portfolio.data.local

import androidx.room.Query
import androidx.room.Upsert

interface UserBalanceDao {

    @Query("SELECT cashBalance FROM UserBalanceEntity WHERE id = 1")
    suspend fun getCashBalance(): Double?

    @Upsert
    suspend fun insertBalance(userBalanceEntity: UserBalanceEntity)

    @Query("UPDATE UserBalanceEntity SET cashBalance = :newCashBalance WHERE id = 1")
    suspend fun updateCashBalance(newCashBalance: Double)

}