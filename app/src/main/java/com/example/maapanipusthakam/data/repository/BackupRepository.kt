package com.example.maapanipusthakam.data.repository

import android.content.ContentValues
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.data.local.AppDbHelper
import com.example.maapanipusthakam.domain.model.DailyPage
import com.example.maapanipusthakam.domain.model.Expense
import com.example.maapanipusthakam.domain.model.IncomeEntry
import com.example.maapanipusthakam.domain.model.LabourPayment
import com.example.maapanipusthakam.domain.model.Work
import com.example.maapanipusthakam.domain.model.WorkStatus
import com.example.maapanipusthakam.domain.model.Worker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

@Serializable
data class BackupDataPayload(
    val works: List<Work>,
    val dailyPages: List<DailyPage>,
    val incomeEntries: List<IncomeEntry>,
    val workers: List<Worker>,
    val labourPayments: List<LabourPayment>,
    val expenses: List<Expense>
)

@Serializable
data class BackupEnvelope(
    val schemaVersion: Int = 1,
    val appVersion: String = "1.0.0",
    val backupDate: Long, // epoch day
    val createdAtMillis: Long,
    val payloadJson: String,
    val checksumSha256: String
)

class BackupRepository(
    private val dbHelper: AppDbHelper,
    private val workRepo: WorkRepository,
    private val notebookRepo: NotebookRepository
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    /**
     * Creates a self-contained JSON backup string with SHA-256 verification
     */
    suspend fun createBackupJson(): String = withContext(Dispatchers.IO) {
        val allWorks = workRepo.getAllWorksSync()
        val allPages = getAllDailyPagesSync()
        val allIncome = getAllIncomeSync()
        val allWorkers = notebookRepo.getAllWorkers()
        val allLabour = getAllLabourSync()
        val allExpenses = getAllExpensesSync()

        val payload = BackupDataPayload(
            works = allWorks,
            dailyPages = allPages,
            incomeEntries = allIncome,
            workers = allWorkers,
            labourPayments = allLabour,
            expenses = allExpenses
        )

        val payloadStr = json.encodeToString(payload)
        val checksum = calculateSha256(payloadStr)

        val envelope = BackupEnvelope(
            schemaVersion = 1,
            appVersion = "1.0.0",
            backupDate = DateFormatter.todayEpochDay(),
            createdAtMillis = DateFormatter.currentTimeMillis(),
            payloadJson = payloadStr,
            checksumSha256 = checksum
        )

        json.encodeToString(envelope)
    }

    /**
     * Validates a backup string and returns envelope metadata
     */
    fun validateBackup(backupJsonStr: String): BackupEnvelope? {
        return try {
            val envelope = json.decodeFromString<BackupEnvelope>(backupJsonStr)
            val computedHash = calculateSha256(envelope.payloadJson)
            if (computedHash.equals(envelope.checksumSha256, ignoreCase = true)) {
                envelope
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Restores data from validated backup JSON string inside an atomic transaction
     */
    suspend fun restoreBackup(envelope: BackupEnvelope): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = try {
            json.decodeFromString<BackupDataPayload>(envelope.payloadJson)
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("బ్యాకప్ ఫైల్ చదవడం సాధ్యం కాలేదు."))
        }

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            // Clear current tables
            db.delete("expenses", null, null)
            db.delete("labour_payments", null, null)
            db.delete("income_entries", null, null)
            db.delete("daily_pages", null, null)
            db.delete("works", null, null)
            db.delete("workers", null, null)

            // Restore Workers
            for (w in payload.workers) {
                val cv = ContentValues().apply {
                    put("id", w.id)
                    put("name", w.name)
                    put("role", w.role)
                    put("phone", w.phone)
                    put("is_active", if (w.isActive) 1 else 0)
                    put("created_at", w.createdAt)
                    put("updated_at", w.updatedAt)
                }
                db.insert("workers", null, cv)
            }

            // Restore Works
            for (w in payload.works) {
                val cv = ContentValues().apply {
                    put("id", w.id)
                    put("name", w.name)
                    put("location", w.location)
                    put("owner_name", w.ownerName)
                    put("phone", w.phone)
                    put("notes", w.notes)
                    put("start_date", w.startDate)
                    if (w.completionDate != null) put("completion_date", w.completionDate) else putNull("completion_date")
                    put("status", w.status.name)
                    put("created_at", w.createdAt)
                    put("updated_at", w.updatedAt)
                }
                db.insert("works", null, cv)
            }

            // Restore Daily Pages
            for (dp in payload.dailyPages) {
                val cv = ContentValues().apply {
                    put("id", dp.id)
                    put("work_id", dp.workId)
                    put("date", dp.date)
                    put("work_description", dp.workDescription)
                    put("created_at", dp.createdAt)
                    put("updated_at", dp.updatedAt)
                }
                db.insert("daily_pages", null, cv)
            }

            // Restore Income
            for (i in payload.incomeEntries) {
                val cv = ContentValues().apply {
                    put("id", i.id)
                    put("work_id", i.workId)
                    put("daily_page_id", i.dailyPageId)
                    put("amount", i.amount)
                    put("source", i.source)
                    put("reason", i.reason)
                    put("note", i.note)
                    put("date", i.date)
                    put("created_at", i.createdAt)
                    put("updated_at", i.updatedAt)
                    if (i.deletedAt != null) put("deleted_at", i.deletedAt) else putNull("deleted_at")
                }
                db.insert("income_entries", null, cv)
            }

            // Restore Labour Payments
            for (lp in payload.labourPayments) {
                val cv = ContentValues().apply {
                    put("id", lp.id)
                    put("work_id", lp.workId)
                    put("daily_page_id", lp.dailyPageId)
                    put("worker_id", lp.workerId)
                    put("amount", lp.amount)
                    put("note", lp.note)
                    put("date", lp.date)
                    put("created_at", lp.createdAt)
                    put("updated_at", lp.updatedAt)
                    if (lp.deletedAt != null) put("deleted_at", lp.deletedAt) else putNull("deleted_at")
                }
                db.insert("labour_payments", null, cv)
            }

            // Restore Expenses
            for (e in payload.expenses) {
                val cv = ContentValues().apply {
                    put("id", e.id)
                    put("work_id", e.workId)
                    put("daily_page_id", e.dailyPageId)
                    put("category", e.category)
                    put("amount", e.amount)
                    put("note", e.note)
                    put("date", e.date)
                    put("created_at", e.createdAt)
                    put("updated_at", e.updatedAt)
                    if (e.deletedAt != null) put("deleted_at", e.deletedAt) else putNull("deleted_at")
                }
                db.insert("expenses", null, cv)
            }

            db.setTransactionSuccessful()
            dbHelper.notifyDataChanged()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            db.endTransaction()
        }
    }

    private fun WorkRepository.getAllWorksSync(): List<Work> {
        val list = mutableListOf<Work>()
        dbHelper.readableDatabase.rawQuery("SELECT * FROM works ORDER BY id ASC", null).use { c ->
            while (c.moveToNext()) {
                val statusStr = c.getString(c.getColumnIndexOrThrow("status"))
                val status = try { WorkStatus.valueOf(statusStr) } catch (e: Exception) { WorkStatus.ACTIVE }
                val compDate = if (c.isNull(c.getColumnIndexOrThrow("completion_date"))) null else c.getLong(c.getColumnIndexOrThrow("completion_date"))
                list.add(
                    Work(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        name = c.getString(c.getColumnIndexOrThrow("name")),
                        location = c.getString(c.getColumnIndexOrThrow("location")) ?: "",
                        ownerName = c.getString(c.getColumnIndexOrThrow("owner_name")) ?: "",
                        phone = c.getString(c.getColumnIndexOrThrow("phone")) ?: "",
                        notes = c.getString(c.getColumnIndexOrThrow("notes")) ?: "",
                        startDate = c.getLong(c.getColumnIndexOrThrow("start_date")),
                        completionDate = compDate,
                        status = status,
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
                        updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"))
                    )
                )
            }
        }
        return list
    }

    private fun getAllDailyPagesSync(): List<DailyPage> {
        val list = mutableListOf<DailyPage>()
        dbHelper.readableDatabase.rawQuery("SELECT * FROM daily_pages ORDER BY id ASC", null).use { c ->
            while (c.moveToNext()) {
                list.add(
                    DailyPage(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        workId = c.getLong(c.getColumnIndexOrThrow("work_id")),
                        date = c.getLong(c.getColumnIndexOrThrow("date")),
                        workDescription = c.getString(c.getColumnIndexOrThrow("work_description")) ?: "",
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
                        updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"))
                    )
                )
            }
        }
        return list
    }

    private fun getAllIncomeSync(): List<IncomeEntry> {
        val list = mutableListOf<IncomeEntry>()
        dbHelper.readableDatabase.rawQuery("SELECT * FROM income_entries ORDER BY id ASC", null).use { c ->
            while (c.moveToNext()) {
                val delAt = if (c.isNull(c.getColumnIndexOrThrow("deleted_at"))) null else c.getLong(c.getColumnIndexOrThrow("deleted_at"))
                list.add(
                    IncomeEntry(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        workId = c.getLong(c.getColumnIndexOrThrow("work_id")),
                        dailyPageId = c.getLong(c.getColumnIndexOrThrow("daily_page_id")),
                        amount = c.getLong(c.getColumnIndexOrThrow("amount")),
                        source = c.getString(c.getColumnIndexOrThrow("source")) ?: "",
                        reason = c.getString(c.getColumnIndexOrThrow("reason")) ?: "",
                        note = c.getString(c.getColumnIndexOrThrow("note")) ?: "",
                        date = c.getLong(c.getColumnIndexOrThrow("date")),
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
                        updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at")),
                        deletedAt = delAt
                    )
                )
            }
        }
        return list
    }

    private fun getAllLabourSync(): List<LabourPayment> {
        val list = mutableListOf<LabourPayment>()
        dbHelper.readableDatabase.rawQuery("SELECT * FROM labour_payments ORDER BY id ASC", null).use { c ->
            while (c.moveToNext()) {
                val delAt = if (c.isNull(c.getColumnIndexOrThrow("deleted_at"))) null else c.getLong(c.getColumnIndexOrThrow("deleted_at"))
                list.add(
                    LabourPayment(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        workId = c.getLong(c.getColumnIndexOrThrow("work_id")),
                        dailyPageId = c.getLong(c.getColumnIndexOrThrow("daily_page_id")),
                        workerId = c.getLong(c.getColumnIndexOrThrow("worker_id")),
                        amount = c.getLong(c.getColumnIndexOrThrow("amount")),
                        note = c.getString(c.getColumnIndexOrThrow("note")) ?: "",
                        date = c.getLong(c.getColumnIndexOrThrow("date")),
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
                        updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at")),
                        deletedAt = delAt
                    )
                )
            }
        }
        return list
    }

    private fun getAllExpensesSync(): List<Expense> {
        val list = mutableListOf<Expense>()
        dbHelper.readableDatabase.rawQuery("SELECT * FROM expenses ORDER BY id ASC", null).use { c ->
            while (c.moveToNext()) {
                val delAt = if (c.isNull(c.getColumnIndexOrThrow("deleted_at"))) null else c.getLong(c.getColumnIndexOrThrow("deleted_at"))
                list.add(
                    Expense(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        workId = c.getLong(c.getColumnIndexOrThrow("work_id")),
                        dailyPageId = c.getLong(c.getColumnIndexOrThrow("daily_page_id")),
                        category = c.getString(c.getColumnIndexOrThrow("category")),
                        amount = c.getLong(c.getColumnIndexOrThrow("amount")),
                        note = c.getString(c.getColumnIndexOrThrow("note")) ?: "",
                        date = c.getLong(c.getColumnIndexOrThrow("date")),
                        createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
                        updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at")),
                        deletedAt = delAt
                    )
                )
            }
        }
        return list
    }

    private fun calculateSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
