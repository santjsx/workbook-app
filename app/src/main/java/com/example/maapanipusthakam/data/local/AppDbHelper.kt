package com.example.maapanipusthakam.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    private val _dataChangeEvents = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }
    val dataChangeEvents = _dataChangeEvents.asSharedFlow()

    fun notifyDataChanged() {
        _dataChangeEvents.tryEmit(Unit)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE works (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                location TEXT,
                owner_name TEXT,
                phone TEXT,
                notes TEXT,
                start_date INTEGER NOT NULL,
                completion_date INTEGER,
                status TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE daily_pages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                work_id INTEGER NOT NULL,
                date INTEGER NOT NULL,
                work_description TEXT,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                UNIQUE(work_id, date),
                FOREIGN KEY(work_id) REFERENCES works(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE income_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                work_id INTEGER NOT NULL,
                daily_page_id INTEGER NOT NULL,
                amount INTEGER NOT NULL,
                source TEXT,
                reason TEXT,
                note TEXT,
                date INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                deleted_at INTEGER,
                FOREIGN KEY(work_id) REFERENCES works(id) ON DELETE CASCADE,
                FOREIGN KEY(daily_page_id) REFERENCES daily_pages(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE workers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                role TEXT NOT NULL,
                phone TEXT,
                is_active INTEGER NOT NULL DEFAULT 1,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE labour_payments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                work_id INTEGER NOT NULL,
                daily_page_id INTEGER NOT NULL,
                worker_id INTEGER NOT NULL,
                amount INTEGER NOT NULL,
                note TEXT,
                date INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                deleted_at INTEGER,
                FOREIGN KEY(work_id) REFERENCES works(id) ON DELETE CASCADE,
                FOREIGN KEY(daily_page_id) REFERENCES daily_pages(id) ON DELETE CASCADE,
                FOREIGN KEY(worker_id) REFERENCES workers(id) ON DELETE RESTRICT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE expenses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                work_id INTEGER NOT NULL,
                daily_page_id INTEGER NOT NULL,
                category TEXT NOT NULL,
                amount INTEGER NOT NULL,
                note TEXT,
                date INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                deleted_at INTEGER,
                FOREIGN KEY(work_id) REFERENCES works(id) ON DELETE CASCADE,
                FOREIGN KEY(daily_page_id) REFERENCES daily_pages(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // Performance Indexes
        db.execSQL("CREATE INDEX idx_daily_pages_work_date ON daily_pages(work_id, date)")
        db.execSQL("CREATE INDEX idx_income_work_date ON income_entries(work_id, date)")
        db.execSQL("CREATE INDEX idx_income_page ON income_entries(daily_page_id)")
        db.execSQL("CREATE INDEX idx_labour_work_date ON labour_payments(work_id, date)")
        db.execSQL("CREATE INDEX idx_labour_page ON labour_payments(daily_page_id)")
        db.execSQL("CREATE INDEX idx_labour_worker ON labour_payments(worker_id)")
        db.execSQL("CREATE INDEX idx_expense_work_date ON expenses(work_id, date)")
        db.execSQL("CREATE INDEX idx_expense_page ON expenses(daily_page_id)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Schema migrations will be handled explicitly here when version increments
    }

    companion object {
        const val DATABASE_NAME = "maa_pani_pusthakam.db"
        const val DATABASE_VERSION = 1

        @Volatile
        private var instance: AppDbHelper? = null

        fun getInstance(context: Context): AppDbHelper {
            return instance ?: synchronized(this) {
                instance ?: AppDbHelper(context).also { instance = it }
            }
        }
    }
}
