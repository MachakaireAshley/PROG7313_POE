package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.sql.Date

@Dao
interface TransactionDao
{
    @Insert
    suspend fun Insert(transaction: Transaction)

    @Query(value = "SELECT * FROM transaction_table")
    suspend fun getAll(): List<Transaction>

    @Query("SELECT * FROM transaction_table WHERE date BETWEEN :start AND :end")
    suspend fun getBetweenDates(start: Date, end: Date): List<Transaction>

    @Query("SELECT * FROM transaction_table WHERE transactionType = :type AND date BETWEEN :start AND :end")
    suspend fun getByTypeBetweenDates(type: String, start: Date, end: Date): List<Transaction>
}