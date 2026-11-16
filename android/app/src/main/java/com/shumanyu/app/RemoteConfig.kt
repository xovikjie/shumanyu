package com.shumanyu.app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

internal data class RemoteAnnouncement(val id: String, val title: String, val message: String)

/**
 * GitHub 托管的远程配置：公告、功能开关、书源启停。
 * 只下发数据，不含任何书源地址、密钥或加密内容。
 */
internal object RemoteConfig {
    private const val PREFS = "remote_config"
    private const val URL = "https://raw.githubusercontent.com/xovikjie/shumanyu/main/config.json"
    private const val FALLBACK = "https://cdn.jsdelivr.net/gh/xovikjie/shumanyu@main/config.json"

    var announcement by mutableStateOf<RemoteAnnouncement?>(null)
        private set
    var novelEnabled by mutableStateOf(true)
        private set
    var downloadEnabled by mutableStateOf(true)
        private set
    var entryEnabled by mutableStateOf(true)
        private set
    var enabledSources by mutableStateOf<Set<String>?>(null)
        private set
    var rev by mutableIntStateOf(0)
        private set

    private var cachedLoaded = false

    fun loadCached(context: Context) {
        if (cachedLoaded) return
        cachedLoaded = true
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("json", null) ?: return
        runCatching { apply(raw) }
    }

    suspend fun refresh(context: Context) {
        val raw = withContext(Dispatchers.IO) { fetch(URL) ?: fetch(FALLBACK) } ?: return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("json", raw).apply()
        runCatching { apply(raw) }
    }

    fun sourcesEnabled(id: String): Boolean = enabledSources?.contains(id) ?: true

    fun markAnnouncementSeen(context: Context, id: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("announcement", id).apply()
    }

    fun announcementSeen(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("announcement", null)

    private fun apply(raw: String) {
        val root = JSONObject(raw)
        if (root.optInt("schema", 1) > 1) return
        root.optJSONObject("features")?.let { f ->
            novelEnabled = f.optBoolean("novel", true)
            downloadEnabled = f.optBoolean("download", true)
            entryEnabled = f.optBoolean("entry", true)
        }
        root.optJSONObject("announcement")?.let { a ->
            val id = a.optString("id").trim()
            val message = a.optString("message").trim()
            announcement = if (id.isNotBlank() && message.isNotBlank()) {
                RemoteAnnouncement(id, a.optString("title").trim().ifBlank { "公告" }, message)
            } else null
        } ?: run { announcement = null }
        val sources = root.optJSONObject("sources")?.optJSONArray("enabled")
        enabledSources = sources?.let { arr ->
            (0 until arr.length()).mapNotNull { arr.optString(it).takeIf { s -> s.isNotBlank() } }.toSet()
        }
        rev++
    }

    private fun fetch(url: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 4_000
                readTimeout = 6_000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "ShuManYu-Android")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Cache-Control", "no-cache")
            }
            if (conn.responseCode !in 200..299) return null
            val bytes = conn.inputStream.use { input ->
                val out = java.io.ByteArrayOutputStream()
                val buf = ByteArray(8 * 1024)
                while (true) {
                    val n = input.read(buf)
                    if (n < 0) break
                    if (out.size() + n > 200 * 1024) return null
                    out.write(buf, 0, n)
                }
                out.toByteArray()
            }
            String(bytes, Charsets.UTF_8)
        } catch (_: Exception) {
            null
        } finally {
            runCatching { conn?.disconnect() }
        }
    }
}
