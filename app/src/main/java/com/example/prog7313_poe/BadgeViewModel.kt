package com.example.prog7313_poe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth

class BadgeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val badges = liveData {
        val transactionCount = db.transactionDao().getTransactionCount(userId)
        val totalSavings = db.transactionDao().getTotalSavings(userId) ?: 0.0

        emit(BadgeManager().getBadges(
            totalSavings = totalSavings,
            transactionCount = transactionCount
        ))
    }
}