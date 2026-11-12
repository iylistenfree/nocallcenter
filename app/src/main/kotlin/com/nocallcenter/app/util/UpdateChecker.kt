package com.nocallcenter.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val tagName: String,
    val releaseName: String,
    val releaseNotes: String,
    val htmlUrl: String,
    val downloadUrl: String
)

sealed class UpdateCheckResult {
    object UpToDate : UpdateCheckResult()
    data class Available(val info: UpdateInfo) : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

object UpdateChecker {

    private const val GITHUB_LATEST_RELEASE_API =
        "https://api.github.com/repos/iylistenfree/nocallcenter/releases/latest"

    /**
     * Checks GitHub for the latest release and compares it with the currently installed version.
     */
    suspend fun checkForUpdate(currentVersionName: String): UpdateCheckResult {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(GITHUB_LATEST_RELEASE_API)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "NoCallCenter-Android-App")
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    connectTimeout = 8000
                    readTimeout = 8000
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val jsonStr = reader.use { it.readText() }
                    val json = JSONObject(jsonStr)

                    val tagName = json.optString("tag_name", "").trim()
                    val releaseName = json.optString("name", tagName)
                    val body = json.optString("body", "").trim()
                    val htmlUrl = json.optString("html_url", "https://github.com/iylistenfree/nocallcenter/releases/latest")

                    // Look for the .apk asset direct download link
                    var apkDownloadUrl = htmlUrl
                    val assets = json.optJSONArray("assets")
                    if (assets != null) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val name = asset.optString("name", "")
                            if (name.endsWith(".apk", ignoreCase = true)) {
                                apkDownloadUrl = asset.optString("browser_download_url", htmlUrl)
                                break
                            }
                        }
                    }

                    if (isNewerVersion(currentVersionName, tagName)) {
                        UpdateCheckResult.Available(
                            UpdateInfo(
                                tagName = tagName,
                                releaseName = releaseName,
                                releaseNotes = body,
                                htmlUrl = htmlUrl,
                                downloadUrl = apkDownloadUrl
                            )
                        )
                    } else {
                        UpdateCheckResult.UpToDate
                    }
                } else {
                    UpdateCheckResult.Error("HTTP $responseCode")
                }
            } catch (e: Exception) {
                UpdateCheckResult.Error(e.localizedMessage ?: "Network error")
            } finally {
                connection?.disconnect()
            }
        }
    }

    /**
     * Compares semantic versioning (e.g. 1.0.1 vs 1.0.0 or v1.1.0 vs 1.0.1).
     * Returns true if latest is strictly newer than current.
     */
    fun isNewerVersion(current: String, latest: String): Boolean {
        val cleanCurrent = current.trimStart('v', 'V', ' ', '.')
        val cleanLatest = latest.trimStart('v', 'V', ' ', '.')

        val currentParts = cleanCurrent.split(".", "-").mapNotNull { it.toIntOrNull() }
        val latestParts = cleanLatest.split(".", "-").mapNotNull { it.toIntOrNull() }

        if (currentParts.isEmpty() || latestParts.isEmpty()) {
            return cleanLatest != cleanCurrent && cleanLatest.isNotEmpty()
        }

        val maxLen = maxOf(currentParts.size, latestParts.size)
        for (i in 0 until maxLen) {
            val c = currentParts.getOrElse(i) { 0 }
            val l = latestParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}
