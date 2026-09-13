package com.example.maapanipusthakam.core.update

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class GitHubRelease(
    val tag_name: String = "",
    val name: String = "",
    val body: String = "",
    val html_url: String = "",
    val published_at: String = ""
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val version: String, val releaseNotes: String, val releaseUrl: String) : UpdateCheckResult()
    data object UpToDate : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

object GitHubUpdateManager {

    const val CURRENT_VERSION = "1.1.0"
    // Configured official repository for OTA distribution
    private const val REPO_OWNER = "santjsx"
    private const val REPO_NAME = "workbook-app"
    private const val GITHUB_API_URL = "https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest"

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun checkForUpdates(): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val url = URL(GITHUB_API_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "MaaPaniPusthakam-Android/$CURRENT_VERSION")
            }

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val release = json.decodeFromString<GitHubRelease>(responseText)
                val latestTag = release.tag_name.removePrefix("v").trim()

                if (isNewerVersion(latestTag, CURRENT_VERSION)) {
                    UpdateCheckResult.UpdateAvailable(
                        version = latestTag,
                        releaseNotes = release.body.ifBlank { "కొత్త మార్పులు మరియు సమస్యల పరిష్కారాలు." },
                        releaseUrl = release.html_url
                    )
                } else {
                    UpdateCheckResult.UpToDate
                }
            } else if (responseCode == 404) {
                // No releases yet in repository
                UpdateCheckResult.UpToDate
            } else {
                UpdateCheckResult.Error("అప్‌డేట్ వివరాలు పొందలేకపోయాం.")
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error("నెట్‌వర్క్ అందుబాటులో లేదు.")
        }
    }

    private fun isNewerVersion(remoteVersion: String, currentVersion: String): Boolean {
        val remoteParts = remoteVersion.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = currentVersion.split(".").mapNotNull { it.toIntOrNull() }
        val length = maxOf(remoteParts.size, currentParts.size)

        for (i in 0 until length) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
