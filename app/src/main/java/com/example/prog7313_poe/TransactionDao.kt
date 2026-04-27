package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface TransactionDao
{
    @Insert
    fun insert(transaction: Transaction)

    @Query(value = "SELECT * FROM transaction_table")
    fun getAll(): List<Transaction>

    @Query("SELECT * FROM transaction_table WHERE date BETWEEN :start AND :end")
    fun getBetweenDates(start: Date, end: Date): List<Transaction>

    @Query("SELECT * FROM transaction_table WHERE transactionType = :type AND date BETWEEN :start AND :end")
    fun getByTypeBetweenDates(type: String, start: Date, end: Date): List<Transaction>
}