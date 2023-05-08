package com.workflow.google_wallet_payment_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.workflow.google_wallet_payment_tracker.data.Purchase
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Upsert
    suspend fun upsertPurchase(purchase: Purchase)

    @Delete
    suspend fun deletePurchase(purchase: Purchase)

    @Query("SELECT * FROM purchase ORDER BY date DESC")
    fun getListOfPurchases(): Flow <List<Purchase>>


}