package com.example.maapanipusthakam.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class WorkStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED
}

@Serializable
data class Work(
    val id: Long = 0,
    val name: String,
    val location: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val notes: String = "",
    val startDate: Long,
    val completionDate: Long? = null,
    val status: WorkStatus = WorkStatus.ACTIVE,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

@Serializable
data class DailyPage(
    val id: Long = 0,
    val workId: Long,
    val date: Long, // epoch day
    val workDescription: String = "", // ఈ రోజు చేసిన పని
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

@Serializable
data class IncomeEntry(
    val id: Long = 0,
    val workId: Long,
    val dailyPageId: Long,
    val amount: Long, // In rupees (integer)
    val source: String = "", // ఎవరి దగ్గర నుంచి
    val reason: String = "", // ఏం కోసం
    val note: String = "",
    val date: Long, // epoch day
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val deletedAt: Long? = null
)

@Serializable
data class Worker(
    val id: Long = 0,
    val name: String,
    val role: String = "కూలీ", // మేస్త్రీ, కూలీ, etc.
    val phone: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

@Serializable
data class LabourPayment(
    val id: Long = 0,
    val workId: Long,
    val dailyPageId: Long,
    val workerId: Long,
    val workerName: String = "",
    val workerRole: String = "",
    val amount: Long, // In rupees (integer)
    val note: String = "",
    val date: Long, // epoch day
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val deletedAt: Long? = null
)

@Serializable
data class Expense(
    val id: Long = 0,
    val workId: Long,
    val dailyPageId: Long,
    val category: String, // సిమెంట్, ఇసుక, etc.
    val amount: Long, // In rupees (integer)
    val note: String = "",
    val date: Long, // epoch day
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val deletedAt: Long? = null
)

/**
 * Cumulative summary for a single Work or group of works
 */
data class WorkSummary(
    val totalIncome: Long = 0, // వచ్చింది
    val labourTotal: Long = 0, // కూలీలకు
    val otherExpenseTotal: Long = 0, // ఇతర ఖర్చు
    val totalExpense: Long = 0, // మొత్తం ఖర్చు = labourTotal + otherExpenseTotal
    val remaining: Long = 0 // మిగిలింది = totalIncome - totalExpense (never label as Profit / లాభం)
)

/**
 * Summary for a specific day
 */
data class DailySummary(
    val date: Long,
    val incomeTotal: Long = 0,
    val labourTotal: Long = 0,
    val otherExpenseTotal: Long = 0,
    val totalExpense: Long = 0,
    val remaining: Long = 0
)

/**
 * Standard expense categories as specified in PRD Section 19
 */
object ExpenseCategories {
    val defaults = listOf(
        "సిమెంట్",
        "ఇసుక",
        "ఇటుకలు",
        "రవాణా",
        "టీ / తిండి",
        "నీళ్లు",
        "పనిముట్లు",
        "ఇతర"
    )
}

/**
 * Standard worker roles
 */
object WorkerRoles {
    val defaults = listOf(
        "మేస్త్రీ",
        "కూలీ",
        "సిమెంట్ పని",
        "హెల్పర్"
    )
}
