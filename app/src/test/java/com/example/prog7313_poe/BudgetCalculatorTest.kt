package com.example.prog7313_poe

import org.junit.Assert.*
import org.junit.Test

class BudgetCalculatorTest {

    @Test
    fun getPercentageSpent_normalInput_correctPercent() {
        assertEquals(50, BudgetCalculator.getPercentageSpent(500.0, 1000.0))
    }

    @Test
    fun getPercentageSpent_overSpend_cappedAt100() {
        assertEquals(100, BudgetCalculator.getPercentageSpent(1500.0, 1000.0))
    }

    @Test
    fun getPercentageSpent_zeroBudget_returnsZero() {
        assertEquals(0, BudgetCalculator.getPercentageSpent(500.0, 0.0))
    }

    @Test
    fun getRemainingBudget_positiveRemaining() {
        assertEquals(500.0, BudgetCalculator.getRemainingBudget(500.0, 1000.0), 0.01)
    }

    @Test
    fun getRemainingBudget_negativeRemaining_returnsZero() {
        assertEquals(0.0, BudgetCalculator.getRemainingBudget(1200.0, 1000.0), 0.01)
    }

    @Test
    fun getGoalStatus_belowMin() {
        assertEquals("Below minimum", BudgetCalculator.getGoalStatus(50.0, 100.0, 500.0))
    }

    @Test
    fun getGoalStatus_withinRange() {
        assertEquals("Within budget", BudgetCalculator.getGoalStatus(300.0, 100.0, 500.0))
    }

    @Test
    fun getGoalStatus_aboveMax() {
        assertEquals("Above maximum", BudgetCalculator.getGoalStatus(600.0, 100.0, 500.0))
    }
}