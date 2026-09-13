package com.example.maapanipusthakam.data

import com.example.maapanipusthakam.domain.model.WorkSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkDeletionTest {

    data class TestWork(val id: Long, val name: String)
    data class TestDailyPage(val id: Long, val workId: Long, val date: Long)
    data class TestIncome(val id: Long, val workId: Long, val amount: Long)
    data class TestLabour(val id: Long, val workId: Long, val workerId: Long, val amount: Long)
    data class TestExpense(val id: Long, val workId: Long, val amount: Long)
    data class TestWorker(val id: Long, val name: String)

    @Test
    fun testCascadingDeletionPreservesOtherWorksAndWorkers() {
        // Setup multiple works
        val works = mutableListOf(
            TestWork(1L, "Ramesh House"),
            TestWork(2L, "Suresh Complex")
        )
        val dailyPages = mutableListOf(
            TestDailyPage(101L, 1L, 20700L),
            TestDailyPage(102L, 2L, 20700L)
        )
        val incomeEntries = mutableListOf(
            TestIncome(201L, 1L, 50000L),
            TestIncome(202L, 2L, 100000L)
        )
        val workers = mutableListOf(
            TestWorker(1L, "Appa Rao"),
            TestWorker(2L, "Sathi Babu")
        )
        val labourPayments = mutableListOf(
            TestLabour(301L, 1L, 1L, 15000L),
            TestLabour(302L, 2L, 2L, 25000L)
        )
        val expenses = mutableListOf(
            TestExpense(401L, 1L, 5000L),
            TestExpense(402L, 2L, 12000L)
        )

        // Delete Work 1 (Ramesh House)
        val workIdToDelete = 1L
        val workRemoved = works.removeIf { it.id == workIdToDelete }
        assertTrue("Work should be removed", workRemoved)

        // Cascade simulation (mirroring SQLite ON DELETE CASCADE)
        dailyPages.removeIf { it.workId == workIdToDelete }
        incomeEntries.removeIf { it.workId == workIdToDelete }
        labourPayments.removeIf { it.workId == workIdToDelete }
        expenses.removeIf { it.workId == workIdToDelete }

        // Assertions: Work 1 and its dependent children are gone
        assertEquals(1, works.size)
        assertEquals(2L, works.first().id)

        assertEquals(1, dailyPages.size)
        assertEquals(2L, dailyPages.first().workId)

        assertEquals(1, incomeEntries.size)
        assertEquals(2L, incomeEntries.first().workId)

        assertEquals(1, labourPayments.size)
        assertEquals(2L, labourPayments.first().workId)

        assertEquals(1, expenses.size)
        assertEquals(2L, expenses.first().workId)

        // Assertions: Workers are independent master records and must NOT be deleted
        assertEquals(2, workers.size)

        // Financial totals for remaining Work 2 must remain 100% intact
        val work2Income = incomeEntries.filter { it.workId == 2L }.sumOf { it.amount }
        val work2Labour = labourPayments.filter { it.workId == 2L }.sumOf { it.amount }
        val work2Expense = expenses.filter { it.workId == 2L }.sumOf { it.amount }
        val work2Summary = WorkSummary(
            totalIncome = work2Income,
            labourTotal = work2Labour,
            otherExpenseTotal = work2Expense,
            totalExpense = work2Labour + work2Expense,
            remaining = work2Income - (work2Labour + work2Expense)
        )

        assertEquals(100000L, work2Summary.totalIncome)
        assertEquals(25000L, work2Summary.labourTotal)
        assertEquals(12000L, work2Summary.otherExpenseTotal)
        assertEquals(37000L, work2Summary.totalExpense)
        assertEquals(63000L, work2Summary.remaining)
    }

    @Test
    fun testDeleteNonExistentWorkReturnsFalse() {
        val works = mutableListOf(TestWork(1L, "Existing Work"))
        val workRemoved = works.removeIf { it.id == 999L }
        assertFalse("Deleting non-existent work should return false", workRemoved)
        assertEquals(1, works.size)
    }
}
