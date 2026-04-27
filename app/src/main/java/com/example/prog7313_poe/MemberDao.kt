package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MemberDao
{
    @Insert
    fun Insert(member: Member)

    @Query(value = "SELECT * FROM member_table")
    fun getAll(): List<Member>
}