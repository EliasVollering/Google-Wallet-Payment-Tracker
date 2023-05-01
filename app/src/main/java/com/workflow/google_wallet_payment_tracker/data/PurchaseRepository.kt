package com.workflow.google_wallet_payment_tracker.data

import com.workflow.google_wallet_payment_tracker.data.Purchase
import kotlinx.coroutines.flow.Flow


interface PurchaseRepository {

    suspend fun upsertPurchase(purchase: Purchase)


    suspend fun deletePurchase(purchase: Purchase)


    fun getListOfPurchases(): Flow<List<Purchase>>

}