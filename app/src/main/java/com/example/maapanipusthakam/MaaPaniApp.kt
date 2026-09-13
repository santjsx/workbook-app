package com.example.maapanipusthakam

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.maapanipusthakam.data.local.AppDbHelper
import com.example.maapanipusthakam.data.repository.BackupRepository
import com.example.maapanipusthakam.data.repository.NotebookRepository
import com.example.maapanipusthakam.data.repository.WorkRepository

class MaaPaniApp : Application() {

    lateinit var dbHelper: AppDbHelper
        private set
    lateinit var workRepository: WorkRepository
        private set
    lateinit var notebookRepository: NotebookRepository
        private set
    lateinit var backupRepository: BackupRepository
        private set

    // Elderly user preferences
    var isLargeTextEnabled by mutableStateOf(false)
    var isPageAnimationEnabled by mutableStateOf(true)

    override fun onCreate() {
        super.onCreate()
        instance = this

        dbHelper = AppDbHelper.getInstance(this)
        workRepository = WorkRepository(dbHelper)
        notebookRepository = NotebookRepository(dbHelper)
        backupRepository = BackupRepository(dbHelper, workRepository, notebookRepository)

        val prefs = getSharedPreferences("maa_pani_prefs", Context.MODE_PRIVATE)
        isLargeTextEnabled = prefs.getBoolean("large_text", false)
        isPageAnimationEnabled = prefs.getBoolean("page_anim", true)
    }

    fun setLargeText(enabled: Boolean) {
        isLargeTextEnabled = enabled
        getSharedPreferences("maa_pani_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("large_text", enabled)
            .apply()
    }

    fun setPageAnimation(enabled: Boolean) {
        isPageAnimationEnabled = enabled
        getSharedPreferences("maa_pani_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("page_anim", enabled)
            .apply()
    }

    companion object {
        lateinit var instance: MaaPaniApp
            private set
    }
}
