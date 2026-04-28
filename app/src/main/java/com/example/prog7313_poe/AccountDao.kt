package com.example.prog7313_poe


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountDao
{
    @Insert
    fun insert(account: Account)

    @Query( "SELECT * FROM account_table")
    fun getAll(): List<Account>

    @Query("DELETE FROM account_table WHERE id = :id")
    fun deleteById(id: Int)

    //added a get by id for updating account balance
    @Query("SELECT * FROM account_table WHERE id = :id LIMIT 1")
    fun getById(id: Int): Account?

    @Update
    fun update(account: Account)
}