package com.workflow.google_wallet_payment_tracker.data

import android.content.Context

interface AppContainer {
    val purchaseRepository: PurchaseRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val purchaseRepository: PurchaseRepository by lazy {
        OfflinePurchaseRepository(PurchaseDatabase.getDatabase(context).purchaseDao())
    }
}