package com.example.prog7313_poe

import java.util.Date

object TransactionValidator {
    fun isAmountValid(amount: Double): Boolean = amount > 0
    fun isDateInRange(date: Date, start: Date, end: Date): Boolean = date in start..end
}