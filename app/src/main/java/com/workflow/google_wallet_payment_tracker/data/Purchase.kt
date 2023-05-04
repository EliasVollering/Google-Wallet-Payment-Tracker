package com.workflow.google_wallet_payment_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity()
data class Purchase(
    val title: String,
    val text: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)


/* REAL DATA ClASS
@Entity()
data class Purchase(
    val location: String,
    val date: String,
    val amount: Double,
    val card: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
 */
