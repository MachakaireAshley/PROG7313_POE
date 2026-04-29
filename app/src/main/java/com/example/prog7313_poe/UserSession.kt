package com.example.prog7313_poe

import com.google.firebase.auth.FirebaseAuth

object UserSession {
    val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("No user logged in")
}