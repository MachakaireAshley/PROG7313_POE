package com.example.prog7313_poe


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccountDao
{
    @Insert
    fun Insert(account: Account)

    @Query(value = "SELECT * FROM account_table")
    fun getAll(): List<Account>
}