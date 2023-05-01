package com.workflow.google_wallet_payment_tracker

import android.content.Context
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Upsert
    suspend fun upsertContact(purchase: Purchase)

    @Delete
    suspend fun deleteContact(purchase: Purchase)

    @Query("SELECT * from purchase ORDER BY date ASC")
    fun getListOfPurchases(): Flow <List<Purchase>>
}