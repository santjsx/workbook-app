package com.example.maapanipusthakam.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.data.local.AppDbHelper
import com.example.maapanipusthakam.domain.model.DailyPage
import com.example.maapanipusthakam.domain.model.DailySummary
import com.example.maapanipusthakam.domain.model.Expense
import com.example.maapanipusthakam.domain.model.IncomeEntry
import com.example.maapanipusthakam.domain.model.LabourPayment
import com.example.maapanipusthakam.domain.model.Worker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class SearchResultItem(
    val id: Long,
    val type: String, // INCOME, LABOUR, EXPENSE, WORK
    val workId: Long,
    val workName: String,
    val title: String,
    val subtitle: String,
    val amount: Long?,
    val date: Long
)

class NotebookRepository(private val dbHelper: AppDbHelper) {

    /**
     * Retrieves or creates a daily page for a specific work and epoch day date
     */
    fun getOrCreateDailyPage(workId: Long, date: Long): DailyPage {
        val existing = getDailyPage(workId, date)
        if (existing != null) return existing

        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("work_id", workId)
            put("date", date)
            put("work_description", "")
            put("created_at", now)
            put("updated_at", now)
        }
        val id = dbHelper.writableDatabase.insertWithOnConflict(
            "daily_pages",
            null,
            cv,
            android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
        )
        dbHelper.notifyDataChanged()
        return getDailyPage(workId, date) ?: DailyPage(
            id = id,
            workId = workId,
            date = date,
            workDescription = "",
            createdAt = now,
            updatedAt = now
        )
    }

    fun getDailyPage(workId: Long, date: Long): DailyPage? {
        val db = dbHelper.readableDatabase
        db.rawQuery(
            "SELECT * FROM daily_pages WHERE work_id = ? AND date = ?",
            arrayOf(workId.toString(), date.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursorToDailyPage(cursor)
            }
        }
        return null
    }

    fun getDailyPageFlow(workId: Long, date: Long): Flow<DailyPage?> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                getDailyPage(workId, date)
            }
        }
    }

    fun updateWorkDescription(dailyPageId: Long, description: String) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("work_description", description.trim())
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("daily_pages", cv, "id = ?", arrayOf(dailyPageId.toString()))
        dbHelper.notifyDataChanged()
    }

    // ==================== INCOME ====================

    fun getIncomeForPage(dailyPageId: Long): Flow<List<IncomeEntry>> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                val list = mutableListOf<IncomeEntry>()
                val db = dbHelper.readableDatabase
                db.rawQuery(
                    "SELECT * FROM income_entries WHERE daily_page_id = ? AND deleted_at IS NULL ORDER BY created_at DESC",
                    arrayOf(dailyPageId.toString())
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        list.add(cursorToIncome(cursor))
                    }
                }
                list
            }
        }
    }

    fun addIncome(
        workId: Long,
        dailyPageId: Long,
        amount: Long,
        source: String = "",
        reason: String = "",
        note: String = "",
        date: Long
    ): Long {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("work_id", workId)
            put("daily_page_id", dailyPageId)
            put("amount", amount)
            put("source", source.trim())
            put("reason", reason.trim())
            put("note", note.trim())
            put("date", date)
            put("created_at", now)
            put("updated_at", now)
            putNull("deleted_at")
        }
        val id = dbHelper.writableDatabase.insert("income_entries", null, cv)
        touchWorkAndPage(workId, dailyPageId, now)
        dbHelper.notifyDataChanged()
        return id
    }

    fun updateIncome(id: Long, amount: Long, source: String, reason: String, note: String) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("amount", amount)
            put("source", source.trim())
            put("reason", reason.trim())
            put("note", note.trim())
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("income_entries", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    fun deleteIncome(id: Long) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("deleted_at", now)
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("income_entries", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    fun restoreIncome(id: Long) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            putNull("deleted_at")
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("income_entries", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    // ==================== WORKERS ====================

    fun getAllWorkers(): List<Worker> {
        val list = mutableListOf<Worker>()
        val db = dbHelper.readableDatabase
        db.rawQuery("SELECT * FROM workers ORDER BY name ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursorToWorker(cursor))
            }
        }
        return list
    }

    fun getOrCreateWorker(name: String, role: String): Worker {
        val trimmedName = name.trim()
        val trimmedRole = role.trim().ifEmpty { "కూలీ" }
        val db = dbHelper.readableDatabase
        db.rawQuery(
            "SELECT * FROM workers WHERE name = ? AND role = ?",
            arrayOf(trimmedName, trimmedRole)
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursorToWorker(cursor)
            }
        }

        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("name", trimmedName)
            put("role", trimmedRole)
            put("phone", "")
            put("is_active", 1)
            put("created_at", now)
            put("updated_at", now)
        }
        val id = dbHelper.writableDatabase.insert("workers", null, cv)
        return Worker(id = id, name = trimmedName, role = trimmedRole, createdAt = now, updatedAt = now)
    }

    // ==================== LABOUR ====================

    fun getLabourForPage(dailyPageId: Long): Flow<List<LabourPayment>> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                val list = mutableListOf<LabourPayment>()
                val db = dbHelper.readableDatabase
                val sql = """
                    SELECT lp.*, w.name AS worker_name, w.role AS worker_role
                    FROM labour_payments lp
                    LEFT JOIN workers w ON lp.worker_id = w.id
                    WHERE lp.daily_page_id = ? AND lp.deleted_at IS NULL
                    ORDER BY lp.created_at DESC
                """.trimIndent()
                db.rawQuery(sql, arrayOf(dailyPageId.toString())).use { cursor ->
                    while (cursor.moveToNext()) {
                        list.add(cursorToLabour(cursor))
                    }
                }
                list
            }
        }
    }

    fun addLabourPayment(
        workId: Long,
        dailyPageId: Long,
        workerName: String,
        workerRole: String,
        amount: Long,
        note: String = "",
        date: Long
    ): Long {
        val worker = getOrCreateWorker(workerName, workerRole)
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("work_id", workId)
            put("daily_page_id", dailyPageId)
            put("worker_id", worker.id)
            put("amount", amount)
            put("note", note.trim())
            put("date", date)
            put("created_at", now)
            put("updated_at", now)
            putNull("deleted_at")
        }
        val id = dbHelper.writableDatabase.insert("labour_payments", null, cv)
        touchWorkAndPage(workId, dailyPageId, now)
        dbHelper.notifyDataChanged()
        return id
    }

    fun updateLabourPayment(id: Long, workerName: String, workerRole: String, amount: Long, note: String) {
        val worker = getOrCreateWorker(workerName, workerRole)
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("worker_id", worker.id)
            put("amount", amount)
            put("note", note.trim())
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("labour_payments", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    fun deleteLabourPayment(id: Long) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("deleted_at", now)
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("labour_payments", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    fun restoreLabourPayment(id: Long) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            putNull("deleted_at")
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("labour_payments", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    // ==================== EXPENSE ====================

    fun getExpensesForPage(dailyPageId: Long): Flow<List<Expense>> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                val list = mutableListOf<Expense>()
                val db = dbHelper.readableDatabase
                db.rawQuery(
                    "SELECT * FROM expenses WHERE daily_page_id = ? AND deleted_at IS NULL ORDER BY created_at DESC",
                    arrayOf(dailyPageId.toString())
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        list.add(cursorToExpense(cursor))
                    }
                }
                list
            }
        }
    }

    fun addExpense(
        workId: Long,
        dailyPageId: Long,
        category: String,
        amount: Long,
        note: String = "",
        date: Long
    ): Long {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("work_id", workId)
            put("daily_page_id", dailyPageId)
            put("category", category.trim())
            put("amount", amount)
            put("note", note.trim())
            put("date", date)
            put("created_at", now)
            put("updated_at", now)
            putNull("deleted_at")
        }
        val id = dbHelper.writableDatabase.insert("expenses", null, cv)
        touchWorkAndPage(workId, dailyPageId, now)
        dbHelper.notifyDataChanged()
        return id
    }

    fun updateExpense(id: Long, category: String, amount: Long, note: String) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("category", category.trim())
            put("amount", amount)
            put("note", note.trim())
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("expenses", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    fun deleteExpense(id: Long) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("deleted_at", now)
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("expenses", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    fun restoreExpense(id: Long) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            putNull("deleted_at")
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("expenses", cv, "id = ?", arrayOf(id.toString()))
        dbHelper.notifyDataChanged()
    }

    // ==================== DAILY SUMMARY ====================

    fun getDailySummary(workId: Long, date: Long): Flow<DailySummary> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                val db = dbHelper.readableDatabase
                var income = 0L
                var labour = 0L
                var otherExpense = 0L

                db.rawQuery(
                    "SELECT COALESCE(SUM(amount), 0) FROM income_entries WHERE work_id = ? AND date = ? AND deleted_at IS NULL",
                    arrayOf(workId.toString(), date.toString())
                ).use { c ->
                    if (c.moveToFirst()) income = c.getLong(0)
                }

                db.rawQuery(
                    "SELECT COALESCE(SUM(amount), 0) FROM labour_payments WHERE work_id = ? AND date = ? AND deleted_at IS NULL",
                    arrayOf(workId.toString(), date.toString())
                ).use { c ->
                    if (c.moveToFirst()) labour = c.getLong(0)
                }

                db.rawQuery(
                    "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE work_id = ? AND date = ? AND deleted_at IS NULL",
                    arrayOf(workId.toString(), date.toString())
                ).use { c ->
                    if (c.moveToFirst()) otherExpense = c.getLong(0)
                }

                val totalExpense = labour + otherExpense
                val remaining = income - totalExpense

                DailySummary(
                    date = date,
                    incomeTotal = income,
                    labourTotal = labour,
                    otherExpenseTotal = otherExpense,
                    totalExpense = totalExpense,
                    remaining = remaining
                )
            }
        }
    }

    // ==================== SEARCH ====================

    fun searchAll(query: String): Flow<List<SearchResultItem>> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                val results = mutableListOf<SearchResultItem>()
                val trimmed = query.trim()
                if (trimmed.isEmpty()) return@withContext results

                val pattern = "%$trimmed%"
                val db = dbHelper.readableDatabase

                // 1. Works
                db.rawQuery(
                    "SELECT id, name, location, owner_name, start_date FROM works WHERE name LIKE ? OR location LIKE ? OR owner_name LIKE ? OR notes LIKE ? ORDER BY updated_at DESC LIMIT 20",
                    arrayOf(pattern, pattern, pattern, pattern)
                ).use { c ->
                    while (c.moveToNext()) {
                        results.add(
                            SearchResultItem(
                                id = c.getLong(0),
                                type = "WORK",
                                workId = c.getLong(0),
                                workName = c.getString(1),
                                title = c.getString(1),
                                subtitle = c.getString(2) ?: "",
                                amount = null,
                                date = c.getLong(4)
                            )
                        )
                    }
                }

                // 2. Labour Payments
                val labourSql = """
                    SELECT lp.id, lp.work_id, w.name AS work_name, wk.name AS worker_name, wk.role AS worker_role, lp.amount, lp.date, lp.note
                    FROM labour_payments lp
                    JOIN works w ON lp.work_id = w.id
                    JOIN workers wk ON lp.worker_id = wk.id
                    WHERE lp.deleted_at IS NULL AND (wk.name LIKE ? OR wk.role LIKE ? OR lp.note LIKE ?)
                    ORDER BY lp.date DESC LIMIT 30
                """.trimIndent()
                db.rawQuery(labourSql, arrayOf(pattern, pattern, pattern)).use { c ->
                    while (c.moveToNext()) {
                        val workerName = c.getString(3)
                        val workerRole = c.getString(4)
                        results.add(
                            SearchResultItem(
                                id = c.getLong(0),
                                type = "LABOUR",
                                workId = c.getLong(1),
                                workName = c.getString(2),
                                title = "$workerName ($workerRole)",
                                subtitle = c.getString(7) ?: "",
                                amount = c.getLong(5),
                                date = c.getLong(6)
                            )
                        )
                    }
                }

                // 3. Expenses
                val expSql = """
                    SELECT e.id, e.work_id, w.name AS work_name, e.category, e.note, e.amount, e.date
                    FROM expenses e
                    JOIN works w ON e.work_id = w.id
                    WHERE e.deleted_at IS NULL AND (e.category LIKE ? OR e.note LIKE ?)
                    ORDER BY e.date DESC LIMIT 30
                """.trimIndent()
                db.rawQuery(expSql, arrayOf(pattern, pattern)).use { c ->
                    while (c.moveToNext()) {
                        val cat = c.getString(3)
                        val note = c.getString(4) ?: ""
                        results.add(
                            SearchResultItem(
                                id = c.getLong(0),
                                type = "EXPENSE",
                                workId = c.getLong(1),
                                workName = c.getString(2),
                                title = cat,
                                subtitle = note,
                                amount = c.getLong(5),
                                date = c.getLong(6)
                            )
                        )
                    }
                }

                // 4. Income
                val incSql = """
                    SELECT i.id, i.work_id, w.name AS work_name, i.source, i.reason, i.amount, i.date
                    FROM income_entries i
                    JOIN works w ON i.work_id = w.id
                    WHERE i.deleted_at IS NULL AND (i.source LIKE ? OR i.reason LIKE ? OR i.note LIKE ?)
                    ORDER BY i.date DESC LIMIT 30
                """.trimIndent()
                db.rawQuery(incSql, arrayOf(pattern, pattern, pattern)).use { c ->
                    while (c.moveToNext()) {
                        val source = c.getString(3) ?: ""
                        val reason = c.getString(4) ?: ""
                        val title = if (source.isNotBlank()) "వచ్చిన డబ్బు: $source" else "వచ్చిన డబ్బు"
                        results.add(
                            SearchResultItem(
                                id = c.getLong(0),
                                type = "INCOME",
                                workId = c.getLong(1),
                                workName = c.getString(2),
                                title = title,
                                subtitle = reason,
                                amount = c.getLong(5),
                                date = c.getLong(6)
                            )
                        )
                    }
                }

                results
            }
        }
    }

    private fun touchWorkAndPage(workId: Long, dailyPageId: Long, now: Long) {
        val cvWork = ContentValues().apply { put("updated_at", now) }
        dbHelper.writableDatabase.update("works", cvWork, "id = ?", arrayOf(workId.toString()))

        val cvPage = ContentValues().apply { put("updated_at", now) }
        dbHelper.writableDatabase.update("daily_pages", cvPage, "id = ?", arrayOf(dailyPageId.toString()))
    }

    private fun cursorToDailyPage(c: Cursor): DailyPage {
        return DailyPage(
            id = c.getLong(c.getColumnIndexOrThrow("id")),
            workId = c.getLong(c.getColumnIndexOrThrow("work_id")),
            date = c.getLong(c.getColumnIndexOrThrow("date")),
            workDescription = c.getString(c.getColumnIndexOrThrow("work_description")) ?: "",
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
            updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"))
        )
    }

    private fun cursorToIncome(c: Cursor): IncomeEntry {
        val delAt = if (c.isNull(c.getColumnIndexOrThrow("deleted_at"))) null else c.getLong(c.getColumnIndexOrThrow("deleted_at"))
        return IncomeEntry(
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
    }

    private fun cursorToWorker(c: Cursor): Worker {
        return Worker(
            id = c.getLong(c.getColumnIndexOrThrow("id")),
            name = c.getString(c.getColumnIndexOrThrow("name")),
            role = c.getString(c.getColumnIndexOrThrow("role")),
            phone = c.getString(c.getColumnIndexOrThrow("phone")) ?: "",
            isActive = c.getInt(c.getColumnIndexOrThrow("is_active")) == 1,
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
            updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"))
        )
    }

    private fun cursorToLabour(c: Cursor): LabourPayment {
        val delAt = if (c.isNull(c.getColumnIndexOrThrow("deleted_at"))) null else c.getLong(c.getColumnIndexOrThrow("deleted_at"))
        val workerName = try { c.getString(c.getColumnIndexOrThrow("worker_name")) ?: "" } catch (e: Exception) { "" }
        val workerRole = try { c.getString(c.getColumnIndexOrThrow("worker_role")) ?: "" } catch (e: Exception) { "" }
        return LabourPayment(
            id = c.getLong(c.getColumnIndexOrThrow("id")),
            workId = c.getLong(c.getColumnIndexOrThrow("work_id")),
            dailyPageId = c.getLong(c.getColumnIndexOrThrow("daily_page_id")),
            workerId = c.getLong(c.getColumnIndexOrThrow("worker_id")),
            workerName = workerName,
            workerRole = workerRole,
            amount = c.getLong(c.getColumnIndexOrThrow("amount")),
            note = c.getString(c.getColumnIndexOrThrow("note")) ?: "",
            date = c.getLong(c.getColumnIndexOrThrow("date")),
            createdAt = c.getLong(c.getColumnIndexOrThrow("created_at")),
            updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at")),
            deletedAt = delAt
        )
    }

    private fun cursorToExpense(c: Cursor): Expense {
        val delAt = if (c.isNull(c.getColumnIndexOrThrow("deleted_at"))) null else c.getLong(c.getColumnIndexOrThrow("deleted_at"))
        return Expense(
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
    }
}
