package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("category_table")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val userId: String,
    val name: String,
    val percentage: Int=0,

)