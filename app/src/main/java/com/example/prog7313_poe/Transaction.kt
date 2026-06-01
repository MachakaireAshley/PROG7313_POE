package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prog7313_poe.Account
import java.util.Date

@Entity("transaction_table")
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val userId: String = "",
    val name: String="",
    val amount: Double=0.0,
    val transactionType: String="",
    val date: Date?=null,
    val startTime: Date?=null,
    val endTime: Date?=null,
    val accountId: Int=0,
    val categoryId: Int=0,
    val memberId: Int=0,
    val photo: Boolean = false,
    val photoPath: String?=null,
    val lastUpdated: Long=0

)

{
    constructor() : this(0,"", "", 0.0, "", null, null, null, 0, 0, 0, false, null, 0)
}
