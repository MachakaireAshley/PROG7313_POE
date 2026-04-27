package com.example.prog7313_poe


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccountDao
{
    @Insert
    suspend fun insert(account: Account)

    @Query( "SELECT * FROM account_table")
    suspend fun getAll(): List<Account>

    @Query("DELETE FROM account_table WHERE id = :id")
    suspend fun deleteById(id: Int)
}