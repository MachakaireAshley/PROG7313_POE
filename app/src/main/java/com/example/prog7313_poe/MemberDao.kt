package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MemberDao
{
    @Insert
    suspend fun insert(member: Member)

    @Query("SELECT * FROM member_table")
    suspend fun getAll(): List<Member>

    @Query("DELETE FROM member_table WHERE id = :id")
    suspend fun deleteById(id: Int)
}