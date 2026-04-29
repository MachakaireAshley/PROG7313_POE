package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("member_table")
class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val userId: String,
    val name: String
)