package com.example.maapanipusthakam.domain

import com.example.maapanipusthakam.domain.model.WorkSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialCalculationsTest {

    @Test
    fun testSummaryFormula() {
        val income = 80000L
        val labour = 32000L
        val otherExpenses = 8500L

        val totalExpense = labour + otherExpenses
        val remaining = income - totalExpense

        val summary = WorkSummary(
            totalIncome = income,
            labourTotal = labour,
            otherExpenseTotal = otherExpenses,
            totalExpense = totalExpense,
            remaining = remaining
        )

        assertEquals(80000L, summary.totalIncome)
        assertEquals(32000L, summary.labourTotal)
        assertEquals(8500L, summary.otherExpenseTotal)
        assertEquals(40500L, summary.totalExpense)
        assertEquals(39500L, summary.remaining)
    }

    @Test
    fun testZeroValues() {
        val summary = WorkSummary(
            totalIncome = 0L,
            labourTotal = 0L,
            otherExpenseTotal = 0L,
            totalExpense = 0L,
            remaining = 0L
        )
        assertEquals(0L, summary.remaining)
    }

    @Test
    fun testLargeIndianValues() {
        val income = 15000000L // 1.5 Crores
        val labour = 6000000L
        val otherExpenses = 4500000L
        val totalExpense = labour + otherExpenses
        val remaining = income - totalExpense

        assertEquals(10500000L, totalExpense)
        assertEquals(4500000L, remaining)
    }
}
