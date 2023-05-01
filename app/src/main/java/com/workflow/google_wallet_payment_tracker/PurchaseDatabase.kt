package com.workflow.google_wallet_payment_tracker

import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Purchase::class], version = 1, exportSchema = false)
abstract class PurchaseDatabase: RoomDatabase() {

    abstract fun purchaseDao(): PurchaseDao

    companion object {
        @Volatile
        private var Instance: PurchaseDatabase? = null

        fun getDatabase(context: Context): PurchaseDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PurchaseDatabase::class.java, "purchase_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

