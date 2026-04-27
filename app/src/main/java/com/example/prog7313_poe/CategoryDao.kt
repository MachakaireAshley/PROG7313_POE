package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CategoryDao
{
    @Insert
    fun Insert(category: Category)

    @Query(value = "SELECT * FROM category_table")
    fun getAll(): List<Category>
}