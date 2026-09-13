package com.example.maapanipusthakam.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.data.local.AppDbHelper
import com.example.maapanipusthakam.domain.model.Work
import com.example.maapanipusthakam.domain.model.WorkStatus
import com.example.maapanipusthakam.domain.model.WorkSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class TodayWorkItem(
    val work: Work,
    val todayIncome: Long,
    val todayExpense: Long,
    val hasActivityToday: Boolean
)

class WorkRepository(private val dbHelper: AppDbHelper) {

    fun getActiveWorks(): Flow<List<Work>> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                queryWorks("status = ?", arrayOf(WorkStatus.ACTIVE.name), "updated_at DESC")
            }
        }
    }

    fun getCompletedWorks(): Flow<List<Work>> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                queryWorks("status = ?", arrayOf(WorkStatus.COMPLETED.name), "completion_date DESC, updated_at DESC")
            }
        }
    }

    fun getAllWorks(): Flow<List<Work>> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                queryWorks(null, null, "status ASC, updated_at DESC")
            }
        }
    }

    fun getWorkByIdFlow(id: Long): Flow<Work?> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                getWorkById(id)
            }
        }
    }

    fun getWorkById(id: Long): Work? {
        val db = dbHelper.readableDatabase
        db.rawQuery("SELECT * FROM works WHERE id = ?", arrayOf(id.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursorToWork(cursor)
            }
        }
        return null
    }

    fun createWork(
        name: String,
        location: String = "",
        ownerName: String = "",
        phone: String = "",
        notes: String = "",
        startDate: Long = DateFormatter.todayEpochDay()
    ): Long {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("name", name.trim())
            put("location", location.trim())
            put("owner_name", ownerName.trim())
            put("phone", phone.trim())
            put("notes", notes.trim())
            put("start_date", startDate)
            putNull("completion_date")
            put("status", WorkStatus.ACTIVE.name)
            put("created_at", now)
            put("updated_at", now)
        }
        val id = dbHelper.writableDatabase.insertOrThrow("works", null, cv)
        dbHelper.notifyDataChanged()
        return id
    }

    fun updateWork(work: Work) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("name", work.name.trim())
            put("location", work.location.trim())
            put("owner_name", work.ownerName.trim())
            put("phone", work.phone.trim())
            put("notes", work.notes.trim())
            put("start_date", work.startDate)
            if (work.completionDate != null) {
                put("completion_date", work.completionDate)
            } else {
                putNull("completion_date")
            }
            put("status", work.status.name)
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("works", cv, "id = ?", arrayOf(work.id.toString()))
        dbHelper.notifyDataChanged()
    }

    fun completeWork(workId: Long) {
        val now = DateFormatter.currentTimeMillis()
        val today = DateFormatter.todayEpochDay()
        val cv = ContentValues().apply {
            put("status", WorkStatus.COMPLETED.name)
            put("completion_date", today)
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("works", cv, "id = ?", arrayOf(workId.toString()))
        dbHelper.notifyDataChanged()
    }

    fun reopenWork(workId: Long) {
        val now = DateFormatter.currentTimeMillis()
        val cv = ContentValues().apply {
            put("status", WorkStatus.ACTIVE.name)
            putNull("completion_date")
            put("updated_at", now)
        }
        dbHelper.writableDatabase.update("works", cv, "id = ?", arrayOf(workId.toString()))
        dbHelper.notifyDataChanged()
    }

    /**
     * Permanently deletes a work and cascades deletion to its daily_pages,
     * income_entries, labour_payments, and expenses via foreign key CASCADE.
     */
    fun deleteWork(workId: Long): Boolean {
        val rows = dbHelper.writableDatabase.delete("works", "id = ?", arrayOf(workId.toString()))
        dbHelper.notifyDataChanged()
        return rows > 0
    }

    fun getWorkSummary(workId: Long): Flow<WorkSummary> {
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                calculateWorkSummary(workId)
            }
        }
    }

    fun calculateWorkSummary(workId: Long): WorkSummary {
        val db = dbHelper.readableDatabase
        var income = 0L
        var labour = 0L
        var otherExpense = 0L

        // Total income
        db.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM income_entries WHERE work_id = ? AND deleted_at IS NULL",
            arrayOf(workId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                income = cursor.getLong(0)
            }
        }

        // Total labour
        db.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM labour_payments WHERE work_id = ? AND deleted_at IS NULL",
            arrayOf(workId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                labour = cursor.getLong(0)
            }
        }

        // Total other expenses
        db.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE work_id = ? AND deleted_at IS NULL",
            arrayOf(workId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                otherExpense = cursor.getLong(0)
            }
        }

        val totalExpense = labour + otherExpense
        val remaining = income - totalExpense

        return WorkSummary(
            totalIncome = income,
            labourTotal = labour,
            otherExpenseTotal = otherExpense,
            totalExpense = totalExpense,
            remaining = remaining
        )
    }

    fun getTodayWorks(): Flow<List<TodayWorkItem>> {
        val today = DateFormatter.todayEpochDay()
        return dbHelper.dataChangeEvents.map {
            withContext(Dispatchers.IO) {
                val db = dbHelper.readableDatabase
                val activeWorks = queryWorks("status = ?", arrayOf(WorkStatus.ACTIVE.name), "updated_at DESC")
                
                activeWorks.map { work ->
                    var todayIncome = 0L
                    var todayLabour = 0L
                    var todayOtherExpense = 0L
                    var hasPage = false

                    // Check daily page
                    db.rawQuery(
                        "SELECT id, work_description FROM daily_pages WHERE work_id = ? AND date = ?",
                        arrayOf(work.id.toString(), today.toString())
                    ).use { c ->
                        if (c.moveToFirst()) {
                            hasPage = true
                            val desc = c.getString(1)
                            if (!desc.isNullOrBlank()) {
                                hasPage = true
                            }
                        }
                    }

                    // Today Income
                    db.rawQuery(
                        "SELECT COALESCE(SUM(amount), 0) FROM income_entries WHERE work_id = ? AND date = ? AND deleted_at IS NULL",
                        arrayOf(work.id.toString(), today.toString())
                    ).use { c ->
                        if (c.moveToFirst()) todayIncome = c.getLong(0)
                    }

                    // Today Labour
                    db.rawQuery(
                        "SELECT COALESCE(SUM(amount), 0) FROM labour_payments WHERE work_id = ? AND date = ? AND deleted_at IS NULL",
                        arrayOf(work.id.toString(), today.toString())
                    ).use { c ->
                        if (c.moveToFirst()) todayLabour = c.getLong(0)
                    }

                    // Today Other Expense
                    db.rawQuery(
                        "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE work_id = ? AND date = ? AND deleted_at IS NULL",
                        arrayOf(work.id.toString(), today.toString())
                    ).use { c ->
                        if (c.moveToFirst()) todayOtherExpense = c.getLong(0)
                    }

                    val totalTodayExpense = todayLabour + todayOtherExpense
                    val hasActivity = hasPage || todayIncome > 0 || totalTodayExpense > 0

                    TodayWorkItem(
                        work = work,
                        todayIncome = todayIncome,
                        todayExpense = totalTodayExpense,
                        hasActivityToday = hasActivity
                    )
                }
            }
        }
    }

    private fun queryWorks(where: String?, args: Array<String>?, orderBy: String?): List<Work> {
        val list = mutableListOf<Work>()
        val db = dbHelper.readableDatabase
        db.query("works", null, where, args, null, null, orderBy).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursorToWork(cursor))
            }
        }
        return list
    }

    private fun cursorToWork(cursor: Cursor): Work {
        val statusStr = cursor.getString(cursor.getColumnIndexOrThrow("status"))
        val status = try {
            WorkStatus.valueOf(statusStr)
        } catch (e: Exception) {
            WorkStatus.ACTIVE
        }

        val completionDate = if (cursor.isNull(cursor.getColumnIndexOrThrow("completion_date"))) {
            null
        } else {
            cursor.getLong(cursor.getColumnIndexOrThrow("completion_date"))
        }

        return Work(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            location = cursor.getString(cursor.getColumnIndexOrThrow("location")) ?: "",
            ownerName = cursor.getString(cursor.getColumnIndexOrThrow("owner_name")) ?: "",
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")) ?: "",
            notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")) ?: "",
            startDate = cursor.getLong(cursor.getColumnIndexOrThrow("start_date")),
            completionDate = completionDate,
            status = status,
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
        )
    }
}
