package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity("transaction_table")
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val name: String,
    val amount: Double,
    val transactionType: String,
    val date: Date,
    val startTime: Date,
    val endTime: Date,
    val accountId: Int,
    val categoryId: Int,
    val memberId: Int,
    val photo: Boolean = false,
    val photoPath: String?=null

)
