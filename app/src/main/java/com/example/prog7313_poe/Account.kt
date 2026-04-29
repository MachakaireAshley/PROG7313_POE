package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("account_table")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val userId: String,
    val accountName: String,
    val amount: Double,

)