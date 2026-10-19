package com.shumanyu.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

data class CachedChapter(
    val id: String,
    val title: String,
    val href: String,
    val index: Int,
    val pages: List<String> = emptyList(),
)

data class CachedWork(
    val work: CrawledWork,
    val chapters: List<CachedChapter>,
    val fetchedAt: Long,
)

data class StoredTitle(val id: String, val title: String, val bytes: Long)

object LocalStore {
    private lateinit var root: File

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("shumanyu", Context.MODE_PRIVATE)
        val custom = prefs.getString("cachePath", "").orEmpty()
        val preferred = if (custom.isNotBlank()) File(custom) else defaultCache(context)
        val legacy = File(context.filesDir, "manga-cache")
        root = when {
            preferred.exists() || preferred.mkdirs() -> preferred
            else -> legacy.also { it.mkdirs() }
        }
        if (legacy.absolutePath != root.absolutePath && legacy.exists() && !legacy.list().isNullOrEmpty() && root.list().isNullOrEmpty()) {
            runCatching {
                legacy.copyRecursively(root, overwrite = true)
                legacy.deleteRecursively()
            }
        }
        File(root, "covers").mkdirs()
        File(root, "works").mkdirs()
        File(root, "listings").mkdirs()
        downloadRoot(context)
    }

    fun defaultCache(context: Context): File =
        File(context.getExternalFilesDir(null) ?: context.filesDir, "ShuManYu/cache")

    fun defaultDownload(context: Context): File =
        File(context.getExternalFilesDir(null) ?: context.filesDir, "ShuManYu/download")

    fun cachePath(): String = if (::root.isInitialized) root.absolutePath else ""

    fun downloadRoot(context: Context): File {
        val prefs = context.getSharedPreferences("shumanyu", Context.MODE_PRIVATE)
        val custom = prefs.getString("downloadPath", "").orEmpty()
        val dir = if (custom.isNotBlank()) File(custom) else defaultDownload(context)
        if (!dir.exists()) dir.mkdirs()
        return if (dir.isDirectory) dir else defaultDownload(context).also { it.mkdirs() }
    }

    fun probe(dir: File): Boolean {
        if (!dir.exists() && !dir.mkdirs()) return false
        val probe = File(dir, ".probe")
        return runCatching {
            probe.writeText("ok")
            probe.delete()
            true
        }.getOrDefault(false)
    }

    fun useCache(context: Context, dest: File): Boolean {
        if (!probe(dest)) return false
        if (::root.isInitialized && root.absolutePath != dest.absolutePath && root.exists()) {
            runCatching { root.copyRecursively(dest, overwrite = true) }
            runCatching { root.deleteRecursively() }
        }
        root = dest
        File(root, "covers").mkdirs()
        File(root, "works").mkdirs()
        File(root, "listings").mkdirs()
        context.getSharedPreferences("shumanyu", Context.MODE_PRIVATE).edit().putString("cachePath", dest.absolutePath).apply()
        return true
    }

    fun useDownload(context: Context, dest: File): Boolean {
        if (!probe(dest)) return false
        val from = downloadRoot(context)
        if (from.absolutePath != dest.absolutePath && from.exists()) {
            runCatching { from.copyRecursively(dest, overwrite = true) }
            if (from.absolutePath != dest.absolutePath) runCatching { from.deleteRecursively() }
        }
        context.getSharedPreferences("shumanyu", Context.MODE_PRIVATE).edit().putString("downloadPath", dest.absolutePath).apply()
        return true
    }

    fun cacheBytes(): Long = bytesOf(if (::root.isInitialized) root else return 0)

    fun downloadBytes(context: Context): Long = bytesOf(downloadRoot(context))

    fun listCached(): List<StoredTitle> {
        if (!::root.isInitialized) return emptyList()
        val dir = File(root, "works")
        if (!dir.exists()) return emptyList()
        return dir.listFiles().orEmpty().mapNotNull { f ->
            if (!f.isFile || !f.name.endsWith(".json")) return@mapNotNull null
            runCatching {
                val work = workFrom(JSONObject(f.readText()).getJSONObject("work"))
                StoredTitle(work.id, work.title.ifBlank { work.id }, f.length())
            }.getOrNull()
        }.sortedBy { it.title }
    }

    fun deleteCached(id: String) {
        if (!::root.isInitialized) return
        val cached = loadWork(id)
        workCache.remove(id)
        File(File(root, "works"), safe(id) + ".json").delete()
        val cover = cached?.work?.cover
        if (!cover.isNullOrBlank()) File(File(root, "covers"), coverName(cover)).delete()
    }

    fun listDownloads(context: Context): List<StoredTitle> {
        val dir = downloadRoot(context)
        return dir.listFiles().orEmpty().mapNotNull { child ->
            if (!child.isDirectory || child.name.startsWith(".")) return@mapNotNull null
            val meta = File(child, "meta.json")
            val title = if (meta.exists()) {
                runCatching { JSONObject(meta.readText()).optString("title") }.getOrDefault("")
            } else ""
            StoredTitle(child.name, title.ifBlank { child.name }, bytesOf(child))
        }.sortedBy { it.title }
    }

    fun deleteDownload(context: Context, id: String) {
        File(downloadRoot(context), id).deleteRecursively()
    }

    fun clearCached() {
        if (!::root.isInitialized) return
        workCache.clear()
        File(root, "works").listFiles()?.forEach { it.delete() }
        File(root, "covers").listFiles()?.forEach { it.delete() }
    }

    fun clearDownloads(context: Context) {
        downloadRoot(context).listFiles()?.forEach { child ->
            if (child.name != ".probe") child.deleteRecursively()
        }
    }

    fun fileSafe(id: String) = safe(id)

    private fun bytesOf(dir: File): Long {
        if (!dir.exists()) return 0
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    fun dropListing(siteId: String) {
        if (!::root.isInitialized) return
        File(File(root, "listings"), safe(siteId) + ".json").delete()
    }

    fun saveListing(siteId: String, items: List<CrawledWork>) {
        if (!::root.isInitialized) return
        val arr = JSONArray()
        items.forEach { arr.put(workJson(it)) }
        File(File(root, "listings"), safe(siteId) + ".json").writeText(arr.toString())
    }

    fun loadListing(siteId: String): List<CrawledWork> {
        if (!::root.isInitialized) return emptyList()
        val f = File(File(root, "listings"), safe(siteId) + ".json")
        if (!f.exists()) return emptyList()
        return runCatching {
            val arr = JSONArray(f.readText())
            (0 until arr.length()).map { workFrom(arr.getJSONObject(it)) }
        }.getOrDefault(emptyList())
    }

    fun loadAllListings(): List<CrawledWork> {
        if (!::root.isInitialized) return emptyList()
        val dir = File(root, "listings")
        if (!dir.exists()) return emptyList()
        return dir.listFiles()?.flatMap { f ->
            runCatching {
                val arr = JSONArray(f.readText())
                (0 until arr.length()).map { workFrom(arr.getJSONObject(it)) }
            }.getOrDefault(emptyList())
        } ?: emptyList()
    }

    private val workCache = java.util.concurrent.ConcurrentHashMap<String, CachedWork>()

    fun saveWork(cached: CachedWork) {
        if (!::root.isInitialized) return
        val o = JSONObject()
            .put("work", workJson(cached.work))
            .put("fetchedAt", cached.fetchedAt)
        val ch = JSONArray()
        cached.chapters.forEach { c ->
            ch.put(
                JSONObject()
                    .put("id", c.id)
                    .put("title", c.title)
                    .put("href", c.href)
                    .put("index", c.index)
                    .put("pages", JSONArray(c.pages)),
            )
        }
        o.put("chapters", ch)
        File(File(root, "works"), safe(cached.work.id) + ".json").writeText(o.toString())
        workCache[cached.work.id] = cached
        touchWork(cached.work.id)
    }

    fun loadWork(id: String): CachedWork? {
        if (!::root.isInitialized) return null
        workCache[id]?.let {
            touchWork(id)
            return it
        }
        val f = File(File(root, "works"), safe(id) + ".json")
        if (!f.exists()) return null
        val loaded = runCatching {
            val o = JSONObject(f.readText())
            val work = workFrom(o.getJSONObject("work"))
            val arr = o.optJSONArray("chapters") ?: JSONArray()
            val chapters = (0 until arr.length()).map { i ->
                val c = arr.getJSONObject(i)
                val pagesArr = c.optJSONArray("pages") ?: JSONArray()
                CachedChapter(
                    c.getString("id"),
                    c.getString("title"),
                    c.optString("href"),
                    c.optInt("index"),
                    (0 until pagesArr.length()).map { pagesArr.getString(it) },
                )
            }
            CachedWork(work, chapters, o.optLong("fetchedAt"))
        }.getOrNull()
        if (loaded != null) {
            workCache[id] = loaded
            touchWork(id)
        }
        return loaded
    }

    private val workOrder = java.util.Collections.synchronizedList(ArrayList<String>())

    private fun touchWork(id: String) {
        workOrder.remove(id)
        workOrder.add(id)
        while (workOrder.size > 12) {
            val old = workOrder.removeAt(0)
            if (old != id) workCache.remove(old)
        }
    }

    fun dropLegacyNovels(): Set<String> {
        if (!::root.isInitialized) return emptySet()
        val dropped = HashSet<String>()
        File(root, "listings").listFiles()?.forEach { f ->
            val id = f.nameWithoutExtension
            if (id == "biquge" || id.startsWith("biquge")) {
                f.delete()
                dropped.add(id)
            }
        }
        File(root, "works").listFiles()?.forEach { f ->
            val id = f.nameWithoutExtension
            val text = if (id.startsWith("bq")) "" else runCatching { f.readText() }.getOrDefault("")
            val novel = id.startsWith("bq") ||
                text.contains("biquge365") ||
                text.contains("bqg5.com") ||
                text.contains("\"siteId\":\"biquge\"")
            if (!novel) return@forEach
            f.delete()
            workCache.remove(id)
            dropped.add(id)
        }
        return dropped
    }

    fun saveCover(url: String, bmp: Bitmap) {
        if (!::root.isInitialized || url.isBlank()) return
        runCatching {
            File(File(root, "covers"), coverName(url)).outputStream().use { out ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 82, out)
            }
        }
    }

    fun loadCover(url: String): Bitmap? {
        if (!::root.isInitialized || url.isBlank()) return null
        val f = File(File(root, "covers"), coverName(url))
        if (!f.exists()) return null
        return runCatching { BitmapFactory.decodeFile(f.absolutePath) }.getOrNull()
    }

    private fun workJson(w: CrawledWork) = JSONObject()
        .put("id", w.id)
        .put("siteId", w.siteId)
        .put("title", w.title)
        .put("cover", w.cover)
        .put("href", w.href)
        .put("author", w.author)
        .put("genres", w.genres)
        .put("cat", w.cat)
        .put("tags", JSONArray(w.tags))
        .put("description", w.description)
        .put("described", w.described)
        .put("listed", JSONArray(w.listed))

    private fun workFrom(o: JSONObject) = CrawledWork(
        o.getString("id"),
        o.optString("siteId"),
        o.optString("title"),
        o.optString("cover"),
        o.optString("href"),
        o.optString("author"),
        o.optString("genres", "其他"),
        o.optString("cat"),
        o.optJSONArray("tags")?.let { arr -> (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() } }
            ?: o.optString("genres").split(Regex("[ ·,，]+")).map { it.trim() }.filter { it.isNotBlank() && it != "其他" },
        o.optString("description"),
        o.optBoolean("described"),
        o.optJSONArray("listed")?.let { arr ->
            (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() }
        } ?: emptyList(),
    )

    private fun safe(id: String) = id.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(80)

    private fun coverName(url: String): String {
        val md = MessageDigest.getInstance("SHA-256").digest(url.toByteArray())
        return md.joinToString("") { "%02x".format(it) }.take(40) + ".jpg"
    }
}

object Downloader {
    @Volatile var running: Boolean = false
        private set

    fun start(
        context: Context,
        id: String,
        title: String,
        chapters: List<CachedChapter>,
        onStep: (Int, Int) -> Unit,
        onDone: (String?) -> Unit,
    ) {
        if (running) {
            onDone("正在下载")
            return
        }
        if (chapters.isEmpty()) {
            onDone("还没有目录")
            return
        }
        running = true
        Thread {
            try {
                val book = File(LocalStore.downloadRoot(context), LocalStore.fileSafe(id))
                book.mkdirs()
                File(book, "meta.json").writeText(JSONObject().put("id", id).put("title", title).toString())
                val total = chapters.size
                chapters.forEachIndexed { idx, c ->
                    val pages = if (c.pages.isNotEmpty()) c.pages else Crawler.fetchPages(c.href).first
                    val folder = File(book, "%03d".format(if (c.index > 0) c.index else idx + 1))
                    folder.mkdirs()
                    if (c.title.isNotBlank()) File(folder, "title.txt").writeText(c.title)
                    pages.forEachIndexed { i, url ->
                        val out = File(folder, "%03d.jpg".format(i + 1))
                        if (out.length() > 800) return@forEachIndexed
                        val bmp = Crawler.downloadBitmap(url) ?: return@forEachIndexed
                        out.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
                    }
                    Handler(Looper.getMainLooper()).post { onStep(idx + 1, total) }
                }
                Handler(Looper.getMainLooper()).post { onDone(null) }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post { onDone(e.message ?: "下载失败") }
            } finally {
                running = false
            }
        }.start()
    }
}
