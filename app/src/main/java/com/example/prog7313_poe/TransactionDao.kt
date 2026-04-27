package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao
{
    @Insert
    fun Insert(transaction: Transaction)

    @Query(value = "SELECT * FROM transaction_table")
    fun getAll(): List<Transaction>

}