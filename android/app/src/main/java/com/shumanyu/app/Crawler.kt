package com.shumanyu.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Process
import android.util.Base64
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.nio.charset.Charset
import org.json.JSONArray
import org.json.JSONObject

data class WebSite(
    val id: String,
    val name: String,
    val url: String,
    var enabled: Boolean = true,
    var lastError: String = "",
    var itemCount: Int = 0,
    val kind: String = "html",
)

data class CrawledWork(
    val id: String,
    val siteId: String,
    val title: String,
    val cover: String,
    val href: String,
    val author: String = "",
    val genres: String = "其他",
    val cat: String = "",
    val tags: List<String> = emptyList(),
    val description: String = "",
    val described: Boolean = false,
    val listed: List<String> = emptyList(),
)

data class Progress(
    val chapterId: String,
    val page: Int,
    val percent: Int,
    val at: Long,
)

object AppLog {
    private val lines = ArrayDeque<String>(400)
    private var file: java.io.File? = null
    @Volatile private var ready = false

    fun init(context: Context) {
        if (ready) return
        ready = true
        val f = java.io.File(context.applicationContext.filesDir, "app.log")
        file = f
        val old = runCatching { if (f.exists()) f.readLines().takeLast(320) else emptyList() }.getOrDefault(emptyList())
        synchronized(lines) {
            lines.clear()
            old.forEach { lines.addLast(it) }
        }
        val prev = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            add("crash", "${t.name} ${e.javaClass.simpleName}: ${e.message}")
            add("crash", e.stackTraceToString().lineSequence().take(12).joinToString(" | "))
            prev?.uncaughtException(t, e)
        }
        add("app", "start ${Build.MANUFACTURER} ${Build.MODEL} sdk=${Build.VERSION.SDK_INT}")
    }

    @Synchronized
    fun add(tag: String, msg: String) {
        val ts = java.text.SimpleDateFormat("MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        val line = "$ts [$tag] ${msg.replace("\n", " ").take(300)}"
        lines.addLast(line)
        while (lines.size > 400) lines.removeFirst()
        val f = file ?: return
        runCatching {
            f.appendText(line + "\n")
            if (f.length() > 160_000) {
                f.writeText(lines.joinToString("\n") + "\n")
            }
        }
    }

    @Synchronized
    fun dump(context: Context, sites: List<WebSite> = emptyList()): String {
        init(context)
        val b = StringBuilder()
        b.appendLine("书漫屿 ${appVersion(context)}")
        b.appendLine(java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date()))
        b.appendLine("device ${Build.MANUFACTURER} ${Build.MODEL} sdk=${Build.VERSION.SDK_INT}")
        b.appendLine("pkg ${context.packageName}")
        if (sites.isNotEmpty()) {
            b.appendLine("sites:")
            for (s in sites) {
                b.appendLine("  ${s.name} kind=${s.kind} enabled=${s.enabled} count=${s.itemCount} err=${s.lastError} url=${s.url}")
            }
        }
        b.appendLine("--- app log ---")
        val stored = runCatching { file?.takeIf { it.exists() }?.readText()?.takeLast(80_000) }.getOrNull().orEmpty()
        if (stored.isBlank() && lines.isEmpty()) b.appendLine("(no app events yet)")
        else if (stored.isNotBlank()) b.appendLine(stored.trim())
        else for (l in lines) b.appendLine(l)
        b.appendLine("--- logcat ---")
        val cat = runCatching {
            val pid = android.os.Process.myPid()
            val proc = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-t", "250", "--pid=$pid", "*:V"))
            val text = proc.inputStream.bufferedReader().use { it.readText() }
            proc.waitFor()
            text
        }.getOrElse { "logcat failed: ${it.message}" }
        b.appendLine(if (cat.isBlank()) "(logcat empty)" else cat.take(60_000))
        val out = b.toString()
        if (out.isBlank()) return "书漫屿 ${appVersion(context)}\n(empty dump)\n"
        return out
    }

    private fun appVersion(context: Context): String = runCatching {
        val info = if (Build.VERSION.SDK_INT >= 33) {
            context.packageManager.getPackageInfo(
                context.packageName,
                android.content.pm.PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        info.versionName?.ifBlank { null }
    }.getOrNull() ?: "?"
}

data class FetchedWork(
    val author: String,
    val tags: List<String>,
    val cover: String,
    val description: String,
    val chapters: List<CachedChapter>,
    val title: String = "",
)

object Sites {
    const val JM = "jm"
    const val IKAN = "ikan"
    const val JJ = "jj"
    const val ALBUM = "album"
    const val BIQUGE = "biquge"
    const val BIQUGE2 = "biquge2"
    const val BIQUGE3 = "biquge3"
    const val COPY = "copy"

    val defaults = listOf(
        WebSite(BIQUGE, "笔趣阁", "https://www.biquges.cc/", true, kind = BIQUGE),
        WebSite(COPY, "拷贝漫画", "https://www.copy5000.com/", true, kind = COPY),
        WebSite(JM, "禁漫天堂", "https://18comic.vip/", true, kind = JM),
        WebSite(IKAN, "爱看韩漫", "https://www.ikanhm.top/", true, kind = IKAN),
    )

    val jmCats = listOf(
        "推荐", "全部", "最近", "排行榜", "月排行", "周排行", "天排行",
        "韓漫", "A漫", "本本", "美漫", "中文", "同人", "單本", "短篇",
        "Cosplay", "3D", "其他",
    )
    val ikanCats = listOf("推荐", "全部", "连载", "完结")
    val albumCats = listOf("最新", "亚洲", "Cosplay")
    val biqugeCats = listOf("推荐", "玄幻", "修真", "都市", "穿越", "网游", "科幻", "其他", "排行")
    val biqugesCats = listOf("推荐", "玄幻", "武侠", "都市", "历史", "网游", "科幻", "言情", "其他", "排行")
    val copyCats = listOf("推荐", "最新", "日榜", "周榜", "月榜", "完结")

    fun kindOf(site: WebSite): String {
        if (site.kind.isNotBlank() && site.kind != "html") return site.kind
        val u = site.url.lowercase()
        return when {
            "18comic" in u || "jmcomic" in u || "jmapinode" in u -> JM
            "jjmh.top" in u -> JJ
            "ikanhm" in u || "ikmmh" in u || "jjmhw" in u -> IKAN
            "nsfwx" in u -> ALBUM
            "biquge365" in u || "bqg5.com" in u || "biquges.cc" in u || "biquge5200" in u || "8tsw.com" in u ||
                site.id == BIQUGE || site.id == BIQUGE2 || site.id == BIQUGE3 ||
                site.name == "笔趣阁" || site.name == "笔趣阁2" || site.name == "笔趣阁3" -> BIQUGE
            "copy5000" in u || "copy2026" in u || "copy2000" in u || "mangacopy" in u ||
                site.id == COPY || site.name.contains("拷贝漫画") -> COPY
            else -> "html"
        }
    }

    fun catsFor(site: WebSite?): List<String> {
        if (site != null && kindOf(site) == BIQUGE && site.url.contains("biquges.cc", true)) return biqugesCats
        return when (site?.let { kindOf(it) }) {
            XiuSources.KIND -> XiuSources.categories(requireNotNull(site).id)
            JM -> jmCats
            IKAN, JJ -> ikanCats
            ALBUM -> albumCats
            BIQUGE -> biqugeCats
            COPY -> copyCats
            else -> listOf("推荐")
        }
    }

    fun isNovel(site: WebSite): Boolean {
        if (kindOf(site) == BIQUGE || kindOf(site) == XiuSources.KIND) return true
        val blob = (site.name + " " + site.url).lowercase()
        return "biquge" in blob || site.name.contains("笔趣阁") || site.name.contains("小说") || site.name.contains("小說")
    }

    fun isAdult(site: WebSite): Boolean {
        val k = kindOf(site)
        if (k == JM || k == IKAN || k == ALBUM) return true
        return Regex("""\(18\+\)|18\+|18＋""", RegexOption.IGNORE_CASE).containsMatchIn(site.name)
    }

    fun displayName(site: WebSite): String {
        val n = site.name.trim().let { if(it.endsWith(".com",true))it.dropLast(4) else it }
        if (Regex("""\(18\+\)|18\+|18＋""", RegexOption.IGNORE_CASE).containsMatchIn(n)) return n
        return if (isAdult(site)) "$n(18+)" else n
    }

    fun adultWork(work: CrawledWork?, sites: List<WebSite>): Boolean {
        if (work == null) return false
        val site = sites.find { it.id == work.siteId }
        if (site != null) return isAdult(site)
        if (work.siteId == JM || work.siteId == IKAN || work.siteId == ALBUM) return true
        val href = work.href.lowercase()
        if (listOf("18comic", "jmcomic", "jmapi", "ikanhm", "ikmmh", "jjmhw", "nsfwx").any { it in href }) return true
        val id = work.id
        return id.startsWith("jm") || id.startsWith("ik") || id.startsWith("jj") || id.startsWith("ab")
    }
}

object Crawler {
    const val UA =
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/153.0.0.0 Mobile Safari/537.36"
    private const val JM_UA =
        "Mozilla/5.0 (Linux; Android 9; V1938CT Build/PQ3A.190705.11211812; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Safari/537.36"
    private const val JM_SECRET = "185Hcomic3PAPP7R"
    private const val JM_DOMAIN_SECRET = "diosfjckwpqpdfjkvnqQjsik"
    private const val JM_VER = "2.1.7"
    private val JM_IMAGE_HOSTS = listOf(
        "cdn-msp.jmapiproxy1.cc",
        "cdn-msp.jmapiproxy2.cc",
        "cdn-msp2.jmapiproxy2.cc",
        "cdn-msp3.jmapiproxy2.cc",
        "cdn-msp.jmapinodeudzn.net",
        "cdn-msp3.jmapinodeudzn.net",
    )
    private val JM_CDN = "https://cdn-msp.jmapiproxy1.cc"
    private const val LISTING_CAP = 1200
    private const val JM_PAGES = 6
    private val JM_API_HOSTS = listOf(
        "https://www.cdnhjk.net",
        "https://www.cdngwc.cc",
        "https://www.cdngwc.net",
        "https://www.cdngwc.club",
    )
    private val JM_DOMAIN_SERVERS = listOf(
        "https://rup4a04-c01.tos-ap-southeast-1.bytepluses.com/newsvr-2025.txt",
        "https://rup4a04-c02.tos-cn-hongkong.bytepluses.com/newsvr-2025.txt",
        "https://rup4a04-c03.tos-cn-beijing.bytepluses.com.cn/newsvr-2025.txt",
        "https://www.cdnmhwscc.vip/newsvr-2025.txt",
        "https://www.cdnmhws.cc/newsvr-2025.txt",
    )

    @Volatile private var jmHosts: List<String> = JM_API_HOSTS
    @Volatile private var jmHostsAt: Long = 0L

    private val lenientFactory: SSLSocketFactory by lazy {
        val tm = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers() = emptyArray<X509Certificate>()
            },
        )
        SSLContext.getInstance("TLS").apply { init(null, tm, SecureRandom()) }.socketFactory
    }
    private val lenientHost = HostnameVerifier { _, _ -> true }

    fun isSafe(raw: String): Boolean = safeSourceUrl(raw)

    fun friendlyError(e: Throwable): String {
        val s = buildString {
            var t: Throwable? = e
            while (t != null) {
                append(t.javaClass.simpleName).append(' ').append(t.message ?: "").append(' ')
                t = t.cause
            }
        }
        return when {
            s.contains("CertPath", true) || s.contains("TrustAnchor", true) ||
                s.contains("SSLHandshake", true) || s.contains("Certificate", true) ||
                s.contains("certificatio", true) -> "网站证书异常，已尝试兼容连接"
            s.contains("UnknownHost") || s.contains("unable to resolve", true) -> "无法解析域名，请检查网络"
            s.contains("timeout", true) || s.contains("timed out", true) -> "连接超时"
            s.contains("ConnectException") || s.contains("Failed to connect") -> "无法连接该网站"
            s.contains("403") -> "源站拒绝访问"
            else -> "网络错误"
        }
    }

    fun fetch(
        url: String,
        siteId: String,
        category: String = "推荐",
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): Pair<List<CrawledWork>, String?> {
        if (siteId.startsWith("native22-")) return XiuSources.listing(siteId,category,onPartial)
        if (url.isBlank()) return emptyList<CrawledWork>() to "请先填写网站地址"
        if (!isSafe(url)) return emptyList<CrawledWork>() to "网址不安全或不支持"
        val kind = when {
            siteId == Sites.JM || url.contains("18comic") || url.contains("jmcomic") -> Sites.JM
            siteId == Sites.JJ || url.contains("jjmh.top") -> Sites.JJ
            siteId == Sites.IKAN || url.contains("ikanhm") || url.contains("ikmmh") -> Sites.IKAN
            siteId == Sites.ALBUM || url.contains("nsfwx") -> Sites.ALBUM
            siteId == Sites.COPY || url.contains("copy5000") || url.contains("mangacopy") || url.contains("copy2026") -> Sites.COPY
            siteId == Sites.BIQUGE || siteId == Sites.BIQUGE2 || siteId == Sites.BIQUGE3 ||
                url.contains("biquge365") || url.contains("bqg5.com") || url.contains("biquges.cc") ||
                url.contains("biquge5200") || url.contains("8tsw.com") -> Sites.BIQUGE
            else -> "html"
        }
        return try {
            val (items, err) = when (kind) {
                Sites.JM -> tag(fetchJm(siteId, category, onPartial), category)
                Sites.IKAN -> tag(fetchCms("https://www.ikanhm.top", "ik", siteId, category, onPartial), category)
                Sites.JJ -> tag(fetchCms("https://www.jjmh.top", "jj", siteId, category, onPartial), category)
                Sites.ALBUM -> tag(fetchAlbum(siteId, category, onPartial), category)
                Sites.COPY -> tag(fetchCopy(siteId, category, onPartial), category)
                Sites.BIQUGE -> tag(fetchBqg5(siteId, category, novelHome(siteId, url), onPartial), category)
                else -> {
                    val (html, err, finalUrl) = get(url)
                    if (err != null) emptyList<CrawledWork>() to err
                    else tag(parseGeneric(html, finalUrl, siteId) to null, category)
                }
            }
            if (items.isEmpty()) items to err else fillMissingCovers(items, onPartial) to err
        } catch (e: Exception) {
            val msg = friendlyError(e)
            AppLog.add("list", msg)
            emptyList<CrawledWork>() to msg
        }
    }

    fun search(query: String, sites: List<WebSite>, onPartial: ((List<CrawledWork>) -> Unit)? = null): List<CrawledWork> {
        val q = query.trim()
        if (q.length < 1) return emptyList()
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        for (site in sites.filter { it.enabled && it.url.isNotBlank() }) {
            val kind = Sites.kindOf(site)
            val chunk = try {
                when (kind) {
                    XiuSources.KIND -> XiuSources.search(site.id,q)
                    Sites.JM -> {
                        val items = ArrayList<CrawledWork>()
                        for (p in 1..2) {
                            val data = jmApi("/search?search_query=${enc(q)}&page=$p&main_tag=0&o=mr") ?: break
                            val part = parseJmApi(data, site.id)
                            if (part.isEmpty()) break
                            items.addAll(part)
                        }
                        items
                    }
                    Sites.IKAN -> {
                        val (html, err, finalUrl) = get("https://www.ikanhm.top/search?keyword=${enc(q)}")
                        if (err != null) emptyList() else parseIkan(html, finalUrl, site.id)
                    }
                    Sites.JJ -> {
                        val (html, err, finalUrl) = get("https://www.jjmh.top/search?keyword=${enc(q)}")
                        if (err != null) emptyList() else parseIkan(html, finalUrl, site.id, idPrefix = "jj")
                    }
                    Sites.ALBUM -> {
                        val (html, err, finalUrl) = get("https://nsfwx.pics/?s=${enc(q)}")
                        if (err != null) emptyList() else parseAlbum(html, finalUrl, site.id)
                    }
                    Sites.BIQUGE -> searchBqg5(q, site.id, novelHome(site.id, site.url))
                    Sites.COPY -> searchCopy(q, site.id)
                    else -> emptyList()
                }
            } catch (_: Exception) {
                emptyList()
            }
            for (item in tag(chunk to null, "搜索").first) {
                if (seen.add(item.id)) out.add(item)
            }
            if (out.size >= LISTING_CAP) break
        }
        return out
    }

    private fun tag(result: Pair<List<CrawledWork>, String?>, cat: String): Pair<List<CrawledWork>, String?> {
        val (items, err) = result
        return items.map { if (it.cat.isBlank()) it.copy(cat = cat) else it } to err
    }

    fun ping(url: String): String? {
        if (url.isBlank()) return "请先填写网站地址"
        if (!isSafe(url)) return "网址不安全或不支持"
        return try {
            val conn = open(url, lenient = true)
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.requestMethod = "GET"
            val code = conn.responseCode
            conn.disconnect()
            if (code in 200..499) null else "连接失败 $code"
        } catch (e: Exception) {
            friendlyError(e)
        }
    }

    fun fetchChapters(href: String): Pair<List<CachedChapter>, String?> {
        val (detail, err) = fetchWork(href)
        return (detail?.chapters ?: emptyList()) to err
    }

    fun fetchWork(href: String): Pair<FetchedWork?, String?> {
        if(XiuSources.sourceId(href)!=null)return XiuSources.detail(href)
        if (href.contains("#smyLegado=")) return null to "该合集已移除"
        if (href.contains("#smySource=")) return null to "该小说源已移除"
        if (href.isBlank() || !isSafe(href)) return null to "无法打开该作品"
        return try {
            val jmId = jmIdFrom(href)
            if (jmId != null && isJmHref(href)) {
                val data = jmApi("/album?id=$jmId")
                if (data != null) {
                    val ch = parseJmAlbum(data, jmId)
                    if (ch.isNotEmpty()) {
                        return FetchedWork(
                            author = jmAuthors(data),
                            tags = parseJmTags(data),
                            cover = "$JM_CDN/media/albums/${jmId}_3x4.jpg",
                            description = plainDesc(data.optString("description")),
                            chapters = ch,
                            title = tidyTitle(decode(data.optString("name"))),
                        ) to null
                    }
                }
            }
            if (isAlbumHref(href)) return fetchAlbumWork(href)
            if (isCopyHref(href)) return fetchCopyWork(href)
            if (href.contains("biquge365.net", true)) return null to "这本书源已经移除"
            novelHomeOf(href)?.let { return fetchBqg5Work(href, it) }
            val (html, err, finalUrl) = get(href)
            if (err != null) {
                AppLog.add("fetch", err)
                return null to err
            }
            val ch = parseChapters(html, finalUrl)
            val meta = parseIkanDetail(html, finalUrl)
            val pageTitle = ikanPageTitle(html)
            val desc = meta.description.ifBlank { htmlDescription(html) }
            if (ch.isEmpty()) {
                AppLog.add("fetch", "未解析到目录 $href")
                if (meta.author.isNotBlank() || meta.tags.isNotEmpty() || meta.cover.isNotBlank() || pageTitle.isNotBlank() || desc.isNotBlank()) {
                    return FetchedWork(meta.author, meta.tags, meta.cover, desc, emptyList(), pageTitle) to "未解析到目录"
                }
                return null to "未解析到目录"
            }
            FetchedWork(meta.author, meta.tags, meta.cover, desc, ch, pageTitle) to null
        } catch (e: Exception) {
            val msg = friendlyError(e)
            AppLog.add("fetch", msg)
            null to msg
        }
    }

    fun fetchPages(href: String): Pair<List<String>, String?> {
        if (href.isBlank() || !isSafe(href)) return emptyList<String>() to "无法打开该章节"
        return try {
            val jmId = jmIdFrom(href)
            if (jmId != null && isJmHref(href)) {
                val data = jmApi("/chapter?id=$jmId")
                if (data != null) {
                    val pages = parseJmPages(data, jmId)
                    if (pages.isNotEmpty()) return pages to null
                }
            }
            if (isAlbumHref(href)) {
                val (html, err, finalUrl) = get(href)
                if (err != null) return emptyList<String>() to err
                val pages = parseAlbumPages(html, finalUrl)
                return if (pages.isEmpty()) emptyList<String>() to "未解析到图片" else pages to null
            }
            if (isCopyChapter(href)) return fetchCopyPages(href)
            val (html, err, finalUrl) = get(href)
            if (err != null) emptyList<String>() to err
            else parsePages(html, finalUrl) to null
        } catch (e: Exception) {
            emptyList<String>() to friendlyError(e)
        }
    }

    private val pageRam = object : android.util.LruCache<String, Bitmap>(48 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = (value.byteCount / 1024).coerceAtLeast(1)
    }
    private val pageIo = java.util.concurrent.Executors.newFixedThreadPool(2)

    private fun pageKey(url: String): String {
        val clean = url.substringBefore('#').trim()
        val scramble = Regex("""[#&]s=(\d+)""").find(url)?.groupValues?.get(1) ?: "0"
        return "p8:$clean:$scramble"
    }

    fun peekPage(url: String): Bitmap? {
        if (url.isBlank() || !url.startsWith("http")) return null
        val hit = pageRam.get(pageKey(url)) ?: return null
        return if (hit.isRecycled) null else hit
    }

    fun prefetchPage(url: String) {
        if (url.isBlank() || peekPage(url) != null) return
        pageIo.execute { runCatching { downloadBitmap(url) } }
    }

    fun downloadBitmap(url: String, force: Boolean = false): Bitmap? {
        if (url.isBlank() || !url.startsWith("http")) return null
        val clean = url.substringBefore('#').trim()
        val key = pageKey(url)
        if (!force) {
            pageRam.get(key)?.takeIf { !it.isRecycled }?.let { return it }
            LocalStore.loadCover(key)?.let { disk ->
                val fit = scaleIfHuge(disk)
                if (fit !== disk && !disk.isRecycled) disk.recycle()
                val shown = lean565(fit)
                if (shown !== fit && !fit.isRecycled) fit.recycle()
                pageRam.put(key, shown)
                return shown
            }
        }
        val bytes = downloadBytes(clean) ?: return null
        if (bytes.size < 800) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth < 40 || bounds.outHeight < 40) return null
        val jm = "/media/photos/" in clean
        val host = runCatching { URL(clean).host.lowercase() }.getOrDefault("")
        val sliceHost = host.contains("ikanhm") || host.contains("ikmmh") || host.contains("jjmh") || host.contains("jjmhw")
        val opts = BitmapFactory.Options().apply {
            inSampleSize = if (jm) 1 else readerSample(bounds.outWidth, bounds.outHeight)
            inPreferredConfig = if (jm || sliceHost) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            inScaled = false
            inDither = false
        }
        val raw = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts) ?: return null
        val sample = opts.inSampleSize.coerceAtLeast(1)
        val expectW = (bounds.outWidth / sample).coerceAtLeast(1)
        val expectH = (bounds.outHeight / sample).coerceAtLeast(1)
        if (raw.width * 10 < expectW * 9 || raw.height * 10 < expectH * 9) {
            if (!raw.isRecycled) raw.recycle()
            return null
        }
        val fixed = when {
            jm -> descrambleJm(url, raw)
            else -> raw
        }
        val scaled = scaleIfHuge(fixed)
        if (scaled !== fixed && !fixed.isRecycled) fixed.recycle()
        if (fixed !== raw && scaled !== raw && !raw.isRecycled) raw.recycle()
        val shown = lean565(scaled)
        if (shown !== scaled && !scaled.isRecycled) scaled.recycle()
        pageRam.put(key, shown)
        pageIo.execute { runCatching { LocalStore.saveCover(key, shown) } }
        return shown
    }

    private val coverRam = object : android.util.LruCache<String, Bitmap>(32 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = (value.byteCount / 1024).coerceAtLeast(1)
    }
    private val coverPool = java.util.concurrent.Executors.newFixedThreadPool(8)
    private val coverDiskIo = Executors.newSingleThreadExecutor()
    private val coverRequests = SharedImageRequests(coverPool) { url -> peekCover(url) ?: fetchCover(url) }

    private fun coverUrl(url: String): String = url.trim().substringBefore('#').let {
        if (it.startsWith("//")) "https:$it" else it
    }

    fun peekCover(url: String): Bitmap? {
        if (url.isBlank()) return null
        val hit = coverRam.get(coverUrl(url)) ?: return null
        return if (hit.isRecycled) null else hit
    }

    fun prefetchCovers(urls: List<String>) {
        for (url in urls.map(::coverUrl).distinct().take(12)) {
            // Bound speculative work so scrolling cannot queue an entire catalogue.
            if (coverRequests.size >= 16) break
            if (!url.startsWith("http") || peekCover(url) != null || coverRequests.contains(url)) continue
            coverRequests.request(url)
        }
    }

    suspend fun awaitCover(url: String): Bitmap? {
        val normalized = coverUrl(url)
        if (!normalized.startsWith("http")) return null
        peekCover(normalized)?.let { return it }
        return coverRequests.await(normalized)
    }

    private fun fetchCover(url: String): Bitmap? {
        val key = "c2:" + url.substringBefore('#').trim()
        LocalStore.loadCover(key)?.let { disk ->
            if (disk.width > 0 && !disk.isRecycled) {
                coverRam.put(url, disk)
                return disk
            }
        }
        val bytes = downloadBytes(url) ?: return null
        if (bytes.size < 200) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth < 16 || bounds.outHeight < 16) return null
        val opts = BitmapFactory.Options().apply {
            inSampleSize = coverSample(bounds.outWidth, bounds.outHeight, 480)
            inPreferredConfig = Bitmap.Config.RGB_565
            inScaled = false
        }
        val raw = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts) ?: return null
        coverRam.put(url, raw)
        coverDiskIo.execute { runCatching { LocalStore.saveCover(key, raw) } }
        return raw
    }

    private fun coverSample(width: Int, height: Int, maxPx: Int): Int {
        var sample = 1
        while (sample < 16 && (width / (sample * 2) >= maxPx || height / (sample * 2) >= maxPx)) sample *= 2
        return sample
    }

    private fun readerSample(width: Int, height: Int): Int {
        var sample = 1
        while (sample < 8 && (width / (sample * 2) >= 1440 || height / (sample * 2) >= 5200)) {
            sample *= 2
        }
        return sample
    }

    private fun lean565(src: Bitmap): Bitmap {
        if (src.config == Bitmap.Config.RGB_565) return src
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.RGB_565)
        Canvas(out).drawBitmap(src, 0f, 0f, null)
        return out
    }

    private fun scaleIfHuge(src: Bitmap): Bitmap {
        val maxW = 1440
        val maxH = 5200
        if (src.width <= maxW && src.height <= maxH) return src
        val s = minOf(maxW.toFloat() / src.width, maxH.toFloat() / src.height)
        val w = (src.width * s).toInt().coerceAtLeast(1)
        val h = (src.height * s).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(src, w, h, true)
    }

    private fun downloadBytes(url: String): ByteArray? {
        fun once(target: String, lenient: Boolean): ByteArray? {
            val conn = openImage(target, lenient)
            return try {
                val code = conn.responseCode
                if (code !in 200..299) return null
                val len = conn.contentLength
                conn.inputStream.use { readCapped(it, len) }
            } finally {
                releaseConn(conn)
            }
        }
        fun attempt(target: String): ByteArray? = try {
            once(target, false)
        } catch (e: Exception) {
            if (!isCert(e)) null else try { once(target, true) } catch (_: Exception) { null }
        }
        for (candidate in jmImageFallbacks(url)) {
            val bytes = attempt(candidate)
            if (bytes != null && bytes.size >= 800) return bytes
        }
        return null
    }

    private fun jmImageFallbacks(url: String): List<String> {
        val host = runCatching { URL(url).host.lowercase() }.getOrNull() ?: return listOf(url)
        if (JM_IMAGE_HOSTS.none { it == host }) return listOf(url)
        val path = url.substringAfter(host)
        val out = LinkedHashSet<String>()
        out.add(url)
        JM_IMAGE_HOSTS.forEach { h -> if (h != host) out.add("https://$h$path") }
        return out.toList()
    }

    private fun readCapped(stream: InputStream, expected: Int = -1, max: Int = 32_000_000, strict: Boolean = true): ByteArray? {
        val out = java.io.ByteArrayOutputStream()
        val buf = ByteArray(16 * 1024)
        var n = 0
        while (true) {
            val r = stream.read(buf)
            if (r < 0) break
            if (r == 0) continue
            if (n > max - r) {
                if (strict) return null
                break
            }
            out.write(buf, 0, r)
            n += r
        }
        if (n < 1) return null
        if (strict) {
            if (n < 800) return null
            if (expected > 800 && n + 64 < expected) return null
            val bytes = out.toByteArray()
            return if (imageComplete(bytes)) bytes else null
        }
        return out.toByteArray()
    }

    private fun imageComplete(b: ByteArray): Boolean {
        if (b.size < 32) return false
        if (b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte()) {
            val start = (b.size - 32).coerceAtLeast(1)
            var i = b.size - 1
            while (i >= start) {
                if (b[i] == 0xD9.toByte() && b[i - 1] == 0xFF.toByte()) return true
                i--
            }
            return false
        }
        if (b[0] == 0x89.toByte() && b[1] == 'P'.code.toByte()) {
            val from = (b.size - 24).coerceAtLeast(0)
            var i = from
            while (i + 4 <= b.size) {
                if (b[i] == 'I'.code.toByte() && b[i + 1] == 'E'.code.toByte() && b[i + 2] == 'N'.code.toByte() && b[i + 3] == 'D'.code.toByte()) {
                    return true
                }
                i++
            }
            return false
        }
        if (b[0] == 'G'.code.toByte() && b[1] == 'I'.code.toByte() && b[2] == 'F'.code.toByte()) {
            return b[b.size - 1] == 0x3B.toByte()
        }
        return true
    }

    private fun openImage(url: String, lenient: Boolean): HttpURLConnection {
        val conn = URL(url).openConnection() as HttpURLConnection
        if (lenient && conn is HttpsURLConnection) {
            conn.sslSocketFactory = lenientFactory
            conn.hostnameVerifier = lenientHost
        }
        conn.connectTimeout = 12000
        conn.readTimeout = 22000
        conn.instanceFollowRedirects = true
        conn.setRequestProperty("Connection", "keep-alive")
        val host = runCatching { URL(url).host.lowercase() }.getOrDefault("")
        val jm = listOf("jmapi", "18comic", "jmcomic", "cdn-msp", "cdnmhws", "jmapiproxy").any { it in host }
        val biquge = "biquge365" in host || "bqg5.com" in host || "biquge5200" in host || "8tsw.com" in host
        val copy = "mangafun" in host || "copy5000" in host || "copy2026" in host
        if (jm) {
            conn.setRequestProperty("User-Agent", JM_UA)
            conn.setRequestProperty("Referer", "https://18comic.vip/")
        } else if ("biquge5200" in host) {
            conn.setRequestProperty("User-Agent", UA)
            conn.setRequestProperty("Referer", "http://www.biquge5200.cc/")
        } else if ("8tsw.com" in host) {
            conn.setRequestProperty("User-Agent", UA)
            conn.setRequestProperty("Referer", "https://www.8tsw.com/")
        } else if ("biquges.cc" in host) {
            conn.setRequestProperty("User-Agent", UA)
            conn.setRequestProperty("Referer", "https://www.biquges.cc/")
        } else if ("bqg5.com" in host) {
            conn.setRequestProperty("User-Agent", UA)
            conn.setRequestProperty("Referer", "https://www.bqg5.com/")
        } else if (biquge) {
            conn.setRequestProperty("User-Agent", UA)
            conn.setRequestProperty("Referer", "$BIQUGE_HOME/")
        } else if (copy) {
            conn.setRequestProperty("User-Agent", UA)
            conn.setRequestProperty("Referer", "$COPY_WEB/")
        } else {
            conn.setRequestProperty("User-Agent", UA)
            conn.setRequestProperty("Referer", "https://$host/")
        }
        Regex("[#&]smyReferer=([^&]+)").find(url)?.groupValues?.get(1)?.let { encoded ->
            runCatching { java.net.URLDecoder.decode(encoded,"UTF-8") }.getOrNull()?.takeIf { isSafe(it) }?.let { referer ->
                conn.setRequestProperty("Referer",referer)
                WebCookies.get(url.substringBefore('#')).takeIf { it.isNotBlank() }?.let { conn.setRequestProperty("Cookie",it) }
            }
        }
        conn.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/jpeg,image/png,image/*,*/*;q=0.8")
        return conn
    }

    private fun descrambleJm(url: String, src: Bitmap): Bitmap {
        val m = Regex("""/media/photos/(\d+)/([^/?#]+)""").find(url) ?: return src
        val aid = m.groupValues[1].toIntOrNull() ?: return src
        val file = m.groupValues[2]
        if (file.endsWith(".gif", true)) return src
        val stem = file.substringBeforeLast('.').ifBlank { file }
        val scramble = Regex("""[#&]s=(\d+)""").find(url)?.groupValues?.get(1)?.toIntOrNull() ?: 220980
        if (aid < scramble) return src
        val stemNum = jmSplitNum(aid, stem)
        val extNum = if (file != stem) jmSplitNum(aid, file) else stemNum
        val stemOut = if (stemNum > 1) jmUnscramble(src, stemNum) else src
        if (extNum <= 1 || extNum == stemNum) {
            if (stemNum <= 1) return src
            return if (seamScore(stemOut, stemNum) <= 120) stemOut else {
                stemOut.recycle()
                src
            }
        }
        val extOut = jmUnscramble(src, extNum)
        val stemScore = if (stemNum > 1) seamScore(stemOut, stemNum) else 999
        val extScore = seamScore(extOut, extNum)
        return when {
            stemScore <= extScore && stemScore <= 120 -> {
                if (extOut !== src) extOut.recycle()
                stemOut
            }
            extScore < stemScore && extScore <= 120 -> {
                if (stemOut !== src) stemOut.recycle()
                extOut
            }
            else -> {
                if (stemOut !== src) stemOut.recycle()
                if (extOut !== src) extOut.recycle()
                src
            }
        }
    }

    /** Mean channel difference across descramble seams. Low means the strips join. */
    private fun seamScore(src: Bitmap, num: Int): Int {
        if (num <= 1) return 999
        val w = src.width
        val h = src.height
        val over = h % num
        val move = h / num
        if (move < 2) return 999
        val step = (w / 64).coerceAtLeast(1)
        val a = IntArray(w)
        val b = IntArray(w)
        var total = 0L
        var edges = 0
        for (i in 1 until num) {
            val y = (move + over) + (i - 1) * move
            if (y <= 0 || y >= h) continue
            src.getPixels(a, 0, w, 0, y - 1, w, 1)
            src.getPixels(b, 0, w, 0, y, w, 1)
            var sum = 0L
            var n = 0
            var x = 0
            while (x < w) {
                val c1 = a[x]
                val c2 = b[x]
                sum += kotlin.math.abs(((c1 shr 16) and 255) - ((c2 shr 16) and 255))
                sum += kotlin.math.abs(((c1 shr 8) and 255) - ((c2 shr 8) and 255))
                sum += kotlin.math.abs((c1 and 255) - (c2 and 255))
                n++
                x += step
            }
            if (n > 0) {
                total += sum / (n * 3)
                edges++
            }
        }
        return if (edges == 0) 999 else (total / edges).toInt()
    }

    private fun jmSplitNum(aid: Int, filename: String): Int {
        if (aid < 220980) return 0
        if (aid < 268850) return 10
        val x = if (aid < 421926) 10 else 8
        val s = md5hex("$aid$filename")
        return (s.last().code % x) * 2 + 2
    }

    private fun jmUnscramble(src: Bitmap, num: Int): Bitmap {
        val w = src.width
        val h = src.height
        if (w < 8 || h < num) return src
        val remainder = h % num
        val copyH = h / num
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        for (i in 0 until num) {
            var srcY = h - (copyH * (i + 1)) - remainder
            var destY = copyH * i
            var pieceH = copyH
            if (i == 0) pieceH += remainder else destY += remainder
            if (srcY < 0) {
                pieceH += srcY
                srcY = 0
            }
            if (pieceH <= 0 || destY >= h) continue
            if (srcY + pieceH > h) pieceH = h - srcY
            if (destY + pieceH > h) pieceH = h - destY
            if (pieceH <= 0) continue
            canvas.drawBitmap(src, Rect(0, srcY, w, srcY + pieceH), Rect(0, destY, w, destY + pieceH), null)
        }
        return out
    }

    private fun fetchJm(
        siteId: String,
        category: String,
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): Pair<List<CrawledWork>, String?> {
        val (c, o, search) = jmApiParams(category)
        val pages = if (search != null) 3 else JM_PAGES
        fun pathFor(p: Int) = if (search != null) {
            "/search?search_query=${enc(search)}&page=$p&main_tag=0&o=$o"
        } else {
            "/categories/filter?page=$p&order=&c=${enc(c)}&o=$o"
        }
        val pool = java.util.concurrent.Executors.newFixedThreadPool(3)
        val parts = try {
            val futures = (1..pages).map { p ->
                p to pool.submit<List<CrawledWork>> {
                    val data = jmApi(pathFor(p))
                    if (data == null) emptyList() else parseJmApi(data, siteId)
                }
            }
            val first = runCatching { futures.first().second.get() }.getOrDefault(emptyList())
            if (first.isNotEmpty()) onPartial?.invoke(first.take(LISTING_CAP))
            futures.map { (p, f) -> p to runCatching { f.get() }.getOrDefault(emptyList()) }
        } finally {
            pool.shutdownNow()
        }
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        parts.sortedBy { it.first }.forEach { (_, chunk) ->
            for (item in chunk) if (seen.add(item.id)) out.add(item)
        }
        if (out.isNotEmpty()) return out.take(LISTING_CAP) to null

        var lastErr: String? = "未解析到目录"
        val path = jmPath(category)
        val hosts = listOf("https://18comic.vip", "https://18comic.org", "https://18comic.me")
        for (host in hosts) {
            val (html, err, finalUrl) = get("$host$path")
            if (err != null) {
                lastErr = err
                continue
            }
            val items = parseJm(html, finalUrl, siteId)
            if (items.isNotEmpty()) return items to null
            lastErr = detectWall(html) ?: "未解析到目录"
        }
        return emptyList<CrawledWork>() to lastErr
    }

    private fun fetchCms(
        base: String,
        idPrefix: String,
        siteId: String,
        category: String,
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): Pair<List<CrawledWork>, String?> {
        val jobs = cmsJobs(base, category)
        if (jobs.isEmpty()) return emptyList<CrawledWork>() to "未解析到目录"
        val (u0, extra0) = jobs.first()
        val (html0, err0, final0) = get(u0)
        val first = if (err0 != null || html0.isBlank()) emptyList() else parseIkan(html0, final0, siteId, extra0, idPrefix)
        if (first.isNotEmpty()) onPartial?.invoke(first.take(LISTING_CAP))
        val rest = if (jobs.size == 1) {
            emptyList()
        } else {
            val pool = java.util.concurrent.Executors.newFixedThreadPool(4)
            try {
                jobs.drop(1).mapIndexed { index, (u, extra) ->
                    pool.submit<Triple<Int, List<CrawledWork>, String?>> {
                        val (html, err, finalUrl) = get(u)
                        if (err != null || html.isBlank()) Triple(index, emptyList(), err)
                        else Triple(index, parseIkan(html, finalUrl, siteId, extra, idPrefix), null)
                    }
                }.map { runCatching { it.get() }.getOrElse { Triple(0, emptyList(), it.message) } }
            } finally {
                pool.shutdownNow()
            }
        }
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        var lastErr: String? = err0
        for (item in first) if (seen.add(item.href)) out.add(item)
        rest.sortedBy { it.first }.forEach { (_, chunk, err) ->
            if (err != null && chunk.isEmpty()) lastErr = err
            for (item in chunk) if (seen.add(item.href)) out.add(item)
        }
        return if (out.isEmpty()) emptyList<CrawledWork>() to (lastErr ?: "未解析到目录")
        else out.take(LISTING_CAP) to null
    }

    private fun cmsJobs(base: String, category: String): List<Pair<String, String?>> {
        fun paged(path: String, n: Int, extra: String?): List<Pair<String, String?>> {
            val join = if (path.contains("?")) "&" else "?"
            return (1..n).map { i ->
                val url = if (i == 1) "$base$path" else "$base$path${join}page=$i"
                url to extra
            }
        }
        return when (category) {
            "连载", "連載" -> paged("/booklist?end=0", 6, "连载")
            "完结", "完結" -> paged("/booklist?end=1", 6, "完结")
            "全部" -> paged("/booklist?end=-1", 8, null)
            "最近" -> paged("/update", 4, null)
            "排行榜" -> listOf("$base/rank" to null)
            "推荐", "推薦" -> listOf("$base/" to null) + paged("/booklist?end=-1", 2, null)
            else -> paged("/booklist?end=-1", 4, null)
        }
    }

    private fun fetchAlbum(
        siteId: String,
        category: String,
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): Pair<List<CrawledWork>, String?> {
        val jobs = albumJobs(category)
        if (jobs.isEmpty()) return emptyList<CrawledWork>() to "未解析到目录"
        val (html0, err0, final0) = get(jobs.first())
        if (isAlbumCloak(html0)) return emptyList<CrawledWork>() to "相簿源站暂时打不开"
        val first = if (err0 != null || html0.isBlank()) emptyList() else parseAlbum(html0, final0, siteId)
        if (first.isNotEmpty()) onPartial?.invoke(first.take(LISTING_CAP))
        val rest = if (jobs.size == 1) {
            emptyList()
        } else {
            val pool = java.util.concurrent.Executors.newFixedThreadPool(3)
            try {
                jobs.drop(1).mapIndexed { index, url ->
                    pool.submit<Triple<Int, List<CrawledWork>, String?>> {
                        val (html, err, finalUrl) = get(url)
                        if (isAlbumCloak(html)) Triple(index, emptyList(), "相簿源站暂时打不开")
                        else if (err != null || html.isBlank()) Triple(index, emptyList(), err)
                        else Triple(index, parseAlbum(html, finalUrl, siteId), null)
                    }
                }.map { runCatching { it.get() }.getOrElse { Triple(0, emptyList(), it.message) } }
            } finally {
                pool.shutdownNow()
            }
        }
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        var lastErr: String? = err0
        for (item in first) if (seen.add(item.id)) out.add(item)
        rest.sortedBy { it.first }.forEach { (_, chunk, err) ->
            if (err != null && chunk.isEmpty()) lastErr = err
            for (item in chunk) if (seen.add(item.id)) out.add(item)
        }
        return if (out.isEmpty()) emptyList<CrawledWork>() to (lastErr ?: "未解析到目录")
        else out.take(LISTING_CAP) to null
    }

    private fun albumJobs(category: String): List<String> {
        fun pages(path: String, n: Int): List<String> {
            val root = "https://nsfwx.pics"
            val base = path.trimEnd('/')
            return (1..n).map { i ->
                if (i == 1) {
                    if (base.isEmpty()) "$root/" else "$root$base/"
                } else if (base.isEmpty()) {
                    "$root/page/$i/"
                } else {
                    "$root$base/page/$i/"
                }
            }
        }
        return when (category) {
            "亚洲", "亞洲" -> pages("/category/asia", 3)
            "Cosplay", "cosplay" -> pages("/category/cosplay", 3)
            else -> pages("", 3)
        }
    }

    private fun isAlbumCloak(html: String): Boolean {
        if (html.isBlank()) return false
        val t = html.lowercase()
        return "urldance" in t || "loading……" in html || (html.length < 1200 && "loading" in t && "<img" !in t)
    }

    private fun isAlbumHref(href: String): Boolean {
        val host = runCatching { URL(href).host.lowercase() }.getOrDefault(href.lowercase())
        return "nsfwx" in host
    }

    private fun fetchAlbumWork(href: String): Pair<FetchedWork?, String?> {
        val (html, err, finalUrl) = get(href)
        if (isAlbumCloak(html)) return null to "相簿源站暂时打不开"
        if (err != null && html.isBlank()) return null to err
        val pages = parseAlbumPages(html, finalUrl.ifBlank { href })
        if (pages.isEmpty()) return null to (err ?: "未解析到图片")
        val title = Regex("""<h1[^>]*>([\s\S]*?)</h1>""", RegexOption.IGNORE_CASE)
            .find(html)?.groupValues?.getOrNull(1)
            ?.replace(Regex("<[^>]+>"), " ")
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            ?.let { decode(it) }
            .orEmpty()
        val ch = CachedChapter("ch1", title.ifBlank { "图集" }.take(80), href, 1, pages)
        return FetchedWork("", emptyList(), pages.first(), "", listOf(ch), title) to null
    }

    private fun parseAlbum(html: String, base: String, siteId: String): List<CrawledWork> {
        if (html.isBlank() || isAlbumCloak(html)) return emptyList()
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        val re = Pattern.compile(
            """<a\b([^>]*)>[\s\S]{0,600}?<img\b([^>]*)>""",
            Pattern.CASE_INSENSITIVE,
        )
        val m = re.matcher(html)
        while (m.find() && out.size < LISTING_CAP) {
            val hrefRaw = attr(m.group(1).orEmpty(), "href") ?: continue
            val low = hrefRaw.lowercase()
            if (hrefRaw.startsWith("#") || hrefRaw.startsWith("javascript:")) continue
            if ("/category/" in low || "/tag/" in low || "/page/" in low || "/author/" in low || "/feed" in low) continue
            val href = abs(base, hrefRaw)
            val img = m.group(2).orEmpty()
            val src = coverImageSource(img) ?: continue
            if (src.startsWith("data:") || src.endsWith(".svg")) continue
            val slug = href.substringAfterLast('/').substringBefore('?').replace('-', ' ').trim()
            val title = decode(attr(img, "alt").orEmpty()).ifBlank { decode(attr(m.group(1).orEmpty(), "title").orEmpty()) }.ifBlank { slug }
            if (title.length < 2 || isAd(title, href)) continue
            val id = "ab" + href.hashCode().toUInt().toString(16)
            if (!seen.add(id)) continue
            out.add(
                CrawledWork(
                    id = id,
                    siteId = siteId,
                    title = title.take(60),
                    cover = abs(base, src),
                    href = href,
                ),
            )
        }
        return out
    }

    private fun parseAlbumPages(html: String, base: String): List<String> {
        if (html.isBlank() || isAlbumCloak(html)) return emptyList()
        val mark = Regex("""class=["'][^"']*entry-content[^"']*["']""", RegexOption.IGNORE_CASE).find(html)
        val chunk = if (mark != null) {
            html.substring(mark.range.first, (mark.range.first + 250_000).coerceAtMost(html.length))
        } else {
            html
        }
        val pages = parsePages(chunk, base)
        return if (pages.isNotEmpty() || chunk === html) pages else parsePages(html, base)
    }


    private fun jmApiParams(cat: String): Triple<String, String, String?> = when (cat) {
        "最近" -> Triple("0", "mr", null)
        "排行榜" -> Triple("0", "mv", null)
        "月排行" -> Triple("0", "mv_m", null)
        "周排行" -> Triple("0", "mv_w", null)
        "天排行" -> Triple("0", "mv_t", null)
        "韓漫", "韩漫" -> Triple("hanman", "mr", null)
        "美漫", "A漫" -> Triple("meiman", "mr", null)
        "同人" -> Triple("doujin", "mr", null)
        "單本", "单本", "本本" -> Triple("single", "mr", null)
        "短篇" -> Triple("short", "mr", null)
        "Cosplay" -> Triple("doujin_cosplay", "mr", null)
        "3D" -> Triple("3D", "mr", null)
        "其他" -> Triple("another", "mr", null)
        "中文" -> Triple("0", "mr", "中文")
        "推荐", "全部", "" -> Triple("0", "mr", null)
        else -> Triple("0", "mr", cat)
    }

    private fun jmApi(path: String): JSONObject? {
        refreshJmHosts()
        val ts = (System.currentTimeMillis() / 1000).toString()
        val token = md5hex(ts + JM_SECRET)
        val tokenparam = "$ts,$JM_VER"
        val hosts = jmHosts.ifEmpty { JM_API_HOSTS }
        val pool = java.util.concurrent.Executors.newFixedThreadPool(hosts.size.coerceAtMost(4))
        try {
            val completion = java.util.concurrent.ExecutorCompletionService<JSONObject?>(pool)
            hosts.forEach { host ->
                completion.submit { jmApiOnce(host, path, token, tokenparam, ts) }
            }
            val deadline = System.currentTimeMillis() + 8000L
            repeat(hosts.size) {
                val left = deadline - System.currentTimeMillis()
                if (left <= 0L) return null
                val fut = completion.poll(left, java.util.concurrent.TimeUnit.MILLISECONDS) ?: return null
                val got = runCatching { fut.get() }.getOrNull()
                if (got != null) return got
            }
            return null
        } finally {
            pool.shutdownNow()
        }
    }

    private fun jmApiOnce(host: String, path: String, token: String, tokenparam: String, ts: String): JSONObject? {
        var conn: HttpURLConnection? = null
        return try {
            conn = open("$host$path", lenient = true, connectMs = 4000, readMs = 7000)
            conn.setRequestProperty("User-Agent", JM_UA)
            conn.setRequestProperty("token", token)
            conn.setRequestProperty("tokenparam", tokenparam)
            conn.setRequestProperty("version", JM_VER)
            conn.setRequestProperty("Accept", "application/json,text/plain,*/*")
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate")
            val code = conn.responseCode
            if (code !in 200..299) return null
            val raw = readBody(conn).trim().trimStart('\uFEFF')
            val obj = JSONObject(raw)
            if (obj.optJSONArray("content") != null) return obj
            when (val dataEl = obj.opt("data")) {
                is JSONObject -> dataEl
                is String -> {
                    if (dataEl.length < 8) return null
                    val trimmed = dataEl.trim().replace("\\/", "/")
                    if (trimmed.startsWith("{")) {
                        runCatching { return JSONObject(trimmed) }.getOrNull()
                    } else {
                        val plain = decryptJm(dataEl, ts) ?: decryptJm(trimmed, ts) ?: return null
                        JSONObject(plain)
                    }
                }
                else -> null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    @Volatile private var hostRefreshing = false

    private fun refreshJmHosts() {
        if (jmHosts.isEmpty()) jmHosts = JM_API_HOSTS
        if (System.currentTimeMillis() - jmHostsAt < 6 * 3600_000L) return
        if (hostRefreshing) return
        hostRefreshing = true
        Thread {
            try {
                refreshJmHostsNow()
            } catch (_: Exception) {
            } finally {
                hostRefreshing = false
            }
        }.start()
    }

    private fun refreshJmHostsNow() {
        for (url in JM_DOMAIN_SERVERS) {
            try {
                val (text, err, _) = get(url)
                if (err != null && text.isBlank()) continue
                val raw = text.ifBlank {
                    val conn = open(url, lenient = true)
                    val body = readBody(conn)
                    conn.disconnect()
                    body
                }.trim().trimStart('\uFEFF')
                if (raw.isBlank()) continue
                val plain = if (raw.startsWith("{") && raw.contains("\"Server\"")) raw
                else decryptAes(raw, "", JM_DOMAIN_SECRET) ?: continue
                val hosts = parseJmServers(plain.trimStart('\uFEFF'))
                if (hosts.isNotEmpty()) {
                    val merged = LinkedHashSet<String>()
                    merged.addAll(hosts)
                    merged.addAll(JM_API_HOSTS)
                    jmHosts = merged.toList()
                    jmHostsAt = System.currentTimeMillis()
                    return
                }
            } catch (_: Exception) {
                continue
            }
        }
        jmHosts = JM_API_HOSTS
        jmHostsAt = System.currentTimeMillis()
    }

    private fun parseJmServers(plain: String): List<String> {
        val out = ArrayList<String>()
        val seen = HashSet<String>()
        fun addHost(raw: String) {
            val t = raw.trim().trim('"').trimEnd('/')
            if (t.isBlank() || '.' !in t) return
            val host = t.removePrefix("https://").removePrefix("http://")
            val url = "https://$host"
            if (seen.add(url)) out.add(url)
        }
        val trimmed = plain.trim().trimStart('\uFEFF')
        if (trimmed.startsWith("{")) {
            runCatching {
                val obj = JSONObject(trimmed)
                for (key in listOf("Server", "Setting", "jm3_Server")) {
                    val arr = obj.optJSONArray(key) ?: continue
                    for (i in 0 until arr.length()) {
                        when (val v = arr.opt(i)) {
                            is String -> addHost(v)
                            is JSONArray -> addHost(v.optString(0))
                        }
                    }
                }
            }
            if (out.isNotEmpty()) return out
        }
        for (line in trimmed.lineSequence()) {
            val idx = line.indexOf(':')
            if (idx < 0) continue
            if (!line.substring(0, idx).contains("Server", ignoreCase = true)) continue
            for (part in line.substring(idx + 1).split(',', ';', ' ', '\t')) addHost(part)
        }
        return out
    }

    private fun decryptAes(dataB64: String, ts: String, secret: String): String? {
        return try {
            val key = md5hex(ts + secret).toByteArray(Charsets.UTF_8)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"))
            val raw = Base64.decode(dataB64.replace("\\s+".toRegex(), "").replace("\\/", "/"), Base64.DEFAULT)
            String(cipher.doFinal(raw), Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }

    private fun decryptJm(dataB64: String, ts: String): String? {
        return try {
            val key = md5hex(ts + JM_SECRET).toByteArray(Charsets.UTF_8)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"))
            val raw = Base64.decode(dataB64.replace("\\s+".toRegex(), "").replace("\\/", "/"), Base64.DEFAULT)
            String(cipher.doFinal(raw), Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }

    private fun md5hex(s: String): String {
        val d = MessageDigest.getInstance("MD5").digest(s.toByteArray(Charsets.UTF_8))
        return d.joinToString("") { b -> "%02x".format(b) }
    }

    private fun parseJmApi(data: JSONObject, siteId: String): List<CrawledWork> {
        val arr: JSONArray = data.optJSONArray("content") ?: return emptyList()
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        for (i in 0 until arr.length()) {
            val row = arr.optJSONObject(i) ?: continue
            val id = row.optString("id")
            val title = decode(row.optString("name").ifBlank { row.optString("title") })
            if (id.isBlank() || !id.all { it.isDigit() } || !seen.add(id) || isAd(title, "/album/$id/")) continue
            val author = when (val a = row.opt("author")) {
                is JSONArray -> a.optString(0)
                else -> a?.toString().orEmpty()
            }
            val catTitle = decode(row.optJSONObject("category")?.optString("title").orEmpty()).trim()
            val tags = parseJmTagList(row).toMutableList()
            if (catTitle.length in 1..16 && tags.none { it.equals(catTitle, true) }) tags.add(0, catTitle)
            out.add(
                CrawledWork(
                    id = "jm$id",
                    siteId = siteId,
                    title = title.take(60),
                    cover = "$JM_CDN/media/albums/${id}_3x4.jpg",
                    href = "https://18comic.vip/album/$id/",
                    author = author,
                    genres = (if (tags.isNotEmpty()) tags.joinToString(" ") else guess("$title $catTitle")),
                    tags = tags,
                ),
            )
            if (out.size >= LISTING_CAP) break
        }
        return out
    }

    private fun parseJmAlbum(data: JSONObject, albumId: String): List<CachedChapter> {
        val series = data.optJSONArray("series")
        val out = ArrayList<CachedChapter>()
        val seen = HashSet<String>()
        if (series != null) {
            for (i in 0 until series.length()) {
                val s = series.optJSONObject(i) ?: continue
                val id = s.optString("id")
                if (id.isBlank() || !seen.add(id)) continue
                val name = decode(s.optString("name")).ifBlank { "第 ${out.size + 1} 话" }
                out.add(CachedChapter("jmch$id", name.take(80), "https://18comic.vip/photo/$id/", out.size + 1))
            }
        }
        if (out.isEmpty()) {
            val name = decode(data.optString("name")).ifBlank { "第 1 话" }
            out.add(CachedChapter("jmch$albumId", name.take(80), "https://18comic.vip/photo/$albumId/", 1))
        }
        return out
    }

    private fun jmAuthors(data: JSONObject): String {
        return when (val a = data.opt("author")) {
            is JSONArray -> (0 until a.length()).map { a.optString(it) }.filter { it.isNotBlank() }.joinToString(" ")
            else -> a?.toString().orEmpty()
        }
    }

    private fun parseJmTags(data: JSONObject): List<String> {
        val out = LinkedHashSet<String>()
        out.addAll(parseJmTagList(data))
        data.optJSONObject("category")?.optString("title")?.trim()?.let { if (it.length in 1..12) out.add(it) }
        return out.toList()
    }

    private fun parseJmTagList(row: JSONObject): List<String> {
        val out = LinkedHashSet<String>()
        val tags = row.optJSONArray("tags") ?: return emptyList()
        for (i in 0 until tags.length()) {
            val t = when (val v = tags.opt(i)) {
                is String -> v.trim()
                is JSONObject -> v.optString("tag_name").ifBlank { v.optString("title") }.trim()
                else -> ""
            }
            if (t.length in 1..16 && !isAd(t, t)) out.add(t)
        }
        return out.toList()
    }

    private data class PageMeta(
        val author: String,
        val tags: List<String>,
        val cover: String,
        val description: String,
    )

    private fun parseIkanDetail(html: String, base: String): PageMeta {
        val author = decode(
            Regex("""作者：\s*<a[^>]*>([^<]+)</a>""", RegexOption.IGNORE_CASE).find(html)?.groupValues?.getOrNull(1).orEmpty(),
        )
        val st = decode(
            Regex("""detail-list-title-1[^>]*>([^<]+)""", RegexOption.IGNORE_CASE).find(html)?.groupValues?.getOrNull(1).orEmpty(),
        )
        val tags = ArrayList<String>()
        when {
            st.contains("完") -> tags.add("完结")
            st.contains("连") || st.contains("連") -> tags.add("连载")
        }
        val cover = Regex("""detail-main-cover[\s\S]{0,280}?data-original=["']([^"']+)["']""", RegexOption.IGNORE_CASE)
            .find(html)?.groupValues?.getOrNull(1)
            ?: Regex("""detail-main-cover[\s\S]{0,280}?src=["']([^"']+)["']""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.getOrNull(1)
            ?: ""
        val desc = plainDesc(
            Regex("""class=["']detail-desc["'][^>]*>([\s\S]*?)</p>""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.getOrNull(1).orEmpty(),
        )
        return PageMeta(author, tags, if (cover.isBlank()) "" else abs(base, cover), desc)
    }

    private fun htmlDescription(html: String): String {
        val block = Regex(
            """class=["'][^"']*(?:detail-desc|book-desc|comic-intro|intro-content|manga-intro)[^"']*["'][^>]*>([\s\S]{8,2000}?)</(?:p|div)>""",
            RegexOption.IGNORE_CASE,
        ).find(html)?.groupValues?.getOrNull(1).orEmpty()
        val fromBlock = plainDesc(block)
        if (fromBlock.length >= 8) return fromBlock
        val og = Regex("""property=["']og:description["'][^>]*content=["']([^"']+)["']""", RegexOption.IGNORE_CASE)
            .find(html)?.groupValues?.getOrNull(1)
            ?: Regex("""content=["']([^"']+)["'][^>]*property=["']og:description["']""", RegexOption.IGNORE_CASE)
                .find(html)?.groupValues?.getOrNull(1)
            ?: ""
        val text = plainDesc(og)
        if (text.length in 12..500 && text.count { it == ',' || it == '，' } < 4) return text
        return ""
    }

    private fun plainDesc(raw: String): String {
        if (raw.isBlank()) return ""
        var t = decode(raw)
        t = t.replace(Regex("(?i)<br\\s*/?>"), " ")
        t = t.replace(Regex("(?i)</p>"), " ")
        t = t.replace(Regex("<[^>]+>"), " ")
        t = t.replace("&hellip;", "…")
        t = t.replace(Regex("\\s+"), " ").trim()
        return t.take(1200)
    }

    private fun ikanPageTitle(html: String): String {
        val patterns = listOf(
            Regex("""property=["']og:title["'][^>]*content=["']([^"']+)["']""", RegexOption.IGNORE_CASE),
            Regex("""content=["']([^"']+)["'][^>]*property=["']og:title["']""", RegexOption.IGNORE_CASE),
            Regex("""class=["'][^"']*detail[^"']*title[^"']*["'][^>]*>\s*([^<]{1,80})""", RegexOption.IGNORE_CASE),
            Regex("""<h1[^>]*>\s*([^<]{1,80})</h1>""", RegexOption.IGNORE_CASE),
            Regex("""<title>\s*([^<]{1,120})</title>""", RegexOption.IGNORE_CASE),
        )
        for (p in patterns) {
            val raw = p.find(html)?.groupValues?.getOrNull(1).orEmpty()
            val t = tidyTitle(decode(raw))
            if (t.length >= 2 && !isIdTitle(t)) return t.take(80)
        }
        return ""
    }

    private fun tidyTitle(raw: String): String {
        var t = raw.replace(Regex("\\s+"), " ").trim()
        for (sep in listOf(" - ", " | ", " _ ", " – ", " — ")) {
            if (sep in t) t = t.substringBefore(sep).trim()
        }
        return t
    }

    private fun parseJmPages(data: JSONObject, chapterId: String): List<String> {
        val images = data.optJSONArray("images") ?: return emptyList()
        val scramble = jmScrambleId(chapterId, data)
        val out = ArrayList<String>()
        for (i in 0 until images.length()) {
            val file = when (val v = images.opt(i)) {
                is String -> v
                is JSONObject -> v.optString("name")
                else -> ""
            }
            if (file.isBlank()) continue
            val low = file.lowercase()
            if (listOf("logo", "avatar", "icon", "ads", "banner").any { it in low }) continue
            out.add("$JM_CDN/media/photos/$chapterId/${file.removePrefix("/")}#s=$scramble")
            if (out.size >= 400) break
        }
        return out
    }

    private val scrambleCache = HashMap<String, Int>()

    private fun jmScrambleId(photoId: String, data: JSONObject): Int {
        val fromJson = data.optInt("scramble_id", 0)
        if (fromJson > 0) return fromJson
        synchronized(scrambleCache) { scrambleCache[photoId] }?.let { return it }
        val html = jmGetText(
            "/chapter_view_template?id=$photoId&mode=vertical&page=0&app_img_shunt=1&express=off&v=${System.currentTimeMillis() / 1000}",
        ).orEmpty()
        val id = Regex("""scramble_id\s*=\s*(\d+)""").find(html)?.groupValues?.get(1)?.toIntOrNull() ?: 220980
        synchronized(scrambleCache) { scrambleCache[photoId] = id }
        return id
    }

    private fun jmGetText(path: String): String? {
        refreshJmHosts()
        val ts = (System.currentTimeMillis() / 1000).toString()
        val token = md5hex(ts + JM_SECRET)
        for (host in jmHosts) {
            var conn: HttpURLConnection? = null
            try {
                conn = open("$host$path", lenient = true)
                conn.setRequestProperty("User-Agent", JM_UA)
                conn.setRequestProperty("token", token)
                conn.setRequestProperty("tokenparam", "$ts,$JM_VER")
                conn.setRequestProperty("version", JM_VER)
                conn.setRequestProperty("Accept", "text/html,application/json,*/*")
                val code = conn.responseCode
                if (code !in 200..299) continue
                val body = readBody(conn)
                if (body.isNotBlank()) return body
            } catch (_: Exception) {
            } finally {
                conn?.disconnect()
            }
        }
        return null
    }

    private fun jmIdFrom(href: String): String? {
        val m = Regex("""/(?:album|photo)/(\d+)""", RegexOption.IGNORE_CASE).find(href)
        return m?.groupValues?.getOrNull(1)
    }

    private fun isJmHref(href: String): Boolean {
        val h = href.lowercase()
        return "18comic" in h || "jmcomic" in h || "jmapi" in h
    }

    private fun isAd(title: String, href: String): Boolean {
        val blob = "$title $href".lowercase()
        val hints = listOf(
            "广告", "廣告", "sponsor", "adsense", "/ad/", "/ads/", "promo", "banner",
            "下载app", "下載app", "官方app", "邀请码", "邀請碼", "telegram", "t.me/",
            "discord.gg", "点击领取", "優惠券", "优惠券", "googlesyndication", "pagead",
            "adsbygoogle", "joinchat", "line.me", "onlyfans", "安装app", "免費金幣",
            "签到领", "vip會員", "会员中心",
        )
        if (hints.any { it in blob }) return true
        if (title.length < 2 || title.length > 120) return true
        if (Regex("""^\d+次[点點]击""").containsMatchIn(title)) return true
        if (title == "更多" || title == "登录" || title == "註冊" || title == "注册") return true
        if (Regex("""^(ad|ads|广告|廣告|sponsor)$""", RegexOption.IGNORE_CASE).matches(title.trim())) return true
        return false
    }

    private fun jmPath(cat: String): String = when (cat) {
        "最近" -> "/albums?o=mr"
        "排行榜" -> "/albums?o=mv"
        "月排行" -> "/albums?o=mv&t=m"
        "周排行" -> "/albums?o=mv&t=w"
        "天排行" -> "/albums?o=mv&t=t"
        "韓漫", "韩漫" -> "/albums/hanman"
        "美漫", "A漫" -> "/albums/meiman"
        "同人" -> "/albums/doujin"
        "單本", "单本" -> "/albums/single"
        "短篇" -> "/albums/short"
        "Cosplay" -> "/albums/doujin_cosplay"
        "3D" -> "/albums/3d"
        "其他" -> "/albums/another"
        "推荐", "本本", "全部", "" -> "/albums"
        else -> "/search/photos?search_query=${enc(cat)}"
    }

    private fun parseJm(html: String, base: String, siteId: String): List<CrawledWork> {
        val wall = detectWall(html)
        if (wall != null) return emptyList()
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        val re = Pattern.compile(
            """href=["'][^"']*/album/(\d+)/?[^"']*["'][^>]*?(?:title=["']([^"']+)["'])?""",
            Pattern.CASE_INSENSITIVE,
        )
        val m = re.matcher(html)
        while (m.find() && out.size < LISTING_CAP) {
            val id = m.group(1) ?: continue
            if (!seen.add(id)) continue
            var title = decode(m.group(2).orEmpty())
            if (title.length < 2) {
                val around = html.substring(m.start(), (m.start() + 500).coerceAtMost(html.length))
                title = decode(Regex(""">([^<]{2,80})</""").find(around)?.groupValues?.getOrNull(1).orEmpty())
            }
            if (title.length < 2 || isAd(title, "/album/$id/")) continue
            out.add(
                CrawledWork(
                    id = "jm$id",
                    siteId = siteId,
                    title = title.take(60),
                    cover = "$JM_CDN/media/albums/${id}_3x4.jpg",
                    href = abs(base, "/album/$id/"),
                    genres = guess(title),
                ),
            )
        }
        return out
    }

    private fun ikanStatus(block: String, extra: String?): String? {
        val text = block
        return when {
            text.contains("已完结") || text.contains("完結") || text.contains("完结") -> "完结"
            text.contains("连载") || text.contains("連載") -> "连载"
            extra == "完结" || extra == "完結" -> "完结"
            extra == "连载" || extra == "連載" -> "连载"
            else -> null
        }
    }

    private fun parseIkan(html: String, base: String, siteId: String, extra: String? = null, idPrefix: String = "ik"): List<CrawledWork> {
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        val blockRe = Pattern.compile(
            """<li\b[\s\S]*?book/(\d+)[\s\S]*?</li>""",
            Pattern.CASE_INSENSITIVE,
        )
        val bm = blockRe.matcher(html)
        while (bm.find() && out.size < LISTING_CAP) {
            val id = bm.group(1) ?: continue
            if (!seen.add(id)) continue
            val block = bm.group()
            val title = decode(
                Regex("""manga-list-2-title[\s\S]*?<a[^>]*>([^<]+)</a>""", RegexOption.IGNORE_CASE)
                    .find(block)?.groupValues?.getOrNull(1)
                    ?: attr(block, "title")
                    ?: Regex(""">([^<]{2,40})</a>""").find(block)?.groupValues?.getOrNull(1)
                    ?: "",
            )
            if (title.length < 2 || isAd(title, "/book/$id")) continue
            val cover = Regex("""(?:data-original|src)\s*=\s*"([^"]+cover[^"]*)"""", RegexOption.IGNORE_CASE)
                .find(block)?.groupValues?.getOrNull(1)
                ?: "https://www.jjmhw8.top/static/upload/book/$id/cover.jpg"
            val genres = buildString {
                append(guess(title + block))
                if (!extra.isNullOrBlank()) append(" ").append(extra)
            }
            val status = ikanStatus(block, extra)
            val tagList = if (status == "连载" || status == "完结") listOf(status) else emptyList()
            out.add(
                CrawledWork(
                    id = "$idPrefix$id",
                    siteId = siteId,
                    title = title.take(60),
                    cover = abs(base, cover),
                    href = abs(base, "/book/$id"),
                    genres = genres,
                    tags = tagList,
                ),
            )
        }
        if (out.isNotEmpty()) return out
        val aRe = Pattern.compile("""href="\s*(/book/(\d+))[^"]*"[\s\S]{0,200}?>([^<]{2,60})<""", Pattern.CASE_INSENSITIVE)
        val am = aRe.matcher(html)
        while (am.find() && out.size < LISTING_CAP) {
            val id = am.group(2) ?: continue
            if (!seen.add(id)) continue
            val title = decode(am.group(3).orEmpty())
            if (title.length < 2 || isAd(title, am.group(1).orEmpty())) continue
            out.add(
                CrawledWork(
                    id = "$idPrefix$id",
                    siteId = siteId,
                    title = title.take(60),
                    cover = "https://www.jjmhw8.top/static/upload/book/$id/cover.jpg",
                    href = abs(base, am.group(1) ?: "/book/$id"),
                    genres = extra ?: guess(title),
                    tags = if (extra == "连载" || extra == "完结") listOf(extra) else emptyList(),
                ),
            )
        }
        return out
    }

    private const val BIQUGE_HOME = "https://www.biquge365.net"
    private const val BQG5_HOME = "https://www.biquges.cc"
    private const val COPY_WEB = "https://www.copy5000.com"
    private const val COPY_VERSION = "2026.08.24"
    private val COPY_BOOT = listOf("api.copy202601.com", "api.copy2000.online", "api.mangacopy.com")

    private val copyRoutes = CopyApiRoutes(COPY_BOOT)
    private val biqugeCoverCache = ConcurrentHashMap<String, String>()
    private val coverDiskLock = Any()
    private var coverDiskReady = false

    private fun ensureCoverDisk() {
        if (coverDiskReady) return
        coverDiskReady = true
        val root = LocalStore.cachePath()
        if (root.isBlank()) {
            coverDiskReady = false
            return
        }
        val file = File(root, "biquge-covers.txt")
        if (!file.exists()) return
        runCatching {
            file.readLines().forEach { line ->
                val tab = line.indexOf('\t')
                if (tab <= 0) return@forEach
                val id = line.substring(0, tab).trim()
                val url = line.substring(tab + 1).trim()
                if (id.isNotBlank() && usableBiqugeCover(url)) biqugeCoverCache.putIfAbsent(id, url)
            }
        }
    }

    private fun rememberCover(id: String, url: String) {
        if (id.isBlank() || !usableBiqugeCover(url)) return
        synchronized(coverDiskLock) {
            if (biqugeCoverCache[id] == url) return
            biqugeCoverCache[id] = url
            val root = LocalStore.cachePath()
            if (root.isBlank()) return
            val body = biqugeCoverCache.entries.joinToString("\n") { (k, v) -> "$k\t$v" }
            runCatching { File(root, "biquge-covers.txt").writeText(body) }
        }
    }

    private fun biqugeListUrl(category: String): String {
        val path = when (category) {
            "玄幻" -> "/sort/1_1/"
            "仙侠" -> "/sort/2_1/"
            "都市" -> "/sort/3_1/"
            "网游" -> "/sort/4_1/"
            "科幻" -> "/sort/5_1/"
            "灵异" -> "/sort/6_1/"
            "历史" -> "/sort/7_1/"
            "其他" -> "/sort/8_1/"
            "完本" -> "/full/"
            else -> "/"
        }
        return BIQUGE_HOME + path
    }

    private fun isBiqugeHref(href: String): Boolean = href.contains("biquge365.net", true)

    private fun biqugeBookId(href: String): String? =
        Regex("""/book/(\d+)""").find(href)?.groupValues?.get(1)
            ?: Regex("""/chapter/(\d+)/""").find(href)?.groupValues?.get(1)

    private fun copyListPath(category: String, offset: Int): String {
        val lim = 21
        return when (category) {
            "最新" -> "/api/v3/update/newest?limit=$lim&offset=$offset&platform=1"
            "日榜" -> "/api/v3/ranks?limit=$lim&offset=$offset&date_type=day&platform=1"
            "周榜" -> "/api/v3/ranks?limit=$lim&offset=$offset&date_type=week&platform=1"
            "月榜" -> "/api/v3/ranks?limit=$lim&offset=$offset&date_type=month&platform=1"
            "完结" -> "/api/v3/comics?limit=$lim&offset=$offset&ordering=-datetime_updated&top=finish&platform=1"
            else -> "/api/v3/comics?limit=$lim&offset=$offset&ordering=-popular&platform=1"
        }
    }

    private fun copyGet(url: String): JSONObject? {
        var conn: HttpURLConnection? = null
        val host = runCatching { URL(url).host }.getOrDefault("copy")
        return try {
            conn = open(url, false, 5000, 8000)
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("webp", "1")
            conn.setRequestProperty("platform", "1")
            conn.setRequestProperty("version", COPY_VERSION)
            conn.setRequestProperty("region", "0")
            conn.setRequestProperty("Referer", "$COPY_WEB/")
            val status = conn.responseCode
            if (status !in 200..299) {
                AppLog.add("copy", "$host HTTP $status")
                return null
            }
            val json = JSONObject(readBody(conn))
            if (json.optInt("code") != 200) {
                AppLog.add("copy", "$host API ${json.optInt("code")} ${json.optString("message").take(80)}")
                return null
            }
            json
        } catch (error: Exception) {
            AppLog.add("copy", "$host ${error.javaClass.simpleName}: ${friendlyError(error)}")
            null
        } finally {
            releaseConn(conn)
        }
    }

    private fun ensureCopyHosts() {
        // Discovery is a recovery step, not a prerequisite for otherwise working APIs.
        if (!copyRoutes.beginDiscovery(android.os.SystemClock.elapsedRealtime())) return
        for (host in COPY_BOOT) {
            val json = copyGet("https://$host/api/v3/system/network2?platform=1") ?: continue
            val rows = json.optJSONObject("results")?.optJSONArray("api") ?: continue
            val found = ArrayList<String>()
            for (i in 0 until rows.length()) {
                val row = rows.opt(i)
                if (row is JSONArray) {
                    for (j in 0 until row.length()) {
                        (row.opt(j) as? String)?.let { found.add(it) }
                    }
                } else if (row is String) {
                    found.add(row)
                }
            }
            if (found.isNotEmpty()) {
                copyRoutes.discovered(found)
                return
            }
        }
    }

    private fun copyApi(path: String): JSONObject? {
        val attempted = HashSet<String>()
        fun tryCandidates(): JSONObject? {
            for (host in copyRoutes.candidates()) {
                if (!attempted.add(host)) continue
                val json = copyGet("https://$host$path") ?: continue
                val results = json.opt("results")
                if (results is JSONObject || results is JSONArray) {
                    copyRoutes.succeeded(host)
                    return json
                }
                AppLog.add("copy", "$host 响应缺少有效 results")
            }
            return null
        }
        tryCandidates()?.let { return it }
        ensureCopyHosts()
        return tryCandidates()
    }

    private fun copyAuthor(o: JSONObject): String {
        val arr = o.optJSONArray("author") ?: return ""
        return (0 until arr.length()).mapNotNull { i ->
            arr.optJSONObject(i)?.optString("name")?.takeIf { it.isNotBlank() }
                ?: arr.optString(i).takeIf { it.isNotBlank() }
        }.distinct().joinToString(" / ")
    }

    private fun copyThemes(o: JSONObject): List<String> {
        val arr = o.optJSONArray("theme") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            arr.optJSONObject(i)?.optString("name")?.takeIf { it.isNotBlank() }
        }
    }

    private fun parseCopyList(json: JSONObject, siteId: String, category: String): List<CrawledWork> {
        val list = json.optJSONObject("results")?.optJSONArray("list") ?: return emptyList()
        val out = ArrayList<CrawledWork>()
        for (i in 0 until list.length()) {
            val raw = list.optJSONObject(i) ?: continue
            val comic = raw.optJSONObject("comic") ?: raw
            val path = comic.optString("path_word")
            val title = comic.optString("name").trim()
            if (path.isBlank() || title.length < 1) continue
            val cover = comic.optString("cover")
            out.add(
                CrawledWork(
                    id = "cp$path",
                    siteId = siteId,
                    title = title.take(60),
                    cover = if (cover.startsWith("http")) cover else "",
                    href = "$COPY_WEB/comic/$path",
                    author = copyAuthor(comic),
                    genres = "漫画",
                    cat = category,
                    tags = copyThemes(comic),
                ),
            )
        }
        return out
    }

    private fun fetchCopy(
        siteId: String,
        category: String,
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): Pair<List<CrawledWork>, String?> {
        val merged = LinkedHashMap<String, CrawledWork>()
        var lastErr: String? = "未解析到目录"
        for (page in 0 until 3) {
            val json = copyApi(copyListPath(category, page * 21))
            if (json == null) {
                if (merged.isEmpty()) lastErr = "无法连接拷贝漫画"
                break
            }
            val part = parseCopyList(json, siteId, category)
            if (part.isEmpty()) break
            part.forEach { merged[it.id] = it }
            onPartial?.invoke(merged.values.toList())
            val total = json.optJSONObject("results")?.optInt("total") ?: 0
            if (merged.size >= total || part.size < 21) break
        }
        if (merged.isEmpty()) return emptyList<CrawledWork>() to lastErr
        return merged.values.toList() to null
    }

    private fun searchCopy(query: String, siteId: String): List<CrawledWork> {
        val json = copyApi("/api/v3/search/comic?limit=21&offset=0&q=${enc(query)}&q_type=&platform=1") ?: return emptyList()
        return parseCopyList(json, siteId, "搜索")
    }

    private fun copyPath(href: String): String? =
        Regex("""/comic/([^/?#]+)""").find(href)?.groupValues?.get(1)

    private fun isCopyHref(href: String): Boolean =
        href.contains("copy5000.com", true) && href.contains("/comic/")

    private fun isCopyChapter(href: String): Boolean =
        isCopyHref(href) && href.contains("/chapter/")

    private fun fetchCopyWork(href: String): Pair<FetchedWork?, String?> {
        val path = copyPath(href) ?: return null to "无法打开该作品"
        val json = copyApi("/api/v3/comic2/$path?platform=1") ?: return null to "无法连接拷贝漫画"
        val results = json.optJSONObject("results") ?: return null to "未解析到目录"
        val comic = results.optJSONObject("comic") ?: return null to "未解析到目录"
        val groups = results.optJSONObject("groups")
        val chapters = ArrayList<CachedChapter>()
        if (groups != null) {
            val groupKeys = ArrayList<String>()
            if (groups.has("default")) groupKeys.add("default")
            else {
                val keys = groups.keys()
                while (keys.hasNext()) groupKeys.add(keys.next())
            }
            for (key in groupKeys) {
                val g = groups.optJSONObject(key) ?: continue
                val gpath = g.optString("path_word").ifBlank { "default" }
                val total = g.optInt("count", 0)
                var offset = 0
                while (offset < 3000) {
                    val page = copyApi("/api/v3/comic/$path/group/$gpath/chapters?limit=100&offset=$offset&platform=1")
                        ?: break
                    val list = page.optJSONObject("results")?.optJSONArray("list") ?: break
                    if (list.length() == 0) break
                    for (i in 0 until list.length()) {
                        val ch = list.optJSONObject(i) ?: continue
                        val uuid = ch.optString("uuid")
                        val name = ch.optString("name").ifBlank { "第${chapters.size + 1}话" }
                        if (uuid.isBlank()) continue
                        chapters.add(
                            CachedChapter(
                                "cp$uuid",
                                name.take(80),
                                "$COPY_WEB/comic/$path/chapter/$uuid",
                                chapters.size + 1,
                            ),
                        )
                    }
                    offset += list.length()
                    val got = page.optJSONObject("results")?.optInt("total") ?: total
                    if (offset >= got || list.length() < 100) break
                }
            }
        }
        val cover = comic.optString("cover")
        val status = comic.optJSONObject("status")?.optString("display").orEmpty()
        val tags = (copyThemes(comic) + status).filter { it.isNotBlank() }.distinct()
        return FetchedWork(
            author = copyAuthor(comic),
            tags = tags,
            cover = if (cover.startsWith("http")) cover else "",
            description = comic.optString("brief"),
            chapters = chapters,
            title = comic.optString("name"),
        ) to if (chapters.isEmpty()) "未解析到目录" else null
    }

    private fun fetchCopyPages(href: String): Pair<List<String>, String?> {
        val path = copyPath(href) ?: return emptyList<String>() to "无法打开该章节"
        val uuid = Regex("""/chapter/([^/?#]+)""").find(href)?.groupValues?.get(1)
            ?: return emptyList<String>() to "无法打开该章节"
        val json = copyApi("/api/v3/comic/$path/chapter2/$uuid?platform=1")
            ?: return emptyList<String>() to "无法连接拷贝漫画"
        val chapter = json.optJSONObject("results")?.optJSONObject("chapter")
            ?: return emptyList<String>() to "未解析到图片"
        val contents = chapter.optJSONArray("contents") ?: return emptyList<String>() to "未解析到图片"
        val words = chapter.optJSONArray("words")
        val slots = arrayOfNulls<String>(contents.length())
        for (i in 0 until contents.length()) {
            val url = contents.optJSONObject(i)?.optString("url").orEmpty()
            if (!url.startsWith("http")) continue
            val dest = if (words != null && i < words.length()) words.optInt(i, i) else i
            val at = if (dest in slots.indices) dest else i
            slots[at] = url
        }
        val pages = slots.filterNotNull()
        return if (pages.isEmpty()) emptyList<String>() to "未解析到图片" else pages to null
    }

    private fun novelHome(siteId: String, url: String): String {
        val raw = url.trim()
        if (raw.startsWith("http")) {
            val origin = runCatching {
                val u = URL(raw)
                "${u.protocol}://${u.host}"
            }.getOrNull()
            if (!origin.isNullOrBlank()) return origin.trimEnd('/')
        }
        return when (siteId) {
            Sites.BIQUGE2 -> "http://www.biquge5200.cc"
            Sites.BIQUGE3 -> "https://www.8tsw.com"
            else -> BQG5_HOME
        }
    }

    private fun novelHomeOf(href: String): String? = when {
        href.contains("bqg5.com", true) -> "https://www.bqg5.com"
        href.contains("biquges.cc", true) -> "https://www.biquges.cc"
        href.contains("biquge5200.cc", true) -> "http://www.biquge5200.cc"
        href.contains("8tsw.com", true) -> "https://www.8tsw.com"
        else -> null
    }

    private fun novelTag(home: String): String = when {
        "biquge5200" in home -> "bq52"
        "8tsw.com" in home -> "bq8"
        else -> "bq5"
    }

    private fun bqg5ListUrl(category: String, home: String = BQG5_HOME): String {
        val origin = home.trimEnd('/')
        if ("biquges.cc" in origin) {
            val path = when (category) {
                "玄幻" -> "/list1/"
                "修真", "武侠" -> "/list2/"
                "都市" -> "/list3/"
                "穿越", "历史" -> "/list4/"
                "网游" -> "/list5/"
                "科幻" -> "/list6/"
                "言情" -> "/list7/"
                "其他" -> "/list8/"
                "排行" -> "/top/"
                else -> "/"
            }
            return origin + path
        }
        val path = when (category) {
            "玄幻" -> "/xuanhuanxiaoshuo/"
            "修真" -> "/xiuzhenxiaoshuo/"
            "都市" -> "/dushixiaoshuo/"
            "穿越" -> "/chuanyuexiaoshuo/"
            "网游" -> "/wangyouxiaoshuo/"
            "科幻" -> "/kehuanxiaoshuo/"
            "其他" -> "/qitaxiaoshuo/"
            "排行" -> "/paihangbang/"
            else -> "/"
        }
        return origin + path
    }

    private fun bqg5Key(href: String): String? {
        Regex("""/(\d+)/(\d+)(?:/|$)""").find(href)?.let { return "${it.groupValues[1]}/${it.groupValues[2]}" }
        return Regex("""/(\d+_\d+)(?:/|$)""").find(href)?.groupValues?.get(1)
    }

    private fun fetchBqg5(
        siteId: String,
        category: String,
        home: String = BQG5_HOME,
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): Pair<List<CrawledWork>, String?> {
        val origin = home.trimEnd('/')
        val (html, err, finalUrl) = get(bqg5ListUrl(category, origin))
        if (err != null) return emptyList<CrawledWork>() to err
        val items = parseBqg5List(html, finalUrl, siteId, category, origin)
        if (items.isEmpty()) return emptyList<CrawledWork>() to "未解析到目录"
        onPartial?.invoke(items)
        return items to null
    }

    private fun searchBqg5(query: String, siteId: String, home: String = BQG5_HOME): List<CrawledWork> {
        val origin = home.trimEnd('/')
        val url = if ("biquges.cc" in origin) {
            "$origin/search.php?q=${enc(query)}"
        } else {
            val key = if ("biquge5200" in origin) enc(query) else gbkQuery(query)
            "$origin/modules/article/search.php?searchkey=$key&searchtype=articlename"
        }
        val (html, err, finalUrl) = get(url)
        if (err != null && html.isBlank()) return emptyList()
        val listed = parseBqg5List(html, finalUrl.ifBlank { "$origin/" }, siteId, "搜索", origin)
        if (listed.isNotEmpty()) return listed
        val book = bqg5Key(finalUrl) ?: return emptyList()
        val local = book.replace("/", "_")
        val title = metaContent(html, "og:novel:book_name").ifBlank { return emptyList() }
        val cover = biqugeCoverFrom(html, finalUrl.ifBlank { "$origin/" })
        return listOf(
            CrawledWork(
                id = "${novelTag(origin)}$local",
                siteId = siteId,
                title = tidyBookTitle(title).take(60),
                cover = if (cover.startsWith("http")) cover else "",
                href = "$origin/$book/",
                author = metaContent(html, "og:novel:author"),
                genres = "小说",
                cat = "搜索",
            ),
        )
    }

    private fun gbkQuery(raw: String): String {
        val bytes = raw.toByteArray(Charset.forName("GBK"))
        return bytes.joinToString("") { b ->
            val v = b.toInt() and 0xff
            val plain = v in 48..57 || v in 65..90 || v in 97..122
            if (plain) v.toChar().toString() else "%${v.toString(16).padStart(2, '0')}"
        }
    }

    private fun parseBqg5List(html: String, base: String, siteId: String, category: String, home: String = BQG5_HOME): List<CrawledWork> {
        if (detectWall(html) != null) return emptyList()
        val origin = home.trimEnd('/')
        val tag = novelTag(origin)
        class Acc(var title: String = "", var cover: String = "", var author: String = "", var path: String = "")
        val map = LinkedHashMap<String, Acc>()
        val link = """(?:https?://[^"'/]+)?/?(\d+_\d+)/?"""
        val slash = """(?:https?://[^"'/]+)?/(\d+)/(\d+)/"""
        val imgRe = Pattern.compile(
            """<a[^>]+href=["']$link["'][^>]*>\s*(<img\b[^>]*>)""",
            Pattern.CASE_INSENSITIVE,
        )
        val im = imgRe.matcher(html)
        while (im.find() && map.size < 240) {
            val id = im.group(1) ?: continue
            val src = coverImageSource(im.group(2) ?: continue) ?: continue
            if (src.startsWith("data:") || src.contains("logo", true) || src.contains("nocover", true)) continue
            val acc = map.getOrPut(id) { Acc(path = id) }
            if (acc.path.isBlank()) acc.path = id
            if (acc.cover.isBlank()) acc.cover = abs(base, src)
        }
        val slashImg = Pattern.compile(
            """<a[^>]+href=["']$slash["'][^>]*>\s*(<img\b[^>]*>)""",
            Pattern.CASE_INSENSITIVE,
        ).matcher(html)
        while (slashImg.find() && map.size < 240) {
            val a = slashImg.group(1) ?: continue
            val b = slashImg.group(2) ?: continue
            val src = coverImageSource(slashImg.group(3) ?: continue) ?: continue
            if (src.startsWith("data:") || src.contains("logo", true) || src.contains("nocover", true)) continue
            val id = "${a}_$b"
            val acc = map.getOrPut(id) { Acc(path = "$a/$b") }
            if (acc.path.isBlank()) acc.path = "$a/$b"
            if (acc.cover.isBlank()) acc.cover = abs(base, src)
        }
        val textRe = Pattern.compile(
            """<a[^>]+href=["']$link["'][^>]*>\s*([^<]{1,40})</a>""",
            Pattern.CASE_INSENSITIVE,
        )
        val tm = textRe.matcher(html)
        while (tm.find() && map.size < 400) {
            val id = tm.group(1) ?: continue
            val title = tidyBookTitle(tm.group(2) ?: "")
            if (title.length < 2) continue
            val acc = map.getOrPut(id) { Acc(path = id) }
            if (acc.path.isBlank()) acc.path = id
            if (acc.title.isBlank()) acc.title = title
        }
        val slashText = Pattern.compile(
            """<a[^>]+href=["']$slash["'][^>]*>(?:\s*<img[^>]*>\s*)?([^<]{1,40})</a>""",
            Pattern.CASE_INSENSITIVE,
        ).matcher(html)
        while (slashText.find() && map.size < 400) {
            val a = slashText.group(1) ?: continue
            val b = slashText.group(2) ?: continue
            val title = tidyBookTitle(slashText.group(3) ?: "")
            if (title.length < 2) continue
            val id = "${a}_$b"
            val acc = map.getOrPut(id) { Acc(path = "$a/$b") }
            if (acc.path.isBlank()) acc.path = "$a/$b"
            if (acc.title.isBlank()) acc.title = title
        }
        val authorRe = Pattern.compile(
            """<a[^>]+href=["']$link["'][^>]*>\s*([^<]{1,40})</a>\s*</span>\s*<span class=["']s5["']>([^<]{1,40})</span>""",
            Pattern.CASE_INSENSITIVE,
        )
        val am = authorRe.matcher(html)
        while (am.find()) {
            val id = am.group(1) ?: continue
            val acc = map[id] ?: continue
            if (acc.author.isBlank()) acc.author = decode(am.group(3) ?: "").trim()
        }
        val stamp = if (category.isBlank() || category == "推荐" || category == "搜索") "" else category
        return map.mapNotNull { (id, acc) ->
            val title = acc.title.trim()
            if (title.length < 2) return@mapNotNull null
            CrawledWork(
                id = "$tag$id",
                siteId = siteId,
                title = title.take(60),
                cover = acc.cover,
                href = "$origin/${acc.path.ifBlank { id }}/",
                author = acc.author,
                genres = "小说",
                cat = category.ifBlank { "推荐" },
                tags = if (stamp.isBlank()) emptyList() else listOf(stamp),
            )
        }
    }

    private fun fetchBqg5Work(href: String, home: String = BQG5_HOME): Pair<FetchedWork?, String?> {
        val key = bqg5Key(href) ?: return null to "无法打开该作品"
        val origin = home.trimEnd('/')
        val bookUrl = "$origin/$key/"
        val (html, err, _) = get(bookUrl)
        if (err != null) return null to err
        val chapters = parseBqg5Toc(html, bookUrl)
        val title = metaContent(html, "og:novel:book_name").ifBlank { metaContent(html, "og:title") }
        val author = metaContent(html, "og:novel:author")
        val cover = biqugeCoverFrom(html, bookUrl)
        val desc = metaContent(html, "og:description").ifBlank { metaContent(html, "description") }
        val category = metaContent(html, "og:novel:category")
        val status = metaContent(html, "og:novel:status")
        val tags = listOf(category, status).map { it.trim() }.filter { it.isNotBlank() }.distinct()
        if (chapters.isEmpty()) return FetchedWork(author, tags, cover, desc, emptyList(), title) to "未解析到目录"
        return FetchedWork(author, tags, cover, desc, chapters, title) to null
    }

    private fun parseBqg5Toc(html: String, base: String): List<CachedChapter> {
        val marker = html.indexOf("id=\"list\"")
        val region = if (marker >= 0) html.substring(marker) else html
        val key = bqg5Key(base)
        val out = ArrayList<CachedChapter>()
        val seen = HashSet<String>()
        val patterns = listOf(
            """<a[^>]+href=["']([^"']*?/(\d+_\d+)/(\d+)\.html)["'][^>]*>([\s\S]*?)</a>""" to false,
            """<a[^>]+href=["']([^"']*?/(\d+)/(\d+)/(\d+)\.html)["'][^>]*>([\s\S]*?)</a>""" to true,
        )
        for ((src, slash) in patterns) {
            val m = Pattern.compile(src, Pattern.CASE_INSENSITIVE).matcher(region)
            while (m.find() && out.size < 4000) {
                val href = abs(base, m.group(1) ?: continue)
                if (key != null && !href.contains("/$key/")) continue
                val cid = if (slash) m.group(4) else m.group(3)
                val raw = if (slash) m.group(5) else m.group(4)
                val title = tidyBookTitle(raw.orEmpty().replace(Regex("<[^>]+>"), " "))
                if (cid.isNullOrBlank() || title.isBlank()) continue
                if (!seen.add(href)) continue
                out.add(CachedChapter("c$cid", title.take(80), href, out.size + 1))
            }
        }
        return out
    }

    private fun parseBqg5Text(html: String): String {
        val content = Pattern.compile("""id=["']content["'][^>]*>([\s\S]*?)</div>""", Pattern.CASE_INSENSITIVE).matcher(html)
        val article = Pattern.compile("""<article[^>]*>([\s\S]*?)</article>""", Pattern.CASE_INSENSITIVE).matcher(html)
        val body = when {
            content.find() -> content.group(1) ?: return ""
            article.find() -> article.group(1) ?: return ""
            else -> return ""
        }
        var text = body
        text = text.replace(Regex("""<script[\s\S]*?</script>""", RegexOption.IGNORE_CASE), "")
        text = text.replace(Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE), "\n")
        text = text.replace(Regex("""</p>""", RegexOption.IGNORE_CASE), "\n")
        text = text.replace(Regex("""<[^>]+>"""), "")
        text = novelDecode(text)
        val lines = text.split('\n').map { it.trim() }.filter { keepNovelLine(it) && !it.matches(Regex("""第\(\d+/\d+\)页""")) }
        return lines.joinToString("\n\n") { "　　$it" }
    }

    private fun fetchBiquge(
        siteId: String,
        category: String,
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): Pair<List<CrawledWork>, String?> {
        ensureCoverDisk()
        val (html, err, finalUrl) = get(biqugeListUrl(category))
        if (err != null) return emptyList<CrawledWork>() to err
        val items = parseBiqugeList(html, finalUrl, siteId, category)
        if (items.isEmpty()) return emptyList<CrawledWork>() to "未解析到目录"
        onPartial?.invoke(items)
        return items to null
    }

    private fun fetchBiqugeWork(href: String): Pair<FetchedWork?, String?> {
        val bookId = biqugeBookId(href) ?: return null to "无法打开该作品"
        val bookUrl = "$BIQUGE_HOME/book/$bookId/"
        val (html, err, _) = get(bookUrl)
        if (err != null) return null to err
        val (tocHtml, tocErr, _) = get("$BIQUGE_HOME/newbook/$bookId/")
        if (tocErr != null) return null to tocErr
        val chapters = parseBiqugeToc(tocHtml, "$BIQUGE_HOME/newbook/$bookId/")
        val title = metaContent(html, "og:novel:book_name").ifBlank { metaContent(html, "og:title") }
        val author = metaContent(html, "og:novel:author")
        val cover = biqugeCoverFrom(html, bookUrl)
        if (usableBiqugeCover(cover)) rememberCover(bookId, cover)
        val desc = metaContent(html, "og:description").ifBlank { metaContent(html, "description") }
        val category = metaContent(html, "og:novel:category")
        val status = metaContent(html, "og:novel:status")
        val tags = listOf(category, status).map { it.trim() }.filter { it.isNotBlank() }.distinct()
        if (chapters.isEmpty()) return FetchedWork(author, tags, cover, desc, emptyList(), title) to "未解析到目录"
        return FetchedWork(author, tags, cover, desc, chapters, title) to null
    }

    fun fetchNovel(href: String): Pair<String, String?> {
        if(XiuSources.sourceId(href)!=null)return XiuSources.text(href)
        if (href.contains("#smyLegado=")) return "" to "该合集已移除"
        if (href.contains("#smySource=")) return "" to "该小说源已移除"
        if (href.isBlank() || !isSafe(href)) return "" to "无法打开该章节"
        return try {
            val (html, err, _) = get(href)
            if (err != null) "" to err
            else {
                val text = if (novelHomeOf(href) != null) parseBqg5Text(html) else ""
                if (text.isBlank()) "" to "未解析到正文" else text to null
            }
        } catch (e: Exception) {
            "" to friendlyError(e)
        }
    }

    fun fillMissingCovers(
        items: List<CrawledWork>,
        onPartial: ((List<CrawledWork>) -> Unit)? = null,
    ): List<CrawledWork> {
        if (items.isEmpty()) return items
        // Show list-provided covers before fetching missing detail-page metadata.
        onPartial?.invoke(items)
        prefetchCovers(items.take(8).map { it.cover })
        val out = items.toMutableList()
        val jobs = ArrayList<Int>()
        for (i in out.indices) {
            val item = out[i]
            if (item.cover.isNotBlank() || !isSafe(item.href)) continue
            jobs.add(i)
            if (jobs.size >= 24) break
        }
        if (jobs.isEmpty()) return out
        val pool = Executors.newFixedThreadPool(minOf(6, jobs.size))
        val latch = CountDownLatch(jobs.size)
        for (index in jobs) {
            val href = out[index].href
            pool.execute {
                try {
                    val cover = peekPageCover(href)
                    if (cover.isBlank()) return@execute
                    val snap = synchronized(out) {
                        val cur = out[index]
                        if (cur.cover.isNotBlank()) return@synchronized null
                        out[index] = cur.copy(cover = cover)
                        ArrayList(out)
                    }
                    if (snap != null) {
                        prefetchCovers(listOf(cover))
                        onPartial?.invoke(snap)
                    }
                } catch (_: Exception) {
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await(20, TimeUnit.SECONDS)
        pool.shutdownNow()
        return synchronized(out) { ArrayList(out) }
    }

    private fun peekPageCover(pageUrl: String): String {
        if (!isSafe(pageUrl)) return ""
        val id = bqg5Key(pageUrl) ?: biqugeBookId(pageUrl) ?: pageUrl
        biqugeCoverCache[id]?.takeIf { usableBiqugeCover(it) }?.let { return it }
        var conn: HttpURLConnection? = null
        return try {
            conn = open(pageUrl, false, connectMs = 5000, readMs = 5000)
            val origin = runCatching {
                val u = URL(pageUrl)
                "${u.protocol}://${u.host}/"
            }.getOrDefault(pageUrl)
            conn.setRequestProperty("Referer", origin)
            conn.setRequestProperty("Accept-Encoding", "identity")
            conn.setRequestProperty("Connection", "close")
            if (conn.responseCode !in 200..299) return ""
            val head = readNovelCoverHead(conn)
            val cover = biqugeCoverFrom(head, pageUrl)
            if (!usableBiqugeCover(cover)) return ""
            rememberCover(id, cover)
            cover
        } catch (_: Exception) {
            ""
        } finally {
            runCatching { conn?.disconnect() }
        }
    }

    private fun readNovelCoverHead(conn: HttpURLConnection): String {
        val raw = BufferedInputStream(conn.inputStream)
        raw.mark(4)
        val b1 = raw.read()
        val b2 = raw.read()
        raw.reset()
        val stream: InputStream = if (b1 == 0x1f && b2 == 0x8b) GZIPInputStream(raw) else raw
        val acc = ByteArrayOutputStream(4096)
        val buf = ByteArray(512)
        while (acc.size() < 6144) {
            val n = stream.read(buf)
            if (n < 0) break
            acc.write(buf, 0, n)
            val text = acc.toString(Charsets.UTF_8.name())
            val at = text.indexOf("og:image")
            if (at >= 0 && text.indexOf('>', at) >= 0) break
            if (text.contains("</head>")) break
        }
        return acc.toString(Charsets.UTF_8.name())
    }

    private fun usableBiqugeCover(src: String): Boolean {
        val low = src.lowercase()
        if (low.isBlank() || low.startsWith("data:")) return false
        if (low.contains("logo") || low.contains("zanwu") || low.contains("/style/")) return false
        if (low.contains("///") || low.endsWith("/s.jpg")) return false
        return true
    }

    private fun biqugeCoverFrom(html: String, pageUrl: String): String {
        val og = metaContent(html, "og:image")
        if (usableBiqugeCover(og)) return abs(pageUrl, og)
        val m = Pattern.compile(
            """\s(?:data-original|data-src|src)=["']([^"']+\.(?:jpg|jpeg|png|webp)[^"']*)["']""",
            Pattern.CASE_INSENSITIVE,
        ).matcher(html)
        while (m.find()) {
            val src = m.group(1) ?: continue
            val absSrc = abs(pageUrl, src)
            if (usableBiqugeCover(absSrc)) return absSrc
        }
        return ""
    }

    fun parseBiqugeList(html: String, base: String, siteId: String, category: String): List<CrawledWork> {
        val wall = detectWall(html)
        if (wall != null) return emptyList()
        class Acc(var title: String = "", var cover: String = "")
        val map = LinkedHashMap<String, Acc>()
        val imgRe = Pattern.compile(
            """<a[^>]+href=["']([^"']*?/(?:newbook|book)/(\d+)/?)["'][^>]*>\s*(<img\b[^>]*>)""",
            Pattern.CASE_INSENSITIVE,
        )
        val im = imgRe.matcher(html)
        while (im.find() && map.size < 240) {
            val id = im.group(2) ?: continue
            val src = coverImageSource(im.group(3) ?: continue) ?: continue
            if (!usableBiqugeCover(src)) continue
            val acc = map.getOrPut(id) { Acc() }
            if (acc.cover.isBlank()) acc.cover = abs(base, src)
            biqugeBookId(abs(base, im.group(1) ?: ""))?.let { id ->
                if (usableBiqugeCover(acc.cover)) rememberCover(id, acc.cover)
            }
            val alt = tidyBookTitle(
                Regex("""alt=["']([^"']*)["']""", RegexOption.IGNORE_CASE).find(im.group())?.groupValues?.getOrNull(1).orEmpty(),
            )
            if (acc.title.isBlank() && alt.length >= 2) acc.title = alt
        }
        val textRe = Pattern.compile(
            """<a[^>]+href=["']([^"']*?/book/(\d+)/?)["'][^>]*>\s*([^<]{1,40})</a>""",
            Pattern.CASE_INSENSITIVE,
        )
        val tm = textRe.matcher(html)
        while (tm.find() && map.size < 240) {
            val id = tm.group(2) ?: continue
            val title = tidyBookTitle(tm.group(3).orEmpty())
            if (title.length < 2) continue
            val acc = map.getOrPut(id) { Acc() }
            if (acc.title.isBlank() || acc.title.endsWith("列表")) acc.title = title
        }
        val stamp = category.takeUnless { it.isBlank() || it == "推荐" || it == "推薦" || it == "全部" || it == "搜索" }
        return map.mapNotNull { (id, acc) ->
            val title = acc.title.trim()
            if (title.length < 2) return@mapNotNull null
            CrawledWork(
                id = "bq$id",
                siteId = siteId,
                title = title.take(60),
                cover = acc.cover.ifBlank { biqugeCoverCache[id].orEmpty() },
                href = "$BIQUGE_HOME/book/$id/",
                author = "",
                genres = stamp ?: "小说",
                cat = if (category.isBlank()) "推荐" else category,
                tags = if (stamp == null) emptyList() else listOf(stamp),
            )
        }
    }

    fun parseBiqugeToc(html: String, base: String): List<CachedChapter> {
        val marker = html.indexOf("全部章节")
        val region = if (marker >= 0) {
            val ul = html.indexOf("<ul", marker)
            val ulEnd = if (ul >= 0) html.indexOf("</ul>", ul) else -1
            if (ul >= 0 && ulEnd > ul) html.substring(ul, ulEnd) else html.substring(marker)
        } else html
        val out = ArrayList<CachedChapter>()
        val seen = HashSet<String>()
        val re = Pattern.compile(
            """<a[^>]+href=["']([^"']*?/chapter/\d+/(\d+)\.html)["'][^>]*>([\s\S]*?)</a>""",
            Pattern.CASE_INSENSITIVE,
        )
        val m = re.matcher(region)
        while (m.find() && out.size < 4000) {
            val href = abs(base, m.group(1) ?: continue)
            val cid = m.group(2) ?: continue
            val title = decode(m.group(3).orEmpty().replace(Regex("<[^>]+>"), " "))
            if (title.isBlank() || title == "开始阅读") continue
            if (!seen.add(href)) continue
            out.add(CachedChapter("c$cid", title.take(80), href, out.size + 1))
        }
        return out
    }

    fun parseBiqugeText(html: String): String {
        val m = Pattern.compile("""id=["']txt["'][^>]*>([\s\S]*?)</div>""", Pattern.CASE_INSENSITIVE).matcher(html)
        if (!m.find()) return ""
        var body = m.group(1) ?: return ""
        body = body.replace(Regex("""<script[\s\S]*?</script>""", RegexOption.IGNORE_CASE), "")
        body = body.replace(Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE), "\n")
        body = body.replace(Regex("""</p>""", RegexOption.IGNORE_CASE), "\n")
        body = body.replace(Regex("""<[^>]+>"""), "")
        body = novelDecode(body)
        val lines = body.split('\n').map { it.trim() }.filter { keepNovelLine(it) }
        return lines.joinToString("\n\n") { "　　$it" }
    }

    private fun keepNovelLine(line: String): Boolean {
        if (line.isBlank()) return false
        val t = line.lowercase()
        if ("biquge" in t || "bqg5" in t || "b520" in t || "www." in t || t.startsWith("http")) return false
        if ("笔趣阁" in line || "一秒记住" in line || "请收藏" in line || "最新章节" in line) return false
        if ("手机用户" in line && "阅读" in line) return false
        return true
    }

    private fun tidyBookTitle(raw: String): String {
        var t = decode(raw)
        t = t.removeSuffix("文章列表").removeSuffix("章节列表").removeSuffix("最新章节").trim()
        if (t.startsWith("《") && t.endsWith("》") && t.length > 2) t = t.substring(1, t.length - 1)
        return t.trim()
    }

    private fun novelDecode(s: String): String {
        var t = s
        t = t.replace("&" + "amp;", "&")
        t = t.replace("&" + "quot;", "\"")
        t = t.replace("&#39;", "'")
        t = t.replace("&" + "apos;", "'")
        t = t.replace("&" + "lt;", "<")
        t = t.replace("&" + "gt;", ">")
        t = t.replace("&" + "nbsp;", " ")
        t = t.replace(Regex("&#(\\d+);")) { m ->
            m.groupValues[1].toIntOrNull()?.toChar()?.toString() ?: m.value
        }
        t = t.replace(Regex("&#x([0-9a-fA-F]+);")) { m ->
            m.groupValues[1].toIntOrNull(16)?.toChar()?.toString() ?: m.value
        }
        return t.replace('\u00a0', ' ').replace(Regex("[ \\t\\u3000]+"), " ")
    }

    private fun metaContent(html: String, prop: String): String {
        val q = Pattern.quote(prop)
        val a = Pattern.compile(
            """<meta[^>]*(?:property|name)\s*=\s*["']$q["'][^>]*content\s*=\s*["']([^"']*)["']""",
            Pattern.CASE_INSENSITIVE,
        ).matcher(html)
        if (a.find()) return decode(a.group(1) ?: "")
        val b = Pattern.compile(
            """<meta[^>]*content\s*=\s*["']([^"']*)["'][^>]*(?:property|name)\s*=\s*["']$q["']""",
            Pattern.CASE_INSENSITIVE,
        ).matcher(html)
        return if (b.find()) decode(b.group(1) ?: "") else ""
    }

    private fun postForm(url: String, fields: Map<String, String>): Triple<String, String?, String> {
        var conn: HttpURLConnection? = null
        return try {
            conn = open(url, false)
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            conn.setRequestProperty("Referer", "$BIQUGE_HOME/")
            val body = fields.entries.joinToString("&") { enc(it.key) + "=" + enc(it.value) }
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val finalUrl = conn.url?.toString() ?: url
            if (code !in 200..299) return Triple("", "抓取失败 $code", finalUrl)
            val html = readBody(conn)
            if (html.contains("搜索间隔")) Triple(html, "搜索太频繁，请稍后再试", finalUrl)
            else Triple(html, null, finalUrl)
        } catch (e: Exception) {
            Triple("", friendlyError(e), url)
        } finally {
            conn?.disconnect()
        }
    }

    fun parseGeneric(html: String, base: String, siteId: String): List<CrawledWork> {
        val wall = detectWall(html)
        if (wall != null) return emptyList()
        val out = ArrayList<CrawledWork>()
        val seen = HashSet<String>()
        val re = Pattern.compile("<a\\b([^>]*href\\s*=\\s*[\"']([^\"']+)[\"'][^>]*)>([\\s\\S]*?)</a>", Pattern.CASE_INSENSITIVE)
        val m = re.matcher(html)
        while (m.find() && out.size < LISTING_CAP) {
            val hrefRaw = m.group(2) ?: continue
            val inner = m.group(3) ?: continue
            if (hrefRaw.startsWith("javascript:") || hrefRaw == "#") continue
            if ("googlesyndication" in hrefRaw || "/ads" in hrefRaw.lowercase()) continue
            val imgM = Pattern.compile("<img\\b[^>]*>", Pattern.CASE_INSENSITIVE).matcher(inner)
            if (!imgM.find()) continue
            val img = imgM.group()
            val src = coverImageSource(img) ?: continue
            if (src.startsWith("data:")) continue
            val alt = attr(img, "alt").orEmpty()
            val text = inner.replace(Regex("<[^>]+>"), " ").replace(Regex("\\s+"), " ").trim()
            val title = (alt.ifBlank { text }).take(60)
            if (title.length < 2 || isAd(title, hrefRaw)) continue
            val href = abs(base, hrefRaw)
            val comic = Regex("""/(album|photo|book|comic|manga|manhua|manhwa|chapter)/\d+""", RegexOption.IGNORE_CASE).containsMatchIn(href)
            if (!comic && out.size > 6) continue
            if (!seen.add(href)) continue
            out.add(
                CrawledWork(
                    id = "w" + href.hashCode().toUInt().toString(16),
                    siteId = siteId,
                    title = title,
                    cover = abs(base, src),
                    href = href,
                    genres = guess(title),
                ),
            )
        }
        return out
    }

    fun parseChapters(html: String, base: String): List<CachedChapter> {
        val out = ArrayList<CachedChapter>()
        val seen = HashSet<String>()
        val re = Pattern.compile(
            """<a[^>]+href=["']([^"'#]+)["'][^>]*>([\s\S]*?)</a>""",
            Pattern.CASE_INSENSITIVE,
        )
        val m = re.matcher(html)
        val look = Regex("""第\s*\d+\s*[话話章回]|chapter\s*\d+|ch\.\s*\d+|話|章節|章节""", RegexOption.IGNORE_CASE)
        while (m.find() && out.size < 400) {
            val hrefRaw = m.group(1) ?: continue
            if (hrefRaw.startsWith("javascript:") || hrefRaw.startsWith("mailto:")) continue
            val title = decode(m.group(2).orEmpty().replace(Regex("<[^>]+>"), " "))
            val href = abs(base, hrefRaw)
            val path = href.lowercase()
            val isChapter = look.containsMatchIn(title) ||
                "/chapter" in path || "/photo/" in path ||
                Regex("""/(ch|ep|episode|view)/""", RegexOption.IGNORE_CASE).containsMatchIn(path)
            if (!isChapter || title.length < 1) continue
            if (!seen.add(href)) continue
            out.add(CachedChapter("ch${out.size + 1}", title.take(80).ifBlank { "第 ${out.size + 1} 话" }, href, out.size + 1))
        }
        return out
    }

    fun parsePages(html: String, base: String): List<String> {
        val out = ArrayList<String>()
        val seen = HashSet<String>()
        val re = Pattern.compile("<img\\b[^>]*>", Pattern.CASE_INSENSITIVE)
        val m = re.matcher(html)
        while (m.find() && out.size < 400) {
            val tag = m.group()
            val src = attr(tag, "data-original") ?: attr(tag, "data-src") ?: attr(tag, "data-lazy") ?: attr(tag, "src") ?: continue
            if (src.startsWith("data:") || src.endsWith(".svg")) continue
            val low = src.lowercase()
            if (listOf("logo", "avatar", "icon", "sprite", "loading", "load.gif", "banner", "ads", "pixel", "blank", "spacer", "placeholder", "1x1", "thumb", "small").any { it in low }) continue
            if (!listOf(".jpg", ".jpeg", ".png", ".webp", ".gif", "/media/", "/upload/", "comic").any { it in low }) continue
            val absSrc = abs(base, src)
            if (seen.add(absSrc)) out.add(absSrc)
        }
        return out
    }

    private fun get(url: String): Triple<String, String?, String> {
        fun once(lenient: Boolean) = getOnce(url, lenient)
        val first = runCatching { once(false) }.getOrElse { e ->
            if (!isCert(e)) return Triple("", friendlyError(e), url)
            return runCatching { once(true) }.getOrElse { e2 -> Triple("", friendlyError(e2), url) }
        }
        if (first.second == null) return first
        val retryable = first.second!!.let { err ->
            err.contains("超时") || err.contains("无法连接") || err.startsWith("抓取失败 5") || err.startsWith("抓取失败 429")
        }
        if (!retryable) return first
        return runCatching { once(false) }.getOrNull()?.takeIf { it.second == null } ?: first
    }

    private fun getOnce(url: String, lenient: Boolean): Triple<String, String?, String> {
        var conn: HttpURLConnection? = null
        return try {
            conn = open(url, lenient)
            val code = conn.responseCode
            val finalUrl = conn.url.toString()
            if (code !in 200..299) return Triple("", "抓取失败 $code", finalUrl)
            val html = readBody(conn)
            if (html.length > 2_500_000) return Triple("", "页面过大", finalUrl)
            val wall = detectWall(html)
            if (wall != null) Triple(html, wall, finalUrl) else Triple(html, null, finalUrl)
        } finally {
            releaseConn(conn)
        }
    }

    private fun open(url: String, lenient: Boolean, connectMs: Int = 10000, readMs: Int = 14000): HttpURLConnection {
        val conn = URL(url).openConnection() as HttpURLConnection
        if (lenient && conn is HttpsURLConnection) {
            conn.sslSocketFactory = lenientFactory
            conn.hostnameVerifier = lenientHost
        }
        conn.connectTimeout = connectMs
        conn.readTimeout = readMs
        conn.instanceFollowRedirects = true
        conn.setRequestProperty("User-Agent", UA)
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
        conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
        conn.setRequestProperty("Upgrade-Insecure-Requests", "1")
        conn.setRequestProperty("Connection", "keep-alive")
        conn.setRequestProperty("Sec-Fetch-Dest", "document")
        conn.setRequestProperty("Sec-Fetch-Mode", "navigate")
        conn.setRequestProperty("Sec-Fetch-Site", "none")
        conn.setRequestProperty("Sec-Fetch-User", "?1")
        conn.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"153\", \"Google Chrome\";v=\"153\", \"Not)A;Brand\";v=\"24\"")
        conn.setRequestProperty("sec-ch-ua-mobile", "?1")
        conn.setRequestProperty("sec-ch-ua-platform", "\"Android\"")
        return conn
    }

    private fun readBody(conn: HttpURLConnection): String {
        val bytes = bodyStream(conn).use { readCapped(it, max = 2_600_000, strict = false) } ?: ByteArray(0)
        return decodeHtml(bytes, conn.contentType)
    }

    private fun bodyStream(conn: HttpURLConnection): InputStream {
        val raw = BufferedInputStream(conn.inputStream)
        raw.mark(2)
        val b1 = raw.read()
        val b2 = raw.read()
        raw.reset()
        return if (b1 == 0x1f && b2 == 0x8b) GZIPInputStream(raw) else raw
    }

    private fun decodeHtml(bytes: ByteArray, contentType: String?): String {
        val headerCs = Regex("""charset=([\w-]+)""", RegexOption.IGNORE_CASE).find(contentType ?: "")?.groupValues?.getOrNull(1)
        val sniffLen = minOf(bytes.size, 1500)
        val sniff = if (sniffLen == 0) "" else String(bytes, 0, sniffLen, Charsets.ISO_8859_1)
        val metaCs = Regex("""charset\s*=\s*["']?([\w-]+)""", RegexOption.IGNORE_CASE).find(sniff)?.groupValues?.getOrNull(1)
        val name = (headerCs ?: metaCs ?: "utf-8").lowercase()
        val charset = when {
            "gb" in name -> Charset.forName("GBK")
            "utf" in name -> Charsets.UTF_8
            else -> runCatching { Charset.forName(name) }.getOrDefault(Charsets.UTF_8)
        }
        return String(bytes, charset)
    }

    private fun releaseConn(conn: HttpURLConnection?) {
        if (conn == null) return
        runCatching { conn.errorStream?.close() }
    }

    private fun isCert(e: Throwable): Boolean {
        var t: Throwable? = e
        while (t != null) {
            val n = t.javaClass.simpleName + (t.message ?: "")
            if (n.contains("CertPath", true) || n.contains("TrustAnchor", true) ||
                n.contains("SSLHandshake", true) || n.contains("Validator", true)
            ) return true
            t = t.cause
        }
        return false
    }

    private fun detectWall(html: String): String? {
        val t = html.lowercase()
        if ("cf-mitigated" in t || "just a moment" in t || "challenge-platform" in t || "cdn-cgi/challenge" in t) {
            return "源站开启了防护，稍后再试"
        }
        if ("the region has been denied" in t) return "当前地区无法访问该源"
        return null
    }

    private fun attr(tag: String, name: String): String? {
        val m = Pattern.compile("$name\\s*=\\s*[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE).matcher(tag)
        return if (m.find()) m.group(1) else null
    }

    private fun abs(base: String, maybe: String): String {
        val t = maybe.trim()
        if (t.startsWith("http")) return t
        if (t.startsWith("//")) return "https:$t"
        return try {
            URL(URL(base), t).toString()
        } catch (_: Exception) {
            t
        }
    }

    private fun decode(s: String): String {
        var t = s
        t = t.replace("&" + "amp;", "&")
        t = t.replace("&" + "quot;", "\"")
        t = t.replace("&#39;", "'")
        t = t.replace("&" + "apos;", "'")
        t = t.replace("&" + "lt;", "<")
        t = t.replace("&" + "gt;", ">")
        t = t.replace("&" + "nbsp;", " ")
        t = t.replace("&" + "hearts;", "♥")
        t = t.replace(Regex("&#(\\d+);")) { m ->
            m.groupValues[1].toIntOrNull()?.toChar()?.toString() ?: m.value
        }
        t = t.replace(Regex("&#x([0-9a-fA-F]+);")) { m ->
            m.groupValues[1].toIntOrNull(16)?.toChar()?.toString() ?: m.value
        }
        return t.replace(Regex("\\s+"), " ").trim()
    }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    private fun guess(title: String): String {
        val t = title.lowercase()
        return when {
            t.contains("冒险") -> "冒险"
            t.contains("百合") -> "百合"
            t.contains("日常") || t.contains("生活") -> "生活"
            t.contains("四格") -> "四格"
            t.contains("悬疑") -> "悬疑"
            t.contains("后宫") || t.contains("後宮") -> "后宫"
            t.contains("热血") || t.contains("熱血") -> "热血"
            t.contains("耽美") || t.contains("bl") -> "耽美"
            t.contains("恐怖") || t.contains("鬼") -> "恐怖"
            t.contains("韩漫") || t.contains("韓漫") -> "韩漫"
            t.contains("校园") || t.contains("校園") -> "校园"
            else -> "其他"
        }
    }


}
