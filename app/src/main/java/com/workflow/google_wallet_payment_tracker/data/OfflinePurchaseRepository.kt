package com.workflow.google_wallet_payment_tracker.data

import kotlinx.coroutines.flow.Flow

class OfflinePurchaseRepository(private val purchaseDao: PurchaseDao) : PurchaseRepository {
    override fun getListOfPurchases(): Flow<List<Purchase>> = purchaseDao.getListOfPurchases()

    override suspend fun upsertPurchase(purchase: Purchase) = purchaseDao.upsertPurchase(purchase)

    override suspend fun deletePurchase(purchase: Purchase) = purchaseDao.deletePurchase(purchase)
}