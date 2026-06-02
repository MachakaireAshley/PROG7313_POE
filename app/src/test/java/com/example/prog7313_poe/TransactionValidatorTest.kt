package com.example.prog7313_poe

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class TransactionValidatorTest {

    @Test
    fun isAmountValid_positiveAmount_returnsTrue() {
        assertTrue(TransactionValidator.isAmountValid(10.0))
    }

    @Test
    fun isAmountValid_zeroAmount_returnsFalse() {
        assertFalse(TransactionValidator.isAmountValid(0.0))
    }

    @Test
    fun isAmountValid_negativeAmount_returnsFalse() {
        assertFalse(TransactionValidator.isAmountValid(-5.0))
    }

    @Test
    fun isDateInRange_dateInsideRange_returnsTrue() {
        val start = Date(1000)
        val end = Date(2000)
        val middle = Date(1500)
        assertTrue(TransactionValidator.isDateInRange(middle, start, end))
    }

    @Test
    fun isDateInRange_dateOutsideRange_returnsFalse() {
        val start = Date(1000)
        val end = Date(2000)
        val outside = Date(3000)
        assertFalse(TransactionValidator.isDateInRange(outside, start, end))
    }
}