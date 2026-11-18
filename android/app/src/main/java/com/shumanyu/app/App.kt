package com.shumanyu.app

import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.unit.IntRect
import androidx.compose.foundation.selection.selectable

import com.itos.coui.shape.CouiCapsuleShape

import com.itos.coui.effect.couiButtonFeedback

import androidx.compose.foundation.border

import com.itos.coui.shape.CouiShape

import com.itos.coui.topappbar.TopAppBarSurface

import com.itos.coui.text.Text as CouiText

import com.itos.coui.text.CouiTextKind

import com.itos.coui.layout.Item

import com.itos.coui.checkbox.Checkbox
import com.itos.coui.radiobutton.RadioButton

import com.itos.coui.theme.CouiTheme

import com.itos.coui.dialog.CouiDialogPlacement

import com.itos.coui.dialog.CouiDialogAction

import com.itos.coui.dialog.CouiDialogActionStyle

import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.scroll.rememberCouiOverscrollEffect

import com.itos.coui.scroll.couiVerticalScroll

import com.itos.coui.layout.Group

import com.itos.coui.icons.CouiIcons
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.menu.CouiMenuItem
import com.itos.coui.menu.Menu
import com.itos.coui.menu.couiMenuAnchor
import com.itos.coui.menu.rememberCouiMenuAnchor
import com.itos.coui.navigation.CouiNavigationItem
import com.itos.coui.navigation.NavigationBar
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.couiBackdrop
import com.itos.coui.backdrop.rememberCouiBackdrop

import android.app.Activity
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.layout.layout
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import java.io.File
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import androidx.compose.runtime.snapshots.Snapshot
import kotlin.math.roundToInt
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val LocalScreenLive = compositionLocalOf { true }

internal enum class Tab { Shelf, History, Discover, Mine }

private sealed interface Screen {
    data object Tabs : Screen
    data object Search : Screen
    /** Independent local search page for 书架 ("shelf") or 历史 ("history"). */
    data class LocalSearch(val scope: String) : Screen
    data class Remote(val id: String) : Screen
    data class RemoteReader(val workId: String, val chapterId: String, val page: Int = 0) : Screen
    data object SourcePick : Screen
    data object Backup : Screen
    data object Theme : Screen
    data object Reading : Screen
    data object Language : Screen
    data object Feedback : Screen
    data object Intro : Screen
    data object More : Screen
    data object Folders : Screen
    data object Notes : Screen
    data object Font : Screen
    data class Folder(val id: String) : Screen
    data object Banner : Screen
    data object Storage : Screen
}

internal data class Copy(
    val shelf: String,
    val discover: String,
    val search: String,
    val mine: String,
    val favOnly: String,
    val all: String,
    val emptyShelf: String,
    val followSystem: String,
    val light: String,
    val dark: String,
    val backup: String,
    val backupSum: String,
    val appearance: String,
    val language: String,
    val startRead: String,
    val addShelf: String,
    val inShelf: String,
    val catalogTitle: String,
    val next: String,
    val reset: String,
    val zhCN: String,
    val zhTW: String,
    val en: String,
    val history: String,
    val historySub: String,
    val bookstore: String,
    val emptyCat: String,
    val emptyHistory: String,
    val offline: String,
    val loading: String,
    val newChapters: String,
    val cancel: String,
    val feedback: String,
    val feedbackSum: String,
    val themeSettings: String,
    val themeSettingsSum: String,
    val intro: String,
    val introBody: String,
    val noNewChapters: String,
)

internal fun copyOf(lang: String) = when (lang) {
    "zh-TW" -> Copy(
        "書架", "書城", "搜尋", "我的",
        "收藏", "全部", "書架空空如也", "跟隨系統",
        "淺色", "深色", "備份與還原", "匯出 / 還原本機資料",
        "介面", "語言", "開始閱讀", "加入書架",
        "已加入書架", "目錄", "下一頁", "重設本機資料",
        "簡體中文", "繁體中文", "English", "歷史",
        "看過的都會留在這裡", "書城", "該分類暫無內容", "您暫時沒有瀏覽記錄",
        "目前沒有網路，已顯示離線內容", "正在載入…", "發現新章節", "取消",
        "回饋", "提取問題日誌", "主題設定", "深色模式與背景欄",
        "關於", "書漫嶼是一款集成漫畫和小說源的應用，目前還處於早期階段，漫畫源、小說源以及各項功能還在持續更新中....\n\n軟體僅供學習交流，請勿用於商業用途。\n\n建議與反饋：https://t.me/shumanyu1", "暫無新章節",
    )
    "en" -> Copy(
        "Library", "Store", "Search", "Me",
        "Favorites", "All", "Your shelf is empty", "Follow system",
        "Light", "Dark", "Backup & restore", "Export or restore local data",
        "Appearance", "Language", "Read", "Add to shelf",
        "On shelf", "Chapters", "Next", "Reset local data",
        "Simplified Chinese", "Traditional Chinese", "English", "History",
        "Everything you’ve read stays here", "Store", "Nothing in this category yet", "No browsing history yet",
        "You're offline — showing saved titles", "Loading…", "New chapters", "Cancel",
        "Feedback", "Export problem logs", "Theme", "Dark mode and header",
        "About", "ShuManYu is an early app for manga and novel sources. Sources and features are still being updated....\n\nFor study and exchange only. Not for commercial use.\n\nFeedback: https://t.me/shumanyu1", "No new chapters",
    )
    else -> Copy(
        "书架", "书城", "搜索", "我的",
        "收藏", "全部", "书架空空如也", "跟随系统",
        "浅色", "深色", "备份与恢复", "导出 / 恢复本地数据",
        "界面", "语言", "开始阅读", "加入书架",
        "已加入书架", "目录", "下一页", "重置本地数据",
        "简体中文", "繁体中文", "English", "历史",
        "看过的都会留在这里", "书城", "该分类暂无内容", "您暂时没有浏览记录",
        "当前没有网络，已显示离线内容", "正在加载…", "发现新章节", "取消",
        "反馈", "提取问题日志", "主题设置", "深色模式与背景栏",
        "关于", "书漫屿是一款集成漫画和小说源的应用，目前还处于早期阶段，漫画源、小说源以及各项功能还在持续更新中....\n\n软件仅供学习交流，请勿用于商业用途。\n\n建议与反馈：https://t.me/shumanyu1", "暂无新章节",
    )
}

@Composable
fun ShuManYuApp() {
    val context = LocalContext.current
    remember { RemoteConfig.loadCached(context) }
    val prefs = remember { Prefs(context) }
    var colorMode by remember { mutableStateOf(prefs.colorMode) }
    var langPref by remember { mutableStateOf(prefs.lang) }
    val lang = resolveLang(langPref, LocalConfiguration.current)
    var incognito by remember { mutableStateOf(prefs.incognito) }
    var peekGuard by remember { mutableStateOf(prefs.peekGuard) }
    val forceTablet = remember { prefs.forceTablet }
    var tabletAsked by remember { mutableStateOf(prefs.forceTablet) }
    var fontGear by remember { mutableStateOf(prefs.fontGear) }
    var histLayout by remember { mutableStateOf(prefs.histLayout) }
    var mineBanner by remember { mutableStateOf(prefs.mineBanner) }
    val copy = remember(lang) { copyOf(lang) }
    val systemDark = isSystemInDarkTheme()
    val dark = when (colorMode) {
        ColorSchemeMode.Dark -> true
        ColorSchemeMode.Light -> false
        ColorSchemeMode.System -> systemDark
    }
    com.itos.coui.theme.CouiTheme(darkTheme = dark, colorScheme = couiScheme(dark)) {
        val baseDensity = LocalDensity.current
        val fontScale = when (fontGear) {
            "s" -> 0.85f
            "m" -> 1f
            "l" -> 1.18f
            else -> baseDensity.fontScale
        }
        CompositionLocalProvider(androidx.compose.foundation.LocalIndication provides NoPressIndication, LocalDensity provides Density(baseDensity.density, fontScale = fontScale)) {
        FirstLaunchPermissions(lang)
        val view = LocalView.current
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = !dark
            insets.isAppearanceLightNavigationBars = !dark
            (view.context as? MainActivity)?.applyPeek(peekGuard)
        }
        DisposableEffect(dark) {
            (view.context as? ComponentActivity)?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { dark },
                navigationBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                    android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b),
                ) { dark },
            )
            onDispose {}
        }

        var screen by remember { mutableStateOf<Screen>(Screen.Tabs) }
        var tab by remember { mutableStateOf(if(prefs.openStoreOnFirstLaunch())Tab.Discover else Tab.Shelf) }
        val library = remember { mutableStateListOf<String>().also { it.addAll(prefs.library.filter { id -> id !in NOVEL_IDS }) } }
        val favorites = remember { mutableStateListOf<String>().also { it.addAll(prefs.favorites.filter { id -> id !in NOVEL_IDS }) } }
        val history = remember { mutableStateListOf<String>().also { it.addAll(prefs.history.filter { id -> id !in NOVEL_IDS }) } }
        val readProgress = remember { mutableStateMapOf<String, Progress>().also { it.putAll(prefs.progress) } }
        val webSites = remember { mutableStateListOf<WebSite>().also { it.addAll(prefs.webSites()) } }
        val crawled = remember { mutableStateListOf<CrawledWork>().also { it.addAll(LocalStore.loadAllListings()) } }
        var storeSite by remember {
            mutableStateOf(
                prefs.lastSiteId.takeIf {
                    it == Sites.NOVEL || it == Sites.JM || it == Sites.IKAN || it == Sites.HETUSHU
                } ?: Sites.COPY,
            )
        }
        LaunchedEffect(Unit) {
            prefs.customCover = true
            prefs.savePhotos = true
            applyCoverMode(context, prefs, crawled, true)
        }
        var query by remember { mutableStateOf("") }
        var discoverCols by remember { mutableIntStateOf(2) }
        var online by remember { mutableStateOf(Net.online(context)) }

        fun persistLibrary() { prefs.library = library.toList() }
        fun persistFav() { prefs.favorites = favorites.toList() }
        fun persistHistory() {
            prefs.history = history.toList()
            prefs.progress = readProgress.toMap()
        }
        val appScope = rememberCoroutineScope()
        val appUpdater=remember { AppUpdateController(context.applicationContext) }
        var remoteAnnouncement by remember { mutableStateOf<RemoteAnnouncement?>(null) }
        LaunchedEffect(Unit) {
            RemoteConfig.refresh(context)
            RemoteConfig.announcement
                ?.takeIf { RemoteConfig.announcementSeen(context) != it.id }
                ?.let { remoteAnnouncement = it }
        }
        LaunchedEffect(RemoteConfig.rev) {
            webSites.clear()
            webSites.addAll(prefs.webSites())
            if (!RemoteConfig.sourcesEnabled(storeSite)) storeSite = Sites.COPY
            if (tab == Tab.Discover && !RemoteConfig.entryEnabled) tab = Tab.Shelf
        }
        LaunchedEffect(screen) {
            if (screen is Screen.RemoteReader) appUpdater.check(immediate = true)
        }
        val updateLifecycle=androidx.lifecycle.compose.LocalLifecycleOwner.current
        LaunchedEffect(updateLifecycle) {
            updateLifecycle.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
                appUpdater.check(immediate=true)
                while(true) {
                    delay(15_000L)
                    appUpdater.check()
                }
            }
        }
        val historyPending = remember { java.util.concurrent.atomic.AtomicBoolean(false) }
        DisposableEffect(Unit) {
            onDispose { persistHistory() }
        }
        fun persistSites() { prefs.setWebSites(webSites.toList()) }
        val folders = remember { mutableStateListOf<BookFolder>().also { it.addAll(prefs.folders) } }
        var folderBook by remember { mutableStateOf<String?>(null) }
        fun saveFolders() { prefs.folders = folders.toList() }
        fun openFolderSheet(id: String) { folderBook = id }
        var histMenu by remember { mutableStateOf(false) }
        var histQ by remember { mutableStateOf("") }
        var histPick by remember { mutableStateOf(false) }
        val histSelected = remember { mutableStateListOf<String>() }
        var histAsk by remember { mutableStateOf("") }
        var shelfQ by remember { mutableStateOf("") }
        var shelfPick by remember { mutableStateOf(false) }
        val shelfSelected = remember { mutableStateListOf<String>() }
        var shelfAsk by remember { mutableStateOf(false) }
        var shelfAskAll by remember { mutableStateOf(false) }
        var shelfMenu by remember { mutableStateOf(false) }
        var shelfBusy by remember { mutableStateOf(false) }
        var shelfOpts by remember { mutableStateOf(prefs.shelfOpts) }
        var shelfSheet by remember { mutableStateOf(false) }
        LaunchedEffect(tab) {
            if (tab != Tab.History) {
                histMenu = false
                histPick = false
                histSelected.clear()
                histAsk = ""
            }
            if (tab != Tab.Shelf) {
                shelfSheet = false
                shelfPick = false
                shelfSelected.clear()
                shelfAsk = false
                shelfAskAll = false
                shelfMenu = false
            }
        }
        fun crawlSite(site: WebSite, category: String = "推荐", silent: Boolean = true, onDone: () -> Unit = {}) {
            if (!Net.online(context)) {
                if (!silent) CouiToast.show(copy.offline)
                onDone()
                return
            }
            fun applyListing(category: String, items: List<CrawledWork>) {
                if (items.isEmpty()) return
                val ranked = category != "推荐" && category != "推薦"
                val tagged = items.map { item ->
                    val withCat = if (item.cat.isBlank()) item.copy(cat = category) else item
                    if (ranked) withCat.copy(listed = withCat.listed.filterNot { tagEq(it, category) } + category) else withCat
                }
                val byId = LinkedHashMap<String, CrawledWork>()
                crawled.filter { it.siteId == site.id && it.cat != "搜索" }.forEach { byId[it.id] = it }
                for (item in tagged) {
                    val prev = byId[item.id]
                    byId[item.id] = if (prev == null) item else mergeListed(prev, item, category)
                }
                crawled.removeAll { it.siteId == site.id && it.cat != "搜索" }
                crawled.addAll(byId.values)
            }
            fun patchCovers(items: List<CrawledWork>) {
                for (item in items) {
                    if (item.cover.isBlank()) continue
                    val i = crawled.indexOfFirst { it.id == item.id }
                    if (i < 0) continue
                    val cur = crawled[i]
                    if (cur.cover == item.cover) continue
                    crawled[i] = cur.copy(cover = item.cover)
                }
            }
            val worker=Thread {
                val main = Handler(Looper.getMainLooper())
                var pending: List<CrawledWork>? = null
                val flush = Runnable {
                    val snap = pending ?: return@Runnable
                    pending = null
                    applyListing(category, snap)
                }
                val (items, err) = Crawler.fetch(site.url, site.id, category) { partial ->
                    if (partial.isEmpty()) return@fetch
                    main.post {
                        patchCovers(partial)
                        pending = partial
                        main.removeCallbacks(flush)
                        main.postDelayed(flush, 80)
                    }
                }
                if(Thread.currentThread().isInterrupted) {
                    main.post { main.removeCallbacks(flush);onDone() }
                    return@Thread
                }
                main.post {
                    main.removeCallbacks(flush)
                    pending = null
                    val i = webSites.indexOfFirst { it.id == site.id }
                    if (i >= 0) {
                        webSites[i] = webSites[i].copy(lastError = err.orEmpty(), itemCount = items.size)
                    }
                    if (items.isNotEmpty()) {
                        applyListing(category, items)
                        patchCovers(items)
                        LocalStore.saveListing(site.id, crawled.filter { it.siteId == site.id })
                    } else if (err != null) {
                        AppLog.add("crawl", "${site.name}/$category $err")
                    }
                    persistSites()
                    if (!silent && err != null && items.isEmpty()) {
                        CouiToast.show(err)
                    }
                    onDone()
                }
            }
            worker.start()
        }
        LaunchedEffect(Unit) {
            online = Net.online(context)
        }
        var detailBack by remember { mutableStateOf<Screen>(Screen.Tabs) }
        fun pinDetailBack() {
            detailBack = when (val s = screen) {
                Screen.Search -> Screen.Search
                is Screen.LocalSearch -> s
                Screen.Folders -> Screen.Folders
                is Screen.Folder -> s
                is Screen.Remote, is Screen.RemoteReader -> detailBack
                else -> Screen.Tabs
            }
        }
        fun openWork(id: String) {
            pinDetailBack()
            screen = Screen.Remote(id)
        }
        fun openRead(id: String) {
            val disk = LocalStore.loadWork(id)
            val chs = disk?.chapters?.sortedBy { it.index }.orEmpty()
            val saved = readProgress[id]
            val ch = chs.find { it.id == saved?.chapterId } ?: chs.firstOrNull()
            if (ch != null) {
                pinDetailBack()
                prefs.markChapterRead(id, ch.id)
                screen = Screen.RemoteReader(id, ch.id, if (ch.id == saved?.chapterId) saved?.page ?: 0 else 0)
            } else {
                openWork(id)
            }
        }
        /** Opens a page in the system browser (the in-app browser was removed in 0.1.0). */
        fun openSite(url: String) {
            if (url.isBlank()) return
            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
        }
        fun markRead(id: String, chapterId: String, page: Int) {
            val m = catalog.find { it.id == id }
            val pct = if (m != null) percent(m, chapterId, page) else 0
            readProgress[id] = Progress(chapterId, page, pct, System.currentTimeMillis())
            if (!prefs.incognito) {
                history.remove(id)
                history.add(0, id)
                while (history.size > 80) history.removeAt(history.lastIndex)
            }
            if (historyPending.compareAndSet(false, true)) {
                appScope.launch {
                    delay(480)
                    historyPending.set(false)
                    persistHistory()
                }
            }
        }

        fun startRefreshLibrary() {
            if (shelfBusy) return
            if (!Net.online(context)) {
                CouiToast.show(copy.offline)
                return
            }
            val ids = library.toList()
            shelfBusy = true
            CouiToast.hold("更新中...")
            Notify.live(context, 0, ids.size, "更新中...")
            Thread {
                val news = ArrayList<ChapterBump>()
                ids.forEachIndexed { index, id ->
                    val seed = crawled.find { it.id == id } ?: LocalStore.loadWork(id)?.work
                    val href = seed?.href
                    val bump = if (href.isNullOrBlank()) null else Updates.refresh(id, href, seed)
                    if (bump != null && bump.added > 0) news.add(bump)
                    Notify.live(context, index + 1, ids.size.coerceAtLeast(1), bump?.title ?: "更新中...")
                }
                val found = news.toList()
                Handler(Looper.getMainLooper()).post {
                    shelfBusy = false
                    Notify.liveClear(context)
                    crawled.clear()
                    crawled.addAll(
                        LocalStore.loadAllListings().filter { w ->
                            w.siteId != "ru" && !w.href.contains("rumanhua", true) && !w.id.startsWith("ru")
                        },
                    )
                    if (found.isEmpty()) {
                        CouiToast.show("没有章节更新诶😩")
                    } else {
                        CouiToast.show("有章节更新快去通知栏看看是哪一本！")
                        found.forEach { Notify.chapter(context, it.title, it.index, it.chapterTitle) }
                    }
                }
            }.start()
        }
        fun refreshLibrary() = startRefreshLibrary()

        val shelfScroll = rememberLazyListState()
        val histScroll = rememberLazyListState()
        val discoverScroll = rememberLazyListState()
        val discoverCat = remember { mutableStateOf("推荐") }
        val mineScroll = rememberLazyListState()
        val libraryKey = library.joinToString(",")
        val historyKey = history.joinToString(",")
        val gateLibrary = remember(libraryKey) { library.toList() }
        val gateHistory = remember(historyKey) { history.toList() }
        val tabContent: @Composable (PaddingValues) -> Unit = { padding ->
            Column(Modifier.fillMaxSize()) {
                if (!online) {
                    Text(
                        copy.offline,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 13.sp,
                        color = AppTheme.colorScheme.primary,
                    )
                }
                Box(Modifier.weight(1f)) {
                    when (tab) {
                        Tab.Shelf -> ShelfPane(
                            padding = padding,
                            copy = copy,
                            library = gateLibrary,
                            crawled = crawled,
                            progress = readProgress,
                            prefs = prefs,
                            opts = shelfOpts,
                            selecting = shelfPick,
                            selected = shelfSelected.toSet(),
                            refreshing = shelfBusy,
                            tablet = forceTablet,
                            onOpen = { openWork(it) },
                            onToggle = { id ->
                                if (shelfSelected.contains(id)) shelfSelected.remove(id) else shelfSelected.add(id)
                            },
                            onLong = { id ->
                                if (!shelfPick) {
                                    shelfPick = true
                                    shelfSelected.clear()
                                }
                                if (shelfSelected.contains(id)) shelfSelected.remove(id) else shelfSelected.add(id)
                            },
                            onManual = { order ->
                                if (shelfOpts.sort != "manual") {
                                    val keep = library.filter { it !in order }
                                    library.clear()
                                    library.addAll(order + keep)
                                    shelfOpts = shelfOpts.copy(sort = "manual")
                                    prefs.shelfOpts = shelfOpts
                                }
                            },
                            onReorder = { order ->
                                val pending = order.iterator()
                                val keep = order.toSet()
                                val next = library.map { id -> if (id in keep) pending.next() else id }
                                library.clear()
                                library.addAll(next)
                                if (shelfOpts.sort != "manual") {
                                    shelfOpts = shelfOpts.copy(sort = "manual")
                                    prefs.shelfOpts = shelfOpts
                                }
                                persistLibrary()
                            },
                            onTrash = { if (shelfSelected.isNotEmpty()) shelfAsk = true },
                            onRefresh = { refreshLibrary() },
                            listState = shelfScroll,
                        )
                        Tab.History -> HistoryPane(
                            padding = padding,
                            copy = copy,
                            lang = lang,
                            history = gateHistory,
                            progress = readProgress,
                            crawled = crawled,
                            selecting = histPick,
                            selected = histSelected.toSet(),
                            onOpen = { openWork(it) },
                            onToggle = { id ->
                                if (histSelected.contains(id)) histSelected.remove(id) else histSelected.add(id)
                            },
                            onLong = { id ->
                                if (!histPick) {
                                    histPick = true
                                    histSelected.clear()
                                }
                                if (histSelected.contains(id)) histSelected.remove(id) else histSelected.add(id)
                            },
                            onTrash = { if (histSelected.isNotEmpty()) histAsk = "some" },
                            layout = histLayout,
                            listState = histScroll,
                        )
                        Tab.Discover -> DiscoverPane(
                            padding = padding,
                            copy = copy,
                            lang = lang,
                            crawled = crawled,
                            webSites = webSites,
                            storeSite = storeSite,
                            online = online,
                            cols = discoverCols,
                            asList = forceTablet,
                            onOpen = { openWork(it) },
                            onSearch = { screen = Screen.Search },
                            onPickSource = { screen = Screen.SourcePick },
                            onWeb = { openSite(it) },
                            onToggleCols = { discoverCols = if (discoverCols == 2) 3 else 2 },
                            onCrawl = { site, cat, done -> crawlSite(site, cat, silent = true, onDone = done) },
                            listState = discoverScroll,
                            catState = discoverCat,
                            bleedBottom = !forceTablet,
                        )
                        Tab.Mine -> MinePane(
                            padding = padding,
                            copy = copy,
                            lang = lang,
                            lib = gateLibrary.size,
                            fav = favorites.size,
                            hist = gateHistory.size,
                            langLabel = when (langPref) {
                                "system" -> copy.followSystem
                                "zh-TW" -> copy.zhTW
                                "en" -> copy.en
                                else -> copy.zhCN
                            },
                            onTheme = { screen = Screen.Theme },
                            onReading = { screen = Screen.Reading },
                            readingLabel = moreOf(lang).reading,
                            readingSum = when (lang) {
                                "en" -> "Mode, display and page controls"
                                "zh-TW" -> "閱讀模式、顯示與翻頁"
                                else -> "阅读模式、显示与翻页"
                            },
                            onLanguage = { screen = Screen.Language },
                            onFolders = { screen = Screen.Folders },
                            onShelf = { tab = Tab.Shelf },
                            onHistory = { tab = Tab.History },
                            onIntro = { screen = Screen.Intro },
                            onFeedback = { screen = Screen.Feedback },
                            onStorage = { screen = Screen.Storage },
                            onBackup = { screen = Screen.Backup },
                            onMore = { screen = Screen.More },
                            listState = mineScroll,
                            banner = mineBanner,
                        )
                    }
                }
            }
        }

        fun visibleHistoryIds(): List<String> = history.toList()
        fun titleHits(ids: List<String>, raw: String): List<String> {
            val q = raw.trim()
            if (q.isBlank()) return emptyList()
            return ids.filter { id ->
                val m = catalog.find { it.id == id }
                val w = crawled.find { it.id == id }
                val diskTitle = LocalStore.loadWork(id)?.work?.title
                keywordHit(realTitle(m?.title, w?.title, diskTitle, id), q)
            }
        }
        fun parentScreen(s: Screen): Screen = when (s) {
            Screen.Search -> Screen.Tabs
            is Screen.Remote -> detailBack
            is Screen.RemoteReader -> Screen.Remote(s.workId)
            is Screen.Folder -> Screen.Folders
            Screen.Notes -> Screen.Intro
            Screen.Font -> Screen.More
            Screen.Banner -> Screen.Theme
            else -> Screen.Tabs
        }
        fun leaveScreen() {
            when (val s = screen) {
                Screen.Tabs -> Unit
                Screen.Search -> {
                    query = ""
                    screen = Screen.Tabs
                }
                is Screen.LocalSearch -> {
                    if (s.scope == "shelf") shelfQ = "" else histQ = ""
                    screen = Screen.Tabs
                }
                is Screen.Remote -> screen = detailBack
                is Screen.RemoteReader -> screen = Screen.Remote(s.workId)
                is Screen.Folder -> screen = Screen.Folders
                Screen.Notes -> screen = Screen.Intro
                Screen.Font -> screen = Screen.More
                Screen.Banner -> screen = Screen.Theme
                else -> screen = Screen.Tabs
            }
        }
        val navState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        val backTransition = navState.transitionState
        val backActive = backTransition is NavigationEventTransitionState.InProgress
        val backEvent = (backTransition as? NavigationEventTransitionState.InProgress)?.latestEvent
        val backProgress = backEvent?.progress ?: 0f
        val backEdge = backEvent?.swipeEdge ?: NavigationEvent.EDGE_NONE
        val windowInfo = LocalWindowInfo.current
        val density = LocalDensity.current
        val backShift = remember(windowInfo.containerSize, density) {
            with(density) { windowInfo.containerSize.width.toDp() } / 20f - 8.dp
        }
        NavigationBackHandler(
            state = navState,
            isBackEnabled = screen != Screen.Tabs,
            onBackCompleted = { leaveScreen() },
        )
        fun screenToken(s: Screen): String = when (s) {
            Screen.Tabs -> "tabs"
            Screen.Search -> "search"
            is Screen.LocalSearch -> "local-search:${s.scope}"
            is Screen.Remote -> "remote:${s.id}"
            is Screen.RemoteReader -> "reader:${s.workId}:${s.chapterId}"
            Screen.SourcePick -> "source-pick"
            Screen.Backup -> "backup"
            Screen.Theme -> "theme"
            Screen.Reading -> "reading"
            Screen.Language -> "language"
            Screen.Feedback -> "feedback"
            Screen.Intro -> "intro"
            Screen.More -> "more"
            Screen.Folders -> "folders"
            Screen.Notes -> "notes"
            Screen.Font -> "font"
            Screen.Banner -> "banner"
            is Screen.Folder -> "folder:${s.id}"
            Screen.Storage -> "storage"
        }
        fun layerChain(s: Screen): List<Screen> {
            val out = ArrayList<Screen>(4)
            var cur = s
            val guard = HashSet<String>()
            while (true) {
                val token = screenToken(cur)
                if (!guard.add(token)) break
                out.add(cur)
                val parent = parentScreen(cur)
                if (screenToken(parent) == token) break
                cur = parent
            }
            out.reverse()
            return out
        }
        @Composable
        fun Draw(which: Screen, live: Boolean) {
        when (val s = which) {
            Screen.Tabs -> {
                val topBar: @Composable (CouiBackdrop?) -> Unit = { tabBackdrop ->
                    when (tab) {
                        Tab.Discover -> Unit
                        Tab.History -> FloatingTabHeader(
                            backdrop = tabBackdrop,
                            title = if (histPick) "已选 ${histSelected.size}" else copy.history,
                            actions = {
                                if (histPick) {
                                    ToolbarTextButton(if (lang == "en") "Cancel" else "取消") {
                                        histPick = false
                                        histSelected.clear()
                                    }
                                    ToolbarGap()
                                    ToolbarTextButton(if (lang == "en") "All" else "全选", accent = true) {
                                        histSelected.clear()
                                        histSelected.addAll(visibleHistoryIds())
                                    }
                                } else {
                                    IconButton(onClick = { histQ = ""; screen = Screen.LocalSearch("history") }) {
                                        Icon(CouiIcons.Outlined.Search, contentDescription = if (lang == "en") "Search" else "搜索", tint = AppTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                                    }
                                    ToolbarGap()
                                    CornerMenu(
                                        expanded = histMenu,
                                        onExpandedChange = { histMenu = it },
                                        actions = listOf(
                                            MenuAction(
                                                if (histLayout == "h") (if (lang == "en") "Vertical layout" else "切换竖排") else (if (lang == "en") "Horizontal layout" else "切换横排"),
                                                ShuIcons.Rows,
                                            ) {
                                                histLayout = if (histLayout == "h") "v" else "h"
                                                prefs.histLayout = histLayout
                                            },
                                            MenuAction(moreOf(lang).batchDeleteHistory, ShuIcons.SelectMany) {
                                                histPick = true
                                                histSelected.clear()
                                                histSelected.addAll(visibleHistoryIds())
                                            },
                                            MenuAction(moreOf(lang).clearAllHistory, ShuIcons.Trash, dividerBefore = true) { histAsk = "all" },
                                        ),
                                    )
                                }
                            },
                        )
                        Tab.Shelf -> FloatingTabHeader(
                            backdrop = tabBackdrop,
                            title = if (shelfPick) "已选 ${shelfSelected.size}" else copy.shelf,
                            actions = {
                                if (shelfPick) {
                                    ToolbarTextButton(if (lang == "en") "Cancel" else "取消") {
                                        shelfPick = false
                                        shelfSelected.clear()
                                    }
                                    ToolbarGap()
                                    ToolbarTextButton(if (lang == "en") "All" else "全选", accent = true) {
                                        shelfSelected.clear()
                                        shelfSelected.addAll(library)
                                    }
                                } else {
                                    IconButton(onClick = { shelfQ = ""; screen = Screen.LocalSearch("shelf") }) {
                                        Icon(CouiIcons.Outlined.Search, contentDescription = if (lang == "en") "Search" else "搜索", tint = AppTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                                    }
                                    ToolbarGap()
                                    IconButton(onClick = { shelfSheet = true }) {
                                        Icon(
                                            CouiIcons.Outlined.ListFilter,
                                            contentDescription = "筛选",
                                            tint = if (shelfOpts.filtering) AppTheme.colorScheme.primary else AppTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(24.dp),
                                        )
                                    }
                                    ToolbarGap()
                                    IconButton(onClick = { refreshLibrary() }) {
                                        Icon(ShuIcons.Refresh, contentDescription = "刷新", tint = AppTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                                    }
                                    ToolbarGap()
                                    CornerMenu(
                                        expanded = shelfMenu,
                                        onExpandedChange = { shelfMenu = it },
                                        actions = listOf(
                                            MenuAction(if (lang == "en") "Select to remove" else if (lang == "zh-TW") "批量刪除" else "批量删除", ShuIcons.SelectMany) {
                                                shelfPick = true
                                                shelfSelected.clear()
                                            },
                                            MenuAction(if (lang == "en") "Clear shelf" else if (lang == "zh-TW") "清空書架" else "清空书架", ShuIcons.Trash, dividerBefore = true) {
                                                shelfAskAll = true
                                            },
                                        ),
                                    )
                                }
                            },
                        )
                        Tab.Mine -> Unit
                    }
                }
        val floatingBar = !forceTablet
        Box(Modifier.fillMaxSize()) {
                key(colorMode, dark) {
                Box(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxSize(),
                ) {
                    if (forceTablet) {
                        TabletRail(tab = tab, copy = copy, onTab = { tab = it })
                    }
                    Scaffold(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        containerColor = AppTheme.colorScheme.surface,
                        topBar = {},
                        bottomBar = {
                            Box(Modifier.bottomBarVeil()) {
                            if (floatingBar) {
                                    // COUI 的底栏中间胶囊固定为左右附件预留 2×56dp + 间距；
                                    // 本应用不用左右附件，把这部分宽度补回给底栏，胶囊就能按
                                    // 16dp 页面边距铺满，同时保留官方的玻璃材质与拖拽动效。
                                    val renderer = androidx.compose.runtime.remember { CouiFloatingRenderer() }
                                    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        // 胶囊目标宽度：左右各留 16dp，最多 300dp；比默认的 243dp 宽，
                                        // 又不会像铺满那样显得过宽。
                                        val capsule = minOf(maxWidth - 32.dp, 300.dp)
                                        val navTabs = if (RemoteConfig.entryEnabled) Tab.entries.toList() else Tab.entries.filter { it != Tab.Discover }
                                        NavigationBar(
                                            modifier = Modifier.requiredWidth(capsule + 168.dp),
                                            renderer = renderer,
                                            items = navTabs.map { dest ->
                                                when (dest) {
                                                    Tab.Shelf -> CouiNavigationItem(copy.shelf, icon = ShuIcons.BookOpenOutlined, selectedIcon = CouiIcons.Filled.BookOpen)
                                                    Tab.History -> CouiNavigationItem(copy.history, icon = ShuIcons.HistoryOutlined, selectedIcon = ShuIcons.HistoryFilled)
                                                    Tab.Discover -> CouiNavigationItem(copy.discover, icon = ShuIcons.StoreOutlined, selectedIcon = ShuIcons.StoreFilled)
                                                    Tab.Mine -> CouiNavigationItem(copy.mine, icon = ShuIcons.UserCircleOutlined, selectedIcon = CouiIcons.Filled.UserCircle)
                                                }
                                            },
                                            selectedIndex = navTabs.indexOf(tab).coerceAtLeast(0),
                                            onSelection = { tab = navTabs[it] },
                                            dark = dark,
                                        )
                                    }
                                }
                            }
                        },
                    ) { scaffoldPad ->
                        var headPx by remember { mutableIntStateOf(0) }
                        val density = LocalDensity.current
                        val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        val bottomPad = scaffoldPad.calculateBottomPadding()
                        val barShown = tab == Tab.Shelf || tab == Tab.History
                        val barTop = if (headPx == 0) statusTop + 56.dp else with(density) { headPx.toDp() }
                        val contentPad = when (tab) {
                            Tab.Mine -> PaddingValues(bottom = bottomPad)
                            Tab.Discover -> PaddingValues(top = statusTop, bottom = bottomPad)
                            else -> PaddingValues(top = barTop, bottom = bottomPad)
                        }
                        val tabBackdrop = rememberCouiBackdrop()
                        Box(Modifier.fillMaxSize()) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .then(if (barShown) Modifier.couiBackdrop(tabBackdrop) else Modifier)
                                    .background(AppTheme.colorScheme.surface),
                            ) { tabContent(contentPad) }
                            if (barShown) {
                                Box(
                                    Modifier
                                        .align(Alignment.TopCenter)
                                        .fillMaxWidth()
                                        .onSizeChanged { headPx = it.height },
                                ) {
                                    topBar(tabBackdrop)
                                }
                            }
                        }
                    }
                }
                }
                }
                if (live) BackHandler(enabled = histAsk.isNotEmpty() || histMenu || histPick || shelfSheet || shelfPick || shelfAsk || shelfAskAll || shelfMenu) {
                    when {
                        histAsk.isNotEmpty() -> histAsk = ""
                        shelfAsk -> shelfAsk = false
                        shelfAskAll -> shelfAskAll = false
                        histMenu -> histMenu = false
                        shelfMenu -> shelfMenu = false
                        shelfSheet -> shelfSheet = false
                        shelfPick -> {
                            shelfPick = false
                            shelfSelected.clear()
                        }
                        else -> {
                            histPick = false
                            histSelected.clear()
                        }
                    }
                }
                val more = moreOf(lang)
                if (histAsk.isNotEmpty()) {
                    val askTitle = when (lang) {
                        "en" -> "Delete?"
                        "zh-TW" -> "確定刪除嗎"
                        else -> "确定删除吗"
                    }
                    ConfirmSheet(
                        title = askTitle,
                        message = if (histAsk == "all") more.confirmClear else more.confirmBatch,
                        confirm = more.confirm,
                        cancel = copy.cancel,
                        onConfirm = {
                            if (histAsk == "all") {
                                history.clear()
                            } else {
                                val drop = histSelected.toSet()
                                history.removeAll { it in drop }
                                drop.forEach { readProgress.remove(it) }
                            }
                            persistHistory()
                            histPick = false
                            histSelected.clear()
                            histAsk = ""
                        },
                        onDismiss = { histAsk = "" },
                    )
                }
                if (shelfAsk) {
                    val askTitle = when (lang) {
                        "en" -> "Remove?"
                        "zh-TW" -> "確定移出書架嗎"
                        else -> "确定移出书架吗"
                    }
                    val askMsg = when (lang) {
                        "en" -> "Remove the selected titles from the shelf?"
                        "zh-TW" -> "確定把所選漫畫移出書架？"
                        else -> "确定把所选漫画移出书架？"
                    }
                    ConfirmSheet(
                        title = askTitle,
                        message = askMsg,
                        confirm = more.confirm,
                        cancel = copy.cancel,
                        onConfirm = {
                            val drop = shelfSelected.toSet()
                            library.removeAll { it in drop }
                            persistLibrary()
                            shelfPick = false
                            shelfSelected.clear()
                            shelfAsk = false
                        },
                        onDismiss = { shelfAsk = false },
                    )
                }
                if (shelfAskAll) {
                    ConfirmSheet(
                        title = when (lang) {
                            "en" -> "Remove all?"
                            "zh-TW" -> "確定全部移出書架嗎"
                            else -> "确定全部移出书架吗"
                        },
                        message = when (lang) {
                            "en" -> "Every title on the shelf will be removed."
                            "zh-TW" -> "書架上的漫畫都會移出，不會刪除已下載的檔案。"
                            else -> "书架上的漫画都会移出，不会删除已下载的文件。"
                        },
                        confirm = more.confirm,
                        cancel = copy.cancel,
                        onConfirm = {
                            library.clear()
                            persistLibrary()
                            shelfPick = false
                            shelfSelected.clear()
                            shelfAskAll = false
                        },
                        onDismiss = { shelfAskAll = false },
                    )
                }
                if (shelfSheet) {
                    ShelfSheet(
                        lang = lang,
                        opts = shelfOpts,
                        onChange = {
                            shelfOpts = it
                            prefs.shelfOpts = it
                        },
                        onDismiss = { shelfSheet = false },
                    )
                }
                }
            }
            is Screen.LocalSearch -> {
                val shelfScope = s.scope == "shelf"
                val q = if (shelfScope) shelfQ else histQ
                val hits = remember(q, library.toList(), gateHistory, crawled.size) {
                    titleHits(if (shelfScope) gateLibrary else gateHistory, q)
                }
                val searchList = rememberLazyListState()
                SearchPage(
                    query = q,
                    onQuery = { if (shelfScope) shelfQ = it else histQ = it },
                    hint = if (shelfScope) {
                        if (lang == "en") "Search this shelf" else if (lang == "zh-TW") "搜尋書架" else "搜索书架"
                    } else {
                        if (lang == "en") "Search history" else if (lang == "zh-TW") "搜尋瀏覽記錄" else "搜索浏览记录"
                    },
                    cancel = copy.cancel,
                    onCancel = { leaveScreen() },
                ) { pad ->
                    when {
                        q.isBlank() -> Unit
                        hits.isEmpty() -> EmptyState("(´・ω・`)", if (lang == "en") "No matching titles" else "没有找到相关的书", Modifier.padding(pad))
                        shelfScope -> ShelfPane(
                            padding = pad,
                            copy = copy,
                            library = hits,
                            crawled = crawled,
                            progress = readProgress,
                            prefs = prefs,
                            opts = ShelfOpts(),
                            selecting = false,
                            selected = emptySet(),
                            onOpen = { openWork(it) },
                            onToggle = {},
                            onLong = {},
                            onManual = {},
                            onReorder = {},
                            onTrash = {},
                            onRefresh = {},
                            refreshing = false,
                            tablet = forceTablet,
                            listState = searchList,
                        )
                        else -> HistoryPane(
                            padding = pad,
                            copy = copy,
                            lang = lang,
                            history = hits,
                            progress = readProgress,
                            crawled = crawled,
                            selecting = false,
                            selected = emptySet(),
                            onOpen = { openWork(it) },
                            onToggle = {},
                            onLong = {},
                            onTrash = {},
                            layout = histLayout,
                            listState = searchList,
                        )
                    }
                }
            }
            Screen.Search -> 
                SearchPane(
                    copy = copy,
                    query = query,
                    onQuery = { query = it },
                    crawled = crawled,
                    webSites = webSites,
                    siteId = storeSite,
                    onBack = {
                        query = ""
                        screen = Screen.Tabs
                    },
                    onOpen = { openWork(it) },
                    onHits = { hits ->
                        hits.forEach { w ->
                            val i = crawled.indexOfFirst { it.id == w.id }
                            if (i < 0) {
                                crawled.add(w)
                            } else {
                                val cur = crawled[i]
                                val t = realTitle(cur.title, w.title)
                                val cover = cur.cover.ifBlank { w.cover }
                                if ((t.isNotBlank() && t != cur.title) || (cur.cover.isBlank() && cover.isNotBlank())) {
                                    crawled[i] = cur.copy(title = t.ifBlank { cur.title }, cover = cover)
                                }
                            }
                        }
                    },
                )
            
            is Screen.Remote -> 
                RemotePane(
                    id = s.id,
                    copy = copy,
                    crawled = crawled,
                    webSites = webSites,
                    inLib = library.contains(s.id),
                    online = online,
                    resume = readProgress[s.id],
                    readKeys = prefs.readChapterKeys(),
                    onBack = { screen = detailBack },
                    onLib = {
                        if (library.contains(s.id)) library.remove(s.id) else {
                            library.add(0, s.id)
                            prefs.noteAdded(s.id)
                        }
                        persistLibrary()
                    },
                    onReadChapter = { chId, page ->
                        prefs.markChapterRead(s.id, chId)
                        markRead(s.id, chId, page)
                        screen = Screen.RemoteReader(s.id, chId, page)
                    },
                    onWeb = { url, _ -> openSite(url) },
                    foldersOn = true,
                    isFavorite = s.id in favorites,
                    onFolder = {
                        if(s.id in favorites) favorites.remove(s.id) else favorites.add(0,s.id)
                        persistFav()
                    },
                )
            
            is Screen.RemoteReader ->
                ReadDispatch(
                    workId = s.workId,
                    chapterId = s.chapterId,
                    startPage = s.page,
                    crawled = crawled,
                    copy = copy,
                    prefs = prefs,
                    bookmarked = favorites.contains(s.workId),
                    onBookmark = {
                        if (favorites.contains(s.workId)) favorites.remove(s.workId) else favorites.add(0, s.workId)
                        persistFav()
                    },
                    onClose = { screen = Screen.Remote(s.workId) },
                    onChapter = { ch -> screen = Screen.RemoteReader(s.workId, ch, 0) },
                    onProgress = { ch, page -> markRead(s.workId, ch, page) },
                    onFolder = { if(s.workId !in favorites) { favorites.add(0,s.workId);persistFav() } },
                    onCoverSaved = { path ->
                        val i = crawled.indexOfFirst { it.id == s.workId }
                        if (i >= 0) {
                            val site = crawled[i].siteId
                            crawled[i] = crawled[i].copy(cover = path)
                            LocalStore.saveListing(site, crawled.filter { it.siteId == site })
                        }
                    },
                )
            
            Screen.SourcePick ->
                SourcePickPane(
                    lang = lang,
                    selected = storeSite,
                    onPick = {
                        storeSite = it
                        prefs.lastSiteId = it
                        screen = Screen.Tabs
                    },
                    onBack = { screen = Screen.Tabs },
                )

            Screen.Backup -> 
                BackupPane(
                    copy = copy,
                    prefs = prefs,
                    library = library,
                    favorites = favorites,
                    onBack = { screen = Screen.Tabs },
                    onImported = { data ->
                        library.clear(); library.addAll(data.library.filter { it !in NOVEL_IDS }); persistLibrary()
                        favorites.clear(); favorites.addAll(data.favorites.filter { it !in NOVEL_IDS }); persistFav()
                        history.clear(); history.addAll(data.history.filter { it !in NOVEL_IDS })
                        readProgress.clear(); readProgress.putAll(prefs.progress)
                        persistHistory()
                        webSites.clear(); webSites.addAll(prefs.webSites())
                        colorMode = prefs.colorMode
                        langPref = prefs.lang
                    },
                )
            
            Screen.Theme -> 
                ThemePane(
                    copy = copy,
                    lang = lang,
                    colorMode = colorMode,
                    onMode = { colorMode = it; prefs.colorMode = it },
                    onBanner = { screen = Screen.Banner },
                    onBack = { screen = Screen.Tabs },
                )
            
            Screen.Reading -> 
                ReadingPane(prefs = prefs, lang = lang, onBack = { screen = Screen.Tabs })
            
            Screen.Intro -> 
                IntroPane(
                    copy = copy,
                    onBack = { screen = Screen.Tabs },
                    onNotes = { screen = Screen.Notes },
                    onCheckUpdate = {
                        if (appUpdater.checking) {
                            CouiToast.show("正在检查更新…")
                        } else {
                            appScope.launch {
                                appUpdater.check(immediate = true)
                                if (appUpdater.available == null) {
                                    CouiToast.show(
                                        if (Net.online(context)) "当前已是最新版本" else "网络不可用，无法检查更新",
                                    )
                                }
                            }
                        }
                    },
                )
            
            Screen.Language -> 
                LanguagePane(
                    copy = copy,
                    lang = langPref,
                    onLang = { langPref = it; prefs.lang = it },
                    onBack = { screen = Screen.Tabs },
                )
            
            Screen.Feedback -> 
                FeedbackPane(
                    copy = copy,
                    sites = webSites,
                    onBack = { screen = Screen.Tabs },
                )
            
            Screen.Storage -> 
                StoragePane(
                    lang = lang,
                    keep = (library + favorites + history + folders.flatMap { it.books }).toSet(),
                    onBack = { screen = Screen.Tabs },
                )
            
            Screen.More -> 
                MorePane(
                    lang = lang,
                    incognito = incognito,
                    onIncognito = {
                        incognito = it
                        prefs.incognito = it
                    },
                    peekGuard = peekGuard,
                    onPeek = {
                        peekGuard = it
                        prefs.peekGuard = it
                        (context as? MainActivity)?.applyPeek(it)
                    },
                    fontGear = fontGear,
                    onFont = { screen = Screen.Font },
                    forceTablet = tabletAsked,
                    onForceTablet = {
                        tabletAsked = it
                        prefs.forceTablet = it
                        val msg = when (lang) {
                            "en" -> "Restart the app to apply"
                            "zh-TW" -> "重新開啟應用後生效"
                            else -> "重启应用后生效"
                        }
                        CouiToast.show(msg)
                    },
                    onBack = { screen = Screen.Tabs },
                )

            is Screen.Folder -> {
                val folder = folders.find { it.id == s.id }
                FolderPane(
                    lang = lang,
                    name = folder?.name ?: "",
                    ids = folder?.books ?: emptyList(),
                    layout = folder?.layout ?: "list",
                    crawled = crawled,
                    onBack = { screen = Screen.Folders },
                    onOpen = { openRead(it) },
                    onRemove = { id ->
                        val i = folders.indexOfFirst { it.id == s.id }
                        if (i >= 0) {
                            folders[i] = folders[i].copy(books = folders[i].books.filter { it != id })
                            saveFolders()
                        }
                    },
                    onRename = { next ->
                        val title = next.trim()
                        val i = folders.indexOfFirst { it.id == s.id }
                        if (i >= 0 && title.isNotBlank()) {
                            folders[i] = folders[i].copy(name = title)
                            saveFolders()
                        }
                    },
                    onLayout = { next ->
                        val i = folders.indexOfFirst { it.id == s.id }
                        if (i >= 0) {
                            folders[i] = folders[i].copy(layout = next)
                            saveFolders()
                        }
                    },
                )
            }
            
            Screen.Folders -> 
                FoldersPane(
                    lang = lang,
                    folders = folders.toList(),
                    favoriteIds = favorites.toList(),
                    crawled = crawled,
                    webSites = webSites,
                    onFavoriteRemove = { id -> favorites.remove(id);persistFav();folders.indices.forEach { i -> folders[i]=folders[i].copy(books=folders[i].books.filterNot { it==id }) };saveFolders() },
                    onAssign = { id -> openFolderSheet(id) },
                    onBack = { screen = Screen.Tabs },
                    onOpen = { screen = Screen.Folder(it) },
                    onCover = { openWork(it) },
                    onDelete = { id ->
                        folders.removeAll { it.id == id }
                        saveFolders()
                    },
                    onCreate = { name ->
                        val title = name.trim()
                        if (title.isNotBlank()) {
                            folders.add(BookFolder("f${System.currentTimeMillis()}", title))
                            saveFolders()
                        }
                    },
                )
            
            Screen.Notes ->
                NotesPane(lang = lang, onBack = { screen = Screen.Intro })
            
            Screen.Font -> 
                FontPane(
                    lang = lang,
                    gear = fontGear,
                    onGear = {
                        fontGear = it
                        prefs.fontGear = it
                    },
                    onBack = { screen = Screen.More },
                )

            Screen.Banner ->
                BannerPane(
                    lang = lang,
                    path = mineBanner,
                    onPick = { uriText ->
                        val uri = Uri.parse(uriText)
                        val dest = File(context.filesDir, "mine-banner.jpg")
                        val copied = runCatching {
                            val input = context.contentResolver.openInputStream(uri) ?: return@runCatching false
                            input.use { src -> dest.outputStream().use { src.copyTo(it) } }
                            dest.length() > 0L
                        }.getOrDefault(false)
                        if (copied) {
                            mineBanner = dest.absolutePath
                            prefs.mineBanner = dest.absolutePath
                        }
                    },
                    onClear = {
                        mineBanner = ""
                        prefs.mineBanner = ""
                        File(context.filesDir, "mine-banner.jpg").delete()
                    },
                    onBack = { screen = Screen.Theme },
                )
            
        }
        }
        val layers = remember(screen, detailBack) { layerChain(screen) }
        val pageBg = AppTheme.colorScheme.surface.copy(alpha = 1f)
        val shellBg = if (screen is Screen.RemoteReader) Color.Black else pageBg
        SideEffect {
            val window = (view.context as Activity).window
            window.setBackgroundDrawable(ColorDrawable(shellBg.toArgb()))
        }
        // ---- 页面转场：一镜到底展开 / 收回（Expand.kt），与预测性返回手势整合 ----
        val rootSize = remember { arrayOf(androidx.compose.ui.geometry.Size.Zero) }
        val gesture = remember { androidx.compose.animation.core.Animatable(0f) }
        val lastEdge = remember { intArrayOf(NavigationEvent.EDGE_NONE) }
        if (backActive) lastEdge[0] = backEdge
        LaunchedEffect(backActive, backProgress) {
            if (backActive) gesture.snapTo(backProgress)
            else if (gesture.value > 0f) gesture.animateTo(0f, tween(220, easing = PageMotion.FadeEase))
        }
        val shiftPx = with(density) { backShift.toPx() }
        fun edgeShift(edge: Int, t: Float): Float = when (edge) {
            NavigationEvent.EDGE_LEFT -> shiftPx * t
            NavigationEvent.EDGE_RIGHT -> -shiftPx * t
            else -> 0f
        }
        val nav = remember { PageNav(layers) { screenToken(it) } }
        var navTick by remember { mutableIntStateOf(0) }
        // 读一次 navTick：转场结束时 +1，触发重组以回到静止的单层绘制。
        check(navTick >= 0)
        val gNow = androidx.compose.runtime.snapshots.Snapshot.withoutReadObservation { gesture.value }
        val transit = nav.update(
            layers,
            rootSize[0],
            gestureV = 1f - PageMotion.GESTURE_SHRINK * gNow,
            fallbackScale = 1f - 0.1f * gNow,
            fallbackShift = edgeShift(lastEdge[0], gNow),
        )
        val anim = remember(transit?.id) { androidx.compose.animation.core.Animatable(transit?.from ?: 1f) }
        LaunchedEffect(transit?.id) {
            val t = transit ?: return@LaunchedEffect
            // 新页在进度 0（不可见）的那一帧里完成首次组合与测量；之后每帧只改图层参数，不再重组二级页。
            val ms = when {
                t.origin == null -> PageMotion.FALLBACK_MS
                t.kind == TransitKind.Open -> PageMotion.OPEN_MS
                else -> PageMotion.CLOSE_MS
            }
            anim.animateTo(if (t.kind == TransitKind.Open) 1f else 0f, tween(ms, easing = PageMotion.MoveEase))
            nav.finish(t.id)
            navTick++
        }
        val gestureOn = backActive || gesture.isRunning
        val gestureOrigin = if (transit == null && gestureOn && layers.size >= 2) {
            nav.originOf(screenToken(layers.last()), screenToken(layers[layers.lastIndex - 1]), rootSize[0])
        } else null
        // 0 = 静止的顶层，1 = 下层，2 = 正在展开/收回的页，3 = 预测性返回中跟手的顶层
        val drawn: List<Pair<Screen, Int>> = when {
            transit != null -> listOf(transit.under to 1, transit.page to 2)
            gestureOn && layers.size >= 2 -> listOf(layers[layers.lastIndex - 1] to 1, layers.last() to 3)
            else -> listOf(layers.last() to 0)
        }
        val toastBackdrop = rememberCouiBackdrop()
        val screenCorner = rememberScreenCornerPx()
        Box(
            Modifier
                .fillMaxSize()
                .onSizeChanged { rootSize[0] = androidx.compose.ui.geometry.Size(it.width.toFloat(), it.height.toFloat()) }
                .background(shellBg),
        ) {
            // 轻提示的玻璃材质只采样「页面层」：录制背景的这一层里不能包含轻提示自己，
            // 否则提示胶囊会去画一张含有它自己的快照（0.1.0/0.1.1 真机上弹出轻提示即闪退）。
            // 常开录制：轻提示弹出的那一帧背景就已录好，不会去画一张还没录过的空层。
            Box(Modifier.fillMaxSize().then(if (useFloatingChrome) Modifier.couiBackdrop(toastBackdrop) else Modifier)) {
            drawn.forEach { (layer, role) ->
                key(screenToken(layer)) {
                    val bg = if (layer is Screen.RemoteReader) Color.Black else pageBg
                    val motion = when (role) {
                        2 -> transit!!.let { t ->
                            Modifier.expandTransform(t.origin, bg, t.fallbackScale, t.fallbackShift, screenCorner) { anim.value }
                        }
                        3 -> if (gestureOrigin != null) {
                            Modifier.expandTransform(gestureOrigin, bg, screenCorner = screenCorner) { 1f - PageMotion.GESTURE_SHRINK * gesture.value }
                        } else {
                            Modifier.graphicsLayer {
                                val p = gesture.value
                                val sc = 1f - 0.1f * p
                                scaleX = sc
                                scaleY = sc
                                translationX = edgeShift(lastEdge[0], p)
                                transformOrigin = TransformOrigin.Center
                                // 跟手缩小时保持屏幕圆角（随缩放换算，视觉半径不变），松手回满屏后才恢复直角。
                                roundedPage(p > 0f, screenCorner / sc)
                            }
                        }
                        else -> Modifier
                    }
                    val live = when (role) {
                        1 -> transit?.kind == TransitKind.Close
                        2 -> transit?.kind == TransitKind.Open
                        else -> true
                    }
                    Box(Modifier.fillMaxSize().then(motion).background(bg)) {
                        CompositionLocalProvider(LocalScreenLive provides live) {
                            Draw(layer, live = live)
                        }
                    }
                    if (role == 1) {
                        // 下层随上层展开轻微压暗（COUI 一镜到底的背景层），只在绘制阶段读进度。
                        Box(
                            Modifier.fillMaxSize().drawBehind {
                                val v = if (transit != null) anim.value else 1f - PageMotion.GESTURE_SHRINK * gesture.value
                                drawRect(Color.Black.copy(alpha = 0.12f * v.coerceIn(0f, 1f)))
                            }.then(
                                if (transit == null) Modifier.pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                    }
                                } else Modifier,
                            ),
                        )
                    }
                }
            }
            if (transit != null) {
                // 转场期间不接受点按，避免在动画中途再次跳转。
                Box(
                    Modifier.fillMaxSize().pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                        }
                    },
                )
            }
            }
        if (folderBook != null) {
            FolderPickSheet(
                lang = lang,
                folders = folders.toList(),
                onCreate = { name ->
                    val book = folderBook ?: return@FolderPickSheet
                    val title = name.trim()
                    if (title.isBlank()) return@FolderPickSheet
                    folders.add(BookFolder("f${System.currentTimeMillis()}", title, listOf(book)))
                    saveFolders()
                    folderBook = null
                },
                onPick = { fid ->
                    val book = folderBook ?: return@FolderPickSheet
                    val i = folders.indexOfFirst { it.id == fid }
                    if (i >= 0 && book !in folders[i].books) {
                        folders[i] = folders[i].copy(books = folders[i].books + book)
                        saveFolders()
                    }
                    folderBook = null
                },
                onDismiss = { folderBook = null },
            )
        }
        remoteAnnouncement?.let { note ->
            val close = {
                RemoteConfig.markAnnouncementSeen(context, note.id)
                remoteAnnouncement = null
            }
            Dialog(
                onDismissRequest = close,
                title = note.title,
                confirmAction = CouiDialogAction(
                    text = "知道了",
                    onClick = close,
                    style = CouiDialogActionStyle.Primary,
                    colors = CouiDialogDefaults.primaryActionColors(CouiTheme.colorScheme.accent),
                ),
                placement = CouiDialogPlacement.Bottom,
            ) {
                Text(
                    note.message,
                    fontSize = 14.sp,
                    color = CouiTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp).couiVerticalScroll(rememberScrollState()),
                )
            }
        }
        appUpdater.available?.let { release ->
            BackHandler(enabled = !appUpdater.downloading) { appUpdater.later() }
            AppUpdateDialog(release, appUpdater)
        }
        CouiToastHost(if (useFloatingChrome) toastBackdrop else null)
        }
        }
    }
}

internal enum class BarTitle { Below, End, Center }

@Composable
internal fun SubTopBar(
    title: String,
    onBack: () -> Unit,
    align: BarTitle = BarTitle.End,
    actions: @Composable () -> Unit = {},
) {
    val large = align == BarTitle.Below
    if (large) {
        TopAppBar(
            title = title,
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(CouiIcons.Outlined.Back, contentDescription = "返回") }
            },
            actions = { actions() },
        )
    } else {
        SmallTopAppBar(
            title = title,
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(CouiIcons.Outlined.Back, contentDescription = "返回") }
            },
            actions = { actions() },
        )
    }
}

@Composable
internal fun CornerMenu(expanded: Boolean, onExpandedChange: (Boolean) -> Unit, actions: List<MenuAction>) {
    val anchor = rememberCouiMenuAnchor()
    val anchorAt = remember { arrayOfNulls<androidx.compose.ui.layout.LayoutCoordinates>(1) }
    val anchorRadius = with(LocalDensity.current) { 20.dp.toPx() }
    Box(Modifier.couiMenuAnchor(anchor).onPlaced { anchorAt[0] = it }) {
        IconButton(onClick = { onExpandedChange(!expanded) }) {
            Icon(CouiIcons.Outlined.More, contentDescription = "更多", tint = AppTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
        }
        Menu(
            expanded = expanded && actions.isNotEmpty(),
            onDismissRequest = { onExpandedChange(false) },
            items = actions.mapIndexed { index, a ->
                CouiMenuItem(value = index, text = a.label, icon = a.icon, dividerBefore = a.dividerBefore)
            },
            onItemClick = { item ->
                onExpandedChange(false)
                // 菜单项打开的页面从右上角菜单按钮处展开。
                anchorAt[0]?.takeIf { it.isAttached }?.let { c ->
                    val p = c.positionInRoot()
                    ExpandOrigin.arm(androidx.compose.ui.geometry.Rect(p.x, p.y, p.x + c.size.width, p.y + c.size.height), anchorRadius)
                }
                actions[item.value].onClick()
            },
            anchor = anchor,
        )
    }
}

/** One entry of a page's 更多 (COUI floating) menu. */
internal data class MenuAction(
    val label: String,
    val icon: ImageVector,
    val dividerBefore: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
internal fun ShelfPane(
    padding: PaddingValues,
    copy: Copy,
    library: List<String>,
    crawled: List<CrawledWork>,
    progress: Map<String, Progress>,
    prefs: Prefs,
    opts: ShelfOpts,
    selecting: Boolean,
    selected: Set<String>,
    onOpen: (String) -> Unit,
    onToggle: (String) -> Unit,
    onLong: (String) -> Unit,
    onManual: (List<String>) -> Unit,
    onReorder: (List<String>) -> Unit,
    onTrash: () -> Unit,
    onRefresh: () -> Unit,
    refreshing: Boolean,
    tablet: Boolean = false,
    listState: LazyListState,
) {
    val lang = resolveLang(prefs.lang, LocalConfiguration.current)
    val metas = remember(library.joinToString(","), progress.entries.joinToString { "${it.key}:${it.value.chapterId}:${it.value.page}:${it.value.at}" }, crawled.joinToString { "${it.id}:${it.title}" }, opts, LocalStore.downloadRev) {
        library.mapIndexedNotNull { index, id -> shelfMeta(index, id, crawled, progress, prefs) }
    }
    val shown = remember(metas, opts) { filterShelf(metas, opts) }
    var dragId by remember { mutableStateOf<String?>(null) }
    var hover by remember { mutableIntStateOf(-1) }
    var finger by remember { mutableStateOf(Offset.Zero) }
    var touchInItem by remember { mutableStateOf(Offset.Zero) }
    var boxWin by remember { mutableStateOf(Offset.Zero) }
    var boxH by remember { mutableFloatStateOf(0f) }
    var itemW by remember { mutableFloatStateOf(0f) }
    var itemH by remember { mutableFloatStateOf(0f) }
    val slots = remember { mutableStateMapOf<String, Offset>() }
    val shownNow = rememberUpdatedState(shown)
    val reorderNow = rememberUpdatedState(onReorder)
    val longNow = rememberUpdatedState(onLong)
    val density = LocalDensity.current
    val cols = when {
        opts.layout == "list" -> 1
        tablet && opts.layout == "comfortable" -> 4
        tablet -> 6
        opts.layout == "comfortable" -> 2
        else -> 3
    }
    val gapX = with(density) { (if (cols == 2) 14.dp else 10.dp).toPx() }
    val gapY = with(density) { (if (opts.layout == "list") 0.dp else if (cols == 2) 16.dp else 12.dp).toPx() }
    val strideX = itemW + if (opts.layout == "list") 0f else gapX
    val strideY = (itemH + gapY).coerceAtLeast(1f)
    val follow = rememberUpdatedState<(String) -> Unit>({ id ->
        val slot = slots[id] ?: return@rememberUpdatedState
        val origin = shownNow.value.indexOfFirst { it.id == id }
        if (origin < 0 || strideX < 1f) return@rememberUpdatedState
        val rel = (finger - touchInItem) - slot
        val col0 = origin % cols
        val row0 = origin / cols
        val dCol = (rel.x / strideX).roundToInt()
        val dRow = (rel.y / strideY).roundToInt()
        val col = (col0 + dCol).coerceIn(0, cols - 1)
        val row = (row0 + dRow).coerceIn(0, shownNow.value.lastIndex / cols)
        hover = (row * cols + col).coerceIn(0, shownNow.value.lastIndex)
    })
    LaunchedEffect(dragId) {
        val id = dragId ?: return@LaunchedEffect
        while (isActive && dragId == id) {
            val top = with(density) { padding.calculateTopPadding().toPx() }
            val bottom = boxH - with(density) { padding.calculateBottomPadding().toPx() }
            val edge = with(density) { 56.dp.toPx() }
            val y = finger.y - boxWin.y
            val delta = when {
                y < top + edge -> -22f * (1f - ((y - top) / edge).coerceIn(0f, 1f))
                y > bottom - edge -> 22f * (((y - (bottom - edge)) / edge).coerceIn(0f, 1.4f))
                else -> 0f
            }
            if (delta != 0f) {
                listState.scroll { scrollBy(delta) }
                follow.value(id)
            }
            delay(16)
        }
    }
    fun commitDrag() {
        val id = dragId ?: return
        val list = shownNow.value
        val from = list.indexOfFirst { it.id == id }
        val to = hover
        if (from < 0 || to < 0 || from == to) return
        val ids = list.map { it.id }.toMutableList()
        val item = ids.removeAt(from)
        ids.add(to.coerceIn(0, ids.size), item)
        val base = filterShelf(
            metas,
            opts.copy(fDl = false, fUnread = false, fDone = false),
        ).map { it.id }
        onManual(base)
        reorderNow.value(ids)
    }
    val fromIndex = shown.indexOfFirst { it.id == dragId }
    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                boxWin = it.positionInWindow()
                boxH = it.size.height.toFloat()
            },
    ) {
    PullRefreshBox(
        refreshing = refreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (library.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState("¯\\_(ツ)_/¯", copy.emptyShelf)
            }
        } else if (shown.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState("(´・ω・`)", copy.emptyCat)
            }
        } else if (opts.layout == "list") {
            LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), state = listState, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                if (opts.count) {
                    item {
                        Text(
                            shelfWord(lang, "total").replace("%d", shown.size.toString()),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = AppTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                        )
                    }
                }
                items(shown, key = { it.id }) { m ->
                    val self = shown.indexOfFirst { it.id == m.id }
                    val visual = shelfShift(self, fromIndex, hover)
                    val sy = if (dragId == null || self < 0 || self == fromIndex) 0f else (visual - self) * strideY
                    if (selecting) {
                        ShelfListRow(m, opts, lang, selecting, m.id in selected, onOpen, onToggle, onLong)
                    } else {
                        ShelfCell(
                            id = m.id,
                            shiftX = 0f,
                            shiftY = sy,
                            dragging = dragId != null,
                            hidden = dragId == m.id,
                            onPlace = { slots[m.id] = it },
                            onSize = { w, h -> itemW = w; itemH = h },
                            onClick = { onOpen(m.id) },
                            onDragStart = { local ->
                                val slot = slots[m.id] ?: Offset.Zero
                                touchInItem = local
                                finger = slot + local
                                hover = shownNow.value.indexOfFirst { it.id == m.id }
                            },
                            onDrag = { amount, engaged ->
                                finger += amount
                                if (engaged && dragId != m.id) dragId = m.id
                                if (dragId == m.id) follow.value(m.id)
                            },
                            onDragEnd = { engaged ->
                                if (engaged && dragId == m.id) commitDrag()
                                else if (!engaged) longNow.value(m.id)
                                dragId = null
                                hover = -1
                            },
                            onDragCancel = {
                                dragId = null
                                hover = -1
                            },
                        ) {
                            ShelfListRow(m, opts, lang, selecting, m.id in selected, {}, {}, {}, hits = false)
                        }
                    }
                }
                item { Spacer(Modifier.height(28.dp)) }
            }
        } else {
            LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), state = listState, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                if (opts.count) {
                    item {
                        Text(
                            shelfWord(lang, "total").replace("%d", shown.size.toString()),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = AppTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                        )
                    }
                }
                items(shown.chunked(cols), key = { row -> row.joinToString { it.id } }) { row ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = if (cols == 2) 16.dp else 12.dp, vertical = if (cols == 2) 8.dp else 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (cols == 2) 14.dp else 10.dp),
                    ) {
                        row.forEach { m ->
                            val self = shown.indexOfFirst { it.id == m.id }
                            val visual = shelfShift(self, fromIndex, hover)
                            val sx = if (dragId == null || self < 0 || self == fromIndex) 0f else ((visual % cols) - (self % cols)) * strideX
                            val sy = if (dragId == null || self < 0 || self == fromIndex) 0f else ((visual / cols) - (self / cols)) * strideY
                            Box(Modifier.weight(1f)) {
                                if (selecting) {
                                    ShelfGridTile(m, opts, selecting, m.id in selected, onOpen, onToggle, onLong)
                                } else {
                                    ShelfCell(
                                        id = m.id,
                                        shiftX = sx,
                                        shiftY = sy,
                                        dragging = dragId != null,
                                        hidden = dragId == m.id,
                                        onPlace = { slots[m.id] = it },
                                        onSize = { w, h -> itemW = w; itemH = h },
                                        onClick = { onOpen(m.id) },
                                        onDragStart = { local ->
                                            val slot = slots[m.id] ?: Offset.Zero
                                            touchInItem = local
                                            finger = slot + local
                                            hover = shownNow.value.indexOfFirst { it.id == m.id }
                                        },
                                        onDrag = { amount, engaged ->
                                            finger += amount
                                            if (engaged && dragId != m.id) dragId = m.id
                                            if (dragId == m.id) follow.value(m.id)
                                        },
                                        onDragEnd = { engaged ->
                                            if (engaged && dragId == m.id) commitDrag()
                                            else if (!engaged) longNow.value(m.id)
                                            dragId = null
                                            hover = -1
                                        },
                                        onDragCancel = {
                                            dragId = null
                                            hover = -1
                                        },
                                    ) {
                                        ShelfGridTile(m, opts, selecting, m.id in selected, {}, {}, {}, hits = false)
                                    }
                                }
                            }
                        }
                        repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
                item { Spacer(Modifier.height(28.dp)) }
            }
        }
    }
    val ghost = shown.find { it.id == dragId }
    if (ghost != null && itemW > 0f) {
        val local = (finger - touchInItem) - boxWin
        Box(
            Modifier
                .zIndex(30f)
                .offset { IntOffset(local.x.roundToInt(), local.y.roundToInt()) }
                .width(with(density) { itemW.toDp() })
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .graphicsLayer {
                    val sx = if (itemW > 1f) (touchInItem.x / itemW).coerceIn(0f, 1f) else 0.5f
                    val sy = if (itemH > 1f) (touchInItem.y / itemH).coerceIn(0f, 1f) else 0.5f
                    transformOrigin = TransformOrigin(sx, sy)
                    scaleX = 1.04f
                    scaleY = 1.04f
                },
        ) {
            if (opts.layout == "list") {
                ShelfListRow(ghost, opts, lang, false, false, {}, {}, {}, hits = false)
            } else {
                ShelfGridTile(ghost, opts, false, false, {}, {}, {}, hits = false)
            }
        }
    }
    if (selecting && selected.isNotEmpty()) {
        CouiDangerFab(ShuIcons.Trash, "移除", onTrash, Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = padding.calculateBottomPadding() + 16.dp))
    }
    }
}

private data class ShelfMeta(
    val id: String,
    val title: String,
    val cover: String,
    val author: String,
    val genre: String,
    val local: Boolean,
    val downloaded: Boolean,
    val unread: Int,
    val done: Boolean,
    val readAt: Long,
    val checkedAt: Long,
    val chapters: Int,
    val latest: Int,
    val seen: Int,
    val chapterTitle: String,
    val addedAt: Long,
    val acquiredAt: Long,
)

private fun shelfMeta(
    index: Int,
    id: String,
    crawled: List<CrawledWork>,
    progress: Map<String, Progress>,
    prefs: Prefs,
): ShelfMeta? {
    val m = catalog.find { it.id == id }
    val w = crawled.find { it.id == id }
    val disk = LocalStore.loadWork(id)
    if (m == null && w == null && disk == null) return null
    val synthetic = 9_000_000_000_000L - index
    val chapters = disk?.chapters ?: emptyList()
    val total = chapters.size
    val p = progress[id]
    val readIdx = if (p == null) -1 else chapters.indexOfFirst { it.id == p.chapterId }
    val rawSeen = if (readIdx >= 0) chapters[readIdx].index else 0
    val seen = when {
        rawSeen > 0 -> rawSeen
        readIdx >= 0 -> readIdx + 1
        else -> 0
    }
    val unread = when {
        total <= 0 -> if (p == null || p.percent < 100) -1 else 0
        p == null -> total
        readIdx < 0 -> total
        p.percent >= 100 -> (total - readIdx - 1).coerceAtLeast(0)
        else -> (total - readIdx).coerceAtLeast(0)
    }
    val blob = listOfNotNull(w?.cat, w?.genres, w?.tags?.joinToString(" "), m?.status, m?.genres).joinToString(" ")
    val genre = w?.tags?.filter { it.isNotBlank() }?.take(2)?.joinToString(" ")?.ifBlank { null }
        ?: w?.cat?.takeIf { it.isNotBlank() && it != "推荐" }
        ?: w?.genres?.takeIf { it.isNotBlank() && it != "其他" }
        ?: m?.genres?.takeIf { it.isNotBlank() }
        ?: ""
    return ShelfMeta(
        id = id,
        title = realTitle(m?.title, w?.title, disk?.work?.title, id).ifBlank { id },
        cover = m?.cover ?: w?.cover ?: disk?.work?.cover.orEmpty(),
        author = m?.author ?: w?.author ?: disk?.work?.author.orEmpty(),
        genre = genre,
        local = disk != null || m != null,
        downloaded = LocalStore.isDownloaded(id),
        unread = unread,
        done = blob.contains("完结") || blob.contains("完結"),
        readAt = p?.at ?: 0L,
        checkedAt = disk?.fetchedAt ?: 0L,
        chapters = if (total > 0) total else m?.chapters?.size ?: 0,
        latest = chapters.maxOfOrNull { it.index } ?: m?.chapters?.size ?: 0,
        seen = seen,
        chapterTitle = when {
            readIdx >= 0 -> chapters[readIdx].title
            else -> m?.chapters?.find { it.id == p?.chapterId }?.title.orEmpty()
        },
        addedAt = prefs.addedAt(id).takeIf { it > 0 } ?: synthetic,
        acquiredAt = disk?.fetchedAt?.takeIf { it > 0 } ?: synthetic,
    )
}

private fun filterShelf(items: List<ShelfMeta>, opts: ShelfOpts): List<ShelfMeta> {
    val filtered = items.filter { m ->
        if (opts.fDl && !m.downloaded) return@filter false
        if (opts.fUnread && m.unread == 0) return@filter false
        if (opts.fDone && !m.done) return@filter false
        true
    }
    if (opts.sort == "manual") return filtered
    val dir = if (opts.asc) 1 else -1
    return filtered.sortedWith { a, b ->
        val c = when (opts.sort) {
            "name" -> a.title.compareTo(b.title, ignoreCase = true)
            "recentRead" -> a.readAt.compareTo(b.readAt)
            "recentCheck" -> a.checkedAt.compareTo(b.checkedAt)
            "unread" -> a.unread.compareTo(b.unread)
            "chapters" -> a.chapters.compareTo(b.chapters)
            "latest" -> a.latest.compareTo(b.latest)
            "added" -> a.addedAt.compareTo(b.addedAt)
            else -> a.acquiredAt.compareTo(b.acquiredAt)
        }
        c * dir
    }
}

private fun shelfShift(index: Int, from: Int, to: Int): Int {
    if (index < 0 || from < 0 || to < 0 || from == to) return index
    if (index == from) return to
    if (from < to && index in (from + 1)..to) return index - 1
    if (to < from && index in to until from) return index + 1
    return index
}

@Composable
private fun ShelfCell(
    id: String,
    shiftX: Float,
    shiftY: Float,
    dragging: Boolean,
    hidden: Boolean,
    onPlace: (Offset) -> Unit,
    onSize: (Float, Float) -> Unit,
    onClick: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset, Boolean) -> Unit,
    onDragEnd: (Boolean) -> Unit,
    onDragCancel: () -> Unit,
    content: @Composable () -> Unit,
) {
    val placeN = rememberUpdatedState(onPlace)
    val sizeN = rememberUpdatedState(onSize)
    val clickN = rememberUpdatedState(onClick)
    val startN = rememberUpdatedState(onDragStart)
    val dragN = rememberUpdatedState(onDrag)
    val endN = rememberUpdatedState(onDragEnd)
    val cancelN = rememberUpdatedState(onDragCancel)
    val ax = animateFloatAsState(if (dragging) shiftX else 0f, if (dragging) tween(170) else snap(), label = "sx$id")
    val ay = animateFloatAsState(if (dragging) shiftY else 0f, if (dragging) tween(170) else snap(), label = "sy$id")
    Box(
        Modifier
            .zIndex(if (shiftX != 0f || shiftY != 0f) 1f else 0f)
            .expandSource(16.dp)
            .onGloballyPositioned { placeN.value(it.positionInWindow()) }
            .onSizeChanged { sizeN.value(it.width.toFloat(), it.height.toFloat()) },
    ) {
        Box(
            Modifier.graphicsLayer {
                translationX = ax.value
                translationY = ay.value
                alpha = if (hidden) 0f else 1f
            },
        ) {
            content()
            Box(
                Modifier
                    .matchParentSize()
                    .couiClickable { clickN.value() }
                    .pointerInput(id) {
                        var moved = false
                        var travel = 0f
                        detectDragGesturesAfterLongPress(
                            onDragStart = { local ->
                                moved = false
                                travel = 0f
                                startN.value(local)
                            },
                            onDragEnd = { endN.value(moved) },
                            onDragCancel = { cancelN.value() },
                            onDrag = { change, amount ->
                                change.consume()
                                travel += amount.getDistance()
                                if (travel > viewConfiguration.touchSlop * 1.4f) moved = true
                                dragN.value(amount, moved)
                            },
                        )
                    },
            )
        }
    }
}

@Composable
private fun ShelfGridTile(
    m: ShelfMeta,
    opts: ShelfOpts,
    selecting: Boolean,
    selected: Boolean,
    onOpen: (String) -> Unit,
    onToggle: (String) -> Unit,
    onLong: (String) -> Unit,
    hits: Boolean = true,
) {
    Card(
        Modifier.couiPress(hits),
        cornerRadius = 16.dp,
        onClick = if (!hits) null else {
            { if (selecting) onToggle(m.id) else onOpen(m.id) }
        },
        onLongPress = if (!hits) null else {
            { onLong(m.id) }
        },
    ) {
        Box {
            SmartCover(m.cover, Modifier.fillMaxWidth().aspectRatio(2f / 3f))
            if (selecting) {
                Box(Modifier.align(Alignment.TopStart).padding(6.dp)) { Checkbox(checked = selected) }
            }
            ShelfBadges(m, opts)
        }
        Text(m.title, Modifier.padding(start = 6.dp, end = 6.dp, top = 6.dp), maxLines = 1)
        if (opts.genre && m.genre.isNotBlank()) {
            Text(m.genre, Modifier.padding(start = 6.dp, end = 6.dp), fontSize = 11.sp, color = AppTheme.colorScheme.onSurfaceVariantSummary, maxLines = 1)
        }
        Text(
            m.author.ifBlank { "在线" },
            Modifier.padding(start = 6.dp, end = 6.dp, bottom = 8.dp),
            fontSize = 11.sp,
            color = AppTheme.colorScheme.onSurfaceVariantSummary,
            maxLines = 1,
        )
    }
}

@Composable
private fun ShelfListRow(
    m: ShelfMeta,
    opts: ShelfOpts,
    lang: String,
    selecting: Boolean,
    selected: Boolean,
    onOpen: (String) -> Unit,
    onToggle: (String) -> Unit,
    onLong: (String) -> Unit,
    hits: Boolean = true,
) {
    val stamp = shelfStamp(m, lang)
    Card(
        Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth(),
        onClick = if (!hits) null else {
            { if (selecting) onToggle(m.id) else onOpen(m.id) }
        },
        onLongPress = if (!hits) null else {
            { onLong(m.id) }
        },
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (selecting) {
                Checkbox(checked = selected)
                Spacer(Modifier.width(8.dp))
            }
            Box {
                SmartCover(m.cover, Modifier.width(56.dp).height(78.dp).clip(RoundedCornerShape(8.dp)))
                ShelfBadges(m, opts, compact = true)
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(m.title, maxLines = 2, fontWeight = FontWeight.Medium)
                if (m.author.isNotBlank()) {
                    Text(m.author, fontSize = 12.sp, color = AppTheme.colorScheme.onSurfaceVariantSummary, maxLines = 1)
                }
                if (stamp.isNotBlank()) {
                    Text(
                        stamp,
                        fontSize = 12.sp,
                        color = AppTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (opts.genre && m.genre.isNotBlank()) {
                    Text(m.genre, fontSize = 12.sp, color = AppTheme.colorScheme.primary, maxLines = 1)
                }
            }
        }
    }
}

private fun shelfStamp(m: ShelfMeta, lang: String): String {
    val ch = when {
        m.seen > 0 -> when (lang) {
            "en" -> "Ep. ${m.seen}"
            "zh-TW" -> "第${m.seen}話"
            else -> "第${m.seen}话"
        }
        m.chapterTitle.isNotBlank() -> m.chapterTitle
        else -> ""
    }
    val day = if (m.readAt > 0L) dayLabel(m.readAt, lang) else ""
    val clock = if (m.readAt > 0L) watchClock(m.readAt, lang) else ""
    return listOf(ch, day, clock).filter { it.isNotBlank() }.joinToString(" · ")
}

@Composable
private fun BoxScope.ShelfBadges(m: ShelfMeta, opts: ShelfOpts, compact: Boolean = false) {
    val showSeen = opts.markUnread && m.seen > 0
    if (showSeen) {
        Text(
            "${m.seen}",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(AppTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                .padding(horizontal = if (compact) 4.dp else 6.dp, vertical = 1.dp),
            color = Color.White,
            fontSize = if (compact) 10.sp else 11.sp,
        )
    }
    if (opts.markLocal && m.local) {
        Text(
            "本",
            modifier = Modifier
                .align(if (showSeen) Alignment.TopStart else Alignment.TopEnd)
                .padding(4.dp)
                .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp),
            color = Color.White,
            fontSize = 10.sp,
        )
    }
    if (opts.markDl && m.downloaded) {
        Text(
            "↓",
            modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BannerPane(
    lang: String,
    path: String,
    onPick: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    val title = if (lang == "en") "Header background" else if (lang == "zh-TW") "背景欄" else "背景栏"
    val pick = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onPick(uri.toString())
    }
    val choose = if (lang == "en") "Choose from gallery" else if (lang == "zh-TW") "從相簿選擇" else "从相册选择"
    val reset = if (lang == "en") "Use the default" else if (lang == "zh-TW") "恢復預設" else "恢复默认"
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.fillMaxWidth().height(196.dp).clip(RoundedCornerShape(18.dp))) {
                if (path.isNotBlank() && File(path).exists()) {
                    SmartCover(path, Modifier.fillMaxSize())
                } else {
                    Image(
                        painter = painterResource(R.drawable.mine_snow),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            CouiButton(choose, { pick.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, Modifier.fillMaxWidth(), kind = CouiButtonKind.Primary)
            CouiButton(reset, onClear, Modifier.fillMaxWidth())
        }
    }
}

@Composable
internal fun FolderPickSheet(
    lang: String,
    folders: List<BookFolder>,
    onCreate: (String) -> Unit,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val title = when (lang) { "en" -> "Collection"; "zh-TW" -> "收藏夾"; else -> "收藏夹" }
    val create = when (lang) { "en" -> "Create"; "zh-TW" -> "建立並加入"; else -> "创建并加入" }
    Dialog(
        onDismissRequest = onDismiss,
        title = title,
        confirmAction = CouiDialogAction(text = when (lang) { "en" -> "Cancel"; else -> "取消" }, onClick = onDismiss),
        placement = CouiDialogPlacement.Bottom,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = true),
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CouiInputField(
                value = name,
                onValueChange = { name = it },
                placeholder = when (lang) { "en" -> "Name"; "zh-TW" -> "收藏夾名稱"; else -> "收藏夹名称" },
            )
            CouiButton(create, { onCreate(name) }, Modifier.fillMaxWidth(), kind = CouiButtonKind.Primary, enabled = name.isNotBlank())
        }
        if (folders.isNotEmpty()) Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
            folders.forEach { folder ->
                ArrowPreference(folder.name, summary = "${folder.books.size}", onClick = { onPick(folder.id) })
            }
        }
    }
}

@Composable
private fun FolderPane(
    lang: String,
    name: String,
    ids: List<String>,
    layout: String,
    crawled: List<CrawledWork>,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onRemove: (String) -> Unit,
    onRename: (String) -> Unit,
    onLayout: (String) -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf(false) }
    var picking by remember { mutableStateOf(false) }
    val selected = remember { mutableStateListOf<String>() }
    var ask by remember { mutableStateOf(false) }
    var draft by remember(name) { mutableStateOf(name) }
    val title = name.ifBlank { if (lang == "en") "Collection" else "收藏夹" }
    fun label(zh: String, tw: String, en: String) = if (lang == "en") en else if (lang == "zh-TW") tw else zh
    BackHandler(enabled = ask || renaming || menu || picking) {
        when {
            ask -> ask = false
            renaming -> renaming = false
            menu -> menu = false
            else -> {
                picking = false
                selected.clear()
            }
        }
    }
    fun arm(id: String) {
        if (!picking) {
            picking = true
            menu = false
            selected.clear()
        }
        if (selected.contains(id)) selected.remove(id) else selected.add(id)
    }
    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = if (picking) {
                        if (lang == "en") "Selected ${selected.size}" else "已选 ${selected.size}"
                    } else {
                        title
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (picking) {
                                picking = false
                                selected.clear()
                            } else {
                                onBack()
                            }
                        }) { Icon(CouiIcons.Outlined.Back, contentDescription = "返回") }
                    },
                    actions = {
                        if (picking) {
                            ToolbarTextButton(label("取消", "取消", "Cancel")) {
                                picking = false
                                selected.clear()
                            }
                            ToolbarGap()
                            ToolbarTextButton(label("全选", "全選", "All"), accent = true) {
                                selected.clear()
                                selected.addAll(ids)
                            }
                        } else {
                            CornerMenu(
                                expanded = menu,
                                onExpandedChange = { menu = it },
                                actions = listOf(
                                    MenuAction(label("改名", "改名", "Rename"), ShuIcons.Edit) {
                                        draft = name
                                        renaming = true
                                    },
                                    if (layout == "v") MenuAction(label("改为列表", "改為列表", "List"), ShuIcons.Rows) { onLayout("list") }
                                    else MenuAction(label("改为竖版", "改為豎版", "Vertical"), CouiIcons.Outlined.LayoutSpliced) { onLayout("v") },
                                ),
                            )
                        }
                    },
                )
            },
        ) { padding ->
            if (ids.isEmpty()) {
                EmptyState("(´・ω・`)", label("这里还没有漫画", "這裡還沒有漫畫", "Nothing here"), Modifier.fillMaxSize().padding(padding))
            } else if (layout == "v") {
                LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                    items(ids.chunked(2)) { row ->
                        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { id ->
                                val w = crawled.find { it.id == id }
                                val disk = LocalStore.loadWork(id)?.work
                                val book = realTitle(w?.title, disk?.title, id)
                                Column(
                                    Modifier.weight(1f).expandSource(16.dp).couiClickable(
                                        onClick = { if (picking) arm(id) else onOpen(id) },
                                        onLongClick = { arm(id) },
                                    ),
                                ) {
                                    Box {
                                        SmartCover(coverOf(id, crawled), Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(8.dp)))
                                        if (picking) Box(Modifier.padding(6.dp)) { Checkbox(checked = id in selected) }
                                    }
                                    Text(book, modifier = Modifier.padding(top = 6.dp), maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 13.sp)
                                }
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                    item { Spacer(Modifier.height(88.dp)) }
                }
            } else {
                LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                    items(ids, key = { it }) { id ->
                        val w = crawled.find { it.id == id }
                        val disk = LocalStore.loadWork(id)?.work
                        val book = realTitle(w?.title, disk?.title, id)
                        Card(Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().couiClickable(
                                    onClick = { if (picking) arm(id) else onOpen(id) },
                                    onLongClick = { arm(id) },
                                ).padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (picking) {
                                    Checkbox(checked = id in selected)
                                    Spacer(Modifier.width(10.dp))
                                }
                                SmartCover(
                                    coverOf(id, crawled),
                                    Modifier.width(52.dp).height(72.dp).clip(RoundedCornerShape(6.dp)),
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(book, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(88.dp)) }
                }
            }
        }
        if (picking && selected.isNotEmpty()) {
            CouiDangerFab(ShuIcons.Trash, "删除", { ask = true }, Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(end = 20.dp, bottom = 16.dp))
        }
        if (renaming) {
            CouiNameDialog(
                title = label("改名", "改名", "Rename"),
                initial = draft,
                placeholder = label("收藏夹名称", "收藏夾名稱", "Name"),
                confirm = label("保存", "儲存", "Save"),
                cancel = label("取消", "取消", "Cancel"),
                onConfirm = { onRename(it); renaming = false },
                onDismiss = { renaming = false },
            )
        }
        if (ask) {
            ConfirmSheet(
                title = label("移出收藏夹？", "移出收藏夾？", "Remove?"),
                message = label("所选漫画会离开这个收藏夹。", "所選漫畫會離開這個收藏夾。", "Selected titles leave this folder."),
                confirm = label("删除", "刪除", "Delete"),
                cancel = label("取消", "取消", "Cancel"),
                onConfirm = {
                    selected.toList().forEach { onRemove(it) }
                    selected.clear()
                    picking = false
                    ask = false
                },
                onDismiss = { ask = false },
            )
        }
    }
}

@Composable
private fun MorePane(
    lang: String,
    incognito: Boolean,
    onIncognito: (Boolean) -> Unit,
    peekGuard: Boolean,
    onPeek: (Boolean) -> Unit,
    fontGear: String,
    onFont: () -> Unit,
    forceTablet: Boolean,
    onForceTablet: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val title = when (lang) { "en" -> "More settings"; "zh-TW" -> "更多設定"; else -> "更多设置" }
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                val fontSum = when (fontGear) {
                    "s" -> if (lang == "en") "Small" else "小"
                    "m" -> if (lang == "en") "Medium" else "中"
                    "l" -> if (lang == "en") "Large" else "大"
                    else -> if (lang == "en") "Follow system" else if (lang == "zh-TW") "跟隨系統" else "跟随系统"
                }
                Group(title = when (lang) { "en" -> "Display"; "zh-TW" -> "顯示"; else -> "显示" }) {
                    ArrowPreference(
                        title = when (lang) { "en" -> "Adjust type"; "zh-TW" -> "調整字體"; else -> "调整字体" },
                        summary = fontSum,
                        onClick = onFont,
                    )
                    SwitchPreference(
                        checked = forceTablet,
                        onCheckedChange = onForceTablet,
                        title = when (lang) { "en" -> "Force tablet layout"; "zh-TW" -> "強制使用平板界面"; else -> "强制使用平板界面" },
                        summary = when (lang) {
                            "en" -> "Restart to apply. Store then uses a list"
                            "zh-TW" -> "開啟後需要重新啟動應用才生效。書城改為列表"
                            else -> "打开后需要重启应用才生效。书城改为列表"
                        },
                    )
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                Group(title = when (lang) { "en" -> "Privacy"; "zh-TW" -> "隱私"; else -> "隐私" }) {
                    SwitchPreference(
                        checked = incognito,
                        onCheckedChange = onIncognito,
                        title = when (lang) { "en" -> "Incognito"; "zh-TW" -> "隱身模式"; else -> "隐身模式" },
                        summary = when (lang) {
                            "en" -> "Don't record reading history"
                            "zh-TW" -> "開啟後不記錄閱讀歷史"
                            else -> "开启后不记录阅读历史"
                        },
                    )
                    SwitchPreference(
                        checked = peekGuard,
                        onCheckedChange = onPeek,
                        title = when (lang) { "en" -> "Anti-peek"; "zh-TW" -> "防窺"; else -> "防窥" },
                        summary = when (lang) {
                            "en" -> "Hide the preview when switching apps, and block screenshots and recording"
                            "zh-TW" -> "切換應用時隱藏預覽，並禁止截屏錄屏"
                            else -> "切换应用时隐藏预览，并禁止截屏录屏"
                        },
                    )

                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

@Composable
internal fun TabletRail(tab: Tab, copy: Copy, onTab: (Tab) -> Unit) {
    val bookOpen = CouiIcons.Filled.BookOpen
    val userCircle = CouiIcons.Filled.UserCircle
    val items = buildList {
        add(Triple(Tab.Shelf, if (tab == Tab.Shelf) bookOpen else ShuIcons.BookOpenOutlined, copy.shelf))
        add(Triple(Tab.History, if (tab == Tab.History) ShuIcons.HistoryFilled else ShuIcons.HistoryOutlined, copy.history))
        if (RemoteConfig.entryEnabled) {
            add(Triple(Tab.Discover, if (tab == Tab.Discover) ShuIcons.StoreFilled else ShuIcons.StoreOutlined, copy.discover))
        }
        add(Triple(Tab.Mine, if (tab == Tab.Mine) userCircle else ShuIcons.UserCircleOutlined, copy.mine))
    }
    Column(
        Modifier
            .width(88.dp)
            .fillMaxHeight()
            .background(CouiTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 20.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { (dest, icon, label) ->
            TabletRailItem(icon, label, tab == dest) { onTab(dest) }
        }
    }
}

/** 平板侧栏项：COUI 选中胶囊（主题色 12% 底）+ 线框/实心图标切换 + COUI 字体，按压缩放与反馈。 */
@Composable
private fun TabletRailItem(icon: ImageVector, label: String, on: Boolean, onClick: () -> Unit) {
    val accent = CouiTheme.colorScheme.accent
    val tint = if (on) accent else CouiTheme.colorScheme.onSurface.copy(alpha = 0.72f)
    val source = remember { MutableInteractionSource() }
    val pill by androidx.compose.animation.animateColorAsState(if (on) accent.copy(alpha = 0.12f) else Color.Transparent, label = "rail")
    Column(
        Modifier
            .width(72.dp)
            .couiPress()
            .clip(CouiShape(18.dp, 3f))
            .couiButtonFeedback(CouiTheme.isDark, source, true, list = true)
            .selectable(on, source, null, role = androidx.compose.ui.semantics.Role.Tab, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(width = 56.dp, height = 32.dp).clip(CouiCapsuleShape).background(pill), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(4.dp))
        CouiText(text = label, kind = CouiTextKind.GroupTitle, color = tint, maxLines = 1)
    }
}

private fun shelfWord(lang: String, key: String): String {
    val zh = mapOf(
        "filter" to "筛选",
        "sort" to "排序",
        "display" to "显示",
        "downloaded" to "已下载",
        "unread" to "未读",
        "done" to "已完结",
        "name" to "名称",
        "recentRead" to "最近阅读",
        "recentCheck" to "最近检查",
        "unreadSort" to "未读",
        "chapters" to "章节数",
        "latest" to "最新章节",
        "acquired" to "获取日期",
        "added" to "新增日期",
        "manual" to "手动",
        "mode" to "显示模式",
        "compact" to "紧凑网格",
        "comfortable" to "松散网格",
        "list" to "列表",
        "marks" to "标记",
        "markDl" to "已下载标记",
        "markUnread" to "未读标记",
        "markLocal" to "本地标记",
        "labels" to "标签",
        "genre" to "显示分类标签",
        "count" to "显示漫画数目",
        "total" to "共 %d 部",
    )
    val tw = zh + mapOf(
        "filter" to "篩選",
        "display" to "顯示",
        "downloaded" to "已下載",
        "unread" to "未讀",
        "done" to "已完結",
        "recentRead" to "最近閱讀",
        "recentCheck" to "最近檢查",
        "unreadSort" to "未讀",
        "chapters" to "章節數",
        "latest" to "最新章節",
        "acquired" to "獲取日期",
        "added" to "新增日期",
        "manual" to "手動",
        "mode" to "顯示模式",
        "compact" to "緊湊網格",
        "comfortable" to "鬆散網格",
        "marks" to "標記",
        "markDl" to "已下載標記",
        "markUnread" to "未讀標記",
        "markLocal" to "本地標記",
        "labels" to "標籤",
        "genre" to "顯示分類標籤",
        "count" to "顯示漫畫數目",
        "total" to "共 %d 部",
    )
    val en = mapOf(
        "filter" to "Filter",
        "sort" to "Sort",
        "display" to "Display",
        "downloaded" to "Downloaded",
        "unread" to "Unread",
        "done" to "Completed",
        "name" to "Title",
        "recentRead" to "Last read",
        "recentCheck" to "Last check",
        "unreadSort" to "Unread",
        "chapters" to "Chapters",
        "latest" to "Latest chapter",
        "acquired" to "Date fetched",
        "added" to "Date added",
        "manual" to "Manual",
        "mode" to "Display mode",
        "compact" to "Compact grid",
        "comfortable" to "Comfortable grid",
        "list" to "List",
        "marks" to "Badges",
        "markDl" to "Downloaded badge",
        "markUnread" to "Unread badge",
        "markLocal" to "Local badge",
        "labels" to "Labels",
        "genre" to "Show categories",
        "count" to "Show manga count",
        "total" to "%d titles",
    )
    val table = when (lang) {
        "en" -> en
        "zh-TW" -> tw
        else -> zh
    }
    return table[key] ?: key
}

@Composable
internal fun ShelfSheet(
    lang: String,
    opts: ShelfOpts,
    onChange: (ShelfOpts) -> Unit,
    onDismiss: () -> Unit,
) {
    var page by remember { mutableStateOf(0) }
    Dialog(
        onDismissRequest = onDismiss,
        confirmAction = CouiDialogAction(text = if (lang == "en") "Done" else "完成", onClick = onDismiss),
        placement = CouiDialogPlacement.Bottom,
    ) {
        CouiTabs(
            items = listOf("filter", "sort", "display").mapIndexed { i, key -> i to shelfWord(lang, key) },
            selected = page,
            onSelect = { page = it },
            background = CouiTheme.colorScheme.menuSurface,
        )
        Column(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp)
                .couiVerticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            when (page) {
                0 -> {
                    CheckboxPreference(shelfWord(lang, "downloaded"), opts.fDl, { onChange(opts.copy(fDl = it)) })
                    CheckboxPreference(shelfWord(lang, "unread"), opts.fUnread, { onChange(opts.copy(fUnread = it)) })
                    CheckboxPreference(shelfWord(lang, "done"), opts.fDone, { onChange(opts.copy(fDone = it)) })
                }
                1 -> {
                    listOf(
                        "manual", "name", "recentRead", "recentCheck", "unread", "chapters", "latest", "acquired", "added",
                    ).forEach { key ->
                        val on = opts.sort == key
                        RadioButtonPreference(
                            title = shelfWord(lang, if (key == "unread") "unreadSort" else key),
                            selected = on,
                            summary = if (on) (if (opts.asc) (if (lang == "en") "Ascending · tap to flip" else "升序 · 再点切换") else (if (lang == "en") "Descending · tap to flip" else "降序 · 再点切换")) else null,
                            onClick = { onChange(if (on) opts.copy(asc = !opts.asc) else opts.copy(sort = key)) },
                        )
                    }
                }
                else -> {
                    SmallTitle(shelfWord(lang, "mode"))
                    listOf("compact", "comfortable", "list").forEach { id ->
                        RadioButtonPreference(shelfWord(lang, id), opts.layout == id, { onChange(opts.copy(layout = id)) })
                    }
                    SmallTitle(shelfWord(lang, "marks"))
                    CheckboxPreference(shelfWord(lang, "markDl"), opts.markDl, { onChange(opts.copy(markDl = it)) })
                    CheckboxPreference(shelfWord(lang, "markUnread"), opts.markUnread, { onChange(opts.copy(markUnread = it)) })
                    CheckboxPreference(shelfWord(lang, "markLocal"), opts.markLocal, { onChange(opts.copy(markLocal = it)) })
                    SmallTitle(shelfWord(lang, "labels"))
                    CheckboxPreference(shelfWord(lang, "genre"), opts.genre, { onChange(opts.copy(genre = it)) })
                    CheckboxPreference(shelfWord(lang, "count"), opts.count, { onChange(opts.copy(count = it)) })
                }
            }
        }
    }
}

@Composable
internal fun HistoryPane(
    padding: PaddingValues,
    copy: Copy,
    lang: String,
    history: List<String>,
    progress: Map<String, Progress>,
    crawled: List<CrawledWork>,
    selecting: Boolean,
    selected: Set<String>,
    onOpen: (String) -> Unit,
    onToggle: (String) -> Unit,
    onLong: (String) -> Unit,
    onTrash: () -> Unit,
    layout: String = "v",
    listState: LazyListState,
) {
    Box(Modifier.fillMaxSize()) {
        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState("(; ￣Д￣)", copy.emptyHistory)
            }
        } else {
            LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), state = listState, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                val visible = history
                val groups = visible.groupBy { dayLabel(progress[it]?.at ?: 0L, lang) }
                groups.forEach { (day, ids) ->
                    item(key = "day-$day") {
                        Text(
                            day,
                            modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 6.dp),
                            fontSize = 13.sp,
                            color = AppTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                    item(key = "card-$day") {
                        if (layout == "h") {
                            Column(Modifier.padding(horizontal = 12.dp)) {
                                ids.chunked(3).forEach { row ->
                                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        row.forEach { id ->
                                            val m = catalog.find { it.id == id }
                                            val w = crawled.find { it.id == id }
                                            val disk = LocalStore.loadWork(id)?.work
                                            val title = realTitle(m?.title, w?.title, disk?.title, id)
                                            val cover = m?.cover ?: w?.cover ?: disk?.cover
                                            Column(
                                                Modifier.weight(1f).expandSource(16.dp).couiClickable(
                                                    onClick = { if (selecting) onToggle(id) else onOpen(id) },
                                                    onLongClick = { onLong(id) },
                                                ),
                                            ) {
                                                Box {
                                                    if (!cover.isNullOrBlank()) {
                                                        SmartCover(cover, Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(8.dp)))
                                                    } else {
                                                        Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f).background(AppTheme.colorScheme.surface))
                                                    }
                                                    if (selecting) {
                                                        Box(Modifier.padding(6.dp)) { Checkbox(checked = id in selected) }
                                                    }
                                                }
                                                Text(title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                            }
                                        }
                                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                                    }
                                }
                            }
                        } else {
                        Group {
                            ids.forEach { id ->
                                val m = catalog.find { it.id == id }
                                val w = crawled.find { it.id == id }
                                val diskTitle = LocalStore.loadWork(id)?.work?.title
                                val title = realTitle(m?.title, w?.title, diskTitle, id)
                                val summary = historyLine(id, progress[id], lang, copy.historySub)
                                val cover = m?.cover ?: w?.cover ?: LocalStore.loadWork(id)?.work?.cover
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .expandSource(16.dp)
                                        .pointerInput(id, selecting) {
                                            detectTapGestures(
                                                onLongPress = { onLong(id) },
                                                onTap = { if (selecting) onToggle(id) else onOpen(id) },
                                            )
                                        }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (selecting) {
                                        Checkbox(checked = id in selected)
                                        Spacer(Modifier.width(10.dp))
                                    }
                                    if (!cover.isNullOrBlank()) {
                                        SmartCover(cover, Modifier.width(40.dp).height(56.dp).clip(RoundedCornerShape(8.dp)))
                                        Spacer(Modifier.width(12.dp))
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(
                                            summary,
                                            fontSize = 12.sp,
                                            color = AppTheme.colorScheme.onSurfaceVariantSummary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                        }
                    }
                }
                item { Spacer(Modifier.height(88.dp)) }
            }
        }
        if (selecting && selected.isNotEmpty()) {
            CouiDangerFab(ShuIcons.Trash, "删除", onTrash, Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = padding.calculateBottomPadding() + 16.dp))
        }
    }
}

private fun keywordHit(title: String, q: String): Boolean {
    if (q.isBlank()) return true
    return title.contains(q, ignoreCase = true)
}

private fun historyLine(id: String, p: Progress?, lang: String, fallback: String): String {
    if (p == null) return fallback
    val disk = LocalStore.loadWork(id)
    val local = catalog.find { it.id == id }
    val cached = disk?.chapters?.find { it.id == p.chapterId }
    val built = local?.chapters?.find { it.id == p.chapterId }
    val title = cached?.title ?: built?.title.orEmpty()
    val idx = cached?.index?.takeIf { it > 0 }
        ?: local?.chapters?.indexOfFirst { it.id == p.chapterId }?.takeIf { it >= 0 }?.plus(1)
        ?: Regex("(\\d+)").find(title)?.groupValues?.getOrNull(1)?.toIntOrNull()
    val unit = when {
        title.contains("章") -> if (lang == "en") "ch." else "章"
        lang == "en" -> "ep."
        else -> "话"
    }
    val head = if (idx != null) {
        if (lang == "en") "At $unit $idx" else "看到第${idx}$unit"
    } else if (title.isNotBlank()) title else if (lang == "en") "Read" else "已阅读"
    return listOf(head, dayLabel(p.at, lang), watchClock(p.at, lang)).filter { it.isNotBlank() }.joinToString(" · ")
}

private fun watchClock(at: Long, lang: String): String {
    if (at <= 0L) return ""
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = at }
    val hour24 = cal.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
    val am = hour24 < 12
    val h12 = when (val h = hour24 % 12) { 0 -> 12; else -> h }
    return if (lang == "en") {
        "$h12:$minute ${if (am) "AM" else "PM"}"
    } else {
        "${if (am) "上午" else "下午"}$h12:$minute"
    }
}

private fun dayLabel(at: Long, lang: String): String {
    if (at <= 0L) return if (lang == "en") "Earlier" else if (lang == "zh-TW") "更早" else "更早"
    val now = java.util.Calendar.getInstance()
    val day = java.util.Calendar.getInstance().apply { timeInMillis = at }
    fun same(a: java.util.Calendar, b: java.util.Calendar) =
        a.get(java.util.Calendar.YEAR) == b.get(java.util.Calendar.YEAR) &&
            a.get(java.util.Calendar.DAY_OF_YEAR) == b.get(java.util.Calendar.DAY_OF_YEAR)
    if (same(now, day)) return if (lang == "en") "Today" else if (lang == "zh-TW") "今天" else "今天"
    now.add(java.util.Calendar.DAY_OF_YEAR, -1)
    if (same(now, day)) return if (lang == "en") "Yesterday" else if (lang == "zh-TW") "昨天" else "昨天"
    val month = day.get(java.util.Calendar.MONTH) + 1
    val date = day.get(java.util.Calendar.DAY_OF_MONTH)
    return if (lang == "en") "$month/$date" else "${month}月${date}日"
}

@Composable
internal fun DiscoverPane(
    padding: PaddingValues,
    copy: Copy,
    lang: String,
    crawled: List<CrawledWork>,
    webSites: List<WebSite>,
    storeSite: String,
    online: Boolean,
    cols: Int,
    asList: Boolean = false,
    onOpen: (String) -> Unit,
    onSearch: () -> Unit,
    onPickSource: () -> Unit,
    onWeb: (String) -> Unit,
    onToggleCols: () -> Unit,
    onCrawl: (WebSite, String, () -> Unit) -> Unit,
    listState: LazyListState,
    catState: androidx.compose.runtime.MutableState<String>,
    bleedBottom: Boolean = false,
) {
    val novel = storeSite == Sites.NOVEL
    val site = if (novel) null
    else webSites.firstOrNull { it.id == storeSite }
        ?: webSites.firstOrNull { Sites.kindOf(it) == Sites.COPY }
    var cat by catState
    var busy by remember { mutableStateOf(false) }
    var activeRequest by remember { mutableStateOf<Any?>(null) }
    var retryListing by remember { mutableIntStateOf(0) }
    val fetchedAt = remember { mutableStateMapOf<String, Long>() }
    val cats = if (site == null) emptyList() else Sites.catsFor(site)
    val siteLabel = when {
        novel -> if (lang == "en") "Novels" else if (lang == "zh-TW") "小說" else "小说"
        site != null -> Sites.displayName(site)
        else -> copy.bookstore
    }
    val cards = if (site == null) emptyList() else crawled.filter { w ->
        if (w.siteId != site.id || w.cat == "搜索") return@filter false
        if (cat == "推荐" || cat == "推薦") w.cat == "推荐" || w.cat == "推薦"
        else w.cat == cat || w.listed.any { tagEq(it, cat) }
    }.distinctBy { it.id }
    val screenLive = LocalScreenLive.current
    LaunchedEffect(listState, cards, cols, asList, screenLive) {
        if (!screenLive) return@LaunchedEffect
        snapshotFlow {
            val info = listState.layoutInfo
            val first = info.visibleItemsInfo.firstOrNull()?.index ?: 0
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: first
            first to last
        }.collect { (first, last) ->
            val itemsPerRow = if (asList) 1 else cols
            val from = (first * itemsPerRow).coerceAtMost(cards.size)
            val to = ((last + 3) * itemsPerRow).coerceAtMost(cards.size)
            if (from < to) Crawler.prefetchCovers(cards.subList(from, to).map { it.cover })
        }
    }
    LaunchedEffect(storeSite) {
        if (cats.isNotEmpty() && cat !in cats) cat = cats.first()
    }
    val keepPlace = remember { mutableStateOf(true) }
    LaunchedEffect(storeSite, cat) {
        if (keepPlace.value) {
            keepPlace.value = false
            return@LaunchedEffect
        }
        listState.scrollToItem(0)
    }
    LaunchedEffect(storeSite, cat, online, screenLive, retryListing) {
        if (!screenLive) return@LaunchedEffect
        val s = site ?: return@LaunchedEffect
        if (!online || cat !in cats) return@LaunchedEffect
        val key = "${s.id}:$cat"
        val requestToken = Any()
        activeRequest = requestToken
        val age = System.currentTimeMillis() - (fetchedAt[key] ?: 0L)
        val has = cards.isNotEmpty()
        if (has && age in 1..180_000) { busy = false; return@LaunchedEffect }
        busy = !has
        onCrawl(s, cat) {
            if (activeRequest === requestToken) busy = false
            fetchedAt[key] = System.currentTimeMillis()
        }
    }
    var storeMenu by remember { mutableStateOf(false) }
    var discoverHead by remember { mutableIntStateOf(0) }
    val discoverTop = with(LocalDensity.current) {
        if (discoverHead == 0) padding.calculateTopPadding() + 96.dp else discoverHead.toDp()
    }
    val discoverBackdrop = rememberCouiBackdrop()
    Box(Modifier.fillMaxSize().then(if (bleedBottom) Modifier else Modifier.padding(bottom = padding.calculateBottomPadding()))) {
        Box(Modifier.fillMaxSize().couiBackdrop(discoverBackdrop).background(AppTheme.colorScheme.surface)) {
            when {
                novel -> EmptyState("(・ω・)ノ", if (lang == "en") "No novel source yet. Novels will come back later." else Crawler.NO_NOVEL_SOURCE, Modifier.padding(top = discoverTop))
                !online && cards.isEmpty() -> EmptyState("(´・ω・`)", copy.offline, Modifier.padding(top = discoverTop), actionText = if (lang == "en") "Retry" else "重试", onAction = { retryListing++ })
                busy && cards.isEmpty() -> EmptyState(null, copy.loading, Modifier.padding(top = discoverTop), loading = true)
                site != null && cards.isEmpty() ->
                    EmptyState("(´・ω・`)", site.lastError.ifBlank { copy.emptyCat }, Modifier.padding(top = discoverTop), actionText = if (lang == "en") "Retry" else "重试", onAction = { retryListing++ })
                asList -> LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), 
                    state = listState,
                    contentPadding = PaddingValues(top = discoverTop, bottom = if (bleedBottom) padding.calculateBottomPadding() else 0.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(cards, key = { "${it.id}#${it.cover.hashCode()}" }) { w ->
                        DiscoverListRow(w) { onOpen(w.id) }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
                else -> {
                    val rows = remember(cards, cols) { cards.chunked(cols) }
                    LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), 
                        state = listState,
                        contentPadding = PaddingValues(top = discoverTop, bottom = if (bleedBottom) padding.calculateBottomPadding() else 0.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(rows.size, key = { index -> rows[index].joinToString("|") { "${it.id}#${it.cover.hashCode()}" } + "@$index" }) { index ->
                            val row = rows[index]
                            Row(
                                Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                row.forEach { w ->
                                    Box(Modifier.weight(1f).expandSource(16.dp).couiClickable(press = true, list = false, shape = null) { onOpen(w.id) }) { StoreCover(w.title, w.cover) }
                                }
                                repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onSizeChanged { discoverHead = it.height },
        ) {
            StoreHeaderBar(
                backdrop = discoverBackdrop,
                title = copy.bookstore,
                lang = lang,
                siteLabel = siteLabel,
                cats = cats,
                selected = cat,
                showGrid = !asList && !novel,
                menuOpen = storeMenu,
                onMenu = { storeMenu = it },
                onToggleCols = onToggleCols,
                onPickSource = onPickSource,
                onWeb = site?.let { s -> { onWeb(s.url) } },
                onSearch = onSearch,
                onCat = { cat = it },
            )
        }
    }
}

/** 书城 header: floating buttons, persistent search pill, current source and its categories. */
@Composable
internal fun StoreHeaderBar(
    backdrop: CouiBackdrop?,
    title: String,
    lang: String,
    siteLabel: String,
    cats: List<String>,
    selected: String,
    showGrid: Boolean,
    menuOpen: Boolean,
    onMenu: (Boolean) -> Unit,
    onToggleCols: () -> Unit,
    onPickSource: () -> Unit,
    onWeb: (() -> Unit)?,
    onSearch: () -> Unit,
    onCat: (String) -> Unit,
) {
    FloatingTabHeader(
        backdrop = backdrop,
        title = title,
        actions = {
            IconButton(onClick = onSearch) {
                Icon(CouiIcons.Outlined.Search, contentDescription = if (lang == "en") "Search" else "搜索", tint = AppTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
            }
            ToolbarGap()
            if (showGrid) {
                IconButton(onClick = onToggleCols) {
                    Icon(CouiIcons.Outlined.LayoutSpliced, contentDescription = "网格", tint = AppTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                }
                ToolbarGap()
            }
            CornerMenu(
                expanded = menuOpen,
                onExpandedChange = onMenu,
                actions = listOfNotNull(
                    MenuAction(if (lang == "en") "Switch source" else if (lang == "zh-TW") "切換書源" else "切换书源", ShuIcons.Swap) { onPickSource() },
                    onWeb?.let { open -> MenuAction(if (lang == "en") "Open in browser" else if (lang == "zh-TW") "在瀏覽器開啟" else "在浏览器打开", CouiIcons.Outlined.CloudLink) { open() } },
                ),
            )
        },
        below = {
            val label: @Composable () -> Unit = {
                Text(
                    siteLabel,
                    color = AppTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 148.dp).expandSource(8.dp).couiClickable(onClick = onPickSource).padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
            // 书源名放进分类标签行的前置位（ChipTabRow 的 leadingAction），两者同处一行、互不覆盖。
            if (cats.isNotEmpty()) CouiTabs(
                items = cats.map { it to it },
                selected = if (selected in cats) selected else cats.first(),
                onSelect = onCat,
                modifier = Modifier.padding(bottom = 2.dp),
                leading = label,
            ) else Box(Modifier.fillMaxWidth().heightIn(min = 36.dp).padding(start = 8.dp, bottom = 6.dp), contentAlignment = Alignment.CenterStart) { label() }
        },
    )
}

/**
 * 切换书源. A full page with its own Scaffold so the COUI top bar always has a backdrop.
 * (The old picker drew SubTopBar inside the tab content, outside any Scaffold topBar slot:
 * COUI TopAppBar then hit checkNotNull(LocalCouiBackdrop) and crashed the app.)
 */
@Composable
internal fun SourcePickPane(lang: String, selected: String, onPick: (String) -> Unit, onBack: () -> Unit) {
    val title = if (lang == "en") "Switch source" else if (lang == "zh-TW") "切換書源" else "切换书源"
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { pad ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = pad, modifier = Modifier.fillMaxSize()) {
            item {
                Group(title = if (lang == "en") "Manga" else if (lang == "zh-TW") "漫畫" else "漫画") {
                    Sites.defaults.filterNot { Sites.isNovelSite(it.id) }.filter { RemoteConfig.sourcesEnabled(it.id) }.forEach { s ->
                        RadioButtonPreference(
                            title = Sites.displayName(s),
                            selected = selected == s.id,
                            onClick = { onPick(s.id) },
                        )
                    }
                }
            }
            if (RemoteConfig.novelEnabled) {
                item {
                    Group(title = if (lang == "en") "Novels" else if (lang == "zh-TW") "小說" else "小说") {
                        val novels = Sites.defaults.filter { Sites.isNovelSite(it.id) && RemoteConfig.sourcesEnabled(it.id) }
                        if (novels.isEmpty()) {
                            RadioButtonPreference(
                                title = if (lang == "en") "Novels" else if (lang == "zh-TW") "小說" else "小说",
                                summary = if (lang == "en") "No novel source yet, coming later" else if (lang == "zh-TW") "暫無小說源，等待後續加入" else "暂无小说源，等待后续加入",
                                selected = selected == Sites.NOVEL,
                                onClick = { onPick(Sites.NOVEL) },
                            )
                        } else {
                            novels.forEach { s ->
                                RadioButtonPreference(
                                    title = Sites.displayName(s),
                                    selected = selected == s.id,
                                    onClick = { onPick(s.id) },
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

private fun normTag(s: String): String = s.trim().lowercase()
    .replace("韓", "韩")
    .replace("單", "单")
    .replace("連", "连")
    .replace("結", "结")
    .replace("載", "载")

private fun tagEq(a: String, b: String) = normTag(a) == normTag(b)

internal fun mergeListed(prev: CrawledWork, item: CrawledWork, category: String): CrawledWork {
    val tags = LinkedHashSet<String>()
    prev.tags.forEach { if (it.isNotBlank()) tags.add(it) }
    item.tags.forEach { if (it.isNotBlank()) tags.add(it) }
    val listed = LinkedHashSet<String>()
    prev.listed.forEach { if (it.isNotBlank()) listed.add(it) }
    item.listed.forEach { if (it.isNotBlank()) listed.add(it) }
    val rec = category == "推荐" || category == "推薦" ||
        prev.cat == "推荐" || prev.cat == "推薦" ||
        item.cat == "推荐" || item.cat == "推薦"
    val cat = when {
        rec -> "推荐"
        category == "全部" && prev.cat.isNotBlank() && prev.cat != "全部" -> prev.cat
        item.cat.isNotBlank() -> item.cat
        else -> prev.cat
    }
    return prev.copy(
        title = realTitle(item.title, prev.title),
        cover = item.cover.ifBlank { prev.cover },
        href = item.href.ifBlank { prev.href },
        author = item.author.ifBlank { prev.author },
        genres = if (item.genres.isNotBlank() && item.genres != "其他") item.genres else prev.genres,
        cat = cat,
        tags = tags.toList(),
        listed = listed.toList(),
        description = item.description.ifBlank { prev.description },
        described = prev.described || item.described,
    )
}

private fun serialMark(w: CrawledWork): String {
    val blob = w.tags.joinToString(" ") + " " + w.genres
    return when {
        blob.contains("完结") || blob.contains("完結") -> "完结"
        blob.contains("连载") || blob.contains("連載") -> "连载"
        else -> ""
    }
}

@Composable
private fun DiscoverListRow(w: CrawledWork, onOpen: () -> Unit) {
    val serial = serialMark(w)
    val genre = w.genres.takeIf { it.isNotBlank() && it != "其他" }.orEmpty()
    val meta = listOf(serial, genre).filter { it.isNotBlank() }.distinct().joinToString(" · ")
    Card(
        Modifier.padding(horizontal = 8.dp, vertical = 4.dp).fillMaxWidth(),
        onClick = onOpen,
    ) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(64.dp).height(86.dp).clip(RoundedCornerShape(8.dp)).background(AppTheme.colorScheme.onSurface.copy(alpha = 0.06f))) {
                SmartCover(w.cover, Modifier.fillMaxSize(), plate = true, fallbackTitle = w.title)
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    w.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                )
                if (w.author.isNotBlank()) {
                    Text(
                        w.author,
                        fontSize = 12.sp,
                        color = AppTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (meta.isNotBlank()) {
                    Text(
                        meta,
                        fontSize = 12.sp,
                        color = AppTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun NovelCoverPlate(title: String, modifier: Modifier = Modifier) {
    Box(modifier.background(AppTheme.colorScheme.surface),contentAlignment=Alignment.BottomStart) {
        Text(
            title,
            modifier=Modifier.fillMaxWidth().background(Color(0xCC000000)).padding(8.dp),
            color=Color.White,
            fontSize=13.sp,
            maxLines=1,
            overflow=TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun StoreCover(title: String, cover: String) {
    val shape = CouiShape(16.dp, 3f)
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(shape)
                .background(CouiTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                .border(0.5.dp, CouiTheme.colorScheme.outlineVariant, shape),
        ) {
            SmartCover(cover, Modifier.matchParentSize(), plate = true)
        }
        CouiText(
            text = title,
            kind = CouiTextKind.PreferenceValue,
            color = CouiTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, top = 6.dp),
        )
    }
}

/** Remembers the last search per source so returning from a detail page does not re-query. */
private object SearchMemory {
    private var siteId = ""
    private var query = ""
    private var at = 0L
    var scroll = 0
    var results: List<CrawledWork> = emptyList()
        private set

    fun matches(site: String, q: String): Boolean = site == siteId && q == query

    fun validFor(site: String, q: String): Boolean =
        q.isNotBlank() && matches(site, q) && results.isNotEmpty() &&
            System.currentTimeMillis() - at < 30 * 60_000L

    fun remember(site: String, q: String, items: List<CrawledWork>) {
        siteId = site
        query = q
        results = items
        at = System.currentTimeMillis()
        scroll = 0
    }
}

@Composable
private fun SearchPane(
    copy: Copy,
    query: String,
    onQuery: (String) -> Unit,
    crawled: List<CrawledWork>,
    webSites: List<WebSite>,
    siteId: String,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onHits: (List<CrawledWork>) -> Unit = {},
) {
    // Search follows the store's current source: 拷贝漫画 only searches 拷贝漫画, and so on.
    val site = webSites.firstOrNull { it.id == siteId }
        ?: webSites.firstOrNull { Sites.kindOf(it) == Sites.COPY }
    val q = query.trim()
    val cached = site != null && SearchMemory.validFor(site.id, q)
    var live by remember { mutableStateOf(if (cached) SearchMemory.results else emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var retrySearch by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = if (cached) SearchMemory.scroll else 0)
    val screenLive = LocalScreenLive.current
    val queryNow = rememberUpdatedState(query)
    DisposableEffect(Unit) {
        onDispose {
            if (SearchMemory.matches(site?.id.orEmpty(), queryNow.value.trim())) {
                SearchMemory.scroll = listState.firstVisibleItemIndex
            }
        }
    }
    LaunchedEffect(q, site?.id, screenLive, retrySearch) {
        if (!screenLive) return@LaunchedEffect
        if (q.length < 1 || site == null) {
            live = emptyList()
            searching = false
            return@LaunchedEffect
        }
        // Coming back from a detail page must keep the previous hits instead of re-querying.
        if (retrySearch == 0 && SearchMemory.validFor(site.id, q)) {
            live = SearchMemory.results
            searching = false
            return@LaunchedEffect
        }
        searching = true
        delay(300)
        val result = kotlinx.coroutines.runInterruptible(Dispatchers.IO) {
            runCatching { Crawler.search(q, listOf(site)) }.getOrElse {
                AppLog.add("search", it.message ?: "search fail")
                emptyList()
            }
        }
        if (queryNow.value.trim() == q) {
            live = result
            searching = false
            if (result.isNotEmpty()) SearchMemory.remember(site.id, q, result)
        }
    }
    LaunchedEffect(live) {
        if (!screenLive) return@LaunchedEffect
        if (live.isNotEmpty()) onHits(live)
    }
    val remote = remember(q, live, crawled, site?.id) {
        if (q.isEmpty() || site == null) emptyList()
        else {
            val local = crawled.filter { it.siteId == site.id && keywordHit(it.title, q) }
            val merged = LinkedHashMap<String, CrawledWork>()
            for (item in local + live.filter { it.siteId == site.id || it.siteId.isBlank() }) {
                val prev = merged[item.id]
                merged[item.id] = when {
                    prev == null -> item
                    prev.cover.isBlank() && item.cover.isNotBlank() -> prev.copy(cover = item.cover, author = prev.author.ifBlank { item.author })
                    else -> prev
                }
            }
            merged.values.toList()
        }
    }
    SearchPage(
        query = query,
        onQuery = onQuery,
        hint = if (copy.search == "Search") "Search titles or keywords" else "搜索书名或关键词",
        cancel = copy.cancel,
        onCancel = onBack,
    ) { pad ->
    Column(Modifier.fillMaxSize().padding(pad)) {
        when {
                q.isEmpty() -> Unit
                searching && remote.isEmpty() -> EmptyState(null, copy.loading, Modifier.fillMaxWidth().weight(1f), loading = true)
                remote.isEmpty() -> EmptyState("(´・ω・`)", copy.emptyCat, Modifier.fillMaxWidth().weight(1f))
                else -> {
                    if (searching) CouiLinearProgress(null, Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp))
                    SmallTitle("${copy.search} · ${remote.size}")
                    LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), state = listState, modifier = Modifier.fillMaxWidth().weight(1f)) {
                        items(remote.chunked(2)) { row ->
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                row.forEach { m ->
                                    Column(Modifier.weight(1f).expandSource(8.dp).couiClickable { onOpen(m.id) }) {
                                        SmartCover(
                                            m.cover,
                                            Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(8.dp)),
                                            fallbackTitle = m.title,
                                        )
                                        Text(
                                            realTitle(m.title),
                                            modifier = Modifier.padding(top = 6.dp, start = 2.dp, end = 2.dp),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = 13.sp,
                                        )
                                    }
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
    }

@Composable
private fun MineStat(value: String, label: String, ink: Color = Color.Unspecified, onClick: (() -> Unit)? = null) {
    val num = if (ink == Color.Unspecified) AppTheme.colorScheme.onSurface else ink
    val sub = if (ink == Color.Unspecified) AppTheme.colorScheme.onSurfaceVariantSummary else ink.copy(alpha = 0.72f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick == null) Modifier else Modifier.expandSource(12.dp).couiClickable(onClick = onClick),
    ) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = num)
        Text(label, fontSize = 12.sp, color = sub)
    }
}

@Composable
internal fun MinePane(
    padding: PaddingValues,
    copy: Copy,
    lang: String,
    lib: Int,
    fav: Int,
    hist: Int,
    langLabel: String,
    onTheme: () -> Unit,
    onReading: () -> Unit,
    readingLabel: String,
    readingSum: String,
    onLanguage: () -> Unit,
    onFolders: () -> Unit,
    onShelf: () -> Unit,
    onHistory: () -> Unit,
    onIntro: () -> Unit,
    onFeedback: () -> Unit,
    onStorage: () -> Unit,
    onBackup: () -> Unit,
    onMore: () -> Unit,
    listState: LazyListState,
    banner: String,
) {
    var q by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    BackHandler(enabled = searching) { searching = false; q = "" }
    val mineBackdrop = rememberCouiBackdrop()
    val hint = if (lang == "en") "Search settings" else if (lang == "zh-TW") "搜尋功能項" else "搜索功能项"
    data class MineRow(val title: String, val summary: String, val onClick: () -> Unit)
    val groups = listOf(
        listOf(
            MineRow(
                when (lang) { "en" -> "More settings"; "zh-TW" -> "更多設定"; else -> "更多设置" },
                when (lang) { "en" -> "Back, incognito and privacy"; "zh-TW" -> "返回、隱身與隱私"; else -> "返回、隐身与隐私" },
                onMore,
            ),
        ),
        listOf(
            MineRow(copy.themeSettings, copy.themeSettingsSum, onTheme),
            MineRow(readingLabel, readingSum, onReading),
            MineRow(copy.language, langLabel, onLanguage),
            MineRow(when (lang) { "en" -> "Favorites"; "zh-TW" -> "收藏"; else -> "收藏" }, "", onFolders),
        ),
        listOf(
            MineRow(copy.backup, copy.backupSum, onBackup),
            MineRow(
                when (lang) { "en" -> "Storage"; "zh-TW" -> "儲存空間"; else -> "存储空间" },
                when (lang) { "en" -> "Cache, downloads and folders"; "zh-TW" -> "緩存、下載與目錄"; else -> "缓存、下载与目录" },
                onStorage,
            ),
        ),
        listOf(
            MineRow(copy.intro, "", onIntro),
            MineRow(copy.feedback, copy.feedbackSum, onFeedback),
        ),
    )
    val query = q.trim()
    val shown = if (query.isBlank()) groups else groups.map { rows ->
        rows.filter { it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true) }
    }.filter { it.isNotEmpty() }
    var headPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headDp = if (headPx == 0) statusTop + 60.dp else with(density) { headPx.toDp() }
    val bottom = padding.calculateBottomPadding()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = headDp, bottom = bottom + 24.dp),
            modifier = Modifier.fillMaxSize().then(if (useFloatingChrome) Modifier.couiBackdrop(mineBackdrop) else Modifier).background(AppTheme.colorScheme.surface),
            overscrollEffect = rememberCouiOverscrollEffect(),
        ) {
                if (query.isBlank()) {
                    item {
                        val ink = Color(0xFF1C1C1E)
                        Card(
                            Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxWidth(),
                        ) {
                            Box(Modifier.fillMaxWidth().height(196.dp).clip(RoundedCornerShape(18.dp))) {
                                if (banner.isNotBlank() && File(banner).exists()) {
                                    SmartCover(banner, Modifier.fillMaxSize())
                                } else {
                                    Image(
                                        painter = painterResource(R.drawable.mine_snow),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                                Column(
                                    Modifier.align(Alignment.Center).offset(y = (-16).dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text("书漫屿", fontSize = 28.sp, fontWeight = FontWeight.Medium, color = ink)
                                    Text("ShuManYu", fontSize = 14.sp, color = ink.copy(alpha = 0.75f))
                                }
                                Row(
                                    Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                    MineStat("$lib", copy.shelf, ink, onShelf)
                                    MineStat("$fav", copy.favOnly, ink, onFolders)
                                    MineStat("$hist", copy.history, ink, onHistory)
                                }
                            }
                        }
                    }
                }
                shown.forEach { rows ->
                    item {
                        Group {
                            rows.forEach { row ->
                                if (row.summary.isBlank()) ArrowPreference(row.title, onClick = row.onClick)
                                else ArrowPreference(row.title, summary = row.summary, onClick = row.onClick)
                            }
                        }
                    }
                }
            }
        val head: @Composable () -> Unit = {
            Column(Modifier.fillMaxWidth().onSizeChanged { headPx = it.height }) {
                Spacer(Modifier.height(statusTop))
                CouiSearchField(
                    query = q,
                    onQuery = { q = it },
                    hint = hint,
                    expanded = searching,
                    onExpandedChange = { searching = it; if (!it) q = "" },
                    cancel = copy.cancel,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
        if (useFloatingChrome) TopAppBarSurface(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(), backdrop = mineBackdrop) { head() }
        else Box(Modifier.align(Alignment.TopCenter).fillMaxWidth().background(AppTheme.colorScheme.surface)) { head() }
    }
}

@Composable
private fun RemotePane(
    id: String,
    copy: Copy,
    crawled: MutableList<CrawledWork>,
    webSites: List<WebSite>,
    inLib: Boolean,
    online: Boolean,
    resume: Progress?,
    readKeys: Set<String>,
    onBack: () -> Unit,
    onLib: () -> Unit,
    onReadChapter: (String, Int) -> Unit,
    onWeb: (String, String) -> Unit,
    foldersOn: Boolean,
    isFavorite: Boolean,
    onFolder: () -> Unit,
) {
    val listed = crawled.find { it.id == id }
    val diskWork = LocalStore.loadWork(id)?.work
    val title0 = realTitle(listed?.title, diskWork?.title)
    val mergedDesc = listed?.description?.takeIf { it.isNotBlank() } ?: diskWork?.description.orEmpty()
    val work0 = (listed ?: diskWork)?.copy(
        title = title0.ifBlank { listed?.title ?: diskWork?.title ?: id },
        description = mergedDesc,
        described = (diskWork?.described == true) || mergedDesc.isNotBlank(),
    )
    var work by remember(id) { mutableStateOf(work0) }
    var chapters by remember { mutableStateOf(repairChapters(LocalStore.loadWork(id)?.chapters ?: emptyList())) }
    var tags by remember { mutableStateOf(seedTags(work0)) }
    var loading by remember { mutableStateOf(false) }
    var pulling by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var dlText by remember(id) { mutableStateOf("") }
    val context = LocalContext.current
    fun persist(w: CrawledWork, list: List<CachedChapter>) {
        val fixed = repairChapters(list)
        LocalStore.saveWork(CachedWork(w, fixed, System.currentTimeMillis()))
        chapters = fixed
        work = w
        val i = crawled.indexOfFirst { it.id == w.id }
        if (i >= 0) crawled[i] = w else crawled.add(w)
    }
    fun applyStore(fromPull: Boolean, bump: ChapterBump?) {
        val cached = LocalStore.loadWork(id)
        if (cached != null) {
            val better = realTitle(cached.work.title, listed?.title, work?.title)
            val w = cached.work.copy(title = better.ifBlank { cached.work.title })
            persist(w, cached.chapters)
            if (w.tags.isNotEmpty()) tags = w.tags
        }
        if (!fromPull) {
            status = if (cached == null) copy.emptyCat else ""
            return
        }
        if (bump != null && bump.added > 0) {
            status = copy.newChapters
        } else {
            status = copy.noNewChapters
        }
    }
    val screenLive = LocalScreenLive.current
    LaunchedEffect(id, online) {
        val disk = LocalStore.loadWork(id)
        if (disk != null) {
            val fixedChapters = repairChapters(disk.chapters)
            chapters = fixedChapters
            if (fixedChapters.size != disk.chapters.size || fixedChapters.zip(disk.chapters).any { (a, b) -> a.id != b.id }) {
                LocalStore.saveWork(disk.copy(chapters = fixedChapters))
            }
            val better = realTitle(listed?.title, work?.title, disk.work.title)
            val fixed = disk.work.copy(title = better.ifBlank { disk.work.title })
            if (fixed.title != disk.work.title) persist(fixed, disk.chapters) else {
                work = fixed
                if (fixed.tags.isNotEmpty()) tags = fixed.tags
            }
        }
        if (!screenLive) return@LaunchedEffect
        val href = work?.href ?: listed?.href ?: return@LaunchedEffect
        if (!online) {
            if (chapters.isEmpty()) status = copy.offline
            return@LaunchedEffect
        }
        val titleOk = !isIdTitle(work?.title)
        val descOk = !work?.description.isNullOrBlank() || disk?.work?.described == true
        if (disk != null && disk.chapters.isNotEmpty() && disk.work.tags.isNotEmpty() && titleOk && descOk) {
            status = ""
            return@LaunchedEffect
        }
        loading = true
        status = copy.loading
        val seed = listed ?: work
        Thread {
            Updates.refresh(id, href, seed)
            Handler(Looper.getMainLooper()).post {
                loading = false
                applyStore(fromPull = false, bump = null)
            }
        }.start()
    }
    val site = webSites.find { it.id == work?.siteId }
    val serialLabel = when {
        tags.any { it.contains("完") } || work?.cat == "完结" || work?.cat == "完結" -> "完结"
        tags.any { it.contains("连") || it.contains("連") } || work?.cat == "连载" || work?.cat == "連載" -> "连载中"
        else -> ""
    }
    val sourceLabel = site?.let { Sites.displayName(it) } ?: "在线源"
    val metaLine = if (serialLabel.isBlank()) sourceLabel else "○  $serialLabel · $sourceLabel"
    val extraTags = tags.filter { t ->
        val x = t.trim()
        x != "连载" && x != "連載" && x != "完结" && x != "完結" && x != "连载中"
    }
    var descOpen by remember(id) { mutableStateOf(false) }
    var coverOpen by remember(id) { mutableStateOf(false) }
    val synopsis = work?.description.orEmpty()
    BackHandler(enabled = coverOpen) { coverOpen = false }
    Box(Modifier.fillMaxSize()) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "",
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(CouiIcons.Outlined.Back, contentDescription = "返回") }
                },
                actions = {
                    if (RemoteConfig.downloadEnabled) {
                    IconButton(onClick = {
                        val w = work
                        if (w == null || chapters.isEmpty()) {
                            CouiToast.show(if (chapters.isEmpty()) "还没有目录" else "还在加载")
                        } else if (Downloader.running) {
                            CouiToast.show("正在下载")
                        } else {
                            CouiToast.show("开始下载")
                            Downloader.start(
                                context = context,
                                id = w.id,
                                title = w.title.ifBlank { title0 },
                                chapters = chapters,
                                onStep = { done, total -> dlText = "下载 $done/$total" },
                                onDone = { err ->
                                    dlText = if (err == null) "已下载" else err
                                    CouiToast.show(if (err == null) "下载完成" else err)
                                },
                            )
                        }
                    }) {
                        Icon(ShuIcons.Download, contentDescription = "下载")
                    }
                    }
                    val url = site?.url.orEmpty()
                    if (url.isNotBlank()) {
                        IconButton(onClick = { onWeb(url, site?.let { Sites.displayName(it) } ?: url) }) {
                            Icon(ShuIcons.Globe, contentDescription = "网站")
                        }
                    }
                },
            )
        },
    ) { padding ->
        PullRefreshBox(
            refreshing = pulling,
            onRefresh = {
                val href = work?.href
                if (!online) {
                    CouiToast.show(copy.offline)
                } else if (!href.isNullOrBlank()) {
                    run {
                    pulling = true
                    CouiToast.hold("更新中...")
                    Notify.live(context, 0, 1, work?.title.orEmpty())
                    Thread {
                        val bump = Updates.refresh(id, href, listed ?: work)
                        Handler(Looper.getMainLooper()).post {
                            pulling = false
                            applyStore(fromPull = true, bump = bump)
                            Notify.liveClear(context)
                            if (bump != null && bump.added > 0) {
                                CouiToast.show("有章节更新快去通知栏看看是哪一本！")
                                Notify.chapter(context, bump.title, bump.index, bump.chapterTitle)
                            } else {
                                CouiToast.show("没有章节更新诶😩")
                            }
                        }
                    }.start()
                    }
                }
            },
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
        val chapterList = rememberLazyListState()
        var chapterDesc by rememberSaveable(id) { mutableStateOf(false) }
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), state = chapterList, modifier = Modifier.fillMaxSize()) {
            item {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        SmartCover(
                            work?.cover.orEmpty(),
                            Modifier
                                .width(118.dp)
                                .height(168.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .couiClickable(enabled = !work?.cover.isNullOrBlank()) { coverOpen = true },
                            fallbackTitle = work?.title?.takeIf { !isIdTitle(it) } ?: title0,
                        )
                        Column(Modifier.weight(1f).padding(top = 4.dp)) {
                            Text(work?.title?.takeIf { !isIdTitle(it) } ?: title0.ifBlank { work?.title ?: id }, style = AppTheme.textStyles.title2, maxLines = 3)
                            Spacer(Modifier.height(6.dp))
                            if (!work?.author.isNullOrBlank()) {
                                Text(work?.author.orEmpty(), fontSize = 14.sp, color = AppTheme.colorScheme.onSurfaceVariantSummary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                metaLine,
                                fontSize = 13.sp,
                                color = AppTheme.colorScheme.onSurfaceVariantSummary,
                            )
                            if (status.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(status, color = AppTheme.colorScheme.primary, fontSize = 13.sp)
                            }
                            if (dlText.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(dlText, color = AppTheme.colorScheme.primary, fontSize = 13.sp)
                            }
                        }
                    }
                    if (extraTags.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        TagRow(extraTags)
                    }
                    if (synopsis.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            synopsis,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = AppTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = if (descOpen) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().couiClickable { descOpen = !descOpen },
                        )
                        Text(
                            if (descOpen) "∧" else "∨",
                            modifier = Modifier.fillMaxWidth().couiClickable { descOpen = !descOpen },
                            textAlign = TextAlign.Center,
                            color = AppTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CouiButton(if (inLib) copy.inShelf else copy.addShelf, onLib, Modifier.weight(1f))
                        if (foldersOn) {
                            val novel = work?.let { Sites.isNovelSite(it.siteId) } == true
                            CouiButton(
                                if(copy.addShelf=="Add to shelf") { if(isFavorite) "Saved" else if(novel) "Save novel" else "Save comic" } else if(isFavorite) { if(novel) "已收藏小说" else "已收藏漫画" } else { if(novel) "收藏小说" else "收藏漫画" },
                                onFolder,
                                Modifier.weight(1f),
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    CouiButton(
                        copy.startRead,
                        onClick = {
                            val savedId = resume?.chapterId
                            val target = chapters.find { it.id == savedId } ?: chapters.firstOrNull()
                            if (target != null) {
                                val page = if (target.id == savedId) (resume?.page ?: 0) else 0
                                onReadChapter(target.id, page)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        kind = CouiButtonKind.Primary,
                        enabled = chapters.isNotEmpty(),
                    )
                }
            }
            if (chapters.isNotEmpty()) {
                val ordered = chapters.sortedBy { it.index }.let { if (chapterDesc) it.asReversed() else it }
                item {
                    Box(Modifier.fillMaxWidth()) {
                        SmallTitle("${copy.catalogTitle} · ${ordered.size}")
                        CouiText(
                            text = if (chapterDesc) "倒序" else "正序",
                            kind = CouiTextKind.Description,
                            color = CouiTheme.colorScheme.accent,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 20.dp)
                                .clip(RoundedCornerShape(50))
                                .couiClickable { chapterDesc = !chapterDesc }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
                items(ordered, key = { it.href.ifBlank { it.id } }) { c ->
                    val savedPage = resume?.takeIf { it.chapterId == c.id && it.page >= 0 }?.page
                    val seen = "$id:${c.id}" in readKeys || resume?.chapterId == c.id
                    val chapterId = c.id
                    val openPage = savedPage ?: 0
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                    ) {
                        Row(
                            Modifier.fillMaxWidth().heightIn(min = 52.dp).padding(start = 16.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                Modifier
                                    .weight(1f)
                                    .couiClickable(enabled = !chapterList.isScrollInProgress) {
                                        onReadChapter(chapterId, openPage)
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    c.title,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 16.sp,
                                    color = if (seen) Color(0xFF9A9A9E) else AppTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (savedPage != null) {
                                    Text(
                                        "第${savedPage + 1}页",
                                        modifier = Modifier.padding(start = 8.dp),
                                        fontSize = 13.sp,
                                        color = AppTheme.colorScheme.onSurfaceVariantSummary,
                                        maxLines = 1,
                                    )
                                }
                            }
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .couiClickable(enabled = !chapterList.isScrollInProgress) {
                                        val w = work ?: return@couiClickable
                                        if (Downloader.running) {
                                            CouiToast.show("正在下载")
                                            return@couiClickable
                                        }
                                        CouiToast.show("开始下载")
                                        Downloader.start(
                                            context = context,
                                            id = w.id,
                                            title = w.title.ifBlank { title0 },
                                            chapters = listOf(c),
                                            onStep = { _, _ -> },
                                            onDone = { err ->
                                                CouiToast.show(if (err == null) "下载完成" else err)
                                            },
                                        )
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(ShuIcons.Download, contentDescription = "下载", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            } else if (loading) {
                item { Text(copy.loading, Modifier.padding(16.dp), color = AppTheme.colorScheme.onSurfaceVariantSummary) }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
        }
    }
    if (coverOpen) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                // 全屏背景点按关闭：不需要按压反馈。
                .clickable(interactionSource = null, indication = null) { coverOpen = false },
            contentAlignment = Alignment.Center,
        ) {
            SmartCover(
                work?.cover.orEmpty(),
                Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
    }
}

private fun seedTags(work: CrawledWork?): List<String> {
    if (work == null) return emptyList()
    if (work.tags.isNotEmpty()) return work.tags
    val cat = work.cat
    if (cat == "连载" || cat == "連載") return listOf("连载")
    if (cat == "完结" || cat == "完結") return listOf("完结")
    return emptyList()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagRow(tags: List<String>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.take(12).forEach { tag ->
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(AppTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(tag, fontSize = 13.sp)
            }
        }
    }
}

private fun isNovelWork(workId: String, crawled: List<CrawledWork>): Boolean {
    val siteId = crawled.find { it.id == workId }?.siteId ?: LocalStore.loadWork(workId)?.work?.siteId ?: return false
    return Sites.isNovelSite(siteId)
}

@Composable
private fun ReadDispatch(
    workId: String,
    chapterId: String,
    startPage: Int,
    crawled: List<CrawledWork>,
    copy: Copy,
    prefs: Prefs,
    bookmarked: Boolean,
    onBookmark: () -> Unit,
    onClose: () -> Unit,
    onChapter: (String) -> Unit,
    onProgress: (String, Int) -> Unit,
    onCoverSaved: (String) -> Unit,
    onFolder: () -> Unit,
) {
    val novel = remember(workId) { isNovelWork(workId, crawled) }
    if (novel) {
        NovelReaderPane(
            workId = workId,
            chapterId = chapterId,
            startPage = startPage,
            copy = copy,
            prefs = prefs,
            onClose = onClose,
            onChapter = onChapter,
            onProgress = onProgress,
            onFolder = onFolder,
        )
    } else {
        RemoteReaderPane(
            workId = workId,
            chapterId = chapterId,
            startPage = startPage,
            copy = copy,
            prefs = prefs,
            bookmarked = bookmarked,
            onBookmark = onBookmark,
            onClose = onClose,
            onChapter = onChapter,
            onProgress = onProgress,
            onCoverSaved = onCoverSaved,
        )
    }
}

@Composable
private fun NovelReaderPane(
    workId: String,
    chapterId: String,
    startPage: Int,
    copy: Copy,
    prefs: Prefs,
    onClose: () -> Unit,
    onChapter: (String) -> Unit,
    onProgress: (String, Int) -> Unit,
    onFolder: () -> Unit,
) {
    val uiLang = resolveLang(prefs.lang, LocalConfiguration.current)
    val cached = remember(workId) { LocalStore.loadWork(workId) }
    val chapters = remember(workId) { cached?.chapters.orEmpty().sortedBy { it.index } }
    val chapter = chapters.find { it.id == chapterId } ?: chapters.firstOrNull()
    val chapterAt = chapters.indexOfFirst { it.id == chapter?.id }
    val scope = rememberCoroutineScope()

    var text by remember(chapterId) { mutableStateOf("") }
    var loading by remember(chapterId) { mutableStateOf(true) }
    var err by remember(chapterId) { mutableStateOf("") }
    var retry by remember(chapterId) { mutableIntStateOf(0) }

    var fontSp by remember { mutableIntStateOf(prefs.novelFont) }
    var gap by remember { mutableFloatStateOf(prefs.novelGap) }
    var night by remember { mutableStateOf(prefs.novelNight) }
    var tone by remember { mutableStateOf(prefs.novelTone) }
    var vertical by remember { mutableStateOf(prefs.novelTurn == "vertical") }
    var chrome by remember { mutableStateOf(false) }
    var catalog by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }

    val dark = night || tone == "night" || tone == "ink" || tone == "black"
    val bg = when {
        night || tone == "night" -> Color(0xFF1C1C1C)
        tone == "black" || tone == "ink" -> Color(0xFF111111)
        tone == "white" -> Color(0xFFF7F7F7)
        tone == "green" -> Color(0xFFDCE8D4)
        tone == "blue" -> Color(0xFFD5E3F0)
        else -> Color(0xFFEFE6C8)
    }
    val fg = if (dark) Color(0xFFD9D9D9) else Color(0xFF1B1B1B)
    val muted = fg.copy(alpha = 0.55f)

    LaunchedEffect(workId, chapterId, retry) {
        val href = chapter?.href
        if (href.isNullOrBlank()) {
            loading = false
            err = copy.emptyCat
            return@LaunchedEffect
        }
        loading = true
        err = ""
        text = ""
        val (body, e) = kotlinx.coroutines.runInterruptible(Dispatchers.IO) { Crawler.fetchNovel(href) }
        text = body
        loading = false
        if (body.isBlank()) err = e ?: "未解析到正文"
    }

    val readingTitle = normalizedNovelChapterTitle(chapter?.title.orEmpty(), chapter?.index ?: 0, text)
    val displayText = remember(text, readingTitle) {
        val peeled = novelReadingBody(text, readingTitle).ifBlank { text }
        val head = readingTitle.ifBlank { chapter?.title.orEmpty() }
        if (head.isBlank() || peeled.startsWith(head)) peeled else "$head\n\n$peeled"
    }
    val bodyStyle = remember(fg, fontSp, gap) {
        TextStyle(color = fg, fontSize = fontSp.sp, lineHeight = (fontSp * gap).sp)
    }

    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val contentTop = statusTop + 52.dp
    val contentBottom = navBottom + 72.dp
    val density = LocalDensity.current
    var box by remember { mutableStateOf(IntSize.Zero) }
    val measurer = rememberTextMeasurer()
    val leaves = remember(displayText, bodyStyle, box, vertical) {
        if (vertical || displayText.isBlank() || box.width < 8 || box.height < 8) {
            emptyList()
        } else {
            val side = with(density) { 22.dp.toPx() * 2 }.toInt()
            val top = with(density) { contentTop.toPx() }.toInt()
            val bottom = with(density) { contentBottom.toPx() }.toInt()
            val w = (box.width - side).coerceAtLeast(1)
            val h = (box.height - top - bottom - 8).coerceAtLeast(1)
            paginateNovel(measurer, displayText, bodyStyle, w, h)
        }
    }

    val pageCount = remember(chapterId) { mutableIntStateOf(1) }
    val pagerState = key(chapterId) {
        rememberPagerState(initialPage = 0, pageCount = { pageCount.intValue.coerceAtLeast(1) })
    }
    val scroll = remember(chapterId) { ScrollState(0) }
    var restored by remember(chapterId) { mutableStateOf(false) }
    var lastSaved by remember(chapterId) { mutableIntStateOf(-1) }

    LaunchedEffect(chapterId, leaves.size, vertical, text, scroll.maxValue) {
        if (text.isBlank()) return@LaunchedEffect
        if (!vertical && leaves.isNotEmpty()) {
            pageCount.intValue = leaves.size
            val at = novelRestorePage(startPage, leaves.size)
            if (pagerState.currentPage != at) runCatching { pagerState.scrollToPage(at) }
            restored = true
        } else if (vertical && scroll.maxValue > 0) {
            val target = ((startPage / 1000f) * scroll.maxValue).toInt()
            if (target > 0) runCatching { scroll.scrollTo(target) }
            restored = true
        }
    }
    LaunchedEffect(vertical, pagerState.currentPage, scroll.value, scroll.maxValue, restored) {
        if (!restored || text.isBlank() || chapter == null) return@LaunchedEffect
        val value = if (vertical) {
            if (scroll.maxValue <= 0) 0 else (scroll.value * 1000L / scroll.maxValue).toInt()
        } else {
            pagerState.currentPage
        }
        if (value != lastSaved) {
            lastSaved = value
            onProgress(chapter.id, value)
        }
    }

    fun openChapter(delta: Int) {
        val next = chapters.getOrNull(chapterAt + delta) ?: return
        onChapter(next.id)
    }

    fun turn(delta: Int) {
        if (vertical) {
            chrome = true
            return
        }
        if (leaves.isEmpty()) return
        val target = pagerState.currentPage + delta
        if (target in leaves.indices) {
            scope.launch { runCatching { pagerState.scrollToPage(target) } }
        } else if (delta > 0) {
            openChapter(1)
        } else {
            openChapter(-1)
        }
    }

    BackHandler(enabled = chrome || settings || catalog) {
        when {
            settings -> settings = false
            catalog -> catalog = false
            else -> chrome = false
        }
    }

    val tap = Modifier.pointerInput(vertical, chrome, settings, catalog, leaves.size, chapters.size) {
        detectTapGestures { offset ->
            if (settings || catalog) return@detectTapGestures
            if (chrome) {
                chrome = false
                return@detectTapGestures
            }
            if (vertical) {
                chrome = true
                return@detectTapGestures
            }
            val w = size.width.toFloat().coerceAtLeast(1f)
            when {
                offset.x < w * 0.30f -> turn(-1)
                offset.x > w * 0.70f -> turn(1)
                else -> chrome = true
            }
        }
    }

    val backdrop = rememberCouiBackdrop()
    Box(Modifier.fillMaxSize().background(bg).onSizeChanged { box = it }.then(tap)) {
        when {
            loading && text.isBlank() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CouiCircularProgress(Modifier.size(28.dp))
            }
            text.isBlank() -> Column(
                Modifier.fillMaxSize().padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(err.ifBlank { copy.emptyCat }, color = muted, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                CouiButton("重试", { retry++ }, kind = CouiButtonKind.Secondary, compact = true)
                Spacer(Modifier.height(10.dp))
                CouiButton("返回", onClose, kind = CouiButtonKind.Secondary, compact = true)
            }
            vertical -> Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(start = 22.dp, end = 22.dp, top = contentTop, bottom = contentBottom),
            ) {
                Text(displayText, style = bodyStyle)
                Spacer(Modifier.height(28.dp))
                if (chapterAt in 0 until chapters.lastIndex) {
                    CouiButton("下一章", { openChapter(1) }, Modifier.fillMaxWidth(), kind = CouiButtonKind.Secondary)
                }
            }
            else -> HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(top = contentTop, bottom = contentBottom),
            ) { page ->
                Text(
                    leaves.getOrElse(page) { "" },
                    style = bodyStyle,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
                )
            }
        }

        if (chrome || text.isBlank()) {
            ReaderTopBar(
                backdrop = if (useFloatingChrome) backdrop else null,
                title = readingTitle.ifBlank { chapter?.title.orEmpty() },
                onBack = onClose,
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                ToolbarTextButton(if (copy.addShelf == "Add to shelf") "Save" else "收藏") { onFolder() }
            }
        }
        if (chrome) {
            ReaderBottomPanel(
                backdrop = if (useFloatingChrome) backdrop else null,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CouiRoundIcon(ShuIcons.SkipPrev, "上一章", { openChapter(-1) }, enabled = chapterAt > 0)
                    if (vertical) {
                        val pct = if (scroll.maxValue <= 0) 0 else (scroll.value * 100 / scroll.maxValue)
                        PageSlider(
                            index = pct,
                            count = 101,
                            onSeek = { target ->
                                scope.launch {
                                    runCatching { scroll.scrollTo((scroll.maxValue * target / 100f).toInt()) }
                                }
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        )
                    } else {
                        PageSlider(
                            index = pagerState.currentPage,
                            count = leaves.size.coerceAtLeast(1),
                            onSeek = { target ->
                                scope.launch { runCatching { pagerState.scrollToPage(target) } }
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        )
                    }
                    CouiRoundIcon(
                        ShuIcons.SkipNext,
                        "下一章",
                        { openChapter(1) },
                        enabled = chapterAt in 0 until chapters.lastIndex,
                    )
                }
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    ReaderToolCell { ReaderTool(ShuIcons.Rows, "目录", { catalog = true }) }
                    ReaderToolCell {
                        ReaderTool(CouiIcons.Outlined.DarkLight, if (night) "日间" else "夜间", {
                            night = !night
                            prefs.novelNight = night
                        })
                    }
                    ReaderToolCell { ReaderTool(CouiIcons.Outlined.Setting, "设置", { settings = true }) }
                }
            }
        }

        if (catalog) {
            ChapterCatalogSheet(
                title = if (uiLang == "en") "Chapters" else if (uiLang == "zh-TW") "章節" else "章节",
                chapters = chapters,
                currentId = chapter?.id.orEmpty(),
                lang = uiLang,
                onPick = { picked ->
                    catalog = false
                    if (picked.id != chapter?.id) onChapter(picked.id)
                },
                onDismiss = { catalog = false },
            )
        }

        if (settings) {
            Dialog(
                onDismissRequest = { settings = false },
                confirmAction = CouiDialogAction("完成", { settings = false }),
                placement = CouiDialogPlacement.Bottom,
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 460.dp)
                        .couiVerticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                ) {
                    Group {
                        SliderPreference(
                            value = fontSp.toFloat(),
                            onValueChange = { v ->
                                fontSp = v.roundToInt().coerceIn(15, 32)
                                prefs.novelFont = fontSp
                            },
                            range = 15f..32f,
                            label = "字号",
                            valueText = "$fontSp",
                        )
                        SliderPreference(
                            value = gap,
                            onValueChange = { v ->
                                gap = v
                                prefs.novelGap = v
                            },
                            range = 1.45f..2.3f,
                            label = "行距",
                            valueText = String.format(java.util.Locale.ROOT, "%.2f", gap),
                        )
                        SwitchPreference(night, { night = it; prefs.novelNight = it }, "夜间模式")
                        SwitchPreference(vertical, {
                            vertical = it
                            restored = false
                            prefs.novelTurn = if (it) "vertical" else "page"
                        }, "上下滚动", summary = "关闭后左右翻页")
                    }
                    SmallTitle("背景")
                    Group {
                        listOf(
                            "paper" to "纸张",
                            "white" to "白色",
                            "green" to "护眼绿",
                            "blue" to "淡蓝",
                            "night" to "深色",
                        ).forEach { (id, label) ->
                            RadioButtonPreference(label, tone == id, {
                                tone = id
                                prefs.novelTone = id
                            })
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.borderTone(color: Color, width: Float = 2f): Modifier = this.then(
    Modifier.drawBehind {
        drawCircle(color = color, radius = size.minDimension / 2f - width.dp.toPx() / 2f, style = Stroke(width = width.dp.toPx()))
    },
)

private fun novelReadingBody(raw: String, title: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return trimmed
    val parts = trimmed.split('\n')
    val first = parts.first().trim().trimStart('\u3000', ' ')
    if (first.length > 64) return trimmed
    val compact = { s: String ->
        s.replace(Regex("[\\s\\u3000]"), "")
            .replace(Regex("[（(]第?\\d+\\s*/\\s*\\d+页[）)]"), "")
            .replace(Regex("[（(]\\d+\\s*/\\s*\\d+[）)]"), "")
    }
    val head = compact(first)
    val want = compact(title)
    val titled = want.isNotBlank() && head == want || (want.length >= 4 && head.isNotBlank() && (head.startsWith(want) || head.endsWith(want)))
    if (!titled) return trimmed
    return parts.drop(1).joinToString("\n").trim()
}

private fun novelRestorePage(start: Int, pages: Int): Int {
    if (pages <= 1 || start <= 0) return 0
    if (start < pages) return start
    return ((start / 1000f) * (pages - 1)).toInt().coerceIn(0, pages - 1)
}

private fun paginateNovel(
    measurer: androidx.compose.ui.text.TextMeasurer,
    text: String,
    style: TextStyle,
    width: Int,
    height: Int,
    firstHeight: Int = height,
): List<String> {
    if (text.isBlank() || width <= 0 || height <= 0) return listOf(text)
    val pages = ArrayList<String>()
    var rest = text.trim()
    var guard = 0
    while (rest.isNotEmpty() && guard++ < 800) {
        var lo = 1
        var hi = rest.length
        var fit = 1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val h = measurer.measure(
                AnnotatedString(rest.substring(0, mid)),
                style = style,
                constraints = Constraints(maxWidth = width),
            ).size.height
            if (h <= if(pages.isEmpty())firstHeight else height) {
                fit = mid
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
        if (fit < rest.length) {
            val slice = rest.substring(0, fit)
            val nl = slice.lastIndexOf('\n')
            if (nl >= fit * 0.55) fit = nl + 1
        }
        fit = fit.coerceIn(1, rest.length)
        pages.add(rest.substring(0, fit).trimEnd())
        rest = rest.substring(fit).trimStart('\n')
    }
    return pages.ifEmpty { listOf(text) }
}

private data class StripCell(
    val key: String,
    val chapterId: String,
    val chapterTitle: String,
    val page: Int,
    val url: String,
    val chapterSize: Int,
    val nextTitle: String,
)

@Composable
private fun RemoteReaderPane(
    workId: String,
    chapterId: String,
    startPage: Int,
    copy: Copy,
    prefs: Prefs,
    bookmarked: Boolean,
    onBookmark: () -> Unit,
    onClose: () -> Unit,
    onChapter: (String) -> Unit,
    onProgress: (String, Int) -> Unit,
    onCoverSaved: (String) -> Unit,
) {
    val uiLang = resolveLang(prefs.lang, LocalConfiguration.current)
    val more = remember(uiLang) { moreOf(uiLang) }
    var reading by remember { mutableStateOf(prefs.reading()) }
    val r = reading
    val context = LocalContext.current
    val view = LocalView.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val cached = remember(workId) { LocalStore.loadWork(workId) }
    val chapters = remember(workId) { repairChapters(cached?.chapters.orEmpty()).sortedBy { it.index } }
    val chapter = chapters.find { it.id == chapterId } ?: chapters.firstOrNull()?.takeIf { chapterId.isBlank() }
    var pages by remember(chapterId) { mutableStateOf(chapter?.pages ?: emptyList()) }
    var chapterNonce by remember(chapterId) { mutableIntStateOf(0) }
    val reloads = remember(workId) { mutableStateMapOf<String, Int>() }
    val strip = remember(workId) { mutableStateListOf<StripCell>() }
    val stripLock = remember(workId) { Mutex() }
    val pageCache = remember(workId) { mutableStateMapOf<String, List<String>>() }
    var viewChapterId by remember(workId) { mutableStateOf(chapterId) }
    var holdChapter by remember(workId) { mutableStateOf("") }
    var viewCount by remember(workId) { mutableIntStateOf(0) }
    val nextBusy = remember(workId) { mutableStateOf(false) }
    val prevBusy = remember(workId) { mutableStateOf(false) }
    var pageMenu by remember(chapterId) { mutableStateOf(false) }
    var menuPage by remember(chapterId) { mutableIntStateOf(0) }
    var coverAsk by remember(chapterId) { mutableStateOf(false) }
    var loading by remember(chapterId) { mutableStateOf(pages.isEmpty()) }
    var err by remember(chapterId) { mutableStateOf("") }
    var index by remember(chapterId) { mutableIntStateOf(startPage.coerceAtLeast(0)) }
    var seeded by remember(chapterId) { mutableStateOf(false) }
    var readerReady by remember(chapterId) { mutableStateOf(false) }
    var followReader by remember(chapterId) { mutableStateOf(false) }
    var jumpPin by remember(workId) { mutableStateOf<String?>(null) }
    var chrome by remember { mutableStateOf(!prefs.reading().fullscreen) }
    var rotUse by remember { mutableStateOf<String?>(null) }
    val mode = r.readMode
    val rot = rotUse ?: r.rotation
    var zones by remember(chapterId) { mutableStateOf(r.navOverlay) }
    val focus = remember { FocusRequester() }
    val webtoon = mode == "webtoon" || mode.startsWith("ttb")
    val rtl = mode.startsWith("rtl")
    val verticalPage = mode == "ttb" || mode == "ttb-slide"
    val bg = when (r.bgColor) {
        "white" -> Color.White
        "gray" -> Color(0xFF222222)
        else -> Color.Black
    }
    val fg = if (r.bgColor == "white") Color.Black else Color.White
    val pageFilter = remember(r.grayscale, r.invert, r.brightnessOn, r.brightness) { readingColorFilter(r) }
    val pageTint = if (r.filterOn && r.filterA > 0.5f) {
        Color(
            red = (r.filterR / 255f).coerceIn(0f, 1f),
            green = (r.filterG / 255f).coerceIn(0f, 1f),
            blue = (r.filterB / 255f).coerceIn(0f, 1f),
            alpha = (r.filterA / 255f).coerceIn(0f, 1f),
        )
    } else {
        Color.Transparent
    }
    val tintBlend = when (r.filterBlend) {
        "multiply" -> BlendMode.Multiply
        "screen" -> BlendMode.Screen
        "overlay" -> BlendMode.Overlay
        "lighten" -> BlendMode.Lighten
        "darken" -> BlendMode.Darken
        else -> BlendMode.SrcOver
    }
    var zoom by remember(workId) { mutableFloatStateOf(1f) }
    var pinching by remember(workId) { mutableStateOf(false) }
    var panX by remember(workId) { mutableFloatStateOf(0f) }
    var panY by remember(workId) { mutableFloatStateOf(0f) }
    val zoomNow = rememberUpdatedState(zoom)
    val panXNow = rememberUpdatedState(panX)
    val panYNow = rememberUpdatedState(panY)
    var lastTapAt by remember { mutableLongStateOf(0L) }
    val tapWait = remember { mutableStateOf<Job?>(null) }
    val listState = rememberLazyListState()
    val padFrac = when (r.sidePad) {
        "15" -> 0.15f
        "s" -> 0.06f
        "m" -> 0.12f
        else -> 0f
    }
    var settings by remember { mutableStateOf(false) }
    var rotPick by remember { mutableStateOf(false) }
    var catalog by remember { mutableStateOf(false) }

    BackHandler(enabled = coverAsk || pageMenu || settings || rotPick || catalog) {
        when {
            coverAsk -> coverAsk = false
            pageMenu -> pageMenu = false
            settings -> settings = false
            rotPick -> rotPick = false
            else -> catalog = false
        }
    }

    DisposableEffect(r.fullscreen, chrome) {
        val window = activity?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            if (r.fullscreen && !chrome) {
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            activity?.window?.let { WindowCompat.getInsetsController(it, view).show(WindowInsetsCompat.Type.systemBars()) }
        }
    }
    DisposableEffect(r.keepScreenOn) {
        val window = activity?.window
        if (r.keepScreenOn) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    DisposableEffect(rot) {
        activity?.requestedOrientation = when (rot) {
            "auto" -> ActivityInfo.SCREEN_ORIENTATION_FULL_USER
            "landscape", "lock-landscape" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
    }
    fun seedStrip(ch: CachedChapter, urls: List<String>) {
        if (!webtoon || verticalPage || urls.isEmpty() || strip.isNotEmpty()) return
        pageCache[ch.id] = urls
        val next = chapters.getOrNull(chapters.indexOfFirst { it.id == ch.id } + 1)
        urls.forEachIndexed { i, u ->
            strip.add(StripCell("${ch.id}:$i", ch.id, ch.title, i, u, urls.size, ""))
        }
        if (r.chapterTransition && next != null) {
            strip.add(StripCell("${ch.id}:div", ch.id, ch.title, -1, "", urls.size, next.title))
        }
        viewChapterId = ch.id
        viewCount = urls.size
    }
    LaunchedEffect(chapterId, chapterNonce) {
        if (pages.isNotEmpty() && chapterNonce == 0) {
            loading = false
            val ch = chapter
            if (ch != null && strip.isEmpty()) {
                seedStrip(ch, pages)
                if (strip.isNotEmpty()) {
                    val p = startPage.takeIf { it in pages.indices } ?: 0
                    index = p
                    viewChapterId = ch.id
                    viewCount = pages.size
                    val at = strip.indexOfFirst { it.chapterId == ch.id && it.page == p }.let { if (it < 0) 0 else it }
                    if (at > 0) runCatching { listState.scrollToItem(at.coerceAtMost(strip.lastIndex)) }
                    seeded = true
                    readerReady = true
                }
            }
            return@LaunchedEffect
        }
        val href = chapter?.href ?: return@LaunchedEffect
        loading = pages.isEmpty() && strip.isEmpty()
        err = ""
        val (fresh, e) = withContext(Dispatchers.IO) { Crawler.fetchPages(href) }
        if (fresh.isNotEmpty()) {
            pages = fresh
            pageCache[chapter?.id ?: chapterId] = fresh
            if (cached != null && chapter != null) {
                LocalStore.saveWork(
                    cached.copy(
                        chapters = cached.chapters.map { if (it.id == chapter.id) it.copy(pages = fresh) else it },
                    ),
                )
            }
            val ch = chapter
            if (ch != null && strip.isEmpty()) {
                seedStrip(ch, fresh)
                if (strip.isNotEmpty()) {
                    val p = startPage.takeIf { it in fresh.indices } ?: 0
                    index = p
                    val at = strip.indexOfFirst { it.chapterId == ch.id && it.page == p }.let { if (it < 0) 0 else it }
                    if (at > 0) runCatching { listState.scrollToItem(at.coerceAtMost(strip.lastIndex)) }
                    seeded = true
                    readerReady = true
                }
            } else if (ch != null) {
                fresh.forEachIndexed { i, u ->
                    val at = strip.indexOfFirst { it.chapterId == ch.id && it.page == i }
                    if (at >= 0) strip[at] = strip[at].copy(url = u, chapterSize = fresh.size)
                }
            }
        } else if (pages.isEmpty() && strip.isEmpty()) {
            err = e ?: copy.emptyCat
        }
        // 先把 strip 铺好再收起 loading，避免「pages 已有、strip 还是空」的窗口里
        // 一次轻微滑动被当成到底，触发连续追加。
        loading = false
    }
    LaunchedEffect(zones) {
        if (zones && r.navOverlay) {
            delay(1600)
            zones = false
        }
    }
    LaunchedEffect(pages.size) {
        if (pages.isNotEmpty()) runCatching { focus.requestFocus() }
    }

    fun nextChapter() {
        if (chapter == null) return
        if (index >= pages.lastIndex && pages.isNotEmpty()) prefs.markChapterRead(workId, chapter.id)
        val read = prefs.readChapterKeys()
        val start = chapters.indexOfFirst { it.id == chapter.id }.coerceAtLeast(0)
        val candidates = chapters.drop(start + 1).filter { ch ->
            val filtered = false
            val seen = r.skipRead && "$workId:${ch.id}" in read
            !filtered && !seen
        }
        val next = candidates.firstOrNull()
        if (next != null) onChapter(next.id) else CouiToast.show(copy.emptyCat)
    }

    fun prevChapter() {
        val i = chapters.indexOfFirst { it.id == chapter?.id }
        val prev = chapters.getOrNull(i - 1) ?: return
        onChapter(prev.id)
    }

    fun patch(next: ReadingPrefs) {
        reading = next
        prefs.setReading(next)
    }

    suspend fun urlsFor(ch: CachedChapter): List<String> {
        pageCache[ch.id]?.takeIf { it.isNotEmpty() }?.let { return it }
        if (ch.pages.isNotEmpty()) {
            pageCache[ch.id] = ch.pages
            return ch.pages
        }
        val (fresh, _) = withContext(Dispatchers.IO) { Crawler.fetchPages(ch.href) }
        if (fresh.isNotEmpty()) {
            pageCache[ch.id] = fresh
            val disk = LocalStore.loadWork(workId)
            if (disk != null) {
                LocalStore.saveWork(disk.copy(chapters = disk.chapters.map { if (it.id == ch.id) it.copy(pages = fresh) else it }))
            }
        }
        return fresh
    }

    fun stripChapterIds(): List<String> {
        val ids = ArrayList<String>()
        for (c in strip) {
            if (c.page >= 0 && (ids.isEmpty() || ids.last() != c.chapterId)) ids.add(c.chapterId)
        }
        return ids
    }

    suspend fun trimStrip(dropFirst: Boolean) {
        val ids = stripChapterIds()
        if (ids.size < 3) return
        val drop = if (dropFirst) ids.first() else ids.last()
        val firstVis = listState.firstVisibleItemIndex
        val lastVis = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: firstVis
        val off = listState.firstVisibleItemScrollOffset
        if (dropFirst) {
            val count = strip.indexOfLast { it.chapterId == drop } + 1
            if (count <= 0 || firstVis < count || count >= strip.size) return
            Snapshot.withMutableSnapshot {
                repeat(count) { if (strip.isNotEmpty()) strip.removeAt(0) }
            }
            if (strip.isNotEmpty()) {
                val target = (firstVis - count).coerceIn(0, strip.lastIndex)
                runCatching { listState.scrollToItem(target, off.coerceAtLeast(0)) }
            }
        } else {
            val start = strip.indexOfFirst { it.chapterId == drop }
            if (start <= 0 || lastVis >= start) return
            Snapshot.withMutableSnapshot {
                while (strip.size > start) strip.removeAt(strip.lastIndex)
            }
        }
    }

    suspend fun appendNext(): Boolean = stripLock.withLock {
        val lastId = strip.lastOrNull { it.page >= 0 }?.chapterId ?: chapter?.id ?: return false
        val i = chapters.indexOfFirst { it.id == lastId }
        val next = chapters.getOrNull(i + 1) ?: return false
        if (strip.any { it.chapterId == next.id && it.page >= 0 }) return true
        val urls = urlsFor(next)
        if (urls.isEmpty()) return false
        if (strip.any { it.chapterId == next.id && it.page >= 0 }) return true
        trimStrip(dropFirst = true)
        val after = chapters.getOrNull(i + 2)
        val cells = ArrayList<StripCell>(urls.size + 1)
        urls.forEachIndexed { n, u ->
            cells.add(StripCell("${next.id}:$n", next.id, next.title, n, u, urls.size, ""))
        }
        if (r.chapterTransition) {
            cells.add(StripCell("${next.id}:div", next.id, next.title, -1, "", urls.size, after?.title.orEmpty()))
        }
        Snapshot.withMutableSnapshot { strip.addAll(cells) }
        return true
    }

    suspend fun prependPrev(): Boolean = stripLock.withLock {
        val firstId = strip.firstOrNull { it.page >= 0 }?.chapterId ?: return false
        val i = chapters.indexOfFirst { it.id == firstId }
        val prev = chapters.getOrNull(i - 1) ?: return false
        if (strip.any { it.chapterId == prev.id && it.page >= 0 }) return true
        val urls = urlsFor(prev)
        if (urls.isEmpty()) return false
        if (strip.any { it.chapterId == prev.id && it.page >= 0 }) return true
        val anchorIndex = listState.firstVisibleItemIndex
        val anchorOff = listState.firstVisibleItemScrollOffset
        val anchor = strip.getOrNull(anchorIndex)?.key
        trimStrip(dropFirst = false)
        val added = ArrayList<StripCell>(urls.size + 1)
        urls.forEachIndexed { n, u ->
            added.add(StripCell("${prev.id}:$n", prev.id, prev.title, n, u, urls.size, ""))
        }
        if (r.chapterTransition) {
            val curTitle = chapters.getOrNull(i)?.title.orEmpty()
            added.add(StripCell("${prev.id}:div", prev.id, prev.title, -1, "", urls.size, curTitle))
        }
        Snapshot.withMutableSnapshot { strip.addAll(0, added) }
        if (strip.isNotEmpty()) {
            val found = if (anchor.isNullOrBlank()) -1 else strip.indexOfFirst { it.key == anchor }
            val target = (if (found >= 0) found else anchorIndex + added.size).coerceIn(0, strip.lastIndex)
            runCatching { listState.scrollToItem(target, anchorOff.coerceAtLeast(0)) }
        }
        return true
    }

    fun loadEdge(forward: Boolean) {
        // 内容还没铺好时列表是空的，此时任何边缘加载都会误判成「已到末尾」，
        // 一路把后面的章节追加进来，看着就像直接跳到漫画结尾。
        if (!readerReady) return
        if (!(webtoon && !verticalPage)) return
        val busy = if (forward) nextBusy else prevBusy
        if (busy.value) return
        busy.value = true
        scope.launch {
            try {
                if (forward) appendNext() else prependPrev()
            } finally {
                busy.value = false
            }
        }
    }

    fun jumpChapter(forward: Boolean) {
        val continuous = webtoon && !verticalPage && strip.isNotEmpty()
        if (!continuous) {
            if (forward) nextChapter() else prevChapter()
            return
        }
        val cur = viewChapterId.ifBlank { chapter?.id.orEmpty() }
        val i = chapters.indexOfFirst { it.id == cur }
        val target = if (forward) chapters.getOrNull(i + 1) else chapters.getOrNull(i - 1)
        if (target == null) {
            if (forward) CouiToast.show(copy.emptyCat)
            return
        }
        if (forward) prefs.markChapterRead(workId, cur)
        prefs.markChapterRead(workId, target.id)
        jumpPin = target.id
        holdChapter = target.id
        index = 0
        viewChapterId = target.id
        val known = strip.firstOrNull { it.chapterId == target.id && it.chapterSize > 0 }?.chapterSize ?: 0
        if (known > 0) viewCount = known
        scope.launch {
            var at = strip.indexOfFirst { it.chapterId == target.id && it.page == 0 }
            if (at < 0) {
                val ok = if (forward) appendNext() else prependPrev()
                at = strip.indexOfFirst { it.chapterId == target.id && it.page == 0 }
                if (at < 0 && forward && !ok) {
                    CouiToast.show(copy.emptyCat)
                }
            }
            if (at >= 0) {
                runCatching { listState.scrollToItem(at) }
                index = 0
                viewChapterId = target.id
            }
            holdChapter = ""
            jumpPin = null
        }
    }

    val pagerState = key(chapterId) {
        val opening = startPage.coerceAtLeast(0)
        rememberPagerState(initialPage = opening, pageCount = { pages.size.coerceAtLeast(1) })
    }
    LaunchedEffect(pages.size, chapterId) {
        if (pages.isEmpty() || readerReady) return@LaunchedEffect
        if (webtoon && !verticalPage) return@LaunchedEffect
        val p = startPage.takeIf { it in pages.indices } ?: 0
        index = p
        if (pagerState.currentPage != p && p < pagerState.pageCount) {
            runCatching { pagerState.scrollToPage(p) }
        }
        seeded = true
        readerReady = true
    }
    LaunchedEffect(webtoon, verticalPage, pages.size) {
        if (!(webtoon && !verticalPage) || strip.isNotEmpty() || pages.isEmpty()) return@LaunchedEffect
        val ch = chapter ?: return@LaunchedEffect
        seedStrip(ch, pages)
        if (strip.isEmpty()) return@LaunchedEffect
        val p = startPage.takeIf { it in pages.indices } ?: 0
        index = p
        val at = strip.indexOfFirst { it.chapterId == ch.id && it.page == p }.let { if (it < 0) 0 else it }
        if (at > 0) listState.scrollToItem(at)
        seeded = true
        readerReady = true
    }
    LaunchedEffect(pagerState.currentPage, pages.size, webtoon, verticalPage, readerReady, followReader) {
        if (!readerReady || (webtoon && !verticalPage) || pages.isEmpty()) return@LaunchedEffect
        val want = startPage.coerceIn(0, pages.lastIndex)
        if (!followReader && pagerState.currentPage != want) {
            index = want
            if (want < pagerState.pageCount) runCatching { pagerState.scrollToPage(want) }
            return@LaunchedEffect
        }
        index = pagerState.currentPage.coerceIn(0, pages.lastIndex)
    }
    val pinchingNow = rememberUpdatedState(pinching)
    val zoomGuard = rememberUpdatedState(zoom)
    LaunchedEffect(webtoon, verticalPage) {
        if (!(webtoon && !verticalPage)) return@LaunchedEffect
        snapshotFlow {
            val cell = strip.getOrNull(listState.firstVisibleItemIndex)
            Triple(cell?.chapterId.orEmpty(), cell?.page ?: -2, cell?.chapterSize ?: 0)
        }.collect { (cid, page, size) ->
            val pin = jumpPin
            if (pin != null) {
                viewChapterId = pin
                val known = strip.firstOrNull { it.chapterId == pin && it.chapterSize > 0 }?.chapterSize ?: 0
                if (known > 0) viewCount = known
                index = 0
                if (cid == pin && page == 0) jumpPin = null
                return@collect
            }
            if (cid.isBlank() || size <= 0 || !seeded) return@collect
            val wantId = chapter?.id ?: chapterId
            if (!followReader && cid != wantId) {
                viewChapterId = wantId
                val known = strip.firstOrNull { it.chapterId == wantId && it.chapterSize > 0 }?.chapterSize ?: pages.size
                if (known > 0) viewCount = known
                val p = startPage.coerceAtLeast(0).let { if (known > 0) it.coerceAtMost(known - 1) else it }
                index = p
                val at = strip.indexOfFirst { it.chapterId == wantId && it.page == p }
                    .let { found -> if (found >= 0) found else strip.indexOfFirst { it.chapterId == wantId && it.page >= 0 } }
                if (at >= 0 && listState.firstVisibleItemIndex != at) {
                    launch { runCatching { listState.scrollToItem(at) } }
                }
                return@collect
            }
            if (holdChapter.isNotBlank() && cid != holdChapter) return@collect
            viewChapterId = cid
            viewCount = size
            index = if (page >= 0) page.coerceIn(0, size - 1) else (size - 1).coerceAtLeast(0)
            if (followReader && page >= size - 1 && page >= 0) prefs.markChapterRead(workId, cid)
            val vis = listState.firstVisibleItemIndex
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: vis
            if (followReader && vis <= 1 && !pinchingNow.value && zoomGuard.value >= 0.98f) loadEdge(false)
            val deep = page >= 0 && (page + 1 >= 18 || (size > 1 && page / (size - 1f) >= 0.4f))
            if (followReader && (deep || (strip.isNotEmpty() && last >= strip.lastIndex - 1))) loadEdge(true)
        }
    }
    LaunchedEffect(seeded, index, viewChapterId, webtoon, verticalPage, jumpPin) {
        if (jumpPin != null) return@LaunchedEffect
        if (webtoon && !verticalPage) {
            val want = chapter?.id ?: chapterId
            if (viewChapterId.isBlank()) return@LaunchedEffect
            if (!followReader && viewChapterId != want) return@LaunchedEffect
            if (strip.isEmpty() && pages.isEmpty()) return@LaunchedEffect
            onProgress(viewChapterId, index)
            return@LaunchedEffect
        }
        if (!seeded || pages.isEmpty()) return@LaunchedEffect
        val ch = chapter?.id ?: return@LaunchedEffect
        onProgress(ch, index)
    }
    LaunchedEffect(seeded, index, viewChapterId) {
        if (!seeded) return@LaunchedEffect
        val ahead = ArrayList<String>(2)
        if (webtoon && !verticalPage && strip.isNotEmpty()) {
            val ch = viewChapterId
            for (p in intArrayOf(index + 1, index + 2)) {
                strip.firstOrNull { it.chapterId == ch && it.page == p }?.url?.let { ahead.add(it) }
            }
        } else {
            pages.getOrNull(index + 1)?.let { ahead.add(it) }
            pages.getOrNull(index + 2)?.let { ahead.add(it) }
        }
        ahead.forEach { Crawler.prefetchPage(it) }
    }

    fun step(delta: Int) {
        followReader = true
        if (webtoon && !verticalPage && strip.isNotEmpty()) {
            scope.launch {
                val h = listState.layoutInfo.viewportSize.height.toFloat().coerceAtLeast(1f)
                listState.animateScrollBy(delta * h * 0.9f)
            }
            return
        }
        if (pages.isEmpty()) return
        val dir = if (rtl && !verticalPage) -delta else delta
        val next = (index + dir).coerceIn(0, pages.lastIndex)
        if (next == index && delta > 0) {
            nextChapter()
            return
        }
        index = next
        scope.launch {
            runCatching {
                if (next < pagerState.pageCount) {
                    if (r.pageAnim) pagerState.animateScrollToPage(next) else pagerState.scrollToPage(next)
                }
            }
        }
        if (next >= pages.lastIndex) prefs.markChapterRead(workId, chapter?.id ?: return)
    }

    fun seek(i: Int) {
        followReader = true
        if (webtoon && !verticalPage && strip.isNotEmpty()) {
            val ch = viewChapterId.ifBlank { chapter?.id.orEmpty() }
            val size = (strip.firstOrNull { it.chapterId == ch && it.chapterSize > 0 }?.chapterSize ?: pages.size).coerceAtLeast(1)
            val targetPage = i.coerceIn(0, size - 1)
            val exact = strip.indexOfFirst { it.chapterId == ch && it.page == targetPage }
            // Trimmed strips may no longer hold the exact cell; fall back to the nearest page of
            // the same chapter instead of silently ignoring the drag.
            val near = if (exact >= 0) exact else strip.indexOfFirst { it.chapterId == ch && it.page >= targetPage }
                .let { if (it >= 0) it else strip.indexOfLast { it.chapterId == ch } }
            if (exact >= 0 || near >= 0) {
                index = targetPage
                val at = if (exact >= 0) exact else near
                scope.launch { runCatching { listState.scrollToItem(at) } }
            }
            return
        }
        if (pages.isEmpty()) return
        val target = i.coerceIn(0, pages.lastIndex)
        index = target
        scope.launch {
            runCatching {
                if (target < pagerState.pageCount) {
                    // Jump immediately while dragging; animating here made the slider look stuck.
                    pagerState.scrollToPage(target)
                }
            }
        }
    }

    fun onReaderTap(x: Float, y: Float) {
        if (chrome || settings || rotPick) {
            chrome = false
            rotPick = false
            return
        }
        val continuous = webtoon && !verticalPage
        // 连续条漫里单击只负责唤起/隐藏阅读栏，翻页交给滚动，避免想点出菜单时页面跟着滑一下。
        if (!r.tapTurn || continuous) {
            chrome = true
            return
        }
        val layout = if (continuous) r.webtoonNav else r.navLayout
        val reverse = if (continuous) r.webtoonReverseTap else r.reverseTap
        val edge = if (layout == "edge") 0.16f else 0.28f
        var act = "center"
        if (layout == "l") {
            if (y > 0.72f) act = "next" else if (x < 0.4f) act = "prev"
        } else if (x < edge) act = "prev" else if (x > 1f - edge) act = "next"
        if (reverse == "h" || reverse == "v") {
            if (act == "prev") act = "next" else if (act == "next") act = "prev"
        }
        when (act) {
            "prev" -> step(-1)
            "next" -> step(1)
            else -> chrome = true
        }
    }

    fun pageAt(yNorm: Float): Int {
        if (webtoon && !verticalPage && strip.isNotEmpty()) {
            val info = listState.layoutInfo
            val h = info.viewportSize.height.toFloat().coerceAtLeast(1f)
            val y = yNorm * h
            val hit = info.visibleItemsInfo.firstOrNull { item ->
                y >= item.offset && y < item.offset + item.size
            }?.index
            val cell = hit?.let { strip.getOrNull(it) }
            if (cell != null) return if (cell.page >= 0) cell.page else (cell.chapterSize - 1).coerceAtLeast(0)
        }
        if (pages.isEmpty()) return -1
        return index.coerceIn(0, pages.lastIndex)
    }

    fun imageOf(page: Int): String? {
        if (webtoon && !verticalPage) {
            val ch = viewChapterId.ifBlank { chapter?.id.orEmpty() }
            strip.firstOrNull { it.chapterId == ch && it.page == page }?.url?.let { return it }
        }
        return pages.getOrNull(page)
    }

    fun setAsCover(page: Int) {
        val src = imageOf(page)
        val fail = if (uiLang == "en") "Couldn't update the cover" else "封面更新失败"
        if (src.isNullOrBlank()) {
            CouiToast.show(fail)
            return
        }
        val original = cached?.work?.cover.orEmpty()
        Thread {
            val bmp = Crawler.downloadBitmap(src, force = true)
            Handler(Looper.getMainLooper()).post {
                if (bmp == null) {
                    CouiToast.show(fail)
                    return@post
                }
                val dir = java.io.File(context.filesDir, "covers").apply { mkdirs() }
                val file = java.io.File(dir, "$workId.jpg")
                val wrote = runCatching {
                    file.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it) }
                }.isSuccess && file.exists() && file.length() > 0L
                if (!wrote) {
                    CouiToast.show(fail)
                    return@post
                }
                prefs.noteCover(workId, file.absolutePath, original)
                val disk = LocalStore.loadWork(workId)
                if (disk != null) {
                    LocalStore.saveWork(disk.copy(work = disk.work.copy(cover = file.absolutePath)))
                }
                onCoverSaved(file.absolutePath)
                CouiToast.show(if (uiLang == "en") "Cover updated" else "封面已更新")
            }
        }.start()
    }

    fun savePage(page: Int) {
        val src = imageOf(page) ?: return
        val title = cached?.work?.title ?: "书漫屿"
        val fail = if (uiLang == "en") "Couldn't save" else "保存失败"
        val okWord = if (uiLang == "en") "Saved to album" else "已保存到相册"
        Thread {
            val bmp = Crawler.downloadBitmap(src, force = false)
            val own = r.savePageFolder
            Handler(Looper.getMainLooper()).post {
                if (bmp == null || !saveReaderPage(context, bmp, title, own)) {
                    CouiToast.show(fail)
                } else {
                    CouiToast.show(okWord)
                }
            }
        }.start()
    }

    fun reloadPage(page: Int) {
        if (page < 0 || pages.isEmpty()) {
            chapterNonce++
        } else {
            val key = if (webtoon && !verticalPage) "$viewChapterId:$page" else page.toString()
            reloads[key] = (reloads[key] ?: 0) + 1
        }
    }

    val tapMod = Modifier.readerTaps(
        key = "${r.readMode}${r.navLayout}${r.webtoonNav}${r.tapTurn}",
        onTap = { x, y ->
            val now = android.os.SystemClock.uptimeMillis()
            val doubled = now - lastTapAt < 280L
            if (doubled) {
                tapWait.value?.cancel()
                tapWait.value = null
                lastTapAt = 0L
                val zNow = zoomNow.value
                if (kotlin.math.abs(zNow - 1f) > 0.02f) {
                    val ms = 170
                    val from = zNow
                    scope.launch {
                        val anim = Animatable(from)
                        anim.animateTo(1f, tween(ms, easing = FastOutSlowInEasing)) {
                            zoom = value
                        }
                    }
                    panX = 0f
                    panY = 0f
                }
            } else {
                lastTapAt = now
                tapWait.value?.cancel()
                tapWait.value = scope.launch {
                    delay(280)
                    onReaderTap(x, y)
                }
            }
        },
        onLong = { _, y ->
            if (!settings && !rotPick && !coverAsk) {
                chrome = false
                menuPage = pageAt(y)
                pageMenu = true
            }
        },
    )
    val zoomMod = Modifier.readerZoom(
        key = webtoon,
        webtoon = webtoon && !verticalPage,
        pad = { padFrac },
        zoom = { zoomNow.value },
        panX = { panXNow.value },
        panY = { panYNow.value },
        onPinch = { pinching = it },
        onChange = { z, x, y, focalY, changed ->
            val shrinking = z < 0.98f || zoomNow.value < 0.98f
            if (changed && webtoon && !verticalPage && !shrinking) {
                val z1 = zoomNow.value.coerceIn(0.45f, 4f)
                val ratio = z / z1
                if (kotlin.math.abs(ratio - 1f) > 0.004f) {
                    val next = (listState.firstVisibleItemScrollOffset + focalY) * ratio - focalY
                    val floor = strip.indexOfFirst { it.chapterId == viewChapterId && it.page >= 0 }.coerceAtLeast(0)
                    anchorScrollTo(listState, next, ratio, floor)
                }
            }
            zoom = z
            panX = x
            panY = y
        },
    )
    val advance = remember { mutableStateOf<() -> Unit>({ }) }
    val edgeLoad = remember { mutableStateOf<(Boolean) -> Unit>({ }) }
    val chainAt = remember { mutableLongStateOf(0L) }
    advance.value = {
        val now = android.os.SystemClock.uptimeMillis()
        if (now - chainAt.longValue >= 900L) {
            chainAt.longValue = now
            nextChapter()
        }
    }
    edgeLoad.value = { forward -> loadEdge(forward) }
    var dragPx by remember { mutableFloatStateOf(0f) }
    val sensPx = when (r.hideSens) { "high" -> 18f; "mid" -> 42f; else -> 96f }
    val webStrip = rememberUpdatedState(webtoon && !verticalPage)
    val loadingNow = rememberUpdatedState(loading)
    val hasPages = rememberUpdatedState(pages.isNotEmpty())
    val sensNow = rememberUpdatedState(sensPx)
    val readerReadyNow = rememberUpdatedState(readerReady)
    val chainScroll = remember(listState, pagerState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput && readerReadyNow.value) followReader = true
                if (source == NestedScrollSource.UserInput && chrome) {
                    dragPx += kotlin.math.abs(available.y) + kotlin.math.abs(available.x)
                    if (dragPx > sensNow.value) {
                        chrome = false
                        dragPx = 0f
                    }
                } else if (!chrome) {
                    dragPx = 0f
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // readerReady 之前列表可能是空的：canScrollForward 会误报「已到底」，
                // 一点点滑动就会触发连续追加/上一章，直接冲到结尾。
                if (source != NestedScrollSource.UserInput || !readerReadyNow.value || !hasPages.value || loadingNow.value) {
                    return Offset.Zero
                }
                val atEnd = if (webStrip.value) !listState.canScrollForward else !pagerState.canScrollForward
                val leftover = kotlin.math.abs(available.x) + kotlin.math.abs(available.y)
                if (atEnd && leftover > 36f) {
                    if (webStrip.value) edgeLoad.value.invoke(true) else advance.value.invoke()
                }
                if (webStrip.value && !listState.canScrollBackward && available.y > 36f && !pinchingNow.value && zoomGuard.value >= 0.98f) {
                    edgeLoad.value.invoke(false)
                }
                return Offset.Zero
            }
        }
    }
    val pageFit = r.zoomMode == "fit"
    val pageAlign = when (r.zoomPos) {
        "top" -> Alignment.TopCenter
        "center" -> Alignment.Center
        else -> if (webtoon) Alignment.TopCenter else Alignment.Center
    }

    val readerBackdrop = rememberCouiBackdrop()
    Box(
        Modifier
            .fillMaxSize()
            .background(bg)
            .nestedScroll(chainScroll)
            .focusRequester(focus)
            .focusable()
            .drawWithContent {
                drawContent()
                if (zones) {
                    drawRect(Color(0x330088FF), size = androidx.compose.ui.geometry.Size(size.width * 0.28f, size.height))
                    drawRect(
                        Color(0x330088FF),
                        topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.72f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width * 0.28f, size.height),
                    )
                }
            }
            .then(zoomMod)
            .onPreviewKeyEvent { e ->
                if (!r.volumeTurn || e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (e.key) {
                    Key.VolumeUp, Key.DirectionUp, Key.PageUp -> {
                        step(if (rtl) 1 else -1)
                        true
                    }
                    Key.VolumeDown, Key.DirectionDown, Key.PageDown -> {
                        step(if (rtl) -1 else 1)
                        true
                    }
                    else -> false
                }
            },
    ) {
        Box(Modifier.fillMaxSize().then(if (chrome && useFloatingChrome) Modifier.couiBackdrop(readerBackdrop) else Modifier)) {
        when {
            loading && pages.isEmpty() && strip.isEmpty() -> Box(Modifier.fillMaxSize().then(tapMod), contentAlignment = Alignment.Center) {
                CouiCircularProgress(Modifier.size(32.dp))
            }
            pages.isEmpty() && strip.isEmpty() -> Box(Modifier.fillMaxSize().then(tapMod), contentAlignment = Alignment.Center) {
                Text(err.ifBlank { copy.emptyCat }, color = fg.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            }
            webtoon && !verticalPage -> {
                BoxWithConstraints(Modifier.fillMaxSize().clipToBounds()) {
                    val z = zoom.coerceIn(0.45f, 4f)
                    val base = (1f - padFrac * 2f).coerceIn(0.4f, 1f)
                    val contentW = maxWidth * base * z
                    val rows: List<StripCell> = if (strip.isNotEmpty()) {
                        strip.toList()
                    } else {
                        val ch = chapter
                        if (ch == null || pages.isEmpty()) {
                            emptyList()
                        } else {
                            val next = chapters.getOrNull(chapters.indexOfFirst { it.id == ch.id } + 1)
                            val baseCells = pages.mapIndexed { i, u ->
                                StripCell("${ch.id}:$i", ch.id, ch.title, i, u, pages.size, "")
                            }
                            if (r.chapterTransition && next != null) {
                                baseCells + StripCell("${ch.id}:div", ch.id, ch.title, -1, "", pages.size, next.title)
                            } else {
                                baseCells
                            }
                        }
                    }
                    LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), 
                        state = listState,
                        verticalArrangement = Arrangement.Top,
                        userScrollEnabled = !pinching,
                        modifier = Modifier
                            .requiredWidth(contentW)
                            .fillMaxHeight()
                            .offset {
                                val viewPx = constraints.maxWidth
                                val contentPx = contentW.roundToPx()
                                val centered = (viewPx - contentPx) / 2
                                val x = if (contentPx <= viewPx) {
                                    centered
                                } else {
                                    panX.roundToInt().coerceIn(viewPx - contentPx, 0)
                                }
                                IntOffset(x, 0)
                            }
                            .then(tapMod),
                    ) {
                        items(
                            count = rows.size,
                            key = { idx -> rows.getOrNull(idx)?.key ?: "gap-$idx" },
                            contentType = { idx -> if ((rows.getOrNull(idx)?.page ?: 0) < 0) 0 else 1 },
                        ) { i ->
                            val cell = rows.getOrNull(i) ?: return@items
                            if (cell.page < 0) {
                                if (r.chapterTransition) {
                                    ChapterDivider(
                                        finishedTitle = cell.chapterTitle,
                                        nextTitle = cell.nextTitle,
                                        fg = fg,
                                        lang = uiLang,
                                        onNext = {
                                            val n = chapters.getOrNull(chapters.indexOfFirst { it.id == cell.chapterId } + 1)
                                            val at = if (n == null) -1 else strip.indexOfFirst { it.chapterId == n.id && it.page == 0 }
                                            if (at >= 0) scope.launch { listState.animateScrollToItem(at) }
                                            else loadEdge(true)
                                        },
                                    )
                                }
                            } else {
                                PageImage(
                                    cell.url,
                                    webtoon = true,
                                    colorFilter = pageFilter,
                                    tint = pageTint,
                                    tintBlend = tintBlend,
                                    nonce = reloads["${cell.chapterId}:${cell.page}"] ?: 0,
                                    fit = pageFit,
                                    align = pageAlign,
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                val pageMod = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val z = zoom.coerceIn(0.45f, 4f)
                        scaleX = z
                        scaleY = z
                        translationX = panX
                        translationY = panY
                        transformOrigin = TransformOrigin.Center
                    }
                    .then(tapMod)
                if (verticalPage) {
                    VerticalPager(
                        state = pagerState,
                        modifier = pageMod,
                        beyondViewportPageCount = 1,
                        userScrollEnabled = !pinching && zoom <= 1.05f,
                    ) { page ->
                        val src = pages.getOrNull(page)
                        if (src != null) PageImage(src, webtoon = false, colorFilter = pageFilter, tint = pageTint, tintBlend = tintBlend, nonce = reloads[page.toString()] ?: 0, fit = pageFit, align = pageAlign)
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = pageMod,
                        reverseLayout = rtl,
                        beyondViewportPageCount = 1,
                        userScrollEnabled = !pinching && zoom <= 1.05f,
                    ) { page ->
                        val src = pages.getOrNull(page)
                        if (src != null) PageImage(src, webtoon = false, colorFilter = pageFilter, tint = pageTint, tintBlend = tintBlend, nonce = reloads[page.toString()] ?: 0, fit = pageFit, align = pageAlign)
                    }
                }
            }
        }
        }
        if (chrome) {
            val barChapter = if (webtoon && !verticalPage) {
                chapters.find { it.id == viewChapterId }?.title ?: chapter?.title ?: ""
            } else {
                chapter?.title ?: ""
            }
            val barCount = if (webtoon && !verticalPage) {
                strip.firstOrNull { it.chapterId == viewChapterId && it.chapterSize > 0 }?.chapterSize
                    ?: viewCount.takeIf { it > 0 }
                    ?: pages.size
            } else {
                pages.size
            }
            ReaderChrome(
                title = cached?.work?.title ?: copy.bookstore,
                chapter = barChapter,
                bookmarked = bookmarked,
                index = index,
                count = barCount,
                onBack = onClose,
                onBookmark = onBookmark,
                onSeek = { seek(it) },
                onPrev = { jumpChapter(false) },
                onNext = { jumpChapter(true) },
                onRotate = { rotPick = !rotPick },
                onSettings = { settings = true; rotPick = false },
                onCatalog = { catalog = true; rotPick = false },
                backdrop = if (useFloatingChrome) readerBackdrop else null,
                rotMenu = {
                    if (rotPick) {
                        ReaderPopupMenu(
                        spot = it,
                        options = listOf(
                            "auto" to more.rotAuto,
                            "portrait" to if (uiLang == "en") "Portrait" else "竖屏",
                            "landscape" to if (uiLang == "en") "Landscape" else "横屏",
                        ),
                        selected = rot,
                        onPick = { rotUse = it },
                        onDismiss = { rotPick = false },
                    )
                    }
                },
            )
        } else if (r.showPageNum && (pages.isNotEmpty() || strip.isNotEmpty())) {
            val shownCount = if (webtoon && !verticalPage) {
                (strip.firstOrNull { it.chapterId == viewChapterId && it.chapterSize > 0 }?.chapterSize
                    ?: viewCount.takeIf { it > 0 }
                    ?: pages.size).coerceAtLeast(1)
            } else {
                pages.size.coerceAtLeast(1)
            }
            Text(
                "${(index + 1).coerceAtMost(shownCount)} / $shownCount",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
                    .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        if (settings) {
            ReaderSettingsSheet(
                lang = uiLang,
                more = more,
                reading = r,
                onChange = { patch(it) },
                onDismiss = { settings = false },
            )
        }
        if (catalog) {
            ChapterCatalogSheet(
                title = if (uiLang == "en") "Chapters" else if (uiLang == "zh-TW") "章節" else "章节",
                chapters = chapters,
                currentId = chapter?.id.orEmpty(),
                lang = uiLang,
                onPick = { picked ->
                    catalog = false
                    if (picked.id != chapter?.id) onChapter(picked.id)
                },
                onDismiss = { catalog = false },
            )
        }
        if (pageMenu) {
            val rows = buildList {
                if (prefs.customCover) {
                    add(Triple(CouiIcons.Outlined.Photo, if (uiLang == "en") "Set as cover" else if (uiLang == "zh-TW") "設定為封面" else "设定为封面", "cover"))
                }
                if (prefs.savePhotos) {
                    add(Triple(ShuIcons.Download, if (uiLang == "en") "Save" else if (uiLang == "zh-TW") "儲存" else "储存", "save"))
                }
                add(Triple(ShuIcons.Refresh, if (uiLang == "en") "Reload image" else if (uiLang == "zh-TW") "重新加載圖片" else "重新加载图片", "reload"))
            }
            PageActionSheet(
                rows,
                onPick = { id ->
                    pageMenu = false
                    when (id) {
                        "cover" -> coverAsk = true
                        "save" -> savePage(menuPage)
                        else -> reloadPage(menuPage)
                    }
                },
                onDismiss = { pageMenu = false },
            )
        }
        if (coverAsk) {
            ConfirmSheet(
                title = if (uiLang == "en") "Set this page as the cover?" else "要将此页设为封面吗？",
                message = if (uiLang == "en") "The detail page will use this image." else "确定后，详情页封面会换成这一页。",
                confirm = if (uiLang == "en") "OK" else "确定",
                cancel = if (uiLang == "en") "Cancel" else "取消",
                onConfirm = {
                    coverAsk = false
                    setAsCover(menuPage)
                },
                onDismiss = { coverAsk = false },
                lift = ReaderSheetLift,
            )
        }
    }
}

private fun readingColorFilter(r: ReadingPrefs): ColorFilter? {
    val cm = ColorMatrix()
    var on = false
    if (r.grayscale) {
        val sat = ColorMatrix()
        sat.setToSaturation(0f)
        cm.timesAssign(sat)
        on = true
    }
    if (r.invert) {
        cm.timesAssign(
            ColorMatrix(
                floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f,
                ),
            ),
        )
        on = true
    }
    if (r.brightnessOn && kotlin.math.abs(r.brightness) > 0.4f) {
        val b = r.brightness / 100f * 255f
        cm.timesAssign(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, b,
                    0f, 1f, 0f, 0f, b,
                    0f, 0f, 1f, 0f, b,
                    0f, 0f, 0f, 1f, 0f,
                ),
            ),
        )
        on = true
    }
    return if (on) ColorFilter.colorMatrix(cm) else null
}

@Composable
private fun ChapterCatalogSheet(
    title: String,
    chapters: List<CachedChapter>,
    currentId: String,
    lang: String,
    onPick: (CachedChapter) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentIndex = chapters.indexOfFirst { it.id == currentId }.coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentIndex)
    Dialog(
        onDismissRequest = onDismiss,
        title = title,
        confirmAction = CouiDialogAction(text = if (lang == "en") "Cancel" else "取消", onClick = onDismiss),
        placement = CouiDialogPlacement.Bottom,
    ) {
        LazyColumn(
            Modifier.fillMaxWidth().heightIn(max = 520.dp),
            state = listState,
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            items(chapters.size, key = { chapters[it].id }) { i ->
                val ch = chapters[i]
                val on = ch.id == currentId
                Row(
                    Modifier
                        .fillMaxWidth()
                        .couiClickable { onPick(ch) }
                        .heightIn(min = 52.dp)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = on)
                    Spacer(Modifier.width(16.dp))
                    CouiText(
                        text = ch.title,
                        kind = CouiTextKind.PreferenceTitle,
                        color = if (on) CouiTheme.colorScheme.accent else CouiTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
internal fun ReaderSettingsSheet(
    lang: String,
    more: More,
    reading: ReadingPrefs,
    onChange: (ReadingPrefs) -> Unit,
    onDismiss: () -> Unit,
) {
    fun t(cn: String, tw: String, en: String) = when (lang) {
        "en" -> en
        "zh-TW" -> tw
        else -> cn
    }
    var tab by remember { mutableIntStateOf(0) }
    var pick by remember { mutableStateOf<String?>(null) }
    BackHandler(enabled = pick != null) { pick = null }
    fun labelOf(id: String, opts: List<Pair<String, String>>): String =
        opts.find { it.first == id }?.second ?: t("默认", "預設", "Default")
    val navs = listOf("default" to t("默认", "預設", "Default"), "edge" to t("边缘", "邊緣", "Edges"), "l" to "L")
    val pads = listOf("none" to more.none, "15" to "15%", "s" to t("小", "小", "Small"), "m" to t("中", "中", "Medium"))
    val bgs = listOf("black" to more.bgBlack, "gray" to more.bgGray, "white" to more.bgWhite)
    val blends = listOf(
        "default" to t("默认", "預設", "Default"),
        "multiply" to t("正片叠底", "色彩增值", "Multiply"),
        "screen" to t("滤色", "濾色", "Screen"),
        "overlay" to t("叠加", "疊加", "Overlay"),
        "lighten" to t("变亮", "變亮", "Lighten"),
        "darken" to t("变暗", "變暗", "Darken"),
    )
    Dialog(
        onDismissRequest = { if (pick != null) pick = null else onDismiss() },
        confirmAction = CouiDialogAction(text = if (pick != null) t("返回", "返回", "Back") else t("完成", "完成", "Done"), onClick = { if (pick != null) pick = null else onDismiss() }),
        placement = CouiDialogPlacement.Bottom,
    ) {
        val currentPick = pick
        if (currentPick == null) {
            CouiTabs(
                items = listOf(0 to t("通用", "通用", "General"), 1 to t("滤镜", "濾鏡", "Filter")),
                selected = tab,
                onSelect = { tab = it },
                modifier = Modifier.padding(top = 7.dp),
                background = CouiTheme.colorScheme.menuSurface,
                centered = true,
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 440.dp)
                .couiVerticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            if (currentPick != null) {
                val spec = when (currentPick) {
                    "nav" -> Triple(more.navLayout, navs, reading.webtoonNav)
                    "pad" -> Triple(more.sidePad, pads, reading.sidePad)
                    "bg" -> Triple(more.bgColor, bgs, reading.bgColor)
                    else -> Triple(t("滤镜调和模式", "濾鏡調和模式", "Blend mode"), blends, reading.filterBlend)
                }
                SmallTitle(spec.first)
                spec.second.forEach { (id, label) ->
                    RadioButtonPreference(label, spec.third == id, {
                        when (currentPick) {
                            "nav" -> onChange(reading.copy(webtoonNav = id))
                            "pad" -> onChange(reading.copy(sidePad = id))
                            "bg" -> onChange(reading.copy(bgColor = id))
                            else -> onChange(reading.copy(filterBlend = id))
                        }
                        pick = null
                    })
                }
            } else if (tab == 0) {
                ArrowPreference(more.bgColor, summary = labelOf(reading.bgColor, bgs), onClick = { pick = "bg" })
                SwitchPreference(reading.showPageNum, { onChange(reading.copy(showPageNum = it)) }, more.showPageNum)
                SwitchPreference(reading.fullscreen, { onChange(reading.copy(fullscreen = it)) }, more.fullscreen)
                SwitchPreference(reading.keepScreenOn, { onChange(reading.copy(keepScreenOn = it)) }, more.keepScreenOn)
                SwitchPreference(reading.chapterTransition, { onChange(reading.copy(chapterTransition = it)) }, more.chapterTransition)
                SwitchPreference(reading.pageAnim, { onChange(reading.copy(pageAnim = it)) }, more.pageAnim)
                SmallTitle(t("条漫", "條漫", "Webtoon"))
                ArrowPreference(more.navLayout, summary = labelOf(reading.webtoonNav, navs), onClick = { pick = "nav" })
                SwitchPreference(reading.webtoonReverseTap != "none", { onChange(reading.copy(webtoonReverseTap = if (it) "v" else "none")) }, more.reverseTap)
                ArrowPreference(t("侧边填充", "側邊填充", "Side padding"), summary = labelOf(reading.sidePad, pads), onClick = { pick = "pad" })
            } else {
                SwitchPreference(reading.filterOn, { onChange(reading.copy(filterOn = it)) }, t("自定义滤镜", "自訂濾鏡", "Custom filter"))
                SliderPreference(reading.filterR, { onChange(reading.copy(filterR = it)) }, 0f..255f, label = "R", enabled = reading.filterOn)
                SliderPreference(reading.filterG, { onChange(reading.copy(filterG = it)) }, 0f..255f, label = "G", enabled = reading.filterOn)
                SliderPreference(reading.filterB, { onChange(reading.copy(filterB = it)) }, 0f..255f, label = "B", enabled = reading.filterOn)
                SliderPreference(reading.filterA, { onChange(reading.copy(filterA = it)) }, 0f..255f, label = "A", enabled = reading.filterOn)
                ArrowPreference(t("滤镜调和模式", "濾鏡調和模式", "Blend mode"), summary = labelOf(reading.filterBlend, blends), onClick = { pick = "blend" })
                SwitchPreference(reading.grayscale, { onChange(reading.copy(grayscale = it)) }, t("灰度", "灰階", "Grayscale"))
                SwitchPreference(reading.invert, { onChange(reading.copy(invert = it)) }, t("反转", "反轉", "Invert"))
                SwitchPreference(reading.brightnessOn, { onChange(reading.copy(brightnessOn = it)) }, t("自定义亮度", "自訂亮度", "Custom brightness"))
                SliderPreference(reading.brightness, { onChange(reading.copy(brightness = it)) }, -100f..100f, icon = ShuIcons.Brightness, enabled = reading.brightnessOn)
            }
        }
    }
}

@Composable
private fun ChapterDivider(
    finishedTitle: String,
    nextTitle: String,
    fg: Color,
    lang: String,
    onNext: () -> Unit,
) {
    val done = if (lang == "en") "Finished" else "已读完"
    val next = if (lang == "en") "Next" else "下一章"
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(done, color = fg.copy(alpha = 0.45f), fontSize = 12.sp)
        if (finishedTitle.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(finishedTitle, color = fg.copy(alpha = 0.82f), fontSize = 13.sp, textAlign = TextAlign.Center)
        }
        if (nextTitle.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(next, color = fg.copy(alpha = 0.45f), fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                nextTitle,
                color = fg,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.couiClickable(onClick = onNext),
            )
        }
    }
}

private fun Modifier.readerTaps(
    key: Any?,
    onTap: (Float, Float) -> Unit,
    onLong: (Float, Float) -> Unit,
): Modifier = pointerInput(key) {
    coroutineScope {
        awaitEachGesture {
            val down = awaitFirstDown(pass = PointerEventPass.Initial, requireUnconsumed = false)
            val start = down.position
            var fired = false
            var zooming = false
            val job = launch {
                delay(viewConfiguration.longPressTimeoutMillis)
                if (!zooming) {
                    fired = true
                    onLong(start.x / size.width.coerceAtLeast(1), start.y / size.height.coerceAtLeast(1))
                }
            }
            val slop = viewConfiguration.touchSlop
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Final)
                val pressed = event.changes.filter { it.pressed }
                if (pressed.size >= 2 || event.changes.any { it.isConsumed }) {
                    job.cancel()
                    zooming = true
                }
                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                if (!zooming && (change.position - start).getDistance() > slop) {
                    job.cancel()
                }
                if (!change.pressed || change.changedToUpIgnoreConsumed()) {
                    job.cancel()
                    if (!fired && !zooming && (change.position - start).getDistance() <= slop) {
                        onTap(change.position.x / size.width, change.position.y / size.height)
                    }
                    break
                }
            }
        }
    }
}

private fun anchorScrollTo(list: androidx.compose.foundation.lazy.LazyListState, next: Float, ratio: Float, floor: Int = 0) {
    var idx = list.firstVisibleItemIndex
    var off = next.roundToInt()
    val stop = floor.coerceAtLeast(0)
    if (off < 0) {
        val avg = list.layoutInfo.visibleItemsInfo.map { it.size }.average().takeIf { it > 1.0 } ?: 800.0
        val step = (avg * ratio).toInt().coerceAtLeast(1)
        while (off < 0 && idx > stop) {
            idx -= 1
            off += step
        }
        if (off < 0) off = 0
    }
    list.requestScrollToItem(idx, off.coerceAtLeast(0))
}

private fun Modifier.readerZoom(
    key: Any?,
    webtoon: Boolean,
    pad: () -> Float,
    zoom: () -> Float,
    panX: () -> Float,
    panY: () -> Float,
    onPinch: (Boolean) -> Unit = {},
    onChange: (z: Float, x: Float, y: Float, focalY: Float, changed: Boolean) -> Unit,
): Modifier = pointerInput(key) {
    val minZ = 0.45f
    val maxZ = 4f
    fun contentWidth(z: Float): Float {
        val w = size.width.toFloat().coerceAtLeast(1f)
        val base = (1f - pad() * 2f).coerceIn(0.4f, 1f)
        return w * base * z
    }
    fun clampWebX(z: Float, x: Float): Float {
        val w = size.width.toFloat().coerceAtLeast(1f)
        val content = contentWidth(z)
        return if (content <= w) (w - content) / 2f else x.coerceIn(w - content, 0f)
    }
    fun clampPage(z: Float, x: Float, y: Float): Pair<Float, Float> {
        if (z <= 1.01f) return 0f to 0f
        val w = size.width.toFloat().coerceAtLeast(1f)
        val h = size.height.toFloat().coerceAtLeast(1f)
        val lx = w * (z - 1f) / 2f
        val ly = h * (z - 1f) / 2f
        return x.coerceIn(-lx, lx) to y.coerceIn(-ly, ly)
    }
    awaitEachGesture {
        awaitFirstDown(pass = PointerEventPass.Initial, requireUnconsumed = false)
        var prevSpan = 0f
        var pinch = false
        try {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val pressed = event.changes.filter { it.pressed }
            if (pressed.isEmpty()) break
            if (pressed.size >= 2) {
                if (!pinch) {
                    pinch = true
                    onPinch(true)
                }
                event.changes.forEach { it.consume() }
                val cx = (pressed[0].position.x + pressed[1].position.x) / 2f
                val cy = (pressed[0].position.y + pressed[1].position.y) / 2f
                val dx = pressed[0].position.x - pressed[1].position.x
                val dy = pressed[0].position.y - pressed[1].position.y
                val span = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
                if (prevSpan > 0f) {
                    val z1 = zoom().coerceIn(minZ, maxZ)
                    val z2 = (z1 * span / prevSpan).coerceIn(minZ, maxZ)
                    val ratio = z2 / z1
                    val w = size.width.toFloat().coerceAtLeast(1f)
                    val h = size.height.toFloat().coerceAtLeast(1f)
                    if (webtoon) {
                        val x2 = clampWebX(z2, cx - (cx - panX()) * ratio)
                        onChange(z2, x2, 0f, cy, kotlin.math.abs(z2 - z1) > 0.001f)
                    } else {
                        val px = cx - w / 2f - (cx - w / 2f - panX()) * ratio
                        val py = cy - h / 2f - (cy - h / 2f - panY()) * ratio
                        val (x2, y2) = clampPage(z2, px, py)
                        onChange(z2, x2, y2, cy, kotlin.math.abs(z2 - z1) > 0.001f)
                    }
                }
                prevSpan = span
            } else {
                prevSpan = 0f
                val ch = pressed[0]
                val z = zoom()
                if (z > 1.02f) {
                    val deltaX = ch.position.x - ch.previousPosition.x
                    val deltaY = ch.position.y - ch.previousPosition.y
                    if (webtoon) {
                        val horizontal = kotlin.math.abs(deltaX) >= kotlin.math.abs(deltaY)
                        if (pinch || horizontal) {
                            onChange(z, clampWebX(z, panX() + deltaX), 0f, 0f, false)
                            ch.consume()
                        }
                    } else {
                        val (x2, y2) = clampPage(z, panX() + deltaX, panY() + deltaY)
                        onChange(z, x2, y2, 0f, false)
                        ch.consume()
                    }
                } else if (pinch) {
                    ch.consume()
                }
            }
        }
        } finally {
            if (pinch) onPinch(false)
        }
    }
}

/** 阅读页菜单在系统导航栏边距之外再抬高的距离，避免挡住底部页码/章节。 */
internal val ReaderSheetLift = 16.dp

@Composable
internal fun PageActionSheet(
    items: List<Triple<ImageVector, String, String>>,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
    lift: androidx.compose.ui.unit.Dp = ReaderSheetLift,
) {
    Dialog(
        modifier = Modifier.offset(y = -lift),
        onDismissRequest = onDismiss,
        confirmAction = CouiDialogAction(text = "取消", onClick = onDismiss),
        placement = CouiDialogPlacement.Bottom,
    ) {
        // 行的按压底色四周留白、自带圆角，和面板圆角一致；不画默认焦点/选中底色，也没有描边。
        Column(Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 8.dp)) {
            items.forEach { (icon, label, id) -> SheetActionRow(icon, label) { onPick(id) } }
        }
    }
}

@Composable
private fun SheetActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    // COUI 内缩圆角列表项：按压底色（couiButtonFeedback）只铺在这一行自己的圆角范围里。
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .couiClickable(shape = com.itos.coui.shape.CouiShape(16.dp, 3f), role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = CouiTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        CouiText(text = label, kind = CouiTextKind.PreferenceTitle, textAlign = TextAlign.Start)
    }
}

/**
 * 阅读器工具栏某个按钮的菜单落点，全部是窗口坐标（像素）：[button] 是被点按钮的真实边界，
 * [panelTop] 是底部工具面板的顶边。
 */
internal data class ReaderMenuSpot(val button: IntRect, val panelTop: Int)

/**
 * 工具栏弹出菜单的定位：水平中心对准按钮中心，底边在工具面板顶边之上 [gap]，
 * 左右夹在屏幕边缘 [margin] 以内，上方不越过 [margin]。
 * 不用 COUI Menu 的定位：它把锚点压成一个点（锚点左上角/最近一次触点）再左对齐展开，所以菜单总是偏到按钮右侧。
 */
internal class ReaderMenuPosition(
    private val spot: ReaderMenuSpot,
    private val margin: Int,
    private val gap: Int,
) : androidx.compose.ui.window.PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: androidx.compose.ui.unit.IntSize,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        popupContentSize: androidx.compose.ui.unit.IntSize,
    ): IntOffset {
        val w = popupContentSize.width
        val h = popupContentSize.height
        val centre = (spot.button.left + spot.button.right) / 2
        val maxX = (windowSize.width - margin - w).coerceAtLeast(margin)
        val x = (centre - w / 2).coerceIn(margin, maxX)
        val y = (spot.panelTop - gap - h).coerceAtLeast(margin)
        return IntOffset(x, y)
    }
}

/** 阅读器工具栏弹出菜单：COUI 菜单面板配色/圆角/投影，行用 couiClickable 内缩圆角按压，选中项带 COUI 勾选框。 */
@Composable
internal fun ReaderPopupMenu(
    spot: ReaderMenuSpot,
    options: List<Pair<String, String>>,
    selected: String,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val density = LocalDensity.current
    val position = remember(spot, density) {
        with(density) { ReaderMenuPosition(spot, margin = 16.dp.roundToPx(), gap = 8.dp.roundToPx()) }
    }
    val shape = com.itos.coui.shape.CouiShape(20.dp, 3f)
    androidx.compose.ui.window.Popup(
        popupPositionProvider = position,
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.PopupProperties(focusable = true),
    ) {
        Column(
            Modifier
                .width(androidx.compose.foundation.layout.IntrinsicSize.Max)
                .widthIn(min = 160.dp, max = 280.dp)
                .shadow(16.dp, shape, ambientColor = Color.Black.copy(alpha = 0.18f), spotColor = Color.Black.copy(alpha = 0.18f))
                .clip(shape)
                .background(CouiTheme.colorScheme.menuSurface)
                .padding(8.dp),
        ) {
            options.forEach { (id, label) ->
                val on = id == selected
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .clip(com.itos.coui.shape.CouiShape(14.dp, 3f))
                        .background(if (on) CouiTheme.colorScheme.accent.copy(alpha = 0.1f) else Color.Transparent)
                        .couiClickable(shape = com.itos.coui.shape.CouiShape(14.dp, 3f), role = Role.Button) {
                            onPick(id)
                            onDismiss()
                        }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    com.itos.coui.checkbox.Checkbox(checked = on)
                    Spacer(Modifier.width(12.dp))
                    CouiText(
                        text = label,
                        kind = CouiTextKind.PreferenceTitle,
                        color = if (on) CouiTheme.colorScheme.accent else CouiTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun BoxScope.ReaderChrome(
    title: String,
    chapter: String,
    bookmarked: Boolean,
    index: Int,
    count: Int,
    onBack: () -> Unit,
    onBookmark: () -> Unit,
    onSeek: (Int) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRotate: () -> Unit,
    onSettings: () -> Unit,
    onCatalog: () -> Unit,
    backdrop: CouiBackdrop?,
    rotMenu: @Composable (ReaderMenuSpot) -> Unit = {},
) {
    // 记下按钮与底部面板的真实窗口边界，弹出菜单据此定位（见 [ReaderMenuPosition]）。
    var panelTop by remember { mutableIntStateOf(-1) }
    val toolBounds = remember { mutableStateMapOf<String, IntRect>() }
    fun Modifier.toolSpot(key: String) = onGloballyPositioned { c ->
        val r = c.boundsInWindow()
        toolBounds[key] = IntRect(r.left.roundToInt(), r.top.roundToInt(), r.right.roundToInt(), r.bottom.roundToInt())
    }
    @Composable
    fun MenuAt(key: String, menu: @Composable (ReaderMenuSpot) -> Unit) {
        val b = toolBounds[key] ?: return
        if (panelTop < 0) return
        menu(ReaderMenuSpot(b, panelTop))
    }
    MenuAt("rot", rotMenu)

    ReaderTopBar(
        backdrop = backdrop,
        title = chapter.ifBlank { title },
        onBack = onBack,
        modifier = Modifier.align(Alignment.TopCenter),
    )
    ReaderBottomPanel(backdrop, Modifier.align(Alignment.BottomCenter), onPanelTop = { panelTop = it }) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CouiRoundIcon(ShuIcons.SkipPrev, "上一话", onPrev)
            PageSlider(index, count, onSeek, Modifier.weight(1f).padding(horizontal = 4.dp))
            CouiRoundIcon(ShuIcons.SkipNext, "下一话", onNext)
        }
        // 三个工具等分面板内宽：目录 / 旋转 / 设置。
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            ReaderToolCell { ReaderTool(ShuIcons.Rows, "目录", onCatalog) }
            ReaderToolCell { ReaderTool(ShuIcons.Rotate, "旋转", onRotate, Modifier.toolSpot("rot")) }
            ReaderToolCell { ReaderTool(CouiIcons.Outlined.Setting, "设置", onSettings) }
        }
    }
}

/** 工具栏里等宽的一格：按钮（图标与文字）在格内水平居中。 */
@Composable
internal fun RowScope.ReaderToolCell(content: @Composable () -> Unit) {
    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { content() }
}

/** 页码进度：COUI 官方 Slider（离散步进），两端为当前页/总页数。 */
@Composable
private fun PageSlider(index: Int, count: Int, onSeek: (Int) -> Unit, modifier: Modifier = Modifier) {
    val last = (count - 1).coerceAtLeast(1)
    // Track the finger locally so the thumb follows the drag even before the pager/list settles.
    var drag by remember { mutableStateOf<Float?>(null) }
    LaunchedEffect(count) { drag = null }
    val shown = (drag ?: index.toFloat()).coerceIn(0f, last.toFloat())
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        CouiText(text = "${(index + 1).coerceIn(1, count.coerceAtLeast(1))}", kind = CouiTextKind.Description, color = CouiTheme.colorScheme.onSurfaceVariant, modifier = Modifier.widthIn(min = 28.dp), textAlign = TextAlign.Center)
        com.itos.coui.slider.Slider(
            modifier = Modifier.weight(1f),
            value = shown,
            onValueChange = { v ->
                drag = v
                onSeek(v.roundToInt().coerceIn(0, last))
            },
            valueRange = 0f..last.toFloat(),
            enabled = count > 1,
            onValueChangeFinished = { drag = null },
        )
        CouiText(text = "${count.coerceAtLeast(1)}", kind = CouiTextKind.Description, color = CouiTheme.colorScheme.onSurfaceVariant, modifier = Modifier.widthIn(min = 28.dp), textAlign = TextAlign.Center)
    }
}

private fun saveReaderPage(context: Context, bmp: android.graphics.Bitmap, title: String, ownFolder: Boolean): Boolean {
    val safe = title.replace(Regex("[\\\\/:*?\"<>|]"), " ").take(40).ifBlank { "书漫屿" }
    val name = "page-${System.currentTimeMillis()}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= 29) {
            put(MediaStore.Images.Media.RELATIVE_PATH, if (ownFolder) "Pictures/书漫屿/$safe" else "Pictures/书漫屿")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val uri = runCatching { context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) }.getOrNull()
        ?: return false
    val wrote = runCatching {
        context.contentResolver.openOutputStream(uri)?.use { bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, it) } ?: false
    }.getOrDefault(false)
    if (!wrote) return false
    if (Build.VERSION.SDK_INT >= 29) {
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        context.contentResolver.update(uri, values, null, null)
    }
    return true
}

@Composable
private fun FeedbackPane(copy: Copy, sites: List<WebSite>, onBack: () -> Unit) {
    val context = LocalContext.current
    val saved = "日志已保存"
    val fail = "无法写入日志"
    val scope = rememberCoroutineScope()
    val createLauncher = rememberLauncherForActivityResult(CreateZipDoc()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        AppLog.add("feedback", "export")
        val app = context.applicationContext
        val list = sites.toList()
        // 收集日志要跑 logcat 并等它结束，压缩写入 SAF 也是磁盘 IO：都放到 IO 线程，失败只提示不崩。
        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                runCatching {
                    val text = AppLog.dump(app, list)
                    writeZip(app, uri, "log.txt", text.toByteArray(Charsets.UTF_8))
                }.getOrDefault(false)
            }
            CouiToast.show(if (ok) saved else fail)
        }
    }
    Scaffold(topBar = { SubTopBar(copy.feedback, onBack, BarTitle.End) }) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                Group {
                    ArrowPreference(
                        title = copy.feedbackSum,
                        summary = "提取后保存为 zip",
                        onClick = {
                            val name = "shumanyu-log-" + java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm", java.util.Locale.US)
                                .format(java.util.Date()) + ".zip"
                            createLauncher.launch(name)
                        },
                    )
                }
            }
        }
    }
}

private class CreateZipDoc : ActivityResultContracts.CreateDocument("application/zip") {
    override fun createIntent(context: Context, input: String): Intent =
        super.createIntent(context, input).addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
        )
}

private fun writeZip(context: Context, uri: Uri, entry: String, bytes: ByteArray): Boolean {
    val payload = if (bytes.isEmpty()) "书漫屿\n(empty)\n".toByteArray(Charsets.UTF_8) else bytes
    val tmp = java.io.File(context.cacheDir, "pack-${System.currentTimeMillis()}.zip")
    return runCatching {
        java.util.zip.ZipOutputStream(java.io.BufferedOutputStream(java.io.FileOutputStream(tmp))).use { zip ->
            zip.putNextEntry(ZipEntry(entry))
            zip.write(payload)
            zip.closeEntry()
            zip.flush()
        }
        if (tmp.length() < 22L) return false
        val out = context.contentResolver.openOutputStream(uri, "wt") ?: return false
        out.use { stream -> tmp.inputStream().use { it.copyTo(stream) } }
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        }
        true
    }.getOrDefault(false).also { tmp.delete() }
}

private fun readBackupJson(context: Context, uri: Uri): String? {
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
    val text = if (bytes.size >= 2 && bytes[0] == 0x50.toByte() && bytes[1] == 0x4B.toByte()) {
        ZipInputStream(java.io.ByteArrayInputStream(bytes)).use { zip ->
            var found: String? = null
            while (true) {
                val entry = zip.nextEntry ?: break
                if (!entry.isDirectory && entry.name.endsWith(".json")) {
                    found = zip.readBytes().toString(Charsets.UTF_8)
                    break
                }
            }
            found
        }
    } else {
        bytes.toString(Charsets.UTF_8)
    }
    return text?.trim()?.removePrefix("\uFEFF")?.takeIf { it.startsWith("{") }
}

@Composable
private fun BackupPane(
    copy: Copy,
    prefs: Prefs,
    library: List<String>,
    favorites: List<String>,
    onBack: () -> Unit,
    onImported: (BackupData) -> Unit,
) {
    val context = LocalContext.current
    val lang = resolveLang(prefs.lang, LocalConfiguration.current)
    val more = moreOf(lang)
    val saved = when (lang) { "en" -> "Backup saved"; "zh-TW" -> "備份已建立"; else -> "备份已创建" }
    val restored = when (lang) { "en" -> "Backup restored"; "zh-TW" -> "備份已還原"; else -> "备份已恢复" }
    val bad = when (lang) { "en" -> "Could not read backup"; "zh-TW" -> "無法讀取備份"; else -> "无法读取备份" }
    val failWrite = when (lang) { "en" -> "Could not write backup"; "zh-TW" -> "無法寫入備份"; else -> "无法写入备份" }
    val scope = rememberCoroutineScope()
    val createLauncher = rememberLauncherForActivityResult(CreateZipDoc()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        // 读偏好在主线程，压缩与写入 SAF 文件放到 IO 线程，失败只提示不崩。
        val json = runCatching { prefs.exportJson(library, favorites) }.getOrNull()
        val app = context.applicationContext
        scope.launch {
            val ok = json != null && withContext(Dispatchers.IO) {
                runCatching { writeZip(app, uri, "backup.json", json.toByteArray(Charsets.UTF_8)) }.getOrDefault(false)
            }
            if (ok) {
                prefs.lastBackupAt = System.currentTimeMillis()
                prefs.backupUri = uri.toString()
            }
            CouiToast.show(if (ok) saved else failWrite)
        }
    }
    val openLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val raw = runCatching { readBackupJson(context, uri) }.getOrNull()
        val data = raw?.let { prefs.importJson(it) }
        if (data != null) {
            onImported(data)
            CouiToast.show(restored)
        } else {
            CouiToast.show(bad)
        }
    }
    Box(Modifier.fillMaxSize()) {
        Scaffold(topBar = { SubTopBar(copy.backup, onBack, BarTitle.End) }) { padding ->
            LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                item {
                    Group {
                        ArrowPreference(more.createBackup, summary = more.createBackupSum, onClick = {
                            val name = java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm", java.util.Locale.US)
                                .format(java.util.Date()) + ".zip"
                            createLauncher.launch(name)
                        })
                        ArrowPreference(more.restoreData, summary = more.restoreDataSum, onClick = {
                            openLauncher.launch(arrayOf("application/zip", "application/json", "application/octet-stream", "*/*"))
                        })
                    }
                }
                item { Spacer(Modifier.height(28.dp)) }
            }
        }
    }
}

@Composable
internal fun ThemePane(
    copy: Copy,
    lang: String,
    colorMode: ColorSchemeMode,
    onMode: (ColorSchemeMode) -> Unit,
    onBanner: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(topBar = { SubTopBar(copy.themeSettings, onBack, BarTitle.End) }) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding) {
            item {
                Group(title = copy.appearance) {
                    RadioButtonPreference(
                        title = copy.followSystem,
                        selected = colorMode == ColorSchemeMode.System,
                        onClick = { onMode(ColorSchemeMode.System) },
                    )
                    RadioButtonPreference(
                        title = copy.light,
                        selected = colorMode == ColorSchemeMode.Light,
                        onClick = { onMode(ColorSchemeMode.Light) },
                    )
                    RadioButtonPreference(
                        title = copy.dark,
                        selected = colorMode == ColorSchemeMode.Dark,
                        onClick = { onMode(ColorSchemeMode.Dark) },
                    )
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
            item {
                Group {
                    ArrowPreference(
                        title = if (lang == "en") "Header background" else if (lang == "zh-TW") "背景欄" else "背景栏",
                        summary = if (lang == "en") "Change the top background bar" else if (lang == "zh-TW") "修改頂部背景欄" else "修改顶部背景栏",
                        onClick = onBanner,
                    )
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

@Composable
internal fun IntroPane(
    copy: Copy,
    onBack: () -> Unit,
    onNotes: () -> Unit,
    onCheckUpdate: () -> Unit,
) {
    val context = LocalContext.current
    val link = "https://t.me/shumanyu1"
    val body = copy.introBody
    val cut = body.indexOf(link)
    Scaffold(topBar = { SubTopBar(copy.intro, onBack, BarTitle.End) }) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding) {
            item {
                Group {
                    Column(Modifier.padding(16.dp)) {
                        Text("书漫屿", style = AppTheme.textStyles.title2)
                        Text("ShuManYu ${installedVersion(context)}", color = AppTheme.colorScheme.onSurfaceVariantSummary)
                        Spacer(Modifier.height(10.dp))
                        if (cut >= 0) {
                            Text(body.substring(0, cut), fontSize = 14.sp, color = AppTheme.colorScheme.onSurfaceSecondary)
                            Text(
                                link,
                                fontSize = 14.sp,
                                color = AppTheme.colorScheme.primary,
                                modifier = Modifier.couiClickable {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                        )
                                    }
                                },
                            )
                        } else {
                            Text(body, fontSize = 14.sp, color = AppTheme.colorScheme.onSurfaceSecondary)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("项目地址",fontSize=14.sp,color=AppTheme.colorScheme.onSurfaceSecondary)
                        Text(
                            "https://github.com/${GitHubUpdates.REPOSITORY}",
                            fontSize=14.sp,
                            color=AppTheme.colorScheme.primary,
                            modifier=Modifier.couiClickable {
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/${GitHubUpdates.REPOSITORY}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                            },
                        )
                    }
                }
            }
            item {
                Group(title = "软件功能") {
                    ArrowPreference(
                        title = "检查更新",
                        summary = installedVersion(context),
                        onClick = onCheckUpdate,
                    )
                    ArrowPreference(
                        title = "更新日志",
                        summary = installedVersion(context),
                        onClick = onNotes,
                    )
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

private fun installedVersion(context:Context):String = runCatching {
    context.packageManager.getPackageInfo(context.packageName,0).versionName.orEmpty()
}.getOrDefault("0.1.3")

private data class UpdateSection(val title: String, val items: List<String>)

@Composable
private fun AppUpdateDialog(release: GitHubRelease, updater: AppUpdateController) {
    val accent = CouiTheme.colorScheme.accent
    val downloading = updater.downloading
    val error = updater.downloadError
    val progress = updater.progress
    val sections = remember(release.notes) { parseUpdateSections(release.notes) }
    Dialog(
        onDismissRequest = { if (!downloading) updater.later() },
        title = "发现新版本 ${release.version}",
        confirmAction = CouiDialogAction(
            text = when {
                downloading -> "取消下载"
                error.isNotBlank() -> "重试"
                else -> "下载并安装"
            },
            onClick = { if (downloading) updater.cancelDownload() else updater.startDownload() },
            style = CouiDialogActionStyle.Primary,
            colors = CouiDialogDefaults.primaryActionColors(accent),
        ),
        dismissAction = CouiDialogAction(text = "稍后", onClick = { if (!downloading) updater.later() }),
        placement = CouiDialogPlacement.Bottom,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .couiVerticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                sections.forEach { section ->
                    Text(
                        section.title,
                        style = AppTheme.textStyles.title2,
                        color = CouiTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                    )
                    section.items.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Text(
                                "•",
                                color = CouiTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                item,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = CouiTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
            if (downloading) {
                Spacer(Modifier.height(10.dp))
                CouiLinearProgress(progress, Modifier.fillMaxWidth().padding(start = 6.dp, end = 6.dp, top = 4.dp))
            }
            if (error.isNotBlank() && !downloading) {
                Spacer(Modifier.height(8.dp))
                Text(
                    error,
                    color = CouiTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }
    }
}

/** 把 Release 正文拆成「分组 + 条目」，弹窗按分区标题和圆点列表显示。 */
private fun parseUpdateSections(raw: String): List<UpdateSection> {
    val sections = ArrayList<UpdateSection>()
    var title = "更新内容"
    var items = ArrayList<String>()
    fun flush() {
        if (items.isNotEmpty()) {
            sections.add(UpdateSection(title, items.toList()))
            items = ArrayList()
        }
    }
    raw.lineSequence().forEach { line ->
        val t = line.trim()
        when {
            t.startsWith("<!--") -> Unit
            t.startsWith("##") -> {
                flush()
                title = t.trimStart('#').trim().ifBlank { "更新内容" }
            }
            t.startsWith("- ") -> items.add(t.removePrefix("- ").trim())
            t.startsWith("* ") -> items.add(t.removePrefix("* ").trim())
            t.isBlank() -> Unit
            else -> items.add(t)
        }
    }
    flush()
    if (sections.isEmpty()) {
        sections.add(
            UpdateSection(
                "更新内容",
                listOf(raw.trim().ifBlank { "新版已发布，下载后覆盖安装即可保留数据。" }),
            ),
        )
    }
    return sections
}

@Composable
private fun NotesPane(lang: String, onBack: () -> Unit) {
    val context=LocalContext.current
    val title=if(lang=="en")"What's new" else "更新日志"
    val releases = listOf(
        "0.1.3" to listOf(
            "新增" to listOf(
                "新增禁漫天堂、爱看韩漫书源。",
                "新增和图书小说源。",
                "拷贝漫画书源。",
                "漫画阅读页新增章节目录。",
                "漫画详情页目录支持正序/倒序切换。",
                "UI 全面采用 COUI 重构。",
                "一镜到底展开动画：打开书籍、功能和设置页面时，页面从点击处展开，返回时收回原位。",
                "书城、书架、历史常驻搜索框，点开进入独立搜索页。",
                "新增应用内检查更新、下载 APK 并拉起系统安装。",
            ),
            "修复" to listOf(
                "搜索未跟随当前书源。",
                "从漫画详情返回搜索页会重新加载。",
                "部分漫画图片错位。",
                "阅读时拖动进度条没反应。",
                "连续条漫单击唤出工具栏时页面跟着滑动。",
                "漫画加载中轻微滑动会直接跳到结尾。",
                "存储空间清理闪退。",
                "保存反馈日志闪退。",
                "创建备份闪退。",
                "存储空间清理完成后闪退。",
                "书城分类标签显示错位。",
                "书城切换书源时闪退。",
                "和图书章节正文解析失败。",
            ),
            "优化" to listOf(
                "书架默认改为紧凑网格显示。",
                "适配底栏宽度。",
                "一镜到底动画更顺滑。",
                "章节目录改为居中排版，去掉序号、总数与分割线。",
                "章节目录改为与 COUI 选择弹窗一致的单选行排版。",
                "阅读页工具精简为目录、旋转、设置。",
                "阅读设置精简：保留通用与滤镜，条漫相关设置并入通用。",
                "更新日志按版本分区显示。",
                "小说阅读页改用 COUI 组件重写。",
                "适配 Monet 主题图标。",
                "书城搜索改为右上角放大镜入口。",
                "预测性返回默认开启，移除开关。",
                "存储空间改为缓存与下载两项，下载可一键清理。",
                "漫画阅读页长按菜单显示优化。",
                "阅读器工具栏弹出菜单对齐所点按钮。",
                "按压效果统一为 COUI 样式，去除按压黑色描边。",
                "底栏恢复为之前的尺寸。",
                "书架、历史搜索改为右上角放大镜入口。",
                "一镜到底动画全程带圆角。",
                "书城搜索框缩小并移到顶栏。",
                "全新启动图标。",
                "右上角按钮改为悬浮样式，图标重新绘制。",
                "更多菜单重新设计。",
                "底栏加大，四个图标重绘，选中时显示实心图标。",
                "弹窗出现时底栏自动隐藏，关闭后从底部弹回。",
                "空状态文案更新。",
                "首次打开即提示授权，提示框在屏幕正中。",
                "存储空间清理更彻底。",
                "阅读设置精简。",
                "更新日志重写。",
                "软件介绍改为关于，并在更新日志上方新增检查更新入口。",
            ),
            "移除" to listOf(
                "阅读模式选择（阅读页工具栏与设置里的预设阅读模式）。",
                "阅读模式提示浮层。",
                "点击动画设置。",
                "漫画阅读裁边功能。",
                "笔趣阁（暂无小说源，等待后续加入）。",
                "Monet 取色。",
                "实验室与内置浏览器。",
                "开源声明。",
                "网站管理与添加网站。",
                "清除 Cookies。",
                "阅读设置里的双页拆分、长按弹出选单、锁定横竖屏。",
            ),
        ),
    )
    Scaffold(topBar={SubTopBar(title,onBack,BarTitle.End)}) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding=padding) {
            item {
                Group {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                        val currentVersion = installedVersion(context)
                        Text(
                            "当前版本 $currentVersion",
                            color = AppTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                        )
                        releases.forEachIndexed { index, (version, groups) ->
                            if (index > 0) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                        .height(1.dp)
                                        .background(CouiTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
                                )
                            }
                            Column(Modifier.fillMaxWidth().padding(top = if (index == 0) 14.dp else 0.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "版本 $version",
                                        style = AppTheme.textStyles.title1,
                                        color = AppTheme.colorScheme.onSurface,
                                    )
                                    if (version == currentVersion) {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "当前",
                                            color = AppTheme.colorScheme.primary,
                                            fontSize = 11.sp,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(AppTheme.colorScheme.primary.copy(alpha = 0.12f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp),
                                        )
                                    }
                                }
                                groups.forEach { (head, items) ->
                                    Text(
                                        head,
                                        style = AppTheme.textStyles.title2,
                                        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp),
                                    )
                                    items.forEach { text ->
                                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                                            Text(
                                                "•",
                                                color = AppTheme.colorScheme.onSurfaceVariantSummary,
                                                fontSize = 14.sp,
                                                lineHeight = 22.sp,
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(text, fontSize = 14.sp, lineHeight = 22.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagePane(copy: Copy, lang: String, onLang: (String) -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = { SubTopBar(copy.language, onBack, BarTitle.End) }) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding) {
            item {
                Group {
                    RadioButtonPreference(copy.followSystem, selected = lang == "system", onClick = { onLang("system") })
                    RadioButtonPreference(copy.zhCN, selected = lang == "zh-CN", onClick = { onLang("zh-CN") })
                    RadioButtonPreference(copy.zhTW, selected = lang == "zh-TW", onClick = { onLang("zh-TW") })
                    RadioButtonPreference(copy.en, selected = lang == "en", onClick = { onLang("en") })
                }
            }
        }
    }
}

@Composable
private fun PageImage(
    src: String,
    webtoon: Boolean = true,
    colorFilter: ColorFilter? = null,
    tint: Color = Color.Transparent,
    tintBlend: BlendMode = BlendMode.SrcOver,
    nonce: Int = 0,
    fit: Boolean = false,
    align: Alignment = Alignment.TopCenter,
) {
    var bmp by remember(src, nonce) { mutableStateOf(if (nonce == 0) Crawler.peekPage(src) else null) }
    var fail by remember(src, nonce) { mutableStateOf(false) }
    LaunchedEffect(src, nonce) {
        val cached = bmp
        if (nonce == 0 && cached != null && !cached.isRecycled) return@LaunchedEffect
        val decoded = withContext(Dispatchers.IO) { Crawler.downloadBitmap(src, force = nonce > 0) }
        bmp = decoded
        fail = decoded == null
    }
    val display = bmp
    val shot = remember(display) { display?.asImageBitmap() }
    when {
        shot != null -> {
            val b = display!!
            val ratio = (b.width.toFloat() / b.height.toFloat().coerceAtLeast(1f)).coerceIn(0.00001f, 100000f)
            val layer = Modifier
            val wash = if (tint.alpha > 0.004f) {
                Modifier.drawWithContent {
                    drawContent()
                    drawRect(tint, blendMode = tintBlend)
                }
            } else {
                Modifier
            }
            if (webtoon) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio)
                        .then(layer)
                        .then(wash),
                ) {
                    Image(
                        bitmap = shot,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = if (fit) ContentScale.Fit else ContentScale.FillWidth,
                        alignment = align,
                        colorFilter = colorFilter,
                    )
                }
            } else if (fit) {
                BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val vw = maxWidth.value.coerceAtLeast(1f)
                    val vh = maxHeight.value.coerceAtLeast(1f)
                    val fitW = if (ratio > vw / vh) vw else vh * ratio
                    val fitH = fitW / ratio
                    Box(
                        Modifier
                            .width(fitW.dp)
                            .height(fitH.dp)
                            .then(layer)
                            .then(wash),
                    ) {
                        Image(
                            bitmap = shot,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            colorFilter = colorFilter,
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxSize().then(layer).then(wash)) {
                    Image(
                        bitmap = shot,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = align,
                        colorFilter = colorFilter,
                    )
                }
            }
        }
        fail -> Unit
        else -> Box(
            Modifier
                .fillMaxWidth()
                .height(if (webtoon) 28.dp else 120.dp)
                .background(Color.Transparent),
        )
    }
}

private fun shrinkCover(src: android.graphics.Bitmap, maxPx: Int): android.graphics.Bitmap {
    val max = maxOf(src.width, src.height)
    if (max <= maxPx) return src
    val s = maxPx.toFloat() / max
    val w = (src.width * s).toInt().coerceAtLeast(1)
    val h = (src.height * s).toInt().coerceAtLeast(1)
    val out = android.graphics.Bitmap.createScaledBitmap(src, w, h, true)
    if (out !== src) src.recycle()
    return out
}

private fun sampleSize(width: Int, height: Int, maxPx: Int): Int {
    var sample = 1
    while (width / sample > maxPx * 2 || height / sample > maxPx * 2) {
        val next = sample * 2
        if (width / next < 48 || height / next < 48) break
        sample = next
    }
    return sample
}

private fun decodeCoverFile(path: String, maxPx: Int = 640): android.graphics.Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    val opts = BitmapFactory.Options().apply { inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, maxPx) }
    val raw = BitmapFactory.decodeFile(path, opts) ?: return null
    return shrinkCover(raw, maxPx)
}

@Composable
private fun SmartCover(
    src: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    plate: Boolean = false,
    onReady: (() -> Unit)? = null,
    fallbackTitle: String = "",
) {
    val context = LocalContext.current
    var bmp by remember(src) { mutableStateOf(CoverLoader.peek(src)) }
    var failed by remember(src) { mutableStateOf(false) }
    var retryImage by remember(src) { mutableIntStateOf(0) }
    LaunchedEffect(src,retryImage) {
        failed=false
        val hit = CoverLoader.peek(src)
        if (hit != null) {
            bmp = hit
            return@LaunchedEffect
        }
        val decoded = withContext(Dispatchers.IO) {
            val remote = when {
                src.startsWith("http://") || src.startsWith("https://") -> src
                src.startsWith("//") -> "https:$src"
                else -> ""
            }
            val path = when {
                remote.isNotEmpty() -> ""
                src.startsWith("file:") -> src.removePrefix("file://")
                src.startsWith("/") -> src
                else -> ""
            }
            when {
                remote.isNotEmpty() -> if (retryImage > 0) CoverLoader.refresh(remote) else CoverLoader.await(remote)
                path.isNotEmpty() -> decodeCoverFile(path, 480)
                else -> null
            }
        }
        if (decoded != null) {
            bmp = decoded
        } else failed=true
    }
    val shot = remember(bmp) { bmp?.asImageBitmap() }
    if (shot != null) {
        Image(bitmap = shot, contentDescription = null, modifier = modifier, contentScale = contentScale)
        if (onReady != null) SideEffect { onReady() }
    } else if (fallbackTitle.isNotBlank()) {
        NovelCoverPlate(fallbackTitle, modifier)
    } else if (!plate) {
        Box(modifier.background(AppTheme.colorScheme.surface).couiClickable(enabled = failed && src.isNotBlank(), shape = null) { retryImage++ },contentAlignment=Alignment.Center) {
            if(failed)Column(horizontalAlignment=Alignment.CenterHorizontally) {
                Text(if(src.isBlank())"暂无封面" else "封面暂不可用",fontSize=11.sp,color=AppTheme.colorScheme.onSurfaceVariantSummary)
                if(src.isNotBlank())Text("点击重试",fontSize=10.sp,color=AppTheme.colorScheme.onSurfaceVariantSummary)
            }
        }
    }
}

private fun resolveLang(pref: String, config: Configuration): String {
    if (pref != "system") return pref
    val locale = config.locales[0]
    val hant = locale.script == "Hant" || locale.country == "TW" || locale.country == "HK" || locale.country == "MO"
    return when {
        locale.language == "zh" && hant -> "zh-TW"
        locale.language == "zh" -> "zh-CN"
        locale.language.startsWith("en") -> "en"
        else -> "zh-CN"
    }
}

private fun applyCoverMode(context: Context, prefs: Prefs, crawled: MutableList<CrawledWork>, on: Boolean) {
    prefs.coverEntries().forEach { (id, path, orig) ->
        val next = if (on && path.isNotBlank() && java.io.File(path).exists()) path else orig
        if (next.isBlank()) return@forEach
        val i = crawled.indexOfFirst { it.id == id }
        if (i >= 0 && crawled[i].cover != next) {
            val site = crawled[i].siteId
            crawled[i] = crawled[i].copy(cover = next)
            LocalStore.saveListing(site, crawled.filter { it.siteId == site })
        }
        val disk = LocalStore.loadWork(id)
        if (disk != null && disk.work.cover != next) {
            LocalStore.saveWork(disk.copy(work = disk.work.copy(cover = next)))
        }
    }
}

private fun coverOf(id: String, crawled: List<CrawledWork>): String {
    val listed = crawled.find { it.id == id }?.cover.orEmpty()
    val disk = LocalStore.loadWork(id)?.work?.cover.orEmpty()
    val local = listOf(listed, disk).firstOrNull { it.startsWith("/") || it.startsWith("file:") }
    if (!local.isNullOrBlank()) return local
    val cat = catalog.find { it.id == id }?.cover.orEmpty()
    if (cat.isNotBlank()) return cat
    return listed.ifBlank { disk }
}

@Composable
private fun FoldersPane(
    lang: String,
    folders: List<BookFolder>,
    crawled: List<CrawledWork>,
    favoriteIds: List<String>,
    webSites: List<WebSite>,
    onFavoriteRemove: (String) -> Unit,
    onAssign: (String) -> Unit,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onCover: (String) -> Unit,
    onDelete: (String) -> Unit,
    onCreate: (String) -> Unit,
) {
    var drop by remember { mutableStateOf<String?>(null) }
    var creating by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }
    val title = if (lang == "en") "Favorites" else "收藏"
    val ids = favoriteIds.distinct()
    val works = ids.mapNotNull { id -> (crawled.find { it.id == id } ?: LocalStore.loadWork(id)?.work)?.let { id to it } }.toMap()
    val tags = works.values.flatMap { favoriteTags(it) }.distinct().sorted()
    var selectedTab by remember { mutableStateOf("__all") }
    var editingBook by remember { mutableStateOf<String?>(null) }
    val novelIds=works.values.filter { work ->
        Sites.isNovelSite(work.siteId)
    }.map { it.id }.toSet()
    val tabs = if(ids.isEmpty()) emptyList() else buildList {
        if(folders.isNotEmpty())add("__folders" to "收藏夹")
        add("__all" to if(lang=="en")"All" else "全部")
        if(novelIds.isNotEmpty())add("__novels" to if(lang=="en")"Novels" else if(lang=="zh-TW")"小說" else "小说")
        if(works.keys.any { it !in novelIds })add("__comics" to if(lang=="en")"Manga" else if(lang=="zh-TW")"漫畫" else "漫画")
        addAll(tags.map { it to it })
    }
    LaunchedEffect(tabs,folders.isNotEmpty()) {
        if(tabs.none { it.first==selectedTab })selectedTab=if(ids.isEmpty() && folders.isNotEmpty())"__folders" else "__all"
    }
    val shown = ids.filter { id -> when(selectedTab) {
        "__all" -> true
        "__novels" -> id in novelIds
        "__comics" -> id !in novelIds
        else -> works[id]?.let { selectedTab in favoriteTags(it) }==true
    } }
    val createTitle = if (lang == "en") "New collection" else if (lang == "zh-TW") "建立收藏夾" else "创建收藏夹"
    val nameLabel = if (lang == "en") "Name" else if (lang == "zh-TW") "名稱" else "名称"
    val createWord = if (lang == "en") "Create" else if (lang == "zh-TW") "建立" else "创建"
    Scaffold(
        topBar = {
            SubTopBar(
                title = title,
                onBack = onBack,
                align = BarTitle.Center,
                actions = {
                    IconButton(onClick = {
                        draft = ""
                        creating = true
                    }) { Icon(CouiIcons.Outlined.Add, contentDescription = createTitle) }
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if(tabs.isNotEmpty())Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal=12.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                tabs.forEach { (key,label) ->
                    Text(label,modifier=Modifier.clip(RoundedCornerShape(18.dp)).background(if(selectedTab==key)AppTheme.colorScheme.primary.copy(alpha=0.13f) else Color.Transparent).couiClickable { selectedTab=key }.padding(horizontal=14.dp,vertical=12.dp),color=if(selectedTab==key)AppTheme.colorScheme.primary else AppTheme.colorScheme.onSurface,fontWeight=if(selectedTab==key)FontWeight.Bold else FontWeight.Normal)
                }
            }
            if(selectedTab=="__folders") {
            LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = PaddingValues(bottom=24.dp), modifier = Modifier.fillMaxSize()) {
                items(folders, key = { it.id }) { folder ->
                    Card(Modifier.padding(horizontal = 12.dp, vertical = 6.dp).fillMaxWidth()) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .couiClickable(
                                    onClick = { onOpen(folder.id) },
                                    onLongClick = { drop = folder.id },
                                )
                                .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 12.dp),
                        ) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    folder.name,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    "${folder.books.size}",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colorScheme.onSurface,
                                )
                            }
                            if (folder.books.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                ) {
                                    val shown = folder.books.take(4)
                                    shown.forEach { id ->
                                        SmartCover(
                                            coverOf(id, crawled),
                                            Modifier
                                                .weight(1f)
                                                .aspectRatio(0.78f)
                                                .clip(RoundedCornerShape(4.dp))
                                                .couiClickable { onCover(id) },
                                        )
                                    }
                                    repeat(4 - shown.size) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
            } else if(shown.isEmpty()) {
                EmptyState("(´・ω・`)",if(lang=="en")"No favorites yet" else "暂无收藏",Modifier.fillMaxSize())
            } else {
                LazyColumn(Modifier.fillMaxSize(), overscrollEffect = rememberCouiOverscrollEffect(), contentPadding=PaddingValues(bottom=24.dp)) {
                    items(shown,key={it}) { id ->
                        val work=works[id]
                        Card(Modifier.padding(horizontal=12.dp,vertical=5.dp).fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().couiClickable(onClick={onCover(id)},onLongClick={editingBook=id}).padding(10.dp),verticalAlignment=Alignment.CenterVertically) {
                                SmartCover(coverOf(id,crawled),Modifier.width(58.dp).height(78.dp).clip(RoundedCornerShape(6.dp)))
                                Column(Modifier.weight(1f).padding(horizontal=12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                                    Text(work?.title ?: id,maxLines=2,overflow=TextOverflow.Ellipsis)
                                    Text(work?.let { favoriteTags(it).joinToString(" · ") }.orEmpty(),fontSize=12.sp,color=AppTheme.colorScheme.onSurfaceVariantSummary,maxLines=1,overflow=TextOverflow.Ellipsis)
                                }
                                IconButton(onClick={editingBook=id}) { Text("⋯") }
                            }
                        }
                    }
                }
            }
        }
    }
    editingBook?.let { id ->
        Dialog(
            onDismissRequest = { editingBook = null },
            confirmAction = CouiDialogAction(text = if (lang == "en") "Cancel" else "取消", onClick = { editingBook = null }),
            placement = CouiDialogPlacement.Bottom,
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
                ArrowPreference(if (lang == "en") "Add to collection" else "加入收藏夹", onClick = { editingBook = null; onAssign(id) })
                ArrowPreference(if (lang == "en") "Remove from favorites" else "取消收藏", onClick = { editingBook = null; onFavoriteRemove(id) })
            }
        }
    }

    if (creating) {
        CouiNameDialog(
            title = createTitle,
            initial = "",
            placeholder = nameLabel,
            confirm = createWord,
            cancel = if (lang == "en") "Cancel" else "取消",
            onConfirm = { onCreate(it); creating = false },
            onDismiss = { creating = false },
        )
    }
    val id = drop
    if (id != null) {
        ConfirmSheet(
            title = if (lang == "en") "Delete folder?" else if (lang == "zh-TW") "刪除收藏夾？" else "删除收藏夹？",
            message = if (lang == "en") "Saved titles stay in Favorites." else "只删除收藏夹，作品仍保留在收藏中。",
            confirm = if (lang == "en") "Delete" else "删除",
            cancel = if (lang == "en") "Cancel" else "取消",
            onConfirm = {
                onDelete(id)
                drop = null
            },
            onDismiss = { drop = null },
        )
    }
}


@Composable
private fun FontPane(
    lang: String,
    gear: String,
    onGear: (String) -> Unit,
    onBack: () -> Unit,
) {
    val title = if (lang == "en") "Adjust type" else if (lang == "zh-TW") "調整字體" else "调整字体"
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                Group {
                    Column(Modifier.padding(16.dp)) {
                        Text(if (lang == "en") "Sample" else "示范", fontSize = 13.sp, color = AppTheme.colorScheme.onSurfaceVariantSummary)
                        Spacer(Modifier.height(8.dp))
                        Text("书漫屿", fontSize = 28.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (lang == "en") "The quick brown fox reads a quiet comic on the island." else "这一段用来预览当前字号，小、中、大三档。",
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                        )
                    }
                }
            }
            item {
                Group {
                    RadioButtonPreference(if (lang == "en") "Follow system" else if (lang == "zh-TW") "跟隨系統" else "跟随系统", selected = gear == "system", onClick = { onGear("system") })
                    RadioButtonPreference(if (lang == "en") "Small" else "小", selected = gear == "s", onClick = { onGear("s") })
                    RadioButtonPreference(if (lang == "en") "Medium" else "中", selected = gear == "m", onClick = { onGear("m") })
                    RadioButtonPreference(if (lang == "en") "Large" else "大", selected = gear == "l", onClick = { onGear("l") })
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

internal fun favoriteTags(work: CrawledWork): List<String> =
    (work.tags + work.genres.split(Regex("""[,，、/|\s]+""")))
        .map { it.trim() }.filter { it.isNotBlank() && it !in setOf("推荐","推薦","全部","搜索","其他","小说","漫画") && !it.startsWith("__") }.distinct()

internal fun normalizedNovelChapterTitle(title:String,index:Int,body:String=""):String {
    val clean=title.trim().replace(Regex("""^\d+\s+(?=第)"""),"")
    val heading=if(clean.isBlank() || clean.all { it.isDigit() }) "第${clean.toIntOrNull() ?: (index+1)}章" else clean
    val first=body.trim().lineSequence().firstOrNull().orEmpty().trim().trimStart('　')
    val generic=Regex("""^第[零〇一二三四五六七八九十百千万两\d\s]+[章节回]$""")
    return if(generic.matches(heading) && first.length in 3..70 && first.startsWith(heading))first else heading
}

