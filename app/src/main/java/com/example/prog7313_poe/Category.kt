package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity("category_table")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val name: String,
    val percentage: Int,

)