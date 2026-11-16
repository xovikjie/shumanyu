package com.shumanyu.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlinx.coroutines.sync.withLock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

object LocalStore {
    private lateinit var root: File
    private lateinit var app: Context

    fun init(context: Context) {
        app = context.applicationContext
        downloadedIds = null
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
        downloadedIds = null
        return true
    }

    fun downloadBytes(context: Context): Long = bytesOf(downloadRoot(context))

    /**
     * Everything that is safe to throw away: image cache (covers and reader pages), store
     * listings, loose files in the cache root, data of titles that are not on the shelf, in
     * history or in favorites, the system cache dirs and half-written download pages.
     */
    private fun clearableFiles(context: Context, keep: Set<String>): List<File> {
        val out = ArrayList<File>()
        val keepFiles = keep.map(::safe).toSet()
        if (::root.isInitialized) {
            root.listFiles()?.forEach { f ->
                when {
                    f.isFile -> out.add(f)
                    f.name == "covers" || f.name == "listings" -> out.addAll(f.walkTopDown().filter { it.isFile })
                    f.name == "works" -> f.listFiles()?.forEach { w ->
                        if (w.isFile && w.nameWithoutExtension !in keepFiles) out.add(w)
                    }
                    else -> out.addAll(f.walkTopDown().filter { it.isFile })
                }
            }
        }
        // codeCacheDir 归系统管（HWUI 着色器/管线缓存、ART 等），运行中删掉不安全，也基本不占空间，不清。
        listOfNotNull(context.cacheDir, context.externalCacheDir).forEach { dir ->
            out.addAll(dir.walkTopDown().filter { it.isFile })
        }
        downloadRoot(context).walkTopDown().filter { it.isFile }.forEach { f ->
            if (f.name == ".probe" || (f.extension == "jpg" && f.length() < 800) || f.extension == "part" || f.extension == "tmp") out.add(f)
        }
        return out
    }

    fun clearableBytes(context: Context, keep: Set<String>): Long = clearableFiles(context, keep).sumOf { it.length() }

    private val clearLock = kotlinx.coroutines.sync.Mutex()

    /** [clearCache] on the IO dispatcher; concurrent calls run one after another. */
    suspend fun clearCacheAsync(context: Context, keep: Set<String>): Long =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            clearLock.withLock { runCatching { clearCache(context, keep) }.getOrElse { clearableBytes(context, keep) } }
        }

    /** Empties the cache for real; returns the bytes still left (≈0). Blocking: call off the main thread. */
    fun clearCache(context: Context, keep: Set<String>): Long {
        clearableFiles(context, keep).forEach { runCatching { it.delete() } }
        val kept = keep.toSet()
        workCache.keys.toList().forEach { if (it !in kept) workCache.remove(it) }
        workOrder.removeAll { it !in kept }
        Crawler.clearMemory()
        if (::root.isInitialized) {
            File(root, "covers").mkdirs()
            File(root, "works").mkdirs()
            File(root, "listings").mkdirs()
        }
        return clearableBytes(context, keep)
    }

    /**
     * 删除全部已下载的漫画/小说（下载目录里的每本书连同 meta.json 记录），并清空「已下载」索引；
     * 书架、历史、收藏和作品数据不动。IO 线程执行，与清理缓存共用一把锁，不会两次清理重叠。
     * 返回下载目录剩余字节数。
     */
    suspend fun clearDownloadsAsync(context: Context): Long =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            clearLock.withLock {
                runCatching {
                    downloadRoot(context).listFiles()?.forEach { child ->
                        if (child.name != ".probe") runCatching { child.deleteRecursively() }
                    }
                }
                downloadedIds = null
                downloadRev++
                runCatching { downloadBytes(context) }.getOrDefault(0L)
            }
        }

    /** 已下载作品的目录名（[fileSafe] 后的 id）；首次用到时扫一次下载目录，之后增量维护。 */
    @Volatile private var downloadedIds: Set<String>? = null

    /** 下载记录的版本号：下载完成或清理后 +1，书架据此重算「已下载」标记。 */
    var downloadRev by androidx.compose.runtime.mutableIntStateOf(0)
        private set

    fun isDownloaded(id: String): Boolean {
        if (!::app.isInitialized) return false
        val ids = downloadedIds ?: runCatching {
            downloadRoot(app).listFiles().orEmpty()
                .filter { it.isDirectory && !it.name.startsWith(".") && File(it, "meta.json").exists() }
                .map { it.name }.toSet()
        }.getOrDefault(emptySet()).also { downloadedIds = it }
        return safe(id) in ids
    }

    fun markDownloaded(id: String) {
        downloadedIds = downloadedIds?.plus(safe(id))
        downloadRev++
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
                LocalStore.markDownloaded(id)
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
