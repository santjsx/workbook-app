package com.example.maapanipusthakam

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey

@Serializable
data object HomeNavKey : AppNavKey

@Serializable
data object HistoryNavKey : AppNavKey

@Serializable
data object SearchNavKey : AppNavKey

@Serializable
data object SettingsNavKey : AppNavKey

@Serializable
data object NewWorkNavKey : AppNavKey

@Serializable
data class WorkNotebookNavKey(val workId: Long) : AppNavKey
