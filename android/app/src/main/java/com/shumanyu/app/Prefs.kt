package com.shumanyu.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

data class BookFolder(
    val id: String,
    val name: String,
    val books: List<String> = emptyList(),
    val layout: String = "list",
)

data class ShelfOpts(
    val sort: String = "acquired",
    val asc: Boolean = false,
    val layout: String = "list",
    val fDl: Boolean = false,
    val fUnread: Boolean = false,
    val fDone: Boolean = false,
    val markDl: Boolean = false,
    val markUnread: Boolean = true,
    val markLocal: Boolean = false,
    val genre: Boolean = false,
    val count: Boolean = false,
) {
    val filtering: Boolean get() = fDl || fUnread || fDone
}

class Prefs(context: Context) {
    private val p: SharedPreferences = context.getSharedPreferences("shumanyu", Context.MODE_PRIVATE)

    init {
        context.getSharedPreferences("legado_b778fe6b", Context.MODE_PRIVATE).edit().clear().apply()
        context.filesDir.listFiles()?.filter { it.name.startsWith("legado-b778fe6b") }?.forEach { it.delete() }
        if(!p.getBoolean("readerCompletePageV1",false)) {
            val old=runCatching { JSONObject(p.getString("reading","{}").orEmpty()) }.getOrDefault(JSONObject())
            old.put("zoomMode","fit")
            p.edit().putString("reading",old.toString()).putBoolean("readerCompletePageV1",true).apply()
        }
        migrateBiquge102()
        migrateFavorites102()
        migrateV22()
        migrateColorOS17()
        migrateMiuix100()
        p.edit().remove("xiu2SourcesImportedV1").apply()
        context.getSharedPreferences("novel_source_settings", Context.MODE_PRIVATE).edit().clear().apply()
        if (p.contains("liquidGlass")) p.edit().remove("liquidGlass").apply()
    }

    private fun migrateFavorites102() {
        if (!p.getBoolean("flatFavorites102", false)) {
            favorites = (favorites + folders.flatMap { it.books }).distinct()
            p.edit().putBoolean("flatFavorites102", true).apply()
        }
    }

    private fun migrateBiquge102() {
        if (p.getBoolean("biquge365V102", false)) return
        val oldIds = runCatching { LocalStore.loadAllListings().filter { it.siteId == Sites.BIQUGE }.map { it.id }.toSet() }.getOrDefault(emptySet())
        fun retired(id: String) = id in oldIds || id.startsWith("bq")
        history = history.filterNot(::retired)
        progress = progress.filterKeys { !retired(it) }
        val read = readChapterKeys().filterNot { retired(it.substringBefore(':')) }
        val sites = runCatching { JSONArray(p.getString("webSites", "[]")) }.getOrDefault(JSONArray())
        for (i in 0 until sites.length()) {
            val site = sites.getJSONObject(i)
            if (site.optString("id") == Sites.BIQUGE) site.put("url", Biquge365.HOME+"/").put("lastError", "").put("itemCount", 0)
        }
        LocalStore.dropListing(Sites.BIQUGE)
        p.edit().putString("webSites", sites.toString()).putString("readChapters", read.joinToString("\n")).putBoolean("biquge365V102", true).apply()
    }

    fun openStoreOnFirstLaunch(): Boolean {
        if (p.getBoolean("firstStore102", false) || p.contains("storeBiqugeDefault")) return false
        p.edit().putBoolean("firstStore102", true).apply()
        return true
    }

    var colorMode: ColorSchemeMode
        get() = runCatching {
            ColorSchemeMode.valueOf(p.getString("colorMode", ColorSchemeMode.MonetSystem.name)!!)
        }.getOrDefault(ColorSchemeMode.MonetSystem)
        set(v) {
            p.edit().putString("colorMode", v.name).apply()
        }

    var liquid: Boolean
        get() = p.getBoolean("liquid", true)
        set(v) {
            p.edit().putBoolean("liquid", v).apply()
        }

    var predictiveBack: Boolean
        get() = p.getBoolean("predictiveBack", true)
        set(v) {
            p.edit().putBoolean("predictiveBack", v).apply()
        }

    var glassAlpha: Float
        get() = p.getFloat("glassAlpha", 0.45f).coerceIn(0.18f, 0.82f)
        set(v) {
            p.edit().putFloat("glassAlpha", v.coerceIn(0.18f, 0.82f)).apply()
        }

    var novelFont: Int
        get() = p.getInt("novelFont", 23).coerceIn(16, 32)
        set(v) { p.edit().putInt("novelFont", v.coerceIn(16, 32)).apply() }

    var novelNight: Boolean
        get() = p.getBoolean("novelNight", false)
        set(v) { p.edit().putBoolean("novelNight", v).apply() }

    var novelTurn: String
        get() = p.getString("novelTurn", "cover") ?: "cover"
        set(v) { p.edit().putString("novelTurn", v).apply() }

    var novelGap: Float
        get() = p.getFloat("novelGap", 1.85f).coerceIn(1.45f, 2.3f)
        set(v) { p.edit().putFloat("novelGap", v.coerceIn(1.45f, 2.3f)).apply() }

    var novelEye: Boolean
        get() = p.getBoolean("novelEye", false)
        set(v) { p.edit().putBoolean("novelEye", v).apply() }

    var novelTone: String
        get() = p.getString("novelTone", "paper") ?: "paper"
        set(v) { p.edit().putString("novelTone", v).apply() }

    var lang: String
        get() = p.getString("lang", "system") ?: "system"
        set(v) {
            p.edit().putString("lang", v).apply()
        }

    var backupFreq: String
        get() = p.getString("backupFreq", "manual") ?: "manual"
        set(v) {
            p.edit().putString("backupFreq", v).apply()
        }

    var backupUri: String
        get() = p.getString("backupUri", "") ?: ""
        set(v) {
            p.edit().putString("backupUri", v).apply()
        }

    var lastBackupAt: Long
        get() = p.getLong("lastBackupAt", 0L)
        set(v) {
            p.edit().putLong("lastBackupAt", v).apply()
        }

    var labSeen: Boolean
        get() = p.getBoolean("labSeen", false)
        set(v) {
            p.edit().putBoolean("labSeen", v).apply()
        }

    var folderLab: Boolean
        get() = p.getBoolean("folderLab", true)
        set(v) {
            p.edit().putBoolean("folderLab", v).apply()
        }

    fun enableFoldersOfficial() {
        if (!p.getBoolean("foldersOfficial", false)) {
            p.edit().putBoolean("folderLab", true).putBoolean("foldersOfficial", true).apply()
        }
    }

    var incognito: Boolean
        get() = p.getBoolean("incognito", false)
        set(v) {
            p.edit().putBoolean("incognito", v).apply()
        }

    var allowAdult: Boolean
        get() = p.getBoolean("allowAdult", false)
        set(v) {
            p.edit().putBoolean("allowAdult", v).apply()
        }

    var peekGuard: Boolean
        get() = p.getBoolean("peekGuard", false)
        set(v) {
            p.edit().putBoolean("peekGuard", v).apply()
        }

    var themeSeed: Int
        get() = p.getInt("themeSeed", 0xFF3482FF.toInt())
        set(v) {
            p.edit().putInt("themeSeed", v).apply()
        }

    var themePalette: String
        get() = p.getString("themePalette", "TonalSpot") ?: "TonalSpot"
        set(v) {
            p.edit().putString("themePalette", v).apply()
        }

    var fontGear: String
        get() = p.getString("fontGear", "system") ?: "system"
        set(v) {
            p.edit().putString("fontGear", v).apply()
        }

    var appBrowser: Boolean
        get() = p.getBoolean("appBrowser", true)
        set(v) {
            p.edit().putBoolean("appBrowser", v).apply()
        }

    var mineBanner: String
        get() = p.getString("mineBanner", "") ?: ""
        set(v) {
            p.edit().putString("mineBanner", v).apply()
        }

    var histLayoutOn: Boolean
        get() = p.getBoolean("histLayoutOn", true)
        set(v) {
            p.edit().putBoolean("histLayoutOn", v).apply()
        }

    var histLayout: String
        get() = p.getString("histLayout", "v") ?: "v"
        set(v) {
            p.edit().putString("histLayout", v).apply()
        }

    var customCover: Boolean
        get() = true
        set(_) {
            p.edit().putBoolean("customCover", true).apply()
        }

    var savePhotos: Boolean
        get() = true
        set(_) {
            p.edit().putBoolean("savePhotos", true).apply()
        }

    private fun coverBook(): JSONObject =
        runCatching { JSONObject(p.getString("coverBook", "{}") ?: "{}") }.getOrDefault(JSONObject())

    fun coverOrig(id: String): String? =
        coverBook().optJSONObject(id)?.optString("orig")?.takeIf { it.isNotBlank() }

    fun coverPath(id: String): String? =
        coverBook().optJSONObject(id)?.optString("path")?.takeIf { it.isNotBlank() }

    fun noteCover(id: String, path: String, original: String) {
        val o = coverBook()
        val item = o.optJSONObject(id) ?: JSONObject()
        if (!item.has("orig") && original.isNotBlank() && !original.startsWith("/")) item.put("orig", original)
        item.put("path", path)
        o.put(id, item)
        p.edit().putString("coverBook", o.toString()).apply()
    }

    fun coverEntries(): List<Triple<String, String, String>> {
        val o = coverBook()
        return o.keys().asSequence().map { id ->
            val item = o.optJSONObject(id) ?: JSONObject()
            Triple(id, item.optString("path"), item.optString("orig"))
        }.toList()
    }

    var iconStyle: String
        get() = p.getString("iconStyle", "default") ?: "default"
        set(v) {
            p.edit().putString("iconStyle", v).apply()
        }

    var forceTablet: Boolean
        get() = p.getBoolean("forceTablet", false)
        set(v) {
            p.edit().putBoolean("forceTablet", v).apply()
        }

    var folders: List<BookFolder>
        get() {
            val raw = p.getString("folders", "[]") ?: "[]"
            return runCatching {
                val arr = JSONArray(raw)
                (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    val books = o.optJSONArray("books")
                    BookFolder(
                        o.optString("id"),
                        o.optString("name"),
                        if (books == null) emptyList() else (0 until books.length()).map { books.optString(it) }.filter { it.isNotBlank() },
                        o.optString("layout", "list").ifBlank { "list" },
                    )
                }.filter { it.id.isNotBlank() && it.name.isNotBlank() }
            }.getOrDefault(emptyList())
        }
        set(v) {
            val arr = JSONArray()
            v.forEach { f ->
                val books = JSONArray()
                f.books.forEach { books.put(it) }
                arr.put(JSONObject().put("id", f.id).put("name", f.name).put("layout", f.layout).put("books", books))
            }
            p.edit().putString("folders", arr.toString()).apply()
        }

    var lastSiteId: String
        get() = p.getString("lastSiteId", "") ?: ""
        set(v) {
            p.edit().putString("lastSiteId", v).apply()
        }

    var shelfOpts: ShelfOpts
        get() {
            val raw = p.getString("shelfUi", "") ?: ""
            if (raw.isBlank()) return ShelfOpts()
            return runCatching {
                val o = JSONObject(raw)
                ShelfOpts(
                    sort = o.optString("sort", "acquired"),
                    asc = o.optBoolean("asc", false),
                    layout = o.optString("layout", "list"),
                    fDl = o.optBoolean("fDl", false),
                    fUnread = o.optBoolean("fUnread", false),
                    fDone = o.optBoolean("fDone", false),
                    markDl = o.optBoolean("markDl", false),
                    markUnread = o.optBoolean("markUnread", true),
                    markLocal = o.optBoolean("markLocal", false),
                    genre = o.optBoolean("genre", false),
                    count = o.optBoolean("count", false),
                )
            }.getOrDefault(ShelfOpts())
        }
        set(v) {
            val o = JSONObject()
                .put("sort", v.sort)
                .put("asc", v.asc)
                .put("layout", v.layout)
                .put("fDl", v.fDl)
                .put("fUnread", v.fUnread)
                .put("fDone", v.fDone)
                .put("markDl", v.markDl)
                .put("markUnread", v.markUnread)
                .put("markLocal", v.markLocal)
                .put("genre", v.genre)
                .put("count", v.count)
            p.edit().putString("shelfUi", o.toString()).apply()
        }

    fun noteAdded(id: String) {
        if (id.isBlank()) return
        val o = runCatching { JSONObject(p.getString("libAdded", "{}") ?: "{}") }.getOrDefault(JSONObject())
        if (!o.has(id)) {
            o.put(id, System.currentTimeMillis())
            p.edit().putString("libAdded", o.toString()).apply()
        }
    }

    fun addedAt(id: String): Long {
        val o = runCatching { JSONObject(p.getString("libAdded", "{}") ?: "{}") }.getOrNull() ?: return 0L
        return o.optLong(id, 0L)
    }

    var library: List<String>
        get() = csv("library", "")
        set(v) {
            p.edit().putString("library", v.joinToString(",")).apply()
        }

    var favorites: List<String>
        get() = csv("favorites", "")
        set(v) {
            p.edit().putString("favorites", v.joinToString(",")).apply()
        }

    var history: List<String>
        get() = csv("history", "")
        set(v) {
            p.edit().putString("history", v.joinToString(",")).apply()
        }

    var progress: Map<String, Progress>
        get() {
            val raw = p.getString("progress", "") ?: ""
            if (raw.isBlank()) return emptyMap()
            return runCatching {
                val o = JSONObject(raw)
                o.keys().asSequence().associateWith { k ->
                    val x = o.getJSONObject(k)
                    Progress(x.optString("chapterId"), x.optInt("page"), x.optInt("percent"), x.optLong("at"))
                }
            }.getOrDefault(emptyMap())
        }
        set(v) {
            val o = JSONObject()
            v.forEach { (k, pr) ->
                o.put(
                    k,
                    JSONObject()
                        .put("chapterId", pr.chapterId)
                        .put("page", pr.page)
                        .put("percent", pr.percent)
                        .put("at", pr.at),
                )
            }
            p.edit().putString("progress", o.toString()).apply()
        }

    fun readChapterKeys(): Set<String> {
        val raw = p.getString("readChapters", "") ?: ""
        if (raw.isBlank()) return emptySet()
        return raw.split('\n').filter { it.isNotBlank() }.toSet()
    }

    fun markChapterRead(workId: String, chapterId: String) {
        val next = readChapterKeys() + "$workId:$chapterId"
        p.edit().putString("readChapters", next.joinToString("\n")).apply()
    }

    fun purgeLegacyBiquge() {
        if (p.getBoolean("purgedBiqugeV012b2", false)) return
        val bad = HashSet<String>()
        bad.addAll(LocalStore.dropLegacyNovels())
        fun mark(id: String) {
            if (id.startsWith("bq")) bad.add(id)
        }
        library.forEach(::mark)
        favorites.forEach(::mark)
        history.forEach(::mark)
        progress.keys.forEach(::mark)
        folders.forEach { folder -> folder.books.forEach(::mark) }
        if (bad.isNotEmpty()) {
            library = library.filterNot { it in bad }
            favorites = favorites.filterNot { it in bad }
            history = history.filterNot { it in bad }
            progress = progress.filterKeys { it !in bad }
            folders = folders.map { folder -> folder.copy(books = folder.books.filterNot { it in bad }) }
            val read = readChapterKeys().filterNot { key ->
                val id = key.substringBefore(':')
                id in bad || id.startsWith("bq")
            }
            val added = runCatching { JSONObject(p.getString("libAdded", "{}") ?: "{}") }.getOrDefault(JSONObject())
            added.keys().asSequence().toList().forEach { key ->
                if (key in bad || key.startsWith("bq")) added.remove(key)
            }
            p.edit()
                .putString("readChapters", read.joinToString("\n"))
                .putString("libAdded", added.toString())
                .apply()
        }
        p.edit().putBoolean("purgedBiqugeV012b2", true).apply()
    }

    fun webSites(): List<WebSite> {
        if (lastSiteId.startsWith("xiu2-")) lastSiteId = Sites.BIQUGE
        if (!p.getBoolean("storeBiqugeDefault", false)) {
            lastSiteId = Sites.BIQUGE
            p.edit().putBoolean("storeBiqugeDefault", true).apply()
        }
        val parsed = runCatching {
            val raw = p.getString("webSites", "[]") ?: "[]"
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                WebSite(
                    o.getString("id"),
                    o.getString("name").removeSuffix(".com"),
                    o.optString("url"),
                    o.optBoolean("enabled", true),
                    o.optString("lastError"),
                    o.optInt("itemCount"),
                    o.optString("kind", "html"),
                )
            }
        }.getOrDefault(emptyList())
        val cleaned = parsed.filterNot { s ->
            s.id == "ru" || s.kind == "ru" || s.url.contains("rumanhua", true) || s.name.contains("动漫大全")
        }
        fun keepSite(s: WebSite): Boolean {
            if (s.id == "legado-b778fe6b" || s.kind == "legado-b778fe6b" || s.id.startsWith("xiu2-") || s.kind == "legado-xiu2") return false
            if (s.id == Sites.JM || s.name.contains("禁漫天堂") || Sites.kindOf(s) == Sites.JM) return true
            if (s.id == Sites.JJ || s.kind == Sites.JJ || s.url.contains("jjmh.top", true) || s.name.contains("简简")) return false
            if (s.id == Sites.ALBUM || s.kind == Sites.ALBUM || s.url.contains("nsfwx", true) || s.name.contains("相簿")) return false
            if (s.name.trim() == "漫画源") return false
            return true
        }
        fun droppedNovel(s: WebSite) = (
            s.id == Sites.BIQUGE2 || s.id == Sites.BIQUGE3 ||
                s.name == "笔趣阁2" || s.name == "笔趣阁3" ||
                s.url.contains("biquge5200", true) || s.url.contains("8tsw.com", true))
        val rawKept = cleaned.filter { keepSite(it) }
        val kept = rawKept.filterNot { droppedNovel(it) || (it.id.startsWith("native22-") && XiuSources.rule(it.id)==null) }.toMutableList()
        if(!p.getBoolean("native22AddedV1",false)) {
            XiuSources.sites().forEach { site->if(kept.none { it.id==site.id })kept.add(site) }
            p.edit().putBoolean("native22AddedV1",true).apply()
        }
        val dropped = kept != rawKept
        val seedMore = !p.getBoolean("sitesSeededV201", false)
        val purge203 = !p.getBoolean("sitesSeededV203", false)
        val ensure205 = !p.getBoolean("sitesSeededV205", false)
        val ensure010 = !p.getBoolean("sitesSeededV010", false)
        val ensure012 = !p.getBoolean("sitesSeededV012", false)
        val ensure013 = !p.getBoolean("sitesSeededV013", false)
        val ensure100 = !p.getBoolean("sitesSeededV100", false)
        if (!p.getBoolean("sitesSeededV23", false) || seedMore || purge203 || ensure205 || ensure010 || ensure012 || ensure013 || ensure100 || dropped || kept.size != cleaned.size) {
            val merged = kept.toMutableList()
            merged.removeAll { it.id in setOf("island", "harbor", "mist") }
            Sites.defaults.forEach { d ->
                val idx = merged.indexOfFirst { s ->
                    s.id == d.id || urlsMatch(s.url, d.url) ||
                        (d.id == Sites.JM && (s.name.contains("禁漫天堂") || s.kind == Sites.JM)) ||
                        (d.id == Sites.IKAN && (s.kind == Sites.IKAN || s.url.contains("ikanhm", true) || s.name.contains("爱看韩漫") || s.name.contains("爱看漫画"))) ||
                        (d.id == Sites.BIQUGE && (s.id == Sites.BIQUGE || s.url.contains("biquge365", true) || s.url.contains("bqg5.com", true) || s.name == "笔趣阁"))
                }
                val isJm = d.id == Sites.JM || Sites.kindOf(d) == Sites.JM
                if (isJm) {
                    if (idx < 0) merged.add(0, d)
                } else if (idx >= 0) {
                    val cur = merged[idx]
                    if (cur.url.isBlank()) {
                        merged[idx] = cur.copy(name = d.name, url = d.url, kind = d.kind)
                    } else if (cur.kind.isBlank() || cur.kind == "html") {
                        merged[idx] = cur.copy(kind = d.kind)
                    }
                } else {
                    merged.add(d)
                }
            }
            merged.forEachIndexed { i, s ->
                if (s.id == Sites.IKAN && s.name != "爱看韩漫") merged[i] = s.copy(name = "爱看韩漫")
            }
            if (ensure012) {
                val builtin = Sites.defaults.first { it.id == Sites.BIQUGE }
                val bqIdx = merged.indexOfFirst { s ->
                    s.id == Sites.BIQUGE ||
                        (s.kind == Sites.BIQUGE && (s.url.contains("biquge365", true) || s.url.contains("bqg5.com", true)))
                }
                if (bqIdx >= 0) {
                    val cur = merged[bqIdx]
                    merged[bqIdx] = cur.copy(name = builtin.name, url = builtin.url, kind = Sites.BIQUGE)
                } else {
                    merged.add(0, builtin)
                }
                val hasCopy = merged.any { s ->
                    s.id == Sites.COPY || s.kind == Sites.COPY ||
                        s.url.contains("copy5000", true) || s.name.contains("拷贝漫画")
                }
                if (!hasCopy) {
                    val copy = Sites.defaults.first { it.id == Sites.COPY }
                    val at = merged.indexOfFirst { it.id == Sites.BIQUGE }.let { if (it < 0) 0 else it + 1 }
                    merged.add(at, copy)
                }
            }
            if (ensure100) {
                val builtin = Sites.defaults.first { it.id == Sites.BIQUGE }
                val bqIdx = merged.indexOfFirst { s ->
                    s.id == Sites.BIQUGE || s.name == "笔趣阁" || s.url.contains("bqg5.com", true)
                }
                if (bqIdx >= 0) {
                    val cur = merged[bqIdx]
                    merged[bqIdx] = cur.copy(name = builtin.name, url = builtin.url, kind = Sites.BIQUGE)
                }
            }
            merged.removeAll { droppedNovel(it) }
            if (merged.none { it.id == lastSiteId }) {
                lastSiteId = merged.firstOrNull { it.id == Sites.JM }?.id.orEmpty()
            }
            setWebSites(merged)
            p.edit()
                .putBoolean("sitesSeededV23", true)
                .putBoolean("sitesSeededV201", true)
                .putBoolean("sitesSeededV203", true)
                .putBoolean("sitesSeededV205", true)
                .putBoolean("sitesSeededV010", true)
                .putBoolean("sitesSeededV012", true)
                .putBoolean("sitesSeededV013", true)
                .putBoolean("sitesSeededV100", true)
                .apply()
            return merged
        }
        return kept.ifEmpty { Sites.defaults.also { setWebSites(it) } }
    }

    fun setWebSites(list: List<WebSite>) {
        val arr = JSONArray()
        list.forEach { s ->
            arr.put(
                JSONObject()
                    .put("id", s.id)
                    .put("name", s.name)
                    .put("url", s.url)
                    .put("enabled", s.enabled)
                    .put("lastError", s.lastError)
                    .put("itemCount", s.itemCount)
                    .put("kind", s.kind),
            )
        }
        p.edit().putString("webSites", arr.toString()).apply()
    }

    fun sourceEnabled(id: String, default: Boolean = true) = p.getBoolean("src_$id", default)

    fun setSourceEnabled(id: String, enabled: Boolean) {
        p.edit().putBoolean("src_$id", enabled).apply()
    }

    fun customSources(): List<Source> {
        val raw = p.getString("customSources", "[]") ?: "[]"
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Source(
                    o.getString("id"),
                    o.getString("name"),
                    o.optString("summary"),
                    o.optBoolean("enabled", true),
                    o.optString("url"),
                )
            }
        }.getOrDefault(emptyList())
    }

    fun setCustomSources(list: List<Source>) {
        val arr = JSONArray()
        list.forEach { s ->
            arr.put(
                JSONObject()
                    .put("id", s.id)
                    .put("name", s.name)
                    .put("summary", s.summary)
                    .put("enabled", s.enabled)
                    .put("url", s.url),
            )
        }
        p.edit().putString("customSources", arr.toString()).apply()
    }

    fun reading(): ReadingPrefs {
        val raw = p.getString("reading", "") ?: ""
        if (raw.isBlank()) return ReadingPrefs()
        return runCatching {
            val o = JSONObject(raw)
            ReadingPrefs(
                readMode = o.optString("readMode", "webtoon"),
                tapAnim = o.optString("tapAnim", "normal"),
                modeHint = o.optBoolean("modeHint", false),
                navOverlay = o.optBoolean("navOverlay", false),
                pageAnim = o.optBoolean("pageAnim", true),
                rotation = o.optString("rotation", "lock-portrait"),
                bgColor = o.optString("bgColor", "black"),
                fullscreen = o.optBoolean("fullscreen", true),
                keepScreenOn = o.optBoolean("keepScreenOn", true),
                showPageNum = o.optBoolean("showPageNum", true),
                skipRead = o.optBoolean("skipRead", false),
                skipFiltered = o.optBoolean("skipFiltered", true),
                chapterTransition = o.optBoolean("chapterTransition", true),
                navLayout = o.optString("navLayout", "default"),
                reverseTap = o.optString("reverseTap", "none"),
                zoomMode = o.optString("zoomMode", "fit"),
                zoomPos = o.optString("zoomPos", "auto"),
                cropEdges = o.optBoolean("cropEdges", false),
                splitDouble = o.optBoolean("splitDouble", false),
                webtoonNav = o.optString("webtoonNav", "default"),
                webtoonReverseTap = o.optString("webtoonReverseTap", "none"),
                sidePad = o.optString("sidePad", "none"),
                hideSens = o.optString("hideSens", "low"),
                webtoonCrop = o.optBoolean("webtoonCrop", false),
                webtoonSplit = o.optBoolean("webtoonSplit", false),
                tapTurn = o.optBoolean("tapTurn", true),
                volumeTurn = o.optBoolean("volumeTurn", false),
                longPressMenu = o.optBoolean("longPressMenu", true),
                savePageFolder = o.optBoolean("savePageFolder", false),
                filterOn = o.optBoolean("filterOn", false),
                filterR = o.optDouble("filterR", 0.0).toFloat(),
                filterG = o.optDouble("filterG", 0.0).toFloat(),
                filterB = o.optDouble("filterB", 0.0).toFloat(),
                filterA = o.optDouble("filterA", 0.0).toFloat(),
                filterBlend = o.optString("filterBlend", "default"),
                grayscale = o.optBoolean("grayscale", false),
                invert = o.optBoolean("invert", false),
                brightnessOn = o.optBoolean("brightnessOn", false),
                brightness = o.optDouble("brightness", 0.0).toFloat(),
                barInk = o.optString("barInk", "blue"),
            )
        }.getOrDefault(ReadingPrefs())
    }

    fun setReading(r: ReadingPrefs) {
        val o = JSONObject()
            .put("readMode", r.readMode)
            .put("tapAnim", r.tapAnim)
            .put("modeHint", r.modeHint)
            .put("navOverlay", r.navOverlay)
            .put("pageAnim", r.pageAnim)
            .put("rotation", r.rotation)
            .put("bgColor", r.bgColor)
            .put("fullscreen", r.fullscreen)
            .put("keepScreenOn", r.keepScreenOn)
            .put("showPageNum", r.showPageNum)
            .put("skipRead", r.skipRead)
            .put("skipFiltered", r.skipFiltered)
            .put("chapterTransition", r.chapterTransition)
            .put("navLayout", r.navLayout)
            .put("reverseTap", r.reverseTap)
            .put("zoomMode", r.zoomMode)
            .put("zoomPos", r.zoomPos)
            .put("cropEdges", r.cropEdges)
            .put("splitDouble", r.splitDouble)
            .put("webtoonNav", r.webtoonNav)
            .put("webtoonReverseTap", r.webtoonReverseTap)
            .put("sidePad", r.sidePad)
            .put("hideSens", r.hideSens)
            .put("webtoonCrop", r.webtoonCrop)
            .put("webtoonSplit", r.webtoonSplit)
            .put("tapTurn", r.tapTurn)
            .put("volumeTurn", r.volumeTurn)
            .put("longPressMenu", r.longPressMenu)
            .put("savePageFolder", r.savePageFolder)
            .put("filterOn", r.filterOn)
            .put("filterR", r.filterR.toDouble())
            .put("filterG", r.filterG.toDouble())
            .put("filterB", r.filterB.toDouble())
            .put("filterA", r.filterA.toDouble())
            .put("filterBlend", r.filterBlend)
            .put("grayscale", r.grayscale)
            .put("invert", r.invert)
            .put("brightnessOn", r.brightnessOn)
            .put("brightness", r.brightness.toDouble())
            .put("barInk", r.barInk)
        p.edit().putString("reading", o.toString()).apply()
    }

    fun exportJson(library: List<String>, favorites: List<String>, sources: List<Source>): String {
        val src = JSONArray()
        sources.forEach { s ->
            src.put(
                JSONObject()
                    .put("id", s.id)
                    .put("name", s.name)
                    .put("summary", s.summary)
                    .put("enabled", s.enabled)
                    .put("url", s.url),
            )
        }
        val hist = JSONArray(history)
        val prog = JSONObject()
        progress.forEach { (k, pr) ->
            prog.put(
                k,
                JSONObject()
                    .put("chapterId", pr.chapterId)
                    .put("page", pr.page)
                    .put("percent", pr.percent)
                    .put("at", pr.at),
            )
        }
        val sites = JSONArray()
        webSites().forEach { s ->
            sites.put(
                JSONObject()
                    .put("id", s.id)
                    .put("name", s.name)
                    .put("url", s.url)
                    .put("enabled", s.enabled)
                    .put("lastError", s.lastError)
                    .put("itemCount", s.itemCount)
                    .put("kind", s.kind),
            )
        }
        return JSONObject()
            .put("v", 4)
            .put("colorMode", colorMode.name)
            .put("liquid", liquid)
            .put("glassAlpha", glassAlpha)
            .put("predictiveBack", predictiveBack)
            .put("lang", lang)
            .put("backupFreq", backupFreq)
            .put("library", JSONArray(library))
            .put("favorites", JSONArray(favorites))
            .put("history", hist)
            .put("progress", prog)
            .put("webSites", sites)
            .put("sources", src)
            .put("reading", p.getString("reading", "{}") ?: "{}")
            .toString(2)
    }

    fun importJson(json: String): BackupData? {
        return runCatching {
            val o = JSONObject(json)
            o.optString("colorMode").takeIf { it.isNotBlank() }?.let {
                runCatching { colorMode = ColorSchemeMode.valueOf(it) }
            }
            if (o.has("liquid")) liquid = o.getBoolean("liquid")
            if (o.has("glassAlpha")) glassAlpha = o.getDouble("glassAlpha").toFloat()
            if (o.has("predictiveBack")) predictiveBack = o.getBoolean("predictiveBack")
            if (o.has("lang")) lang = o.getString("lang")
            if (o.has("backupFreq")) backupFreq = o.getString("backupFreq")
            fun arr(key: String) = o.optJSONArray(key)?.let { a ->
                (0 until a.length()).map { a.getString(it) }
            } ?: emptyList()
            val lib = arr("library").filter { it !in NOVEL_IDS }
            val fav = arr("favorites").filter { it !in NOVEL_IDS }
            val hist = arr("history").filter { it !in NOVEL_IDS }
            if (o.has("history")) history = hist
            if (o.has("progress")) {
                val po = o.optJSONObject("progress")
                if (po != null) {
                    progress = po.keys().asSequence().associateWith { k ->
                        val x = po.getJSONObject(k)
                        Progress(x.optString("chapterId"), x.optInt("page"), x.optInt("percent"), x.optLong("at"))
                    }
                }
            }
            if (o.has("webSites")) {
                val sa = o.optJSONArray("webSites")
                if (sa != null) {
                    val list = (0 until sa.length()).map { i ->
                        val s = sa.getJSONObject(i)
                        WebSite(
                            s.getString("id"),
                            s.getString("name"),
                            s.optString("url"),
                            s.optBoolean("enabled", true),
                            s.optString("lastError"),
                            s.optInt("itemCount"),
                            s.optString("kind", "html"),
                        )
                    }
                    setWebSites(list)
                }
            }
            if (o.has("reading")) {
                val r = o.opt("reading")
                when (r) {
                    is JSONObject -> p.edit().putString("reading", r.toString()).apply()
                    is String -> if (r.isNotBlank()) p.edit().putString("reading", r).apply()
                }
            }
            val srcArr = o.optJSONArray("sources")
            val src = if (srcArr != null) {
                (0 until srcArr.length()).map { i ->
                    val s = srcArr.getJSONObject(i)
                    Source(s.getString("id"), s.getString("name"), s.optString("summary"), s.optBoolean("enabled", true), s.optString("url"))
                }
            } else emptyList()
            BackupData(lib, fav, hist, src)
        }.getOrNull()
    }

    private fun migrateV22() {
        if (p.getBoolean("clearedNovelsV22", false)) return
        library = library.filter { it !in NOVEL_IDS }
        history = history.filter { it !in NOVEL_IDS }
        favorites = favorites.filter { it !in NOVEL_IDS }
        val prog = progress.toMutableMap()
        NOVEL_IDS.forEach { prog.remove(it) }
        progress = prog
        p.edit().putBoolean("clearedNovelsV22", true).apply()
    }

    private fun migrateMiuix100() {
        if (p.getBoolean("themeMiuix100", false)) return
        val seed = p.getInt("themeSeed", 0xFF00A870.toInt())
        val edit = p.edit().putBoolean("themeMiuix100", true)
        if (seed == 0xFF00A870.toInt()) edit.putInt("themeSeed", 0xFF3482FF.toInt())
        edit.apply()
    }

    private fun migrateColorOS17() {
        if (p.getBoolean("themeColorOS17", false)) return
        val seed = p.getInt("themeSeed", 0xFF00A870.toInt())
        val edit = p.edit().putBoolean("themeColorOS17", true)
        if (!p.contains("themeSeed") || seed == 0xFF3482FF.toInt()) {
            edit.putInt("themeSeed", 0xFF00A870.toInt())
        }
        edit.apply()
    }

    private fun csv(key: String, fallback: String) =
        (p.getString(key, fallback) ?: fallback).split(",").map { it.trim() }.filter { it.isNotEmpty() }

    private fun urlsMatch(a: String, b: String): Boolean {
        fun n(s: String) = s.removePrefix("https://").removePrefix("http://").trimEnd('/')
        return n(a).contains(n(b)) || n(b).contains(n(a))
    }
}
