package com.shumanyu.app

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class BookFolder(
    val id: String,
    val name: String,
    val books: List<String> = emptyList(),
    val layout: String = "list",
)

data class ShelfOpts(
    val sort: String = "acquired",
    val asc: Boolean = false,
    val layout: String = "compact",
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
        // 预测性返回已固定开启（0.1.2 起无开关），清掉旧版本存下的设置。
        if (p.contains("predictiveBack")) p.edit().remove("predictiveBack").apply()
        context.getSharedPreferences("legado_b778fe6b", Context.MODE_PRIVATE).edit().clear().apply()
        context.filesDir.listFiles()?.filter { it.name.startsWith("legado-b778fe6b") }?.forEach { it.delete() }
        if(!p.getBoolean("readerCompletePageV1",false)) {
            val old=runCatching { JSONObject(p.getString("reading","{}").orEmpty()) }.getOrDefault(JSONObject())
            old.put("zoomMode","fit")
            p.edit().putString("reading",old.toString()).putBoolean("readerCompletePageV1",true).apply()
        }
        migrateFavorites102()
        migrateV22()
        migrateCopyOnly010()
        migrateShelfCompact()
        p.edit().remove("xiu2SourcesImportedV1").apply()
        context.getSharedPreferences("novel_source_settings", Context.MODE_PRIVATE).edit().clear().apply()
        p.edit()
            .remove("liquidGlass").remove("liquid").remove("glassAlpha")
            .remove("themeSeed").remove("themePalette").remove("themeSeedMigrated100").remove("themeColorOS17")
            .remove("labSeen").remove("appBrowser").remove("folderLab").remove("foldersOfficial")
            .remove("customSources").remove("histLayoutOn")
            .apply()
    }

    private fun migrateFavorites102() {
        if (!p.getBoolean("flatFavorites102", false)) {
            favorites = (favorites + folders.flatMap { it.books }).distinct()
            p.edit().putBoolean("flatFavorites102", true).apply()
        }
    }

    /**
     * 0.1.0: 拷贝漫画 became the only source. Drop the old 笔趣阁 listing cache; shelf and
     * history entries are kept untouched. 0.1.3beta re-adds 禁漫天堂 / 爱看韩漫, so their
     * saved works stay in place.
     */
    private fun migrateCopyOnly010() {
        if (p.getBoolean("copyOnly010", false)) return
        runCatching { LocalStore.dropListing("biquge") }
        p.edit().putBoolean("copyOnly010", true).apply()
    }

    /** 0.1.3beta: 书架默认改为紧凑网格卡片视图，旧版本存下的列表布局迁移一次。 */
    private fun migrateShelfCompact() {
        if (p.getBoolean("shelfCompact1", false)) return
        val raw = p.getString("shelfUi", "") ?: ""
        if (raw.isBlank()) {
            p.edit().putBoolean("shelfCompact1", true).apply()
            return
        }
        runCatching {
            val o = JSONObject(raw)
            if (o.optString("layout", "list") == "list") {
                o.put("layout", "compact")
                p.edit().putString("shelfUi", o.toString()).apply()
            }
        }
        p.edit().putBoolean("shelfCompact1", true).apply()
    }

    fun openStoreOnFirstLaunch(): Boolean {
        if (p.getBoolean("firstStore102", false)) return false
        p.edit().putBoolean("firstStore102", true).apply()
        return true
    }

    var colorMode: ColorSchemeMode
        get() = runCatching {
            when (val raw = p.getString("colorMode", ColorSchemeMode.System.name)!!) {
                // Monet colour picking was removed in 0.1.0; keep the light/dark choice.
                "MonetSystem" -> ColorSchemeMode.System
                "MonetLight" -> ColorSchemeMode.Light
                "MonetDark" -> ColorSchemeMode.Dark
                else -> ColorSchemeMode.valueOf(raw)
            }
        }.getOrDefault(ColorSchemeMode.System)
        set(v) {
            p.edit().putString("colorMode", v.name).apply()
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

    var incognito: Boolean
        get() = p.getBoolean("incognito", false)
        set(v) {
            p.edit().putBoolean("incognito", v).apply()
        }

    var peekGuard: Boolean
        get() = p.getBoolean("peekGuard", false)
        set(v) {
            p.edit().putBoolean("peekGuard", v).apply()
        }

    var fontGear: String
        get() = p.getString("fontGear", "system") ?: "system"
        set(v) {
            p.edit().putString("fontGear", v).apply()
        }

    var mineBanner: String
        get() = p.getString("mineBanner", "") ?: ""
        set(v) {
            p.edit().putString("mineBanner", v).apply()
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
                    layout = o.optString("layout", "compact"),
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

    /** Built-in comic sources; only their last error and item count are persisted. */
    fun webSites(): List<WebSite> {
        if (lastSiteId != Sites.COPY && lastSiteId != Sites.JM && lastSiteId != Sites.IKAN &&
            lastSiteId != Sites.NOVEL
        ) {
            lastSiteId = Sites.COPY
        }
        val saved = runCatching {
            val arr = JSONArray(p.getString("webSites", "[]") ?: "[]")
            (0 until arr.length()).map { arr.getJSONObject(it) }
        }.getOrDefault(emptyList())
        return Sites.defaults.map { base ->
            val s = saved.firstOrNull { it.optString("id") == base.id }
            base.copy(
                enabled = true,
                lastError = s?.optString("lastError").orEmpty(),
                itemCount = s?.optInt("itemCount") ?: 0,
            )
        }
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
                rotation = rotationOf(o.optString("rotation", "portrait")),
                bgColor = o.optString("bgColor", "black"),
                fullscreen = o.optBoolean("fullscreen", true),
                keepScreenOn = o.optBoolean("keepScreenOn", true),
                showPageNum = o.optBoolean("showPageNum", true),
                skipRead = o.optBoolean("skipRead", false),
                chapterTransition = o.optBoolean("chapterTransition", true),
                navLayout = o.optString("navLayout", "default"),
                reverseTap = o.optString("reverseTap", "none"),
                zoomMode = o.optString("zoomMode", "fit"),
                zoomPos = o.optString("zoomPos", "auto"),
                webtoonNav = o.optString("webtoonNav", "default"),
                webtoonReverseTap = o.optString("webtoonReverseTap", "none"),
                sidePad = o.optString("sidePad", "none"),
                hideSens = o.optString("hideSens", "low"),
                tapTurn = o.optBoolean("tapTurn", true),
                volumeTurn = o.optBoolean("volumeTurn", false),
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
            .put("chapterTransition", r.chapterTransition)
            .put("navLayout", r.navLayout)
            .put("reverseTap", r.reverseTap)
            .put("zoomMode", r.zoomMode)
            .put("zoomPos", r.zoomPos)
            .put("webtoonNav", r.webtoonNav)
            .put("webtoonReverseTap", r.webtoonReverseTap)
            .put("sidePad", r.sidePad)
            .put("hideSens", r.hideSens)
            .put("tapTurn", r.tapTurn)
            .put("volumeTurn", r.volumeTurn)
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
        p.edit().putString("reading", o.toString()).apply()
    }

    fun exportJson(library: List<String>, favorites: List<String>): String {
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
        return JSONObject()
            .put("v", 4)
            .put("colorMode", colorMode.name)
            .put("lang", lang)
            .put("backupFreq", backupFreq)
            .put("library", JSONArray(library))
            .put("favorites", JSONArray(favorites))
            .put("history", hist)
            .put("progress", prog)
            .put("reading", p.getString("reading", "{}") ?: "{}")
            .toString(2)
    }

    fun importJson(json: String): BackupData? {
        return runCatching {
            val o = JSONObject(json)
            o.optString("colorMode").takeIf { it.isNotBlank() }?.let { raw ->
                runCatching { colorMode = ColorSchemeMode.valueOf(raw.removePrefix("Monet")) }
            }
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
            if (o.has("reading")) {
                val r = o.opt("reading")
                when (r) {
                    is JSONObject -> p.edit().putString("reading", r.toString()).apply()
                    is String -> if (r.isNotBlank()) p.edit().putString("reading", r).apply()
                }
            }
            BackupData(lib, fav, hist)
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

    private fun csv(key: String, fallback: String) =
        (p.getString(key, fallback) ?: fallback).split(",").map { it.trim() }.filter { it.isNotEmpty() }

}

/** 旋转只保留 跟随系统/竖屏/横屏；旧的「锁定竖屏/锁定横屏」与竖屏/横屏效果相同，迁移过去。 */
internal fun rotationOf(raw: String) = when (raw) {
    "lock-portrait", "" -> "portrait"
    "lock-landscape" -> "landscape"
    else -> raw
}
