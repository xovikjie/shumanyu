package com.shumanyu.app

import android.app.Activity
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
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
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
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.Toast
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.viewinterop.AndroidView
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
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.Search
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle

private val LocalScreenLive = compositionLocalOf { true }

private enum class Tab { Shelf, History, Discover, Mine }

private sealed interface Screen {
    data object Tabs : Screen
    data object Search : Screen
    data class Remote(val id: String) : Screen
    data class RemoteReader(val workId: String, val chapterId: String, val page: Int = 0) : Screen
    data object Sources : Screen
    data object Sites : Screen
    data class Web(val url: String, val title: String, val backTo: String = "") : Screen
    data object Backup : Screen
    data object Theme : Screen
    data object Reading : Screen
    data object Language : Screen
    data object Feedback : Screen
    data object Intro : Screen
    data object Lab : Screen
    data object More : Screen
    data object Folders : Screen
    data object Oss : Screen
    data object Notes : Screen
    data object Font : Screen
    data object Gate : Screen
    data class Folder(val id: String) : Screen
    data object Banner : Screen
    data object Storage : Screen
    data class StorageBin(val kind: String) : Screen
}

private data class Copy(
    val shelf: String,
    val discover: String,
    val search: String,
    val mine: String,
    val discoverSub: String,
    val searchSub: String,
    val myShelf: String,
    val favOnly: String,
    val all: String,
    val onlyFav: String,
    val emptyShelf: String,
    val hot: String,
    val onlineSrc: String,
    val findStories: String,
    val searchHint: String,
    val recommend: String,
    val prefs: String,
    val darkTheme: String,
    val followSystem: String,
    val light: String,
    val dark: String,
    val predictive: String,
    val predictiveSum: String,
    val dataBackup: String,
    val sources: String,
    val sourcesSum: String,
    val backup: String,
    val backupSum: String,
    val about: String,
    val appearance: String,
    val appearanceSum: String,
    val language: String,
    val startRead: String,
    val fav: String,
    val faved: String,
    val addShelf: String,
    val inShelf: String,
    val catalogTitle: String,
    val prev: String,
    val next: String,
    val export: String,
    val restore: String,
    val reset: String,
    val addSource: String,
    val sourceName: String,
    val sourceNote: String,
    val importBtn: String,
    val designBody: String,
    val zhCN: String,
    val zhTW: String,
    val en: String,
    val weekly: String,
    val history: String,
    val historySub: String,
    val bookstore: String,
    val animeAll: String,
    val siteList: String,
    val addSite: String,
    val siteUrl: String,
    val noSites: String,
    val emptyCat: String,
    val clearHistory: String,
    val reserved: String,
    val crawlNow: String,
    val emptyHistory: String,
    val offline: String,
    val featured: String,
    val manage: String,
    val loading: String,
    val newChapters: String,
    val help: String,
    val helpBody: String,
    val fillSite: String,
    val checkSites: String,
    val newSites: String,
    val gotIt: String,
    val cancel: String,
    val siteName: String,
    val checking: String,
    val feedback: String,
    val feedbackSum: String,
    val copyLog: String,
    val shareLog: String,
    val searchEmpty: String,
    val themeSettings: String,
    val themeSettingsSum: String,
    val intro: String,
    val introSum: String,
    val introBody: String,
    val noNewChapters: String,
    val checkingUpdates: String,
)

private fun copyOf(lang: String) = when (lang) {
    "zh-TW" -> Copy(
        "書架", "書城", "搜尋", "我的",
        "連接你的漫畫源", "漫畫、作者或類型",
        "我的書架", "收藏夾", "全部", "只看收藏", "你最近沒有看過\n請去書城看一本唄",
        "熱門漫畫", "線上漫畫源 · 即時更新", "發現新故事", "搜尋書漫嶼", "推薦",
        "偏好設定", "深色主題", "跟隨系統", "淺色", "深色",
        "預測性返回", "從邊緣滑動預覽上一頁",
        "資料源與備份", "線上漫畫源", "筆趣閣小說 · 書城直接看",
        "備份與還原", "匯出 / 還原本機資料",
        "關於", "介面", "MIUIX", "語言",
        "開始閱讀", "收藏", "已收藏", "加入書架", "已加入書架", "目錄",
        "上一頁", "下一頁", "匯出本機資料", "從文字還原", "重設本機資料",
        "匯入自訂資料源", "源名稱", "JSON / URL 備註", "解鎖更多精彩內容",
        "書漫嶼介面回到 MIUIX：系統藍、紙感底和底欄。",
        "簡體中文", "繁體中文", "English", "本週閱讀與儲存都在這台裝置上",
        "歷史", "看過的都會留在這裡", "書城", "目錄", "網站列表", "新增網站", "網站地址",
        "還沒有網站。填寫地址後將自動抓取漫畫目錄。", "該分類暫無內容，新增網站後自動抓取",
        "清空歷史", "已預留，填寫網址後抓取", "抓取目錄",
        "你最近還沒有看誒", "目前沒有網路，已顯示離線內容",
        "精選網站", "管理", "正在載入…", "發現新章節",
        "使用幫助",
        "此功能支援數千來自網路上的漫畫站，在使用此功能前，需要針對你想看的漫畫站製作「收錄規則」，或者匯入網友製作的規則\n\n小提醒：\n1. 不是所有添加的網站都會出現在此，有些網站只支援搜尋功能\n2. 按「▼」可以按分組查看網站，可以在網站管理中設定分組\n3. 長按網站的名稱可以刪除或置頂\n4. 「自動檢查」可以清除失效無用網站\n5. 建議每隔一段時間做一次全面的「自動檢查」，實驗證明有些網站時好時壞\n6. 不定期會有可以匯入的新來源，有空打開「匯入網站」功能看看",
        "填寫網站", "檢查網站", "新增網站", "知道了", "取消", "網站名稱", "正在檢查…",
        "回饋", "提取問題日誌", "複製日誌", "分享日誌", "輸入書名搜尋",
        "主題設定", "深色、圖示與取色",
        "軟體介紹", "書漫嶼是什麼",
        "書漫嶼是一款集成漫畫和小說源的應用，目前還處於早期階段，漫畫源、小說源以及各項功能還在持續更新中....\n\n軟體僅供學習交流，請勿用於商業用途。\n\n建議與反饋：https://t.me/shumanyu1",
        "暫無新章節", "正在檢查更新…",
    )
    "en" -> Copy(
        "Library", "Store", "Search", "Me",
        "Connected sources", "Title, author or genre",
        "My library", "Collections", "All", "Favorites", "Nothing here yet\nGo find a title in the store",
        "Trending", "Online sources", "New stories", "Search ShuManYu", "Suggested",
        "Preferences", "Dark theme", "Follow system", "Light", "Dark",
        "Predictive back", "Swipe from the edge to preview the previous screen",
        "Sources & backup", "Online sources", "Biquge novels in the store",
        "Backup & restore", "Export or restore local data",
        "About", "Appearance", "MIUIX", "Language",
        "Read", "Save", "Saved", "Add to shelf", "On shelf", "Chapters",
        "Prev", "Next", "Export data", "Restore from text", "Reset local data",
        "Add a custom source", "Source name", "JSON / URL note", "Add source",
        "The interface is back on MIUIX: system blue, a paper surface, and a bottom bar.",
        "Simplified Chinese", "Traditional Chinese", "English", "Reading and storage stay on this device",
        "History", "Everything you’ve read stays here", "Store", "Catalog", "Sites", "Add site", "Website URL",
        "No websites yet. Add a URL to fetch listings.", "Nothing in this category yet",
        "Clear history", "Reserved — fill in a URL to crawl", "Crawl",
        "You haven't read anything lately", "You're offline — showing saved titles",
        "Featured sites", "Manage", "Loading…", "New chapters",
        "Help",
        "This bookstore can crawl thousands of comic sites. Add a site URL, or import a friend's rules, before browsing.\n\nTips:\n1. Not every added site appears here — some only support search\n2. Use ▼ to group sites in site management\n3. Long-press a name to pin or delete it\n4. Auto-check removes dead sites\n5. Run a full check now and then; some sites flicker\n6. New sources show up in Import from time to time",
        "Add site", "Check sites", "New sites", "Got it", "Cancel", "Site name", "Checking…",
        "Feedback", "Export problem logs", "Copy log", "Share log", "Type a title to search",
        "Theme", "Dark, icon and Monet",
        "About", "What this app is",
        "ShuManYu is an early app for manga and novel sources. Sources and features are still being updated....\n\nFor study and exchange only. Not for commercial use.\n\nFeedback: https://t.me/shumanyu1",
        "No new chapters", "Checking for updates…",
    )
    else -> Copy(
        "书架", "书城", "搜索", "我的",
        "连接你的漫画源", "漫画、作者或类型",
        "我的书架", "收藏夹", "全部", "仅看收藏", "你最近没有看过\n请去书城看一本呗",
        "热门漫画", "在线漫画源 · 实时更新", "发现新故事", "搜索书漫屿", "推荐",
        "偏好设置", "深色主题", "跟随系统", "浅色", "深色",
        "预测性返回", "从边缘滑动预览上一页",
        "数据源与备份", "在线漫画源", "笔趣阁小说 · 书城直接看",
        "备份与恢复", "导出 / 恢复本地数据",
        "关于", "界面", "MIUIX", "语言",
        "开始阅读", "收藏", "已收藏", "加入书架", "已加入书架", "目录",
        "上一页", "下一页", "导出本地数据", "从文本恢复", "重置本地数据",
        "导入自定义数据源", "源名称", "JSON / URL 备注", "解锁更多精彩内容",
        "书漫屿界面回到 MIUIX：系统蓝、纸感底和底栏。",
        "简体中文", "繁体中文", "English", "本周阅读与存储都在这台设备上",
        "历史", "看过的都会留在这里", "书城", "目录", "网站列表", "添加网站", "网站地址",
        "还没有网站。填写地址后将自动抓取漫画目录。", "该分类暂无内容，添加网站后自动抓取",
        "清空历史", "已预留，填写网址后抓取", "抓取目录",
        "你最近还没有看诶", "当前没有网络，已显示离线内容",
        "精选网站", "管理", "正在加载…", "发现新章节",
        "使用帮助",
        "此功能支持数千个来自网络的漫画站。使用前请针对想看的漫画站填写网址，或导入朋友制作的规则。\n\n小提醒：\n1. 不是所有添加的网站都会出现在此，有些网站只支持搜索\n2. 按「▼」可以按分组查看网站，可以在网站管理中设定分组\n3. 长按网站名称可以删除或置顶\n4. 「自动检查」可以清除失效无用网站\n5. 建议每隔一段时间做一次全面检查，有些网站时好时坏\n6. 不定期会有可以导入的新来源，有空打开「填写网站」看看",
        "填写网站", "检查网站", "新增网站", "知道了", "取消", "网站名称", "正在检查…",
        "反馈", "提取问题日志", "复制日志", "分享日志", "输入书名搜索",
        "主题设置", "深色、图标与取色",
        "软件介绍", "书漫屿是什么",
        "书漫屿是一款集成漫画和小说源的应用，目前还处于早期阶段，漫画源、小说源以及各项功能还在持续更新中....\n\n软件仅供学习交流，请勿用于商业用途。\n\n建议与反馈：https://t.me/shumanyu1",
        "暂无新章节", "正在检查更新…",
    )
}

@Composable
fun ShuManYuApp() {
    val context = LocalContext.current
    val prefs = remember { Prefs(context).also { it.enableFoldersOfficial() } }
    var colorMode by remember { mutableStateOf(prefs.colorMode) }
    var langPref by remember { mutableStateOf(prefs.lang) }
    val lang = resolveLang(langPref, LocalConfiguration.current)
    var predictive by remember { mutableStateOf(prefs.predictiveBack) }
    var incognito by remember { mutableStateOf(prefs.incognito) }
    var allowAdult by remember { mutableStateOf(prefs.allowAdult) }
    var peekGuard by remember { mutableStateOf(prefs.peekGuard) }
    var iconStyle by remember { mutableStateOf(prefs.iconStyle) }
    val forceTablet = remember { prefs.forceTablet }
    var tabletAsked by remember { mutableStateOf(prefs.forceTablet) }
    var themeSeed by remember { mutableIntStateOf(prefs.themeSeed) }
    var themePalette by remember { mutableStateOf(prefs.themePalette) }
    var fontGear by remember { mutableStateOf(prefs.fontGear) }
    var histLayout by remember { mutableStateOf(prefs.histLayout) }
    var appBrowser by remember { mutableStateOf(prefs.appBrowser) }
    var mineBanner by remember { mutableStateOf(prefs.mineBanner) }
    var storageRev by remember { mutableIntStateOf(0) }
    val copy = remember(lang) { copyOf(lang) }
    val paletteStyle = remember(themePalette) {
        runCatching { ThemePaletteStyle.valueOf(themePalette) }.getOrDefault(ThemePaletteStyle.TonalSpot)
    }
    val controller = remember(colorMode, themeSeed, paletteStyle) {
        ThemeController(colorMode, keyColor = Color(themeSeed), paletteStyle = paletteStyle)
    }

    MiuixTheme(controller = controller) {
        val baseDensity = LocalDensity.current
        val fontScale = when (fontGear) {
            "s" -> 0.85f
            "m" -> 1f
            "l" -> 1.18f
            else -> baseDensity.fontScale
        }
        CompositionLocalProvider(LocalDensity provides Density(baseDensity.density, fontScale = fontScale)) {
        val systemDark = isSystemInDarkTheme()
        val dark = when (colorMode) {
            ColorSchemeMode.MonetDark, ColorSchemeMode.Dark -> true
            ColorSchemeMode.MonetLight, ColorSchemeMode.Light -> false
            else -> systemDark
        }
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
        var tab by remember { mutableStateOf(Tab.Shelf) }
        val purgedBiquge = remember { prefs.purgeLegacyBiquge(); true }
        val library = remember { mutableStateListOf<String>().also { it.addAll(prefs.library.filter { id -> id !in NOVEL_IDS && (purgedBiquge) }) } }
        val favorites = remember { mutableStateListOf<String>().also { it.addAll(prefs.favorites.filter { id -> id !in NOVEL_IDS }) } }
        val history = remember { mutableStateListOf<String>().also { it.addAll(prefs.history.filter { id -> id !in NOVEL_IDS }) } }
        val readProgress = remember { mutableStateMapOf<String, Progress>().also { it.putAll(prefs.progress) } }
        val webSites = remember { mutableStateListOf<WebSite>().also { it.addAll(prefs.webSites()) } }
        val crawled = remember {
            mutableStateListOf<CrawledWork>().also { list ->
                list.addAll(
                    LocalStore.loadAllListings().filter { w ->
                        w.siteId != "legado-b778fe6b" && !w.siteId.startsWith("xiu2-") && w.siteId != "ru" && w.siteId != Sites.BIQUGE2 && w.siteId != Sites.BIQUGE3 &&
                            !w.href.contains("rumanhua", true) && !w.id.startsWith("ru")
                    },
                )
            }
        }
        LaunchedEffect(Unit) {
            prefs.customCover = true
            prefs.savePhotos = true
            LocalStore.dropListing(Sites.BIQUGE2)
            LocalStore.dropListing(Sites.BIQUGE3)
            LocalStore.dropListing("xiu2-collection")
            LocalStore.dropListing("legado-b778fe6b")
            applyCoverMode(context, prefs, crawled, true)
        }
        val sources = remember {
            mutableStateListOf<Source>().also { list ->
                defaultSources().forEach { s ->
                    list.add(s.copy(enabled = prefs.sourceEnabled(s.id, s.enabled)))
                }
                list.addAll(prefs.customSources())
            }
        }
        var query by remember { mutableStateOf("") }
        var discoverCols by remember { mutableIntStateOf(2) }
        val enabledIds = sources.filter { it.enabled }.map { it.id }.toSet()
        var online by remember { mutableStateOf(Net.online(context)) }

        fun persistLibrary() { prefs.library = library.toList() }
        fun persistFav() { prefs.favorites = favorites.toList() }
        fun persistHistory() {
            prefs.history = history.toList()
            prefs.progress = readProgress.toMap()
        }
        val appScope = rememberCoroutineScope()
        val appUpdater=remember { AppUpdateController(context.applicationContext) }
        LaunchedEffect(Unit) { appUpdater.check() }
        val historyPending = remember { java.util.concurrent.atomic.AtomicBoolean(false) }
        DisposableEffect(Unit) {
            onDispose { persistHistory() }
        }
        fun persistSources() {
            sources.filter { s -> defaultSources().none { it.id == s.id } }.let { prefs.setCustomSources(it) }
            sources.forEach { prefs.setSourceEnabled(it.id, it.enabled) }
        }
        fun persistSites() { prefs.setWebSites(webSites.toList()) }
        val folders = remember { mutableStateListOf<BookFolder>().also { it.addAll(prefs.folders) } }
        var folderBook by remember { mutableStateOf<String?>(null) }
        fun saveFolders() { prefs.folders = folders.toList() }
        fun openFolderSheet(id: String) { folderBook = id }
        var histMenu by remember { mutableStateOf(false) }
        var histFind by remember { mutableStateOf(false) }
        var histQ by remember { mutableStateOf("") }
        var histPick by remember { mutableStateOf(false) }
        val histSelected = remember { mutableStateListOf<String>() }
        var histAsk by remember { mutableStateOf("") }
        var shelfFind by remember { mutableStateOf(false) }
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
        val nativeCrawls=remember { mutableMapOf<String,Thread>() }
        fun crawlSite(site: WebSite, category: String = "推荐", silent: Boolean = true, append: Boolean = false, onDone: () -> Unit = {}) {
            if (!Net.online(context)) {
                if (!silent) Toast.makeText(context, copy.offline, Toast.LENGTH_SHORT).show()
                onDone()
                return
            }
            fun applyListing(category: String, items: List<CrawledWork>, replaceBin: Boolean = false) {
                if (items.isEmpty()) return
                val copySite = Sites.kindOf(site) == Sites.COPY
                val stamp = if (copySite) null else listingBin(category)
                val tagged = items.map { item ->
                    val withCat = if (item.cat.isBlank()) item.copy(cat = category) else item
                    if (Sites.kindOf(site) == XiuSources.KIND) {
                        XiuSources.forCategory(withCat, category)
                    } else if (copySite && category != "推荐" && category != "推薦") {
                        withCat.copy(listed = withCat.listed.filterNot { tagEq(it, category) } + category)
                    } else when {
                        stamp == null -> withCat
                        binFits(withCat, stamp) ->
                            withCat.copy(listed = withCat.listed.filterNot { tagEq(it, stamp) } + stamp)
                        sameTag(withCat.cat, stamp) && withCat.cat != "推荐" && withCat.cat != "推薦" ->
                            withCat.copy(cat = "")
                        else -> withCat
                    }
                }
                val byId = LinkedHashMap<String, CrawledWork>()
                crawled.filter { it.siteId == site.id && it.cat != "搜索" }.forEach { byId[it.id] = it }
                if (replaceBin && stamp != null) {
                    val incoming = tagged.map { it.id }.toHashSet()
                    for ((id, prev) in byId) {
                        if (id in incoming) continue
                        if (!prev.listed.any { tagEq(it, stamp) } && !tagEq(prev.cat, stamp)) continue
                        val nextListed = prev.listed.filterNot { tagEq(it, stamp) }
                        val nextCat = when {
                            prev.cat == "推荐" || prev.cat == "推薦" -> prev.cat
                            tagEq(prev.cat, stamp) -> "全部"
                            else -> prev.cat
                        }
                        byId[id] = prev.copy(listed = nextListed, cat = nextCat)
                    }
                }
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
                val (items, err) = if(append) XiuSources.more(site.id,category) else Crawler.fetch(site.url, site.id, category) { partial ->
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
                        applyListing(category, items, replaceBin = !append)
                        patchCovers(items)
                        LocalStore.saveListing(site.id, crawled.filter { it.siteId == site.id })
                    } else if (err != null) {
                        AppLog.add("crawl", "${site.name}/$category $err")
                    }
                    persistSites()
                    if (!silent && err != null && items.isEmpty()) {
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                    onDone()
                }
            }
            if(Sites.kindOf(site)==XiuSources.KIND)nativeCrawls.put(site.id,worker)?.interrupt()
            worker.start()
        }
        LaunchedEffect(Unit) {
            online = Net.online(context)
        }
        var detailBack by remember { mutableStateOf<Screen>(Screen.Tabs) }
        fun pinDetailBack() {
            detailBack = when (val s = screen) {
                Screen.Search -> Screen.Search
                Screen.Folders -> Screen.Folders
                is Screen.Folder -> s
                is Screen.Remote, is Screen.RemoteReader -> detailBack
                else -> Screen.Tabs
            }
        }
        var adultBlock by remember { mutableStateOf(false) }
        fun openWork(id: String) {
            if (!allowAdult && isHiddenAdult(id, crawled, webSites)) {
                adultBlock = true
                return
            }
            pinDetailBack()
            screen = Screen.Remote(id)
        }
        fun openRead(id: String) {
            if (!allowAdult && isHiddenAdult(id, crawled, webSites)) {
                adultBlock = true
                return
            }
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
        fun openSite(url: String, title: String, backTo: String = "") {
            if (url.isBlank()) return
            if (appBrowser) {
                screen = Screen.Web(url, title, backTo)
            } else {
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            }
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

        fun refreshLibrary() {
            if (shelfBusy) return
            if (!Net.online(context)) {
                Toast.makeText(context, copy.offline, Toast.LENGTH_SHORT).show()
                return
            }
            val ids = library.toList()
            shelfBusy = true
            val icon = AppIcons.mipmap(iconStyle)
            ShelfNotes.hold("更新中...", icon)
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
                        ShelfNotes.flash("没有章节更新诶😩", icon)
                    } else {
                        ShelfNotes.flash("有章节更新快去通知栏看看是哪一本！", icon)
                        found.forEach { Notify.chapter(context, it.title, it.index, it.chapterTitle) }
                    }
                }
            }.start()
        }

        val shelfScroll = rememberLazyListState()
        val histScroll = rememberLazyListState()
        val discoverScroll = rememberLazyListState()
        val discoverCat = remember { mutableStateOf("推荐") }
        val mineScroll = rememberLazyListState()
        val discoverSiteScroll = rememberScrollState()
        val libraryKey = library.joinToString(",")
        val historyKey = history.joinToString(",")
        val crawledKey = crawled.joinToString(",") { "${it.id}:${it.siteId}" }
        val gateLibrary = remember(libraryKey, crawledKey, allowAdult) {
            val raw = library.toList()
            if (allowAdult) raw else raw.filter { !isHiddenAdult(it, crawled, webSites) }
        }
        val gateHistory = remember(historyKey, crawledKey, allowAdult) {
            val raw = history.toList()
            if (allowAdult) raw else raw.filter { !isHiddenAdult(it, crawled, webSites) }
        }
        val tabContent: @Composable (PaddingValues) -> Unit = { padding ->
            Column(Modifier.fillMaxSize()) {
                if (!online) {
                    Text(
                        copy.offline,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MiuixTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 13.sp,
                        color = MiuixTheme.colorScheme.primary,
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
                            query = shelfQ,
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
                            query = histQ,
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
                            crawled = crawled,
                            webSites = webSites,
                            online = online,
                            allowAdult = allowAdult,
                            cols = discoverCols,
                            asList = forceTablet,
                            initialSiteId = prefs.lastSiteId,
                            onOpen = { openWork(it) },
                            onSites = { screen = Screen.Sites },
                            onSearch = { screen = Screen.Search },
                            onWeb = { url, title -> openSite(url, title) },
                            onToggleCols = { discoverCols = if (discoverCols == 2) 3 else 2 },
                            onCrawl = { site, cat, done -> crawlSite(site, cat, silent = true, onDone = done) },
                            onCrawlMore = { site, cat, done -> crawlSite(site, cat, silent = true, append = true, onDone = done) },
                            onSelectSite = { prefs.lastSiteId = it },
                            onAddUrl = { name, raw ->
                                var url = raw.trim()
                                if (!url.startsWith("http://") && !url.startsWith("https://")) url = "https://$url"
                                if (!Crawler.isSafe(url)) {
                                    Toast.makeText(context, if (lang == "en") "Unsupported URL" else "网址不安全或不支持", Toast.LENGTH_SHORT).show()
                                    return@DiscoverPane
                                }
                                val host = runCatching { java.net.URL(url).host.lowercase() }.getOrDefault("")
                                val dup = webSites.any { s ->
                                    val h = runCatching { java.net.URL(s.url).host.lowercase() }.getOrDefault("")
                                    h.isNotBlank() && h == host
                                }
                                if (dup) {
                                    Toast.makeText(context, if (lang == "en") "Already added" else if (lang == "zh-TW") "已經新增過" else "已经添加过", Toast.LENGTH_SHORT).show()
                                    return@DiscoverPane
                                }
                                val id = "site-${System.currentTimeMillis()}"
                                val named = name.ifBlank { siteNameFrom(url) }
                                val kind = Sites.kindOf(WebSite("tmp", named, url))
                                val site = WebSite(id, named, url, true, kind = kind)
                                webSites.add(site)
                                persistSites()
                                crawlSite(site, "推荐", silent = false) {
                                    val n = webSites.find { it.id == id }?.itemCount ?: 0
                                    if (n > 0) Toast.makeText(context, if (lang == "en") "Fetched $n" else "抓到 $n 部", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCheckSites = {
                                if (webSites.isEmpty()) return@DiscoverPane
                                Toast.makeText(context, copy.checking, Toast.LENGTH_SHORT).show()
                                val snapshot = webSites.toList()
                                Thread {
                                    val rows = snapshot.map { s ->
                                        val err = if (s.url.isBlank()) "没有网址" else Crawler.ping(s.url)
                                        s.id to err
                                    }
                                    Handler(Looper.getMainLooper()).post {
                                        rows.forEach { (id, err) ->
                                            val i = webSites.indexOfFirst { it.id == id }
                                            if (i >= 0) webSites[i] = webSites[i].copy(lastError = err.orEmpty())
                                        }
                                        persistSites()
                                        val ok = rows.count { it.second == null }
                                        val msg = when (lang) {
                                            "en" -> "OK $ok / ${rows.size}"
                                            "zh-TW" -> "可用 $ok / ${rows.size}"
                                            else -> "可用 $ok / ${rows.size}"
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                }.start()
                            },
                            listState = discoverScroll,
                            catState = discoverCat,
                            siteScroll = discoverSiteScroll,
                            bleedBottom = !forceTablet,
                        )
                        Tab.Mine -> MinePane(
                            padding = padding,
                            copy = copy,
                            lang = lang,
                            lib = gateLibrary.size,
                            fav = folders.size,
                            hist = gateHistory.size,
                            langLabel = when (langPref) {
                                "system" -> copy.followSystem
                                "zh-TW" -> copy.zhTW
                                "en" -> copy.en
                                else -> copy.zhCN
                            },
                            labTitle = when (lang) {
                                "en" -> "Lab"
                                "zh-TW" -> "實驗室"
                                else -> "实验室"
                            },
                            labSum = when (lang) {
                                "en" -> "Experimental"
                                "zh-TW" -> "實驗功能"
                                else -> "实验功能"
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
                            onLab = { screen = Screen.Lab },
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

        fun visibleHistoryIds(): List<String> {
            val q = histQ.trim()
            if (q.isBlank()) return history.toList()
            return history.filter { id ->
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
            Screen.Sources -> Screen.Lab
            is Screen.Web -> if (s.backTo.isNotBlank()) Screen.Remote(s.backTo) else Screen.Tabs
            is Screen.StorageBin -> Screen.Storage
            is Screen.Folder -> Screen.Folders
            Screen.Oss -> Screen.Intro
            Screen.Notes -> Screen.Intro
            Screen.Font -> Screen.More
            Screen.Gate -> Screen.More
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
                is Screen.Remote -> screen = detailBack
                is Screen.RemoteReader -> screen = Screen.Remote(s.workId)
                Screen.Sources -> screen = Screen.Lab
                Screen.Sites -> screen = Screen.Tabs
                is Screen.Web -> screen = if (s.backTo.isNotBlank()) Screen.Remote(s.backTo) else Screen.Tabs
                is Screen.StorageBin -> screen = Screen.Storage
                is Screen.Folder -> screen = Screen.Folders
                Screen.Oss -> screen = Screen.Intro
                Screen.Notes -> screen = Screen.Intro
                Screen.Font -> screen = Screen.More
                Screen.Gate -> screen = Screen.More
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
            isBackEnabled = predictive && screen != Screen.Tabs,
            onBackCompleted = { leaveScreen() },
        )
        BackHandler(enabled = !predictive && screen != Screen.Tabs) { leaveScreen() }
        fun screenToken(s: Screen): String = when (s) {
            Screen.Tabs -> "tabs"
            Screen.Search -> "search"
            is Screen.Remote -> "remote:${s.id}"
            is Screen.RemoteReader -> "reader:${s.workId}:${s.chapterId}"
            Screen.Sources -> "sources"
            Screen.Sites -> "sites"
            is Screen.Web -> "web:${s.url}"
            Screen.Backup -> "backup"
            Screen.Theme -> "theme"
            Screen.Reading -> "reading"
            Screen.Language -> "language"
            Screen.Feedback -> "feedback"
            Screen.Intro -> "intro"
            Screen.Lab -> "lab"
            Screen.More -> "more"
            Screen.Folders -> "folders"
            Screen.Oss -> "oss"
            Screen.Notes -> "notes"
            Screen.Font -> "font"
            Screen.Gate -> "gate"
            Screen.Banner -> "banner"
            is Screen.Folder -> "folder:${s.id}"
            Screen.Storage -> "storage"
            is Screen.StorageBin -> "bin:${s.kind}"
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
                val topBar: @Composable () -> Unit = {
                    when (tab) {
                        Tab.Discover -> Unit
                        Tab.History -> Column(Modifier.fillMaxWidth()) {
                            AlignTitleBar(if (histPick) "已选 ${histSelected.size}" else copy.history, opaque = false) {
                                if (histPick) {
                                    Text(
                                        if (lang == "en") "Cancel" else "取消",
                                        color = MiuixTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .clickable {
                                                histPick = false
                                                histSelected.clear()
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                    )
                                    Text(
                                        if (lang == "en") "All" else "全选",
                                        color = MiuixTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable {
                                                histSelected.clear()
                                                histSelected.addAll(visibleHistoryIds())
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                    )
                                } else {
                                    IconButton(onClick = {
                                        histFind = !histFind
                                        if (!histFind) histQ = ""
                                    }) {
                                        Icon(MiuixIcons.Basic.Search, contentDescription = copy.search, tint = MiuixTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                                    }
                                    CornerMenu(
                                        expanded = histMenu,
                                        onExpandedChange = { histMenu = it },
                                        actions = listOf(
                                            (if (histLayout == "h") {
                                                if (lang == "en") "Vertical layout" else "改为竖排"
                                            } else {
                                                if (lang == "en") "Horizontal layout" else "改为横排"
                                            }) to {
                                                histLayout = if (histLayout == "h") "v" else "h"
                                                prefs.histLayout = histLayout
                                            },
                                            moreOf(lang).clearAllHistory to { histAsk = "all" },
                                            moreOf(lang).batchDeleteHistory to {
                                                histPick = true
                                                histSelected.clear()
                                                histSelected.addAll(visibleHistoryIds())
                                            },
                                        ),
                                    )
                                }
                            }
                            if (histFind && !histPick) {
                                TextField(
                                    value = histQ,
                                    onValueChange = { histQ = it },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
                                    label = if (lang == "en") "Search comics and novels in history" else if (lang == "zh-TW") "搜尋歷史漫畫、小說" else "搜索历史漫画、小说",
                                )
                            }
                        }
                        Tab.Shelf -> Column(Modifier.fillMaxWidth()) {
                            AlignTitleBar(if (shelfPick) "已选 ${shelfSelected.size}" else copy.shelf, opaque = false) {
                            if (shelfPick) {
                                Text(
                                    if (lang == "en") "Cancel" else "取消",
                                    color = MiuixTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .clickable {
                                            shelfPick = false
                                            shelfSelected.clear()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                )
                                Text(
                                    if (lang == "en") "All" else "全选",
                                    color = MiuixTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable {
                                            shelfSelected.clear()
                                            shelfSelected.addAll(library)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                )
                            } else {
                                IconButton(onClick = {
                                    shelfFind = !shelfFind
                                    if (!shelfFind) shelfQ = ""
                                }) {
                                    Icon(MiuixIcons.Basic.Search, contentDescription = copy.search, tint = MiuixTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                                }
                                IconButton(onClick = { shelfSheet = true }) {
                                    Icon(
                                        Icons.Filter,
                                        contentDescription = "筛选",
                                        tint = if (shelfOpts.filtering) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                                IconButton(onClick = { refreshLibrary() }) {
                                    Icon(Icons.Rotate, contentDescription = "刷新", tint = MiuixTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                                }
                                CornerMenu(
                                    expanded = shelfMenu,
                                    onExpandedChange = { shelfMenu = it },
                                    actions = listOf(
                                        (if (lang == "en") "Select to remove" else if (lang == "zh-TW") "批量刪除" else "批量删除") to {
                                            shelfPick = true
                                            shelfSelected.clear()
                                        },
                                        (if (lang == "en") "Remove all" else if (lang == "zh-TW") "全部刪除" else "全部删除") to {
                                            shelfAskAll = true
                                        },
                                    ),
                                )
                            }
                            }
                            if (shelfFind && !shelfPick) {
                                TextField(
                                    value = shelfQ,
                                    onValueChange = { shelfQ = it },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
                                    label = if (lang == "en") "Search this shelf" else if (lang == "zh-TW") "搜尋當前書架" else "搜索当前书架",
                                )
                            }
                        }
                        Tab.Mine -> Unit
                    }
                }
        val floatingBar = !forceTablet
        val navigationBackdrop = rememberLayerBackdrop()
        Box(Modifier.fillMaxSize()) {
                key(colorMode, dark, themeSeed) {
                Box(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .layerBackdrop(navigationBackdrop),
                ) {
                    if (forceTablet) {
                        TabletRail(tab = tab, copy = copy, onTab = { tab = it })
                    }
                    Scaffold(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        containerColor = MiuixTheme.colorScheme.surface,
                        topBar = {},
                        bottomBar = {},
                    ) { scaffoldPad ->
                        var headPx by remember { mutableIntStateOf(0) }
                        val density = LocalDensity.current
                        val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        val gestureBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        val bottomPad = if (floatingBar) gestureBottom + 64.dp + 16.dp else scaffoldPad.calculateBottomPadding()
                        val barShown = tab == Tab.Shelf || tab == Tab.History
                        val barTop = if (headPx == 0) statusTop + 56.dp else with(density) { headPx.toDp() }
                        val contentPad = when (tab) {
                            Tab.Mine -> PaddingValues(bottom = bottomPad)
                            Tab.Discover -> PaddingValues(top = statusTop, bottom = bottomPad)
                            else -> PaddingValues(top = barTop, bottom = bottomPad)
                        }
                        Box(Modifier.fillMaxSize()) {
                            Box(Modifier.fillMaxSize()) { tabContent(contentPad) }
                            if (barShown) {
                                Box(
                                    Modifier
                                        .align(Alignment.TopCenter)
                                        .fillMaxWidth()
                                        .background(MiuixTheme.colorScheme.surface)
                                        .onSizeChanged { headPx = it.height },
                                ) {
                                    topBar()
                                }
                            }
                        }
                    }
                }
                if (floatingBar) {
                    FloatingBottomBar(
                        backdrop = navigationBackdrop,
                        selected = tab.ordinal,
                        labels = listOf(copy.shelf, copy.history, copy.discover, copy.mine),
                        icons = listOf(Icons.Book, Icons.Clock, Icons.Store, Icons.User),
                        onSelect = { tab = Tab.entries[it] },
                        accent = MiuixTheme.colorScheme.primary,
                        dark = dark,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    )
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
            Screen.Search -> 
                SearchPane(
                    copy = copy,
                    query = query,
                    onQuery = { query = it },
                    crawled = crawled,
                    webSites = webSites,
                    allowAdult = allowAdult,
                    siteId = prefs.lastSiteId,
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
                    onWeb = { url, title -> openSite(url, title, s.id) },
                    foldersOn = true,
                    onFolder = { openFolderSheet(s.id) },
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
                    onFolder = { openFolderSheet(s.workId) },
                    onCoverSaved = { path ->
                        val i = crawled.indexOfFirst { it.id == s.workId }
                        if (i >= 0) {
                            val site = crawled[i].siteId
                            crawled[i] = crawled[i].copy(cover = path)
                            LocalStore.saveListing(site, crawled.filter { it.siteId == site })
                        }
                    },
                )
            
            Screen.Sources -> 
                SourcesPane(copy, sources, onBack = { screen = Screen.Lab }, onChange = { persistSources() })
            
            Screen.Sites -> 
                SitesPane(
                    copy = copy,
                    webSites = webSites,
                    crawled = crawled,
                    allowAdult = allowAdult,
                    onBack = { screen = Screen.Tabs },
                    onChange = { persistSites() },
                )
            
            is Screen.Web -> {
                val back = { if (s.backTo.isNotBlank()) screen = Screen.Remote(s.backTo) else screen = Screen.Tabs }
                
                    WebPane(url = s.url, title = s.title, onBack = back)
                
            }
            Screen.Backup -> 
                BackupPane(
                    copy = copy,
                    prefs = prefs,
                    library = library,
                    favorites = favorites,
                    sources = sources,
                    onBack = { screen = Screen.Tabs },
                    onImported = { data ->
                        library.clear(); library.addAll(data.library.filter { it !in NOVEL_IDS }); persistLibrary()
                        favorites.clear(); favorites.addAll(data.favorites.filter { it !in NOVEL_IDS }); persistFav()
                        history.clear(); history.addAll(data.history.filter { it !in NOVEL_IDS })
                        readProgress.clear(); readProgress.putAll(prefs.progress)
                        persistHistory()
                        webSites.clear(); webSites.addAll(prefs.webSites())
                        if (data.sources.isNotEmpty()) {
                            sources.clear()
                            sources.addAll(data.sources)
                            persistSources()
                        }
                        colorMode = prefs.colorMode
                        predictive = prefs.predictiveBack
                        langPref = prefs.lang
                    },
                )
            
            Screen.Theme -> 
                ThemePane(
                    copy = copy,
                    lang = lang,
                    colorMode = colorMode,
                    onMode = { colorMode = it; prefs.colorMode = it },
                    iconStyle = iconStyle,
                    onIcon = {
                        iconStyle = it
                        prefs.iconStyle = it
                        AppIcons.apply(context, it)
                    },
                    seed = themeSeed,
                    onSeed = {
                        themeSeed = it
                        prefs.themeSeed = it
                    },
                    palette = themePalette,
                    onPalette = {
                        themePalette = it
                        prefs.themePalette = it
                    },
                    onBanner = { screen = Screen.Banner },
                    prefs = prefs,
                    onBack = { screen = Screen.Tabs },
                )
            
            Screen.Reading -> 
                ReadingPane(prefs = prefs, lang = lang, onBack = { screen = Screen.Tabs })
            
            Screen.Intro -> 
                IntroPane(
                    copy = copy,
                    onBack = { screen = Screen.Tabs },
                    onOss = { screen = Screen.Oss },
                    onNotes = { screen = Screen.Notes },
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
            
            Screen.Lab -> 
                LabPane(
                    lang = lang,
                    title = when (lang) {
                        "en" -> "Lab"
                        "zh-TW" -> "實驗室"
                        else -> "实验室"
                    },
                    onBack = { screen = Screen.Tabs },
                    seen = prefs.labSeen,
                    onAck = { prefs.labSeen = true },
                    appBrowser = appBrowser,
                    onAppBrowser = {
                        appBrowser = it
                        prefs.appBrowser = it
                    },
                )
            
            Screen.Storage -> 
                StoragePane(
                    lang = lang,
                    onBack = { screen = Screen.Tabs },
                    onOpen = { screen = Screen.StorageBin(it) },
                    generation = storageRev,
                )
            
            is Screen.StorageBin -> 
                StorageListPane(kind = s.kind, lang = lang, onBack = {
                    storageRev++
                    screen = Screen.Storage
                })
            
            Screen.More -> 
                MorePane(
                    lang = lang,
                    predictive = predictive,
                    onPredictive = {
                        predictive = it
                        prefs.predictiveBack = it
                    },
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
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    onOpenGate = { screen = Screen.Gate },
                    onBack = { screen = Screen.Tabs },
                )

            Screen.Gate ->
                GatePane(
                    lang = lang,
                    allowAdult = allowAdult,
                    onAllowAdult = {
                        allowAdult = it
                        prefs.allowAdult = it
                    },
                    onBack = { screen = Screen.More },
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
                    crawled = crawled,
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
            
            Screen.Oss -> 
                OssPane(lang = lang, onBack = { screen = Screen.Intro })

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
        val pageBg = MiuixTheme.colorScheme.surface.copy(alpha = 1f)
        val shellBg = if (screen is Screen.RemoteReader) Color.Black else pageBg
        SideEffect {
            val window = (view.context as Activity).window
            window.setBackgroundDrawable(ColorDrawable(shellBg.toArgb()))
        }
        val visibleLayers = if (backActive && layers.size >= 2) layers.takeLast(2) else listOf(layers.last())
        Box(Modifier.fillMaxSize().background(shellBg)) {
            visibleLayers.forEachIndexed { index, layer ->
                val top = index == visibleLayers.lastIndex
                key(screenToken(layer)) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .then(
                                if (top && backProgress > 0f) {
                                    Modifier.graphicsLayer {
                                        val s = 1f - 0.1f * backProgress
                                        scaleX = s
                                        scaleY = s
                                        translationX = when (backEdge) {
                                            NavigationEvent.EDGE_LEFT -> backShift.toPx() * backProgress
                                            NavigationEvent.EDGE_RIGHT -> -backShift.toPx() * backProgress
                                            else -> 0f
                                        }
                                        transformOrigin = TransformOrigin.Center
                                    }
                                } else {
                                    Modifier
                                },
                            )
                            .background(if (layer is Screen.RemoteReader) Color.Black else pageBg)
                            .then(
                                if (top) {
                                    Modifier
                                } else {
                                    Modifier.pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                                event.changes.forEach { it.consume() }
                                            }
                                        }
                                    }
                                },
                            ),
                    ) {
                        CompositionLocalProvider(LocalScreenLive provides top) {
                            Draw(layer, live = top)
                        }
                    }
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
        if (adultBlock) {
            BackHandler { adultBlock = false }
            ConfirmSheet(
                title = when (lang) {
                    "en" -> "Restricted content"
                    "zh-TW" -> "限制級內容"
                    else -> "限制级内容"
                },
                message = when (lang) {
                    "en" -> "To view this, open More settings and turn on Restricted content."
                    "zh-TW" -> "如需查看該限制級內容，請去更多設定裡面打開限制級功能開關。"
                    else -> "如需查看该限制级内容，请去更多设置。"
                },
                confirm = when (lang) {
                    "en" -> "Settings"
                    "zh-TW" -> "去設定"
                    else -> "去设置"
                },
                cancel = copy.cancel,
                onConfirm = {
                    adultBlock = false
                    screen = Screen.More
                },
                onDismiss = { adultBlock = false },
            )
        }
        appUpdater.available?.let { release ->
            BackHandler { appUpdater.later() }
            ConfirmSheet(
                title = "发现新版本 ${release.version}",
                message = release.notes.take(240).let { if(release.notes.length>240)it+"…" else it }.ifBlank { "新版已发布，前往 GitHub 查看更新内容并下载。" },
                confirm = "前往下载",
                cancel = "稍后",
                onConfirm = { appUpdater.download() },
                onDismiss = { appUpdater.later() },
            )
        }
        NoteHost()
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
    if (align == BarTitle.Below) {
        TopAppBar(
            title = title,
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Back, contentDescription = title) }
            },
            actions = { actions() },
        )
        return
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(MiuixTheme.colorScheme.surface)
            .statusBarsPadding()
            .heightIn(min = 56.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) { Icon(Icons.Back, contentDescription = title) }
        if (align == BarTitle.Center) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(contentAlignment = Alignment.Center) { actions() }
        } else {
            Text(
                title,
                modifier = Modifier.weight(1f).padding(end = 18.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            actions()
        }
    }
}

@Composable
private fun AlignTitleBar(title: String, opaque: Boolean = true, actions: @Composable () -> Unit = {}) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (opaque) Modifier.background(MiuixTheme.colorScheme.surface) else Modifier)
            .statusBarsPadding()
            .heightIn(min = 56.dp)
            .padding(start = 26.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            fontSize = MiuixTheme.textStyles.title1.fontSize,
            fontWeight = FontWeight.Normal,
            color = MiuixTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        actions()
    }
}

@Composable
private fun OverflowButton(expanded: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(44.dp).clickable(interactionSource=remember { MutableInteractionSource() },indication=null,onClick=onClick),contentAlignment=Alignment.Center) {
        val tint=MiuixTheme.colorScheme.onSurface
        Canvas(Modifier.size(18.dp)) {
            drawCircle(tint,style=Stroke(width=1.4.dp.toPx()))
            repeat(3) { i -> drawCircle(tint,size.minDimension*0.065f,Offset(center.x+(i-1)*size.minDimension*0.22f,center.y)) }
        }
    }
}

@Composable
private fun CornerMenu(expanded:Boolean,onExpandedChange:(Boolean)->Unit,actions:List<Pair<String,()->Unit>>) {
    val window=LocalWindowInfo.current
    var fromRight by remember { mutableStateOf(true) }
    var anchorHeight by remember { mutableIntStateOf(0) }
    Box(Modifier.onGloballyPositioned { coords ->
        fromRight=coords.positionInWindow().x>window.containerSize.width/2f
        anchorHeight=coords.size.height
    }) {
        OverflowButton(expanded=expanded,onClick={onExpandedChange(!expanded)})
        if(expanded && actions.isNotEmpty()) {
            Popup(alignment=if(fromRight)Alignment.TopEnd else Alignment.TopStart,offset=IntOffset(0,anchorHeight),
                onDismissRequest={onExpandedChange(false)},properties=PopupProperties(focusable=true,dismissOnBackPress=true,dismissOnClickOutside=true,clippingEnabled=true)) {
                Column(Modifier.padding(top=4.dp,end=8.dp).widthIn(max=280.dp).width(IntrinsicSize.Max)
                    .shadow(6.dp,RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))
                    .background(MiuixTheme.colorScheme.surface).padding(vertical=4.dp)) {
                    actions.forEachIndexed { index,(label,act) ->
                        if(index>0)Box(Modifier.fillMaxWidth().padding(horizontal=12.dp).height(0.5.dp).background(MiuixTheme.colorScheme.onSurface.copy(alpha=0.08f)))
                        Box(Modifier.fillMaxWidth().heightIn(min=48.dp).clickable { onExpandedChange(false);act() }.padding(horizontal=16.dp,vertical=12.dp),contentAlignment=Alignment.CenterStart) {
                            Text(label,color=MiuixTheme.colorScheme.onSurface,fontSize=15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NativeListingFooter(count:Int,busy:Boolean,hasMore:Boolean,error:String,onMore:()->Unit,onSearch:()->Unit,bottomPadding:androidx.compose.ui.unit.Dp) {
    Column(Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp,top=16.dp,bottom=bottomPadding),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("已加载${count}本",fontSize=13.sp,color=MiuixTheme.colorScheme.onSurfaceVariantSummary)
        if(error.isNotBlank())Text(error,fontSize=13.sp,textAlign=TextAlign.Center,color=MiuixTheme.colorScheme.onSurfaceVariantSummary)
        when {
            busy -> Text("正在加载…",fontSize=14.sp,color=MiuixTheme.colorScheme.onSurfaceVariantSummary)
            hasMore && error.isNotBlank() -> Button(onClick=onMore) { Text("重试") }
            hasMore -> Text("继续下滑，自动加载",fontSize=13.sp,color=MiuixTheme.colorScheme.onSurfaceVariantSummary)
            else -> Button(onClick=onSearch) { Text("搜索其他小说") }
        }
    }
}

@Composable
private fun EmptyFace(face: String, message: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 32.dp)) {
            Text(face, fontSize = 40.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
            Spacer(Modifier.height(14.dp))
            Text(
                message,
                fontSize = 14.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ShelfPane(
    padding: PaddingValues,
    copy: Copy,
    library: List<String>,
    crawled: List<CrawledWork>,
    progress: Map<String, Progress>,
    prefs: Prefs,
    opts: ShelfOpts,
    selecting: Boolean,
    selected: Set<String>,
    query: String = "",
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
    val metas = remember(library.joinToString(","), progress.entries.joinToString { "${it.key}:${it.value.chapterId}:${it.value.page}:${it.value.at}" }, crawled.joinToString { "${it.id}:${it.title}" }, opts) {
        library.mapIndexedNotNull { index, id -> shelfMeta(index, id, crawled, progress, prefs) }
    }
    val q = query.trim()
    val shown = remember(metas, opts, q) {
        val base = filterShelf(metas, opts)
        if (q.isBlank()) base else base.filter { it.title.contains(q, ignoreCase = true) }
    }
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
                EmptyFace("ఠ_ఠ", copy.emptyShelf)
            }
        } else if (shown.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyFace("(´・ω・`)", copy.emptyCat)
            }
        } else if (opts.layout == "list") {
            LazyColumn(state = listState, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                if (opts.count) {
                    item {
                        Text(
                            shelfWord(lang, "total").replace("%d", shown.size.toString()),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
            LazyColumn(state = listState, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                if (opts.count) {
                    item {
                        Text(
                            shelfWord(lang, "total").replace("%d", shown.size.toString()),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = padding.calculateBottomPadding() + 16.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF3B30))
                .clickable(onClick = onTrash),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Trash, contentDescription = "移除", tint = Color.White, modifier = Modifier.size(26.dp))
        }
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
        downloaded = chapters.any { it.pages.isNotEmpty() },
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
                    .clickable { clickN.value() }
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
                Box(Modifier.align(Alignment.TopStart).padding(6.dp)) { CheckDot(selected) }
            }
            ShelfBadges(m, opts)
        }
        Text(m.title, Modifier.padding(start = 6.dp, end = 6.dp, top = 6.dp), maxLines = 1)
        if (opts.genre && m.genre.isNotBlank()) {
            Text(m.genre, Modifier.padding(start = 6.dp, end = 6.dp), fontSize = 11.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary, maxLines = 1)
        }
        Text(
            m.author.ifBlank { "在线" },
            Modifier.padding(start = 6.dp, end = 6.dp, bottom = 8.dp),
            fontSize = 11.sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
                CheckDot(selected)
                Spacer(Modifier.width(8.dp))
            }
            Box {
                SmartCover(m.cover, Modifier.width(56.dp).height(78.dp).clip(RoundedCornerShape(8.dp)))
                ShelfBadges(m, opts, compact = true)
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(m.title, maxLines = 2, fontWeight = FontWeight.Medium)
                if (m.author.isNotBlank()) {
                    Text(m.author, fontSize = 12.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary, maxLines = 1)
                }
                if (stamp.isNotBlank()) {
                    Text(
                        stamp,
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (opts.genre && m.genre.isNotBlank()) {
                    Text(m.genre, fontSize = 12.sp, color = MiuixTheme.colorScheme.primary, maxLines = 1)
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
                .background(MiuixTheme.colorScheme.primary, RoundedCornerShape(6.dp))
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
private fun LabPane(
    lang: String,
    title: String,
    onBack: () -> Unit,
    seen: Boolean,
    onAck: () -> Unit,
    appBrowser: Boolean,
    onAppBrowser: (Boolean) -> Unit,
) {
    var warn by remember { mutableStateOf(!seen) }
    val notice = when (lang) {
        "en" -> "This feature is still in development and may be removed at any time."
        "zh-TW" -> "當前功能處於開發階段，可能隨時廢棄"
        else -> "当前功能处于开发阶段，可能随时废弃"
    }
    val ok = when (lang) {
        "en" -> "OK"
        else -> "知道了"
    }
    Box(Modifier.fillMaxSize()) {
        Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
            LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                item {
                    Card(Modifier.padding(12.dp).fillMaxWidth()) {
                        SwitchPreference(
                            checked = appBrowser,
                            onCheckedChange = onAppBrowser,
                            title = if (lang == "en") "Built-in browser" else if (lang == "zh-TW") "使用內置瀏覽器" else "使用内置浏览器",
                            summary = if (lang == "en") "Off: sites opened inside the app go to the system browser" else if (lang == "zh-TW") "關閉後打開應用內的網站會跳轉到系統自帶瀏覽器" else "关闭后打开应用内的网站会跳转到系统自带浏览器",
                        )
                    }
                }
            }
        }
        if (warn) {
            OverlayScrim(onDismiss = {
                warn = false
                onAck()
            }) {
                Card(Modifier.padding(28.dp).fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text(notice, fontSize = 14.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        Spacer(Modifier.height(18.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                ok,
                                color = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                                    warn = false
                                    onAck()
                                },
                            )
                        }
                    }
                }
            }
        }
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
    val pick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
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
            Button(onClick = { pick.launch("image/*") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColorsPrimary()) { Text(choose) }
            Button(onClick = onClear, modifier = Modifier.fillMaxWidth()) { Text(reset) }
        }
    }
}

@Composable
private fun FolderPickSheet(
    lang: String,
    folders: List<BookFolder>,
    onCreate: (String) -> Unit,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val title = when (lang) { "en" -> "Collection"; "zh-TW" -> "收藏夾"; else -> "收藏夹" }
    val create = when (lang) { "en" -> "Create"; "zh-TW" -> "建立並加入"; else -> "创建并加入" }
    OverlayScrim(onDismiss = onDismiss) {
        Card(Modifier.padding(24.dp).fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = when (lang) { "en" -> "Name"; "zh-TW" -> "收藏夾名稱"; else -> "收藏夹名称" },
                )
                Button(onClick = { onCreate(name) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColorsPrimary()) {
                    Text(create)
                }
                folders.forEach { folder ->
                    ArrowPreference(folder.name, summary = "${folder.books.size}", onClick = { onPick(folder.id) })
                }
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
                        }) { Icon(Icons.Back, contentDescription = "返回") }
                    },
                    actions = {
                        if (picking) {
                            Text(
                                label("取消", "取消", "Cancel"),
                                color = MiuixTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .clickable {
                                        picking = false
                                        selected.clear()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                            )
                            Text(
                                label("全选", "全選", "All"),
                                color = MiuixTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable {
                                        selected.clear()
                                        selected.addAll(ids)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                            )
                        } else {
                            Text(
                                label("编辑", "編輯", "Edit"),
                                color = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 16.dp).clickable { menu = true },
                            )
                        }
                    },
                )
            },
        ) { padding ->
            if (ids.isEmpty()) {
                EmptyFace("(´・ω・`)", label("这里还没有漫画", "這裡還沒有漫畫", "Nothing here"), Modifier.fillMaxSize().padding(padding))
            } else if (layout == "v") {
                LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                    items(ids.chunked(2)) { row ->
                        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { id ->
                                val w = crawled.find { it.id == id }
                                val disk = LocalStore.loadWork(id)?.work
                                val book = realTitle(w?.title, disk?.title, id)
                                Column(
                                    Modifier.weight(1f).combinedClickable(
                                        onClick = { if (picking) arm(id) else onOpen(id) },
                                        onLongClick = { arm(id) },
                                    ),
                                ) {
                                    Box {
                                        SmartCover(coverOf(id, crawled), Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(8.dp)))
                                        if (picking) Box(Modifier.padding(6.dp)) { CheckDot(id in selected) }
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
                LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                    items(ids, key = { it }) { id ->
                        val w = crawled.find { it.id == id }
                        val disk = LocalStore.loadWork(id)?.work
                        val book = realTitle(w?.title, disk?.title, id)
                        Card(Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().combinedClickable(
                                    onClick = { if (picking) arm(id) else onOpen(id) },
                                    onLongClick = { arm(id) },
                                ).padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (picking) {
                                    CheckDot(id in selected)
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
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 20.dp, bottom = 16.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
                    .clickable { ask = true },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Trash, contentDescription = "删除", tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
        if (menu && !picking) {
            MenuSheet(
                listOf(
                    label("改名", "改名", "Rename") to {
                        draft = name
                        renaming = true
                    },
                    (if (layout == "v") label("改为列表", "改為列表", "List") else label("改为竖版", "改為豎版", "Vertical")) to {
                        onLayout(if (layout == "v") "list" else "v")
                    },
                ),
                onDismiss = { menu = false },
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 52.dp,
            )
        }
        if (renaming) {
            OverlayScrim(onDismiss = { renaming = false }) {
                Card(Modifier.padding(24.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(label("改名", "改名", "Rename"), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        TextField(
                            value = draft,
                            onValueChange = { draft = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = label("收藏夹名称", "收藏夾名稱", "Name"),
                        )
                        Button(
                            onClick = {
                                onRename(draft)
                                renaming = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) { Text(label("保存", "儲存", "Save")) }
                    }
                }
            }
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
    predictive: Boolean,
    onPredictive: (Boolean) -> Unit,
    incognito: Boolean,
    onIncognito: (Boolean) -> Unit,
    peekGuard: Boolean,
    onPeek: (Boolean) -> Unit,
    fontGear: String,
    onFont: () -> Unit,
    forceTablet: Boolean,
    onForceTablet: (Boolean) -> Unit,
    onOpenGate: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val title = when (lang) { "en" -> "More settings"; "zh-TW" -> "更多設定"; else -> "更多设置" }
    var askCode by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item { SmallTitle(when (lang) { "en" -> "Display"; "zh-TW" -> "顯示"; else -> "显示" }) }
            item {
                val fontSum = when (fontGear) {
                    "s" -> if (lang == "en") "Small" else "小"
                    "m" -> if (lang == "en") "Medium" else "中"
                    "l" -> if (lang == "en") "Large" else "大"
                    else -> if (lang == "en") "Follow system" else if (lang == "zh-TW") "跟隨系統" else "跟随系统"
                }
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    ArrowPreference(
                        title = when (lang) { "en" -> "Adjust type"; "zh-TW" -> "調整字體"; else -> "调整字体" },
                        summary = fontSum,
                        onClick = onFont,
                    )
                    SwitchPreference(
                        checked = predictive,
                        onCheckedChange = onPredictive,
                        title = when (lang) { "en" -> "Predictive back"; "zh-TW" -> "預測性返回"; else -> "预测性返回" },
                        summary = when (lang) {
                            "en" -> "Swipe from the edge to preview the previous screen"
                            "zh-TW" -> "從邊緣滑動預覽上一頁"
                            else -> "从边缘滑动预览上一页"
                        },
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
            item { SmallTitle(when (lang) { "en" -> "Privacy"; "zh-TW" -> "隱私"; else -> "隐私" }) }
            item {
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    ArrowPreference(
                        title = when (lang) { "en" -> "Mystery switch"; "zh-TW" -> "神秘開關"; else -> "神秘开关" },
                        summary = when (lang) { "en" -> "Enter the number to open it"; "zh-TW" -> "輸入數字後進入"; else -> "输入数字后进入" },
                        onClick = {
                            code = ""
                            askCode = true
                        },
                    )
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
            item { SmallTitle(when (lang) { "en" -> "Network"; "zh-TW" -> "網路"; else -> "网络" }) }
            item {
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    ArrowPreference(
                        title = when (lang) { "en" -> "Clear cookies"; "zh-TW" -> "清除 Cookies"; else -> "清除 Cookies" },
                        summary = when (lang) {
                            "en" -> "Clear site logins stored in the web view"
                            "zh-TW" -> "清除網頁登入狀態"
                            else -> "清除网页登录状态"
                        },
                        onClick = {
                            val cookies = CookieManager.getInstance()
                            cookies.removeAllCookies { cookies.flush() }
                            runCatching { WebStorage.getInstance().deleteAllData() }
                            Toast.makeText(
                                context,
                                when (lang) { "en" -> "Cookies cleared"; "zh-TW" -> "已清除 Cookies"; else -> "已清除 Cookies" },
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    )

                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
    if (askCode) {
        OverlayScrim(onDismiss = { askCode = false }) {
            Card(Modifier.padding(24.dp).fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        when (lang) { "en" -> "Enter the mystery number"; "zh-TW" -> "請輸入神秘數字"; else -> "请输入神秘数字" },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    TextField(
                        value = code,
                        onValueChange = { code = it.filter { ch -> ch.isDigit() }.take(8) },
                        modifier = Modifier.fillMaxWidth(),
                        label = when (lang) { "en" -> "Enter the mystery number"; "zh-TW" -> "請輸入神秘數字"; else -> "请输入神秘数字" },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            when (lang) { "en" -> "Cancel"; else -> "取消" },
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable { askCode = false },
                        )
                        Text(
                            when (lang) { "en" -> "OK"; else -> "确定" },
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                                if (code == "1314") {
                                    askCode = false
                                    code = ""
                                    onOpenGate()
                                } else {
                                    Toast.makeText(
                                        context,
                                        when (lang) { "en" -> "Incorrect"; "zh-TW" -> "不正確"; else -> "不正确" },
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabletRail(tab: Tab, copy: Copy, onTab: (Tab) -> Unit) {
    val items = listOf(
        Triple(Tab.Shelf, Icons.Book, copy.shelf),
        Triple(Tab.History, Icons.Clock, copy.history),
        Triple(Tab.Discover, Icons.Store, copy.discover),
        Triple(Tab.Mine, Icons.User, copy.mine),
    )
    Column(
        Modifier
            .width(96.dp)
            .fillMaxHeight()
            .background(MiuixTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items.forEach { (dest, icon, label) ->
            val on = tab == dest
            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable { onTab(dest) }
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = if (on) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    label,
                    fontSize = 12.sp,
                    color = if (on) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
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
private fun ShelfSheet(
    lang: String,
    opts: ShelfOpts,
    onChange: (ShelfOpts) -> Unit,
    onDismiss: () -> Unit,
) {
    var page by remember { mutableStateOf(0) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(onClick = onDismiss),
    ) {
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MiuixTheme.colorScheme.surface)
                .navigationBarsPadding()
                .clickable(enabled = false, onClick = {}),
        ) {
            Box(Modifier.fillMaxWidth().padding(top = 10.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.width(36.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
            }
            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                listOf("filter", "sort", "display").forEachIndexed { i, key ->
                    Column(
                        Modifier.weight(1f).clickable { page = i },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            shelfWord(lang, key),
                            color = if (page == i) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .width(28.dp)
                                .height(2.dp)
                                .background(if (page == i) MiuixTheme.colorScheme.primary else Color.Transparent),
                        )
                    }
                }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(bottom = 16.dp)) {
                when (page) {
                    0 -> {
                        ShelfCheck(shelfWord(lang, "downloaded"), opts.fDl) { onChange(opts.copy(fDl = it)) }
                        ShelfCheck(shelfWord(lang, "unread"), opts.fUnread) { onChange(opts.copy(fUnread = it)) }
                        ShelfCheck(shelfWord(lang, "done"), opts.fDone) { onChange(opts.copy(fDone = it)) }
                    }
                    1 -> {
                        listOf(
                            "manual", "name", "recentRead", "recentCheck", "unread", "chapters", "latest", "acquired", "added",
                        ).forEach { key ->
                            val label = shelfWord(lang, if (key == "unread") "unreadSort" else key)
                            val on = opts.sort == key
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    onChange(if (on) opts.copy(asc = !opts.asc) else opts.copy(sort = key))
                                }.padding(horizontal = 22.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    if (on) if (opts.asc) "↑" else "↓" else "",
                                    modifier = Modifier.width(28.dp),
                                    color = MiuixTheme.colorScheme.primary,
                                    fontSize = 16.sp,
                                )
                                Text(label, color = if (on) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    else -> {
                        Text(
                            shelfWord(lang, "mode"),
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                        )
                        listOf("compact", "comfortable", "list").forEach { id ->
                            ShelfRadio(shelfWord(lang, id), opts.layout == id) { onChange(opts.copy(layout = id)) }
                        }
                        Text(
                            shelfWord(lang, "marks"),
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                        )
                        ShelfCheck(shelfWord(lang, "markDl"), opts.markDl) { onChange(opts.copy(markDl = it)) }
                        ShelfCheck(shelfWord(lang, "markUnread"), opts.markUnread) { onChange(opts.copy(markUnread = it)) }
                        ShelfCheck(shelfWord(lang, "markLocal"), opts.markLocal) { onChange(opts.copy(markLocal = it)) }
                        Text(
                            shelfWord(lang, "labels"),
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 13.sp,
                        )
                        ShelfCheck(shelfWord(lang, "genre"), opts.genre) { onChange(opts.copy(genre = it)) }
                        ShelfCheck(shelfWord(lang, "count"), opts.count) { onChange(opts.copy(count = it)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShelfCheck(title: String, on: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onChange(!on) }.padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(22.dp).clip(RoundedCornerShape(5.dp)).background(
                if (on) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            ),
            contentAlignment = Alignment.Center,
        ) {
            if (on) Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(14.dp))
        Text(title)
    }
}

@Composable
private fun ShelfRadio(title: String, on: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 22.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(22.dp).clip(CircleShape).background(
                if (on) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            ),
            contentAlignment = Alignment.Center,
        ) {
            if (on) Box(Modifier.size(8.dp).clip(CircleShape).background(Color.White))
        }
        Spacer(Modifier.width(14.dp))
        Text(title, color = if (on) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface)
    }
}

@Composable
private fun HistoryPane(
    padding: PaddingValues,
    copy: Copy,
    lang: String,
    query: String,
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
                EmptyFace("(; ￣Д￣)", copy.emptyHistory)
            }
        } else {
            LazyColumn(state = listState, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                val visible = if (query.isBlank()) history else history.filter { id ->
                    val m = catalog.find { it.id == id }
                    val w = crawled.find { it.id == id }
                    val diskTitle = LocalStore.loadWork(id)?.work?.title
                    keywordHit(realTitle(m?.title, w?.title, diskTitle, id), query.trim())
                }
                val groups = visible.groupBy { dayLabel(progress[it]?.at ?: 0L, lang) }
                groups.forEach { (day, ids) ->
                    item(key = "day-$day") {
                        Text(
                            day,
                            modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 6.dp),
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
                                                Modifier.weight(1f).combinedClickable(
                                                    onClick = { if (selecting) onToggle(id) else onOpen(id) },
                                                    onLongClick = { onLong(id) },
                                                ),
                                            ) {
                                                Box {
                                                    if (!cover.isNullOrBlank()) {
                                                        SmartCover(cover, Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(8.dp)))
                                                    } else {
                                                        Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f).background(MiuixTheme.colorScheme.surface))
                                                    }
                                                    if (selecting) {
                                                        Box(Modifier.padding(6.dp)) { CheckDot(id in selected) }
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
                        Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
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
                                        CheckDot(id in selected)
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
                                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = padding.calculateBottomPadding() + 16.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
                    .clickable(onClick = onTrash),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Trash, contentDescription = "删除", tint = Color.White, modifier = Modifier.size(26.dp))
            }
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

private fun relTime(at: Long): String {
    val d = System.currentTimeMillis() - at
    return when {
        d < 60_000 -> "刚刚"
        d < 3_600_000 -> "${d / 60_000} 分钟前"
        d < 86_400_000 -> "${d / 3_600_000} 小时前"
        d < 172_800_000 -> "昨天"
        else -> {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = at }
            "${cal.get(java.util.Calendar.MONTH) + 1}月${cal.get(java.util.Calendar.DAY_OF_MONTH)}日"
        }
    }
}

private fun isHiddenAdult(id: String, crawled: List<CrawledWork>, sites: List<WebSite>): Boolean {
    val listed = crawled.find { it.id == id }
    if (listed != null) return Sites.adultWork(listed, sites)
    if (id.startsWith("jm") || id.startsWith("ik") || id.startsWith("jj") || id.startsWith("ab")) return true
    val disk = LocalStore.loadWork(id)?.work ?: return false
    return Sites.adultWork(disk, sites)
}

@Composable
private fun DiscoverPane(
    padding: PaddingValues,
    copy: Copy,
    crawled: List<CrawledWork>,
    webSites: List<WebSite>,
    online: Boolean,
    allowAdult: Boolean,
    cols: Int,
    asList: Boolean = false,
    initialSiteId: String,
    onOpen: (String) -> Unit,
    onSites: () -> Unit,
    onSearch: () -> Unit,
    onWeb: (String, String) -> Unit,
    onToggleCols: () -> Unit,
    onCrawl: (WebSite, String, () -> Unit) -> Unit,
    onCrawlMore: (WebSite, String, () -> Unit) -> Unit,
    onSelectSite: (String) -> Unit,
    onAddUrl: (String, String) -> Unit,
    onCheckSites: () -> Unit,
    listState: LazyListState,
    catState: androidx.compose.runtime.MutableState<String>,
    siteScroll: ScrollState,
    bleedBottom: Boolean = false,
) {
    val enabledSites = webSites.filter { it.enabled && (allowAdult || !Sites.isAdult(it)) }
    val fallbackSite = enabledSites.firstOrNull { it.id == Sites.BIQUGE }?.id ?: enabledSites.firstOrNull()?.id
    val resolvedSite = initialSiteId.takeIf { id -> id.isNotBlank() && enabledSites.any { it.id == id } } ?: fallbackSite
    var pane by remember { mutableStateOf("site") }
    var activeSite by remember { mutableStateOf(resolvedSite) }
    LaunchedEffect(Unit) {
        if (initialSiteId != resolvedSite && !resolvedSite.isNullOrBlank()) onSelectSite(resolvedSite)
    }
    LaunchedEffect(allowAdult) {
        if (activeSite != null && enabledSites.none { it.id == activeSite }) {
            val next = fallbackSite
            if (!next.isNullOrBlank()) {
                activeSite = next
                onSelectSite(next)
            }
        }
    }
    var cat by catState
    var busy by remember { mutableStateOf(false) }
    var activeRequest by remember { mutableStateOf<Any?>(null) }
    var retryListing by remember { mutableIntStateOf(0) }
    val fetchedAt = remember { mutableStateMapOf<String, Long>() }
    val site = enabledSites.find { it.id == activeSite }
    val siteLabel = site?.let { Sites.displayName(it) } ?: copy.bookstore
    val cats = Sites.catsFor(site)
    val kind = site?.let { Sites.kindOf(it) }
    val splitStatus = kind == Sites.IKAN || kind == Sites.JJ
    val poolAll = kind == Sites.IKAN || kind == Sites.JJ || kind == Sites.JM || kind == Sites.BIQUGE
    val cards = crawled.filter { w ->
        if (!allowAdult && Sites.adultWork(w, webSites)) return@filter false
        if (site == null || w.siteId != site.id || w.cat == "搜索") return@filter false
        val serial = serialMark(w)
        when {
            kind == XiuSources.KIND -> XiuSources.inCategory(w,cat)
            cat == "全部" && poolAll -> true
            splitStatus && (cat == "连载" || cat == "連載") ->
                w.tags.any { tagEq(it, "连载") } || serial == "连载"
            splitStatus && (cat == "完结" || cat == "完結") ->
                w.tags.any { tagEq(it, "完结") } || serial == "完结"
            kind == Sites.JM && cat != "推荐" && cat != "推薦" -> belongsToBin(w, cat)
            kind == Sites.BIQUGE && cat != "推荐" && cat != "推薦" && cat != "全部" ->
                belongsToBin(w, cat) || w.cat == cat
            cat == "推荐" || cat == "推薦" -> w.cat == "推荐" || w.cat == "推薦"
            kind == Sites.COPY -> w.cat == cat || w.listed.any { tagEq(it, cat) }
            else -> w.cat == cat
        }
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
    LaunchedEffect(activeSite) {
        if (cat !in cats) cat = cats.firstOrNull() ?: "推荐"
    }
    val keepPlace = remember { mutableStateOf(true) }
    LaunchedEffect(activeSite, cat) {
        if (keepPlace.value) {
            keepPlace.value = false
            return@LaunchedEffect
        }
        if (pane == "site") listState.scrollToItem(0)
    }
    LaunchedEffect(activeSite, cat, pane, online, screenLive, retryListing) {
        if (!screenLive) return@LaunchedEffect
        val s = enabledSites.find { it.id == activeSite }
        if (pane == "site" && s != null && online) {
            if(cat !in Sites.catsFor(s))return@LaunchedEffect
            val key = "${s.id}:$cat"
            val requestToken=Any()
            activeRequest=requestToken
            val age = System.currentTimeMillis() - (fetchedAt[key] ?: 0L)
            val has = when {
                Sites.kindOf(s)==XiuSources.KIND -> crawled.any { it.siteId==s.id && it.cat!="搜索" && XiuSources.inCategory(it,cat) }
                cat == "全部" && (Sites.kindOf(s) == Sites.IKAN || Sites.kindOf(s) == Sites.JJ || Sites.kindOf(s) == Sites.JM || Sites.kindOf(s) == Sites.BIQUGE) ->
                    crawled.any { it.siteId == s.id && it.cat != "搜索" }
                cat == "推荐" || cat == "推薦" ->
                    crawled.any { it.siteId == s.id && (it.cat == "推荐" || it.cat == "推薦") }
                Sites.kindOf(s) == Sites.JM ->
                    crawled.any { it.siteId == s.id && it.cat != "搜索" && belongsToBin(it, cat) }
                Sites.kindOf(s) == Sites.COPY && cat != "推荐" && cat != "推薦" ->
                    crawled.any { work -> work.siteId == s.id && (work.cat == cat || work.listed.any { tagEq(it, cat) }) }
                else -> crawled.any { it.siteId == s.id && it.cat == cat }
            }
            if (has && age in 1..180_000) { busy=false;return@LaunchedEffect }
            busy = !has
            onCrawl(s, cat) {
                if(activeRequest===requestToken)busy = false
                fetchedAt[key] = System.currentTimeMillis()
            }
        }
    }
    val loadMore: () -> Unit = {
        val current=site
        if(current!=null && !busy) {
            val requestToken=Any();activeRequest=requestToken;busy=true
            onCrawlMore(current,cat) { if(activeRequest===requestToken)busy=false }
        }
    }
    LaunchedEffect(listState, activeSite, cat, pane, screenLive, online, cards.size, busy, site?.lastError, asList, cols, XiuSources.hasMore(activeSite.orEmpty(),cat)) {
        val current=site ?: return@LaunchedEffect
        if(!screenLive || !online || pane!="site" || kind!=XiuSources.KIND || busy || cards.isEmpty() ||
            current.lastError.isNotBlank() || !XiuSources.hasMore(current.id,cat)) return@LaunchedEffect
        val lastBookRow=if(asList)cards.lastIndex else (cards.size-1)/cols
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }.collect { lastVisible ->
            if(lastVisible>=lastBookRow && !busy) loadMore()
        }
    }
    val addOk = when (copy.addSite) {
        "Add site" -> "Add"
        "新增網站" -> "確定"
        else -> "确定"
    }
    val checkLabel = when (copy.addSite) {
        "Add site" -> "Check sites"
        "新增網站" -> "自動檢測"
        else -> "自动检测"
    }
    var picking by remember { mutableStateOf(false) }
    var storeMenu by remember { mutableStateOf(false) }
    var addOpen by remember { mutableStateOf(false) }
    var addUrl by remember { mutableStateOf("") }
    var addName by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize()) {
    var discoverHead by remember { mutableIntStateOf(0) }
    val discoverTop = with(LocalDensity.current) {
        if (discoverHead == 0) padding.calculateTopPadding() + 96.dp else discoverHead.toDp()
    }
    Box(Modifier.fillMaxSize().then(if (bleedBottom) Modifier else Modifier.padding(bottom = padding.calculateBottomPadding()))) {
        Box(Modifier.fillMaxSize().background(Color.Transparent)) {
        if (pane == "list") {
            Box(Modifier.fillMaxSize().padding(top = discoverTop)) {
                SiteGrid(
                    copy = copy,
                    sites = enabledSites,
                    scroll = siteScroll,
                    onPick = { picked ->
                        activeSite = picked.id
                        cat = Sites.catsFor(picked).firstOrNull() ?: "推荐"
                        onSelectSite(picked.id)
                        pane = "site"
                    },
                )
            }
        } else {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.weight(1f).fillMaxHeight()) {
                    when {
                        !online && cards.isEmpty() -> EmptyFace("(´・ω・`)", copy.offline, Modifier.padding(top = discoverTop))
                        busy && cards.isEmpty() -> EmptyFace("(｀・ω・´)", copy.loading, Modifier.padding(top = discoverTop))
                        site != null && kind==XiuSources.KIND && cards.isEmpty() -> {
                            val searchOnly=cats==listOf("搜索")
                            Column(Modifier.fillMaxSize().padding(top=discoverTop,start=24.dp,end=24.dp),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally) {
                                Text("(｀・ω・´)",fontSize=40.sp,color=MiuixTheme.colorScheme.onSurfaceVariantSummary)
                                Spacer(Modifier.height(14.dp))
                                Text(if(searchOnly)"此源仅支持搜索，请搜索小说名称" else site.lastError.ifBlank { "该分类暂未获取到小说，请重试" },textAlign=TextAlign.Center,color=MiuixTheme.colorScheme.onSurfaceVariantSummary,fontSize=14.sp)
                                Spacer(Modifier.height(16.dp))
                                Button(onClick={if(searchOnly)onSearch() else retryListing++}) { Text(if(searchOnly)"搜索小说" else "重新加载") }
                            }
                        }
                        site != null && cards.isEmpty() ->
                            EmptyFace("(｀・ω・´)", if (site.lastError.isNotBlank()) site.lastError else copy.emptyCat, Modifier.padding(top = discoverTop))
                        cards.isEmpty() -> EmptyFace("(´・ω・`)", copy.emptyCat, Modifier.padding(top = discoverTop))
                        else -> {
                            if (asList) {
                                LazyColumn(
                                    state = listState,
                                    contentPadding = PaddingValues(top = discoverTop, bottom = if (bleedBottom) padding.calculateBottomPadding() else 0.dp),
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    items(cards, key = { "${it.id}#${it.cover.hashCode()}" }) { w ->
                                        DiscoverListRow(w) { onOpen(w.id) }
                                    }
                                    item {
                                        if(site!=null && kind==XiuSources.KIND)NativeListingFooter(cards.size,busy,XiuSources.hasMore(site.id,cat),site.lastError,loadMore,onSearch,if(bleedBottom)padding.calculateBottomPadding() else 0.dp)
                                        Spacer(Modifier.height(24.dp))
                                    }
                                }
                            } else {
                            val rows = remember(cards, cols) { cards.chunked(cols) }
                            LazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(top = discoverTop),
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                items(rows.size, key = { index ->
                                    val row = rows[index]
                                    row.joinToString("|") { "${it.id}#${it.cover.hashCode()}" } + "@$index"
                                }) { index ->
                                    val row = rows[index]
                                    Row(
                                        Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        row.forEach { w ->
                                            Box(
                                                Modifier.weight(1f).clickable { onOpen(w.id) },
                                            ) {
                                                StoreCover(w.title, w.cover)
                                            }
                                        }
                                        repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
                                    }
                                }
                                item {
                                    if(site!=null && kind==XiuSources.KIND)NativeListingFooter(cards.size,busy,XiuSources.hasMore(site.id,cat),site.lastError,loadMore,onSearch,if(bleedBottom)padding.calculateBottomPadding() else 0.dp)
                                    Spacer(Modifier.height(24.dp))
                                }
                            }
                            }
                        }
                    }
                }
            }
        }
        }
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(MiuixTheme.colorScheme.surface)
                .onSizeChanged { discoverHead = it.height },
        ) {
            Column {
            Spacer(Modifier.height(padding.calculateTopPadding()))
            Row(
                Modifier.fillMaxWidth().heightIn(min = 56.dp).padding(start = 26.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    copy.bookstore,
                    modifier = Modifier.weight(1f),
                    fontSize = MiuixTheme.textStyles.title1.fontSize,
                    fontWeight = FontWeight.Normal,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
                IconButton(onClick = onSearch) {
                    Icon(MiuixIcons.Basic.Search, contentDescription = copy.search, modifier = Modifier.size(24.dp))
                }
                if (!asList) {
                    IconButton(onClick = onToggleCols) {
                        Icon(Icons.Grid, contentDescription = "网格", modifier = Modifier.size(24.dp))
                    }
                }
                CornerMenu(
                    expanded = storeMenu,
                    onExpandedChange = { storeMenu = it },
                    actions = listOf(
                        "切换小说/漫画源" to { picking = true },
                        "网站管理" to onSites,
                        copy.addSite to { addOpen = true },
                        checkLabel to onCheckSites,
                    ),
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    siteLabel,
                    color = MiuixTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 148.dp).padding(end = 2.dp),
                )
                SourceTagStrip(
                    tags = cats,
                    selected = if (pane == "site") cat else "",
                    onSelect = { g ->
                        if(kind==XiuSources.KIND && g=="搜索") {
                            onSearch()
                            return@SourceTagStrip
                        }
                        if (activeSite == null && enabledSites.isNotEmpty()) {
                            activeSite = enabledSites.first().id
                            cat = Sites.catsFor(enabledSites.first()).firstOrNull() ?: "推荐"
                        }
                        pane = "site"
                        cat = g
                    },
                )
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
            }
        }
    }
    if (addOpen) {
        OverlayScrim(onDismiss = { addOpen = false }) {
            Card(Modifier.padding(24.dp).fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(copy.addSite, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    TextField(
                        value = addName,
                        onValueChange = { addName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = copy.siteName,
                    )
                    TextField(
                        value = addUrl,
                        onValueChange = { addUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = copy.siteUrl,
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            copy.cancel,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable { addOpen = false },
                        )
                        Text(
                            addOk,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                                val v = addUrl.trim()
                                if (v.isNotBlank()) {
                                    onAddUrl(addName.trim(), v)
                                    addUrl = ""
                                    addName = ""
                                    addOpen = false
                                }
                            },
                        )
                    }
                }
            }
        }
    }
    if (picking) {
        BackHandler { picking = false }
        Column(
            Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.surface)
                .zIndex(4f),
        ) {
            SubTopBar(
                "切换小说/漫画源",
                onBack = { picking = false },
            )
            ArrowPreference(title = "网站管理", summary = "添加、改名或删除网站", onClick = onSites)
            Box(Modifier.weight(1f).padding(bottom = padding.calculateBottomPadding())) {
                SiteGrid(
                    copy = copy,
                    sites = enabledSites,
                    scroll = siteScroll,
                    onPick = { picked ->
                        activeSite = picked.id
                        cat = Sites.catsFor(picked).firstOrNull() ?: "推荐"
                        onSelectSite(picked.id)
                        pane = "site"
                        picking = false
                    },
                )
            }
        }
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

private fun tagAliases(raw: String): Set<String> {
    val n = normTag(raw)
    return when (n) {
        "a漫", "美漫", "英文站", "英文", "english", "englishsite", "english manga", "englishmanga" ->
            setOf("a漫", "美漫", "英文站", "英文", "english", "english manga")
        "本本", "单本", "单行本" -> setOf("本本", "单本", "单行本")
        "中文", "汉化", "chinese" -> setOf("中文", "汉化", "chinese")
        "其他", "其他类" -> setOf("其他", "其他类")
        else -> setOf(n)
    }
}

private fun sameTag(a: String, b: String): Boolean {
    val left = tagAliases(a)
    return tagAliases(b).any { it in left }
}

private fun isSortBin(category: String) =
    category == "最近" || category == "排行榜" || category == "月排行" || category == "周排行" || category == "天排行"

private fun listingBin(category: String): String? = when (category) {
    "", "全部", "搜索", "推荐", "推薦" -> null
    else -> category
}

private fun binFits(w: CrawledWork, stamp: String): Boolean {
    if (isSortBin(stamp)) return true
    if (w.tags.any { sameTag(it, stamp) }) return true
    if (w.genres.isBlank() || w.genres == "其他") return false
    return w.genres.split(Regex("\\s+")).any { it.isNotBlank() && it != "其他" && sameTag(it, stamp) }
}

private fun belongsToBin(w: CrawledWork, cat: String): Boolean {
    if (w.cat.isNotBlank() && w.cat != "全部" && w.cat != "推荐" && w.cat != "推薦" && sameTag(w.cat, cat)) return true
    if (w.listed.any { sameTag(it, cat) }) return true
    if (w.tags.any { sameTag(it, cat) }) return true
    if (w.genres.isBlank() || w.genres == "其他") return false
    return w.genres.split(Regex("\\s+")).any { it.isNotBlank() && it != "其他" && sameTag(it, cat) }
}

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
            Box(Modifier.width(64.dp).height(86.dp).clip(RoundedCornerShape(8.dp)).background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f))) {
                if (w.cover.isNotBlank()) {
                    SmartCover(w.cover, Modifier.fillMaxSize(), plate = true)
                }
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
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (meta.isNotBlank()) {
                    Text(
                        meta,
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun SiteGrid(
    copy: Copy,
    sites: List<WebSite>,
    onPick: (WebSite) -> Unit,
    scroll: ScrollState,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        val novelLabel = when (copy.siteList) {
            "Sites" -> "Novels"
            "網站列表" -> "小說"
            else -> "小说"
        }
        val comicLabel = when (copy.siteList) {
            "Sites" -> "Manga"
            "網站列表" -> "漫畫"
            else -> "漫画"
        }
        val emptyLabel = when (copy.siteList) {
            "Sites" -> "None"
            "網站列表" -> "暫無"
            else -> "暂无"
        }
        SiteBucket(novelLabel, sites.filter { Sites.isNovel(it) }, emptyLabel, onPick)
        SiteBucket(comicLabel, sites.filter { !Sites.isNovel(it) }, emptyLabel, onPick)
    }
}

@Composable
private fun SiteBucket(
    title: String,
    sites: List<WebSite>,
    emptyLabel: String,
    onPick: (WebSite) -> Unit,
) {
    Text(
        title,
        fontSize = 13.sp,
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp),
    )
    if (sites.isEmpty()) {
        Text(
            emptyLabel,
            fontSize = 13.sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.padding(start = 4.dp, bottom = 16.dp),
        )
    } else {
        sites.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { s ->
                    key(s.id) {
                        Box(
                            Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                .clickable { onPick(s) }
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                Sites.displayName(s),
                                fontSize = 13.sp,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private fun siteNameFrom(url: String): String {
    val host = runCatching { java.net.URL(url).host.lowercase() }.getOrDefault("")
    val probe = WebSite("tmp", host, url)
    return when (Sites.kindOf(probe)) {
        Sites.JM -> "禁漫天堂"
        Sites.IKAN -> "爱看韩漫"
        Sites.BIQUGE -> "笔趣阁"
        Sites.COPY -> "拷贝漫画"
        Sites.JJ -> "简简漫画"
        Sites.ALBUM -> "相簿"
        else -> host.removePrefix("www.").substringBefore('.').ifBlank { "网站" }
    }
}

@Composable
private fun RowScope.SourceTagStrip(
    tags: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    val scroll = rememberScrollState()
    val fade = MiuixTheme.colorScheme.surface
    Box(
        Modifier
            .weight(1f)
            .height(36.dp)
            .clipToBounds()
            .drawWithContent {
                drawContent()
                val edge = 18.dp.toPx()
                if (size.width > edge) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            0f to fade,
                            edge / size.width to Color.Transparent,
                            1f to Color.Transparent,
                        ),
                    )
                }
            },
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .horizontalScroll(scroll),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(14.dp))
            tags.forEach { g ->
                val on = g == selected
                Text(
                    g,
                    color = if (on) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 14.sp,
                    fontWeight = if (on) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    modifier = Modifier
                        .clickable { onSelect(g) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
        }
    }
}

@Composable
private fun SourceTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.padding(horizontal = 10.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            fontSize = 15.sp,
            color = if (selected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .width(28.dp)
                .height(2.dp)
                .background(if (selected) MiuixTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(1.dp)),
        )
    }
}

@Composable
private fun NovelCoverPlate(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier.background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            title,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.72f),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StoreCover(title: String, cover: String) {
    val known = cover.isNotBlank() && Crawler.peekCover(cover) != null
    var ready by remember(cover) { mutableStateOf(known) }
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        )
        if (cover.isNotBlank()) {
            SmartCover(
                cover,
                Modifier.matchParentSize(),
                plate = true,
                onReady = { ready = true },
            )
        }
        if (ready) {
            Box(
                Modifier.fillMaxWidth().align(Alignment.BottomStart).background(Color(0xCC000000)).padding(8.dp),
            ) {
                Text(title, color = Color.White, fontSize = 13.sp, maxLines = 2)
            }
        }
    }
}

@Composable
private fun SearchPane(
    copy: Copy,
    query: String,
    onQuery: (String) -> Unit,
    crawled: List<CrawledWork>,
    webSites: List<WebSite>,
    allowAdult: Boolean,
    siteId: String,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onHits: (List<CrawledWork>) -> Unit = {},
) {
    val visibleSites = webSites.filter { it.enabled && (allowAdult || !Sites.isAdult(it)) }
    val site = visibleSites.find { it.id == siteId } ?: visibleSites.firstOrNull()
    val q = query.trim()
    var live by remember { mutableStateOf<List<CrawledWork>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var verifyOpen by remember { mutableStateOf(false) }
    var retrySearch by remember { mutableIntStateOf(0) }
    if(verifyOpen && site!=null) {
        BackHandler { verifyOpen=false;retrySearch++ }
        WebPane(XiuSources.verificationUrl(site.id),"网站验证",onBack={verifyOpen=false;retrySearch++})
        return
    }
    val listState = rememberLazyListState()
    val screenLive = LocalScreenLive.current
    val queryNow = rememberUpdatedState(query)
    LaunchedEffect(q, site?.id,screenLive,retrySearch) {
        if (!screenLive) return@LaunchedEffect
        if (q.length < 1 || site == null) {
            live = emptyList()
            searching = false
            return@LaunchedEffect
        }
        searching = true
        val searchJob = kotlinx.coroutines.currentCoroutineContext()[kotlinx.coroutines.Job]
        delay(350)
        val result = kotlinx.coroutines.runInterruptible(Dispatchers.IO) {
            runCatching { Crawler.search(q, listOf(site)) { partial ->
                Handler(Looper.getMainLooper()).post {
                    if (queryNow.value.trim() == q && searchJob?.isActive == true) live = partial
                }
            } }.getOrElse {
                AppLog.add("search", it.message ?: "search fail")
                emptyList()
            }
        }
        if (queryNow.value.trim() == q) {
            live = result
            searching = false
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
            merged.values.filter { allowAdult || !Sites.adultWork(it, webSites) }.toList()
        }
    }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Back, contentDescription = copy.search) }
            TextField(
                value = query,
                onValueChange = onQuery,
                modifier = Modifier.weight(1f),
                label = "搜索漫画、小说名或者输入关键词....",
                useLabelAsPlaceholder = true,
                singleLine = true,
            )
        }
        if(!searching && q.isNotBlank() && site!=null && Sites.kindOf(site)==XiuSources.KIND && XiuSources.searchError(site.id).isNotBlank()) {
            Column(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text(XiuSources.searchError(site.id),fontSize=13.sp,color=MiuixTheme.colorScheme.onSurfaceVariantSummary)
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    Button(onClick={retrySearch++}) { Text("重试") }
                    if(XiuSources.searchError(site.id).contains("人机验证"))Button(onClick={verifyOpen=true}) { Text("打开网站验证") }
                }
            }
        }
        when {
                q.isEmpty() -> EmptyFace("(｀・ω・´)", copy.searchEmpty, Modifier.fillMaxWidth().weight(1f))
                searching && remote.isEmpty() -> Text(copy.loading, Modifier.padding(16.dp), color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                remote.isEmpty() -> EmptyFace("(´・ω・`)", copy.emptyCat, Modifier.fillMaxWidth().weight(1f))
                else -> {
                    Text(
                        "${copy.search} · ${remote.size}",
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                        fontSize = 13.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().weight(1f)) {
                        items(remote.chunked(2)) { row ->
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                row.forEach { m ->
                                    Column(Modifier.weight(1f).clickable { onOpen(m.id) }) {
                                        SmartCover(
                                            m.cover,
                                            Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(8.dp)),
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

@Composable
private fun MineStat(value: String, label: String, ink: Color = Color.Unspecified, onClick: (() -> Unit)? = null) {
    val num = if (ink == Color.Unspecified) MiuixTheme.colorScheme.onSurface else ink
    val sub = if (ink == Color.Unspecified) MiuixTheme.colorScheme.onSurfaceVariantSummary else ink.copy(alpha = 0.72f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick == null) Modifier else Modifier.clickable(onClick = onClick),
    ) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = num)
        Text(label, fontSize = 12.sp, color = sub)
    }
}

@Composable
private fun MinePane(
    padding: PaddingValues,
    copy: Copy,
    lang: String,
    lib: Int,
    fav: Int,
    hist: Int,
    langLabel: String,
    labTitle: String,
    labSum: String,
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
    onLab: () -> Unit,
    onStorage: () -> Unit,
    onBackup: () -> Unit,
    onMore: () -> Unit,
    listState: LazyListState,
    banner: String,
) {
    var q by remember { mutableStateOf("") }
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
            MineRow(when (lang) { "en" -> "Collections"; "zh-TW" -> "收藏夾"; else -> "收藏夹" }, "", onFolders),
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
            MineRow(labTitle, labSum, onLab),
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
            modifier = Modifier.fillMaxSize(),
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
                    item { Spacer(Modifier.height(12.dp)) }
                    item {
                        Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                            rows.forEach { row ->
                                if (row.summary.isBlank()) ArrowPreference(row.title, onClick = row.onClick)
                                else ArrowPreference(row.title, summary = row.summary, onClick = row.onClick)
                            }
                        }
                    }
                }
            }
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(MiuixTheme.colorScheme.surface)
                .onSizeChanged { headPx = it.height },
        ) {
            Column {
            Spacer(Modifier.height(statusTop))
            BasicTextField(
                value = q,
                onValueChange = { q = it },
                singleLine = true,
                textStyle = TextStyle(color = MiuixTheme.colorScheme.onSurface, fontSize = 16.sp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    .padding(horizontal = 16.dp),
                decorationBox = { inner ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                        if (q.isEmpty()) {
                            Text(hint, color = MiuixTheme.colorScheme.onSurfaceVariantSummary, fontSize = 16.sp)
                        }
                        inner()
                    }
                },
            )
            }
        }
    }
}

@Composable
private fun SourcesPane(copy: Copy, sources: MutableList<Source>, onBack: () -> Unit, onChange: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    Scaffold(topBar = { SubTopBar(copy.sources, onBack) }) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
                    sources.forEachIndexed { i, s ->
                        SwitchPreference(
                            checked = s.enabled,
                            onCheckedChange = {
                                sources[i] = s.copy(enabled = it)
                                onChange()
                            },
                            title = s.name,
                            summary = s.url.ifBlank { s.summary },
                        )
                    }
                }
            }
            item { SmallTitle(copy.addSource) }
            item {
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = copy.sourceName)
                        TextField(value = url, onValueChange = { url = it }, modifier = Modifier.fillMaxWidth(), label = copy.siteUrl)
                        TextField(value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth(), label = copy.sourceNote)
                        Button(
                            onClick = {
                                if (name.isBlank()) return@Button
                                sources.add(Source("custom-${System.currentTimeMillis()}", name.trim(), note.trim().ifBlank { copy.sources }, true, url.trim()))
                                name = ""
                                note = ""
                                url = ""
                                onChange()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) { Text(copy.importBtn) }
                    }
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

@Composable
private fun SitesPane(
    copy: Copy,
    webSites: MutableList<WebSite>,
    crawled: MutableList<CrawledWork>,
    allowAdult: Boolean,
    onBack: () -> Unit,
    onChange: () -> Unit,
) {
    val context = LocalContext.current
    var busyId by remember { mutableStateOf<String?>(null) }
    var rename by remember { mutableStateOf<WebSite?>(null) }
    var renameText by remember { mutableStateOf("") }
    fun crawl(id: String, target: String) {
        if (busyId != null) return
        if (!Net.online(context)) {
            Toast.makeText(context, copy.offline, Toast.LENGTH_SHORT).show()
            return
        }
        if (target.isBlank()) {
            Toast.makeText(context, copy.reserved, Toast.LENGTH_SHORT).show()
            return
        }
        busyId = id
        Thread {
            val (items, err) = Crawler.fetch(target, id)
            Handler(Looper.getMainLooper()).post {
                busyId = null
                val i = webSites.indexOfFirst { it.id == id }
                if (i >= 0) {
                    webSites[i] = webSites[i].copy(lastError = err.orEmpty(), itemCount = items.size, url = webSites[i].url)
                }
                crawled.removeAll { it.siteId == id && (it.cat == "推荐" || it.cat.isBlank()) }
                crawled.addAll(items.map { if (it.cat.isBlank()) it.copy(cat = "推荐") else it })
                if (items.isNotEmpty()) LocalStore.saveListing(id, crawled.filter { it.siteId == id })
                onChange()
                Toast.makeText(context, err ?: "抓到 ${items.size} 部", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }
    Scaffold(topBar = { SubTopBar("网站管理", onBack) }) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            if (webSites.isEmpty()) {
                item { Text(copy.noSites, Modifier.padding(16.dp), color = MiuixTheme.colorScheme.onSurfaceVariantSummary) }
            } else {
                item {
                    val shown = webSites.filter { allowAdult || !Sites.isAdult(it) }
                    Card(Modifier.padding(12.dp).fillMaxWidth()) {
                        if (shown.isEmpty()) {
                            Text(copy.noSites, Modifier.padding(16.dp), color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        }
                        shown.forEach { s ->
                            key(s.id) {
                            val rowBusy = busyId == s.id
                            ArrowPreference(
                                title = Sites.displayName(s),
                                summary = s.url.ifBlank { copy.reserved } + if (s.lastError.isNotBlank()) "\n${s.lastError}" else "",
                                onClick = { crawl(s.id, s.url) },
                            )
                            FlowRow(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { crawl(s.id, s.url) }, enabled = busyId == null) { Text(if (rowBusy) "…" else copy.crawlNow) }
                                Button(onClick = {
                                    rename = s
                                    renameText = s.name
                                }) { Text("改名") }
                                Button(onClick = {
                                    val i = webSites.indexOfFirst { it.id == s.id }
                                    if (i >= 0) webSites.removeAt(i)
                                    crawled.removeAll { it.siteId == s.id }
                                    onChange()
                                }) { Text("删除") }
                            }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
    val editing = rename
    if (editing != null) {
        OverlayScrim(onDismiss = { rename = null }) {
            Card(Modifier.padding(24.dp).fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(copy.siteName, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    TextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = copy.siteName,
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            copy.cancel,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable { rename = null },
                        )
                        Text(
                            "保存",
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                                val next = renameText.trim()
                                val i = webSites.indexOfFirst { it.id == editing.id }
                                if (i >= 0 && next.isNotBlank()) {
                                    webSites[i] = webSites[i].copy(name = next)
                                    onChange()
                                }
                                rename = null
                            },
                        )
                    }
                }
            }
        }
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
                    IconButton(onClick = onBack) { Icon(Icons.Back, contentDescription = "返回") }
                },
                actions = {
                    IconButton(onClick = {
                        val w = work
                        if (w == null || chapters.isEmpty()) {
                            Toast.makeText(context, if (chapters.isEmpty()) "还没有目录" else "还在加载", Toast.LENGTH_SHORT).show()
                        } else if (Downloader.running) {
                            Toast.makeText(context, "正在下载", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show()
                            Downloader.start(
                                context = context,
                                id = w.id,
                                title = w.title.ifBlank { title0 },
                                chapters = chapters,
                                onStep = { done, total -> dlText = "下载 $done/$total" },
                                onDone = { err ->
                                    dlText = if (err == null) "已下载" else err
                                    Toast.makeText(context, if (err == null) "下载完成" else err, Toast.LENGTH_SHORT).show()
                                },
                            )
                        }
                    }) {
                        Icon(Icons.Download, contentDescription = "下载")
                    }
                    val url = site?.url.orEmpty()
                    if (url.isNotBlank()) {
                        IconButton(onClick = { onWeb(url, site?.let { Sites.displayName(it) } ?: url) }) {
                            Icon(Icons.Globe, contentDescription = "网站")
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
                    Toast.makeText(context, copy.offline, Toast.LENGTH_SHORT).show()
                } else if (!href.isNullOrBlank()) {
                    pulling = true
                    val icon = AppIcons.mipmap(Prefs(context).iconStyle)
                    ShelfNotes.hold("更新中...", icon)
                    Notify.live(context, 0, 1, work?.title.orEmpty())
                    Thread {
                        val bump = Updates.refresh(id, href, listed ?: work)
                        Handler(Looper.getMainLooper()).post {
                            pulling = false
                            applyStore(fromPull = true, bump = bump)
                            Notify.liveClear(context)
                            if (bump != null && bump.added > 0) {
                                ShelfNotes.flash("有章节更新快去通知栏看看是哪一本！", icon)
                                Notify.chapter(context, bump.title, bump.index, bump.chapterTitle)
                            } else {
                                ShelfNotes.flash("没有章节更新诶😩", icon)
                            }
                        }
                    }.start()
                }
            },
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
        val chapterList = rememberLazyListState()
        LazyColumn(state = chapterList, modifier = Modifier.fillMaxSize()) {
            item {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        SmartCover(
                            work?.cover.orEmpty(),
                            Modifier
                                .width(118.dp)
                                .height(168.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !work?.cover.isNullOrBlank()) { coverOpen = true },
                        )
                        Column(Modifier.weight(1f).padding(top = 4.dp)) {
                            Text(work?.title?.takeIf { !isIdTitle(it) } ?: title0.ifBlank { work?.title ?: id }, style = MiuixTheme.textStyles.title2, maxLines = 3)
                            Spacer(Modifier.height(6.dp))
                            if (!work?.author.isNullOrBlank()) {
                                Text(work?.author.orEmpty(), fontSize = 14.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                metaLine,
                                fontSize = 13.sp,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                            if (status.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(status, color = MiuixTheme.colorScheme.primary, fontSize = 13.sp)
                            }
                            if (dlText.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(dlText, color = MiuixTheme.colorScheme.primary, fontSize = 13.sp)
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
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = if (descOpen) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().clickable { descOpen = !descOpen },
                        )
                        Text(
                            if (descOpen) "∧" else "∨",
                            modifier = Modifier.fillMaxWidth().clickable { descOpen = !descOpen },
                            textAlign = TextAlign.Center,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onLib, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColorsPrimary()) {
                        Text(if (inLib) copy.inShelf else copy.addShelf)
                    }
                    if (foldersOn) {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onFolder, modifier = Modifier.fillMaxWidth()) {
                            Text(when (copy.addShelf) {
                                "Add to shelf" -> "Add to collection"
                                else -> "加入收藏夹"
                            })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val savedId = resume?.chapterId
                            val target = chapters.find { it.id == savedId } ?: chapters.firstOrNull()
                            if (target != null) {
                                val page = if (target.id == savedId) (resume?.page ?: 0) else 0
                                onReadChapter(target.id, page)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = chapters.isNotEmpty(),
                    ) { Text(copy.startRead) }
                }
            }
            if (chapters.isNotEmpty()) {
                val ordered = chapters.sortedBy { it.index }
                item { SmallTitle("${copy.catalogTitle} · ${ordered.size}") }
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
                                    .clickable(enabled = !chapterList.isScrollInProgress) {
                                        onReadChapter(chapterId, openPage)
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    c.title,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 16.sp,
                                    color = if (seen) Color(0xFF9A9A9E) else MiuixTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (savedPage != null) {
                                    Text(
                                        "第${savedPage + 1}页",
                                        modifier = Modifier.padding(start = 8.dp),
                                        fontSize = 13.sp,
                                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                        maxLines = 1,
                                    )
                                }
                            }
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable(enabled = !chapterList.isScrollInProgress) {
                                        val w = work ?: return@clickable
                                        if (Downloader.running) {
                                            Toast.makeText(context, "正在下载", Toast.LENGTH_SHORT).show()
                                            return@clickable
                                        }
                                        Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show()
                                        Downloader.start(
                                            context = context,
                                            id = w.id,
                                            title = w.title.ifBlank { title0 },
                                            chapters = listOf(c),
                                            onStep = { _, _ -> },
                                            onDone = { err ->
                                                Toast.makeText(context, if (err == null) "下载完成" else err, Toast.LENGTH_SHORT).show()
                                            },
                                        )
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Download, contentDescription = "下载", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            } else if (loading) {
                item { Text(copy.loading, Modifier.padding(16.dp), color = MiuixTheme.colorScheme.onSurfaceVariantSummary) }
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
                .clickable { coverOpen = false },
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
                    .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(tag, fontSize = 13.sp)
            }
        }
    }
}

private fun isBiqugeWork(workId: String, crawled: List<CrawledWork>): Boolean {
    val live = crawled.find { it.id == workId }
    if (live != null) {
        return live.siteId.startsWith("native22-") || live.siteId.startsWith("xiu2-") || live.siteId == Sites.BIQUGE || live.siteId == Sites.BIQUGE2 || live.siteId == Sites.BIQUGE3 ||
            live.href.contains("biquge365.net") || live.href.contains("bqg5.com") || live.href.contains("biquges.cc") ||
            live.href.contains("biquge5200.cc") || live.href.contains("8tsw.com")
    }
    val disk = LocalStore.loadWork(workId)?.work ?: return false
    return disk.siteId.startsWith("native22-") || disk.siteId.startsWith("xiu2-") || disk.siteId == Sites.BIQUGE || disk.siteId == Sites.BIQUGE2 || disk.siteId == Sites.BIQUGE3 ||
        disk.href.contains("biquge365.net") || disk.href.contains("bqg5.com") || disk.href.contains("biquges.cc") ||
        disk.href.contains("biquge5200.cc") || disk.href.contains("8tsw.com")
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
    val novel = remember(workId) { isBiqugeWork(workId, crawled) }
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
    val context = LocalContext.current
    val cached = remember(workId) { LocalStore.loadWork(workId) }
    val chapters = remember(workId) { cached?.chapters.orEmpty().sortedBy { it.index } }
    val chapter = chapters.find { it.id == chapterId } ?: chapters.firstOrNull()
    var text by remember(chapterId) { mutableStateOf("") }
    var loading by remember(chapterId) { mutableStateOf(true) }
    var err by remember(chapterId) { mutableStateOf("") }
    var fontSp by remember { mutableIntStateOf(prefs.novelFont) }
    var night by remember { mutableStateOf(prefs.novelNight) }
    var turn by remember { mutableStateOf(prefs.novelTurn) }
    var gap by remember { mutableFloatStateOf(prefs.novelGap) }
    var eye by remember { mutableStateOf(prefs.novelEye) }
    var tone by remember { mutableStateOf(prefs.novelTone) }
    var chrome by remember { mutableStateOf(false) }
    var catalog by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }
    var bright by remember { mutableFloatStateOf(1f) }
    val scroll = remember(workId, chapterId) { ScrollState(0) }
    var retry by remember(workId,chapterId) { mutableIntStateOf(0) }
    var restored by remember(chapterId) { mutableStateOf(false) }
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    var box by remember { mutableStateOf(IntSize.Zero) }
    val dark = night || tone == "ink" || tone == "black" || tone == "night"
    val bg = when {
        night || tone == "night" -> Color(0xFF1C1C1C)
        tone == "black" || tone == "ink" -> Color(0xFF111111)
        tone == "white" -> Color(0xFFF7F7F7)
        tone == "green" -> Color(0xFFDCE8D4)
        tone == "blue" -> Color(0xFFD5E3F0)
        else -> Color(0xFFEFE6C8)
    }
    val fg = if (dark) Color(0xFFD8D8D8) else Color(0xFF1A1A1A)
    val muted = fg.copy(alpha = 0.55f)
    val bar = if (dark) Color(0xFF242424) else Color(0xFFF6F1E4)
    val accent = Color(0xFFE67A2E)
    BackHandler {
        when {
            catalog -> catalog = false
            settings -> settings = false
            chrome -> chrome = false
            else -> onClose()
        }
    }
    LaunchedEffect(workId,chapterId,retry) {
        val href = chapter?.href
        if (href.isNullOrBlank()) {
            loading = false
            err = copy.emptyCat
            return@LaunchedEffect
        }
        loading = true
        err = ""
        restored = false
        val (body, e) = kotlinx.coroutines.runInterruptible(Dispatchers.IO) { Crawler.fetchNovel(href) }
        text = body
        loading = false
        if (body.isBlank()) err = e ?: "未解析到正文"
    }
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val reading = remember(text, chapter?.title) {
        val peeled = novelReadingBody(text, chapter?.title.orEmpty())
        peeled.ifBlank { text }
    }
    val style = TextStyle(
        color = fg,
        fontSize = fontSp.sp,
        lineHeight = (fontSp * gap).sp,
    )
    val leaves = remember(reading, fontSp, gap, box, turn, night, tone, topInset, bottomInset) {
        if (reading.isBlank() || turn == "vertical" || box.width < 8 || box.height < 8) {
            emptyList()
        } else {
            val side = with(density) { 22.dp.roundToPx() }
            val top = with(density) { (topInset + 16.dp).roundToPx() }
            val bot = with(density) { (bottomInset + 28.dp).roundToPx() }
            paginateNovel(
                measurer,
                reading,
                style,
                (box.width - side * 2).coerceAtLeast(1),
                (box.height - top - bot).coerceAtLeast(1),
            )
        }
    }
    val pageCount = leaves.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })
    LaunchedEffect(leaves.size, chapterId, text, turn, scroll.maxValue) {
        if (text.isBlank() || restored) return@LaunchedEffect
        if (turn == "vertical") {
            if(startPage>0 && scroll.maxValue==0) kotlinx.coroutines.withTimeoutOrNull(1200) { snapshotFlow { scroll.maxValue }.first { it>0 } }
            if (scroll.maxValue > 0 && startPage > 0) {
                scroll.scrollTo((scroll.maxValue.toLong() * startPage.coerceAtMost(1000) / 1000).toInt().coerceIn(0, scroll.maxValue))
            }
            restored = true
            return@LaunchedEffect
        }
        if (leaves.isEmpty()) return@LaunchedEffect
        val at = novelRestorePage(startPage, leaves.size)
        if (at != pagerState.currentPage) runCatching { pagerState.scrollToPage(at) }
        restored = true
    }
    LaunchedEffect(chapterId, text, pagerState.currentPage, scroll.value, scroll.maxValue, turn, restored, loading) {
        if (text.isBlank() || loading || !restored) return@LaunchedEffect
        val chapterKey = chapter?.id ?: chapterId
        if (turn == "vertical") {
            if (scroll.maxValue <= 0) return@LaunchedEffect
            onProgress(chapterKey, (scroll.value * 1000L / scroll.maxValue).toInt().coerceIn(0, 1000))
        } else if (leaves.isNotEmpty()) {
            onProgress(chapterKey, pagerState.currentPage.coerceIn(0, leaves.lastIndex))
        }
    }
    val index = chapters.indexOfFirst { it.id == chapter?.id }
    val clock = remember { mutableStateOf("") }
    LaunchedEffect(chrome) {
        while (true) {
            val cal = java.util.Calendar.getInstance()
            clock.value = "%02d:%02d".format(cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
            delay(20_000)
        }
    }
    val scope = rememberCoroutineScope()
    fun turnPage(delta: Int) {
        if (turn == "vertical") {
            chrome = !chrome
            return
        }
        val next = pagerState.currentPage + delta
        if (next < 0) {
            chapters.getOrNull(index - 1)?.let { onChapter(it.id) }
            return
        }
        if (leaves.isNotEmpty() && next > leaves.lastIndex) {
            chapters.getOrNull(index + 1)?.let { onChapter(it.id) }
            return
        }
        scope.launch {
            runCatching {
                if (turn == "none") pagerState.scrollToPage(next) else pagerState.animateScrollToPage(next)
            }
        }
    }
    fun saveChapter() {
        val body = text
        val ch = chapter ?: return
        if (body.isBlank()) return
        val dir = java.io.File(context.getExternalFilesDir(null) ?: context.filesDir, "novels/$workId")
        dir.mkdirs()
        val safe = ch.title.replace(Regex("""[\\/:*?"<>|]"""), " ").trim().take(40).ifBlank { ch.id }
        java.io.File(dir, "${ch.index}-$safe.txt").writeText(ch.title + "\n\n" + body)
        Toast.makeText(context, "本章已下载", Toast.LENGTH_SHORT).show()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(bg)
            .onSizeChanged { box = it },
    ) {
        val dim = (1f - bright).coerceIn(0f, 0.65f)
        when {
            loading -> Text(copy.loading, Modifier.align(Alignment.Center), color = muted, fontSize = 15.sp)
            text.isBlank() -> Column(Modifier.align(Alignment.Center).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text(err.ifBlank { "未解析到正文" },color=muted,fontSize=15.sp,textAlign=TextAlign.Center)
                Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    Button(onClick={retry++}) { Text("重试") }
                    Button(onClick=onClose) { Text("返回目录") }
                }
            }
            turn == "vertical" -> Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(horizontal = 22.dp)
                    .padding(top = topInset + 16.dp, bottom = bottomInset + 28.dp)
                    .clickable { chrome = !chrome },
            ) {
                Text(reading, color = fg, fontSize = fontSp.sp, lineHeight = (fontSp * gap).sp)
            }
            else -> HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1,
                userScrollEnabled = turn != "none",
            ) { page ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(page, turn, chrome) {
                            detectTapGestures { pos ->
                                if (chrome || catalog || settings) {
                                    chrome = false
                                    catalog = false
                                    settings = false
                                    return@detectTapGestures
                                }
                                val x = pos.x / size.width
                                when {
                                    x < 0.28f -> turnPage(-1)
                                    x > 0.72f -> turnPage(1)
                                    else -> chrome = true
                                }
                            }
                        }
                        .padding(horizontal = 22.dp)
                        .padding(top = topInset + 16.dp, bottom = bottomInset + 28.dp),
                ) {
                    Text(
                        leaves.getOrNull(page).orEmpty(),
                        color = fg,
                        fontSize = fontSp.sp,
                        lineHeight = (fontSp * gap).sp,
                    )
                }
            }
        }
        if (eye && !dark) {
            Box(Modifier.fillMaxSize().background(Color(0x18C47A1A)))
        }
        if (dim > 0.01f) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = dim)))
        }
        if (!chrome && text.isNotBlank()) {
            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val here = if (turn == "vertical") {
                    val pct = if (scroll.maxValue <= 0) 0 else (scroll.value * 100 / scroll.maxValue)
                    "$pct%"
                } else {
                    "${pagerState.currentPage + 1}/${leaves.size.coerceAtLeast(1)}"
                }
                Text(here, color = muted, fontSize = 12.sp)
                Text(clock.value, color = muted, fontSize = 12.sp)
            }
        }
        if (chrome) {
            Row(
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .background(bar)
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClose) { Icon(Icons.Back, contentDescription = "返回", tint = fg) }
                Text(
                    chapter?.title ?: cached?.work?.title.orEmpty(),
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    color = fg,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "加入收藏夹",
                    modifier = Modifier.clickable { onFolder() }.padding(horizontal = 10.dp, vertical = 8.dp),
                    color = fg,
                    fontSize = 15.sp,
                )
                Text(
                    "下载",
                    modifier = Modifier.clickable { saveChapter() }.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = fg,
                    fontSize = 15.sp,
                )
            }
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(bar)
                    .navigationBarsPadding()
                    .padding(top = 8.dp, bottom = 6.dp),
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val prevOn = index > 0
                    val nextOn = index >= 0 && index < chapters.lastIndex
                    Text(
                        "上一章",
                        modifier = Modifier.clickable(enabled = prevOn) { chapters.getOrNull(index - 1)?.let { onChapter(it.id) } }.padding(8.dp),
                        color = if (prevOn) fg else muted,
                        fontSize = 14.sp,
                    )
                    val cur = if (turn == "vertical") {
                        if (scroll.maxValue <= 0) 0 else ((scroll.value.toFloat() / scroll.maxValue) * 100).toInt()
                    } else pagerState.currentPage
                    val total = if (turn == "vertical") 100 else leaves.size.coerceAtLeast(1)
                    PageSlider(
                        index = cur.coerceAtLeast(0),
                        count = total.coerceAtLeast(1),
                        onSeek = { target ->
                            if (turn == "vertical") {
                                scope.launch {
                                    val y = (scroll.maxValue * (target / 100f)).toInt()
                                    scroll.scrollTo(y.coerceIn(0, scroll.maxValue))
                                }
                            } else {
                                scope.launch { runCatching { pagerState.scrollToPage(target.coerceIn(0, leaves.lastIndex.coerceAtLeast(0))) } }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        deep = accent,
                    )
                    Text(
                        "下一章",
                        modifier = Modifier.clickable(enabled = nextOn) { chapters.getOrNull(index + 1)?.let { onChapter(it.id) } }.padding(8.dp),
                        color = if (nextOn) fg else muted,
                        fontSize = 14.sp,
                    )
                }
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NovelTool("目录", fg) { catalog = true }
                    NovelTool(if (night) "日间" else "夜间", fg) {
                        night = !night
                        prefs.novelNight = night
                    }
                    NovelTool("设置", fg) { settings = true }
                }
            }
        }
        if (catalog) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { catalog = false },
            ) {
                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(0.72f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(bar)
                        .clickable(enabled = false) {},
                ) {
                    Text(
                        cached?.work?.title ?: chapter?.title.orEmpty(),
                        modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 16.dp),
                        color = fg,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "目录 · ${chapters.size}",
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp),
                        color = muted,
                        fontSize = 13.sp,
                    )
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(chapters, key = { it.id }) { ch ->
                            val on = ch.id == chapter?.id
                            Text(
                                ch.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        catalog = false
                                        if (ch.id != chapter?.id) onChapter(ch.id)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                color = if (on) accent else fg,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
        if (settings) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
                    .clickable { settings = false },
            ) {
                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(bar)
                        .clickable(enabled = false) {}
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("亮度", color = muted, fontSize = 13.sp, modifier = Modifier.width(42.dp))
                        TrackSlider(bright, 0.35f..1f, true, { bright = it }, Modifier.weight(1f))
                        Text(
                            "护眼",
                            color = if (eye) accent else fg,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 8.dp).clickable {
                                eye = !eye
                                prefs.novelEye = eye
                            },
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("字号", color = muted, fontSize = 13.sp, modifier = Modifier.width(42.dp))
                        Text(
                            "A-",
                            color = fg,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(fg.copy(alpha = 0.08f))
                                .clickable {
                                    fontSp = (fontSp - 1).coerceAtLeast(16)
                                    prefs.novelFont = fontSp
                                    restored = true
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                        Text("$fontSp", color = fg, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 12.dp))
                        Text(
                            "A+",
                            color = fg,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(fg.copy(alpha = 0.08f))
                                .clickable {
                                    fontSp = (fontSp + 1).coerceAtMost(32)
                                    prefs.novelFont = fontSp
                                    restored = true
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                        Spacer(Modifier.weight(1f))
                        Text("系统字体", color = muted, fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("颜色", color = muted, fontSize = 13.sp, modifier = Modifier.width(42.dp))
                        listOf(
                            "white" to Color(0xFFF7F7F7),
                            "paper" to Color(0xFFEFE6C8),
                            "green" to Color(0xFFDCE8D4),
                            "blue" to Color(0xFFD5E3F0),
                            "ink" to Color(0xFF2A2A2A),
                            "black" to Color(0xFF111111),
                            "night" to Color(0xFF1C1C1C),
                        ).forEach { (id, color) ->
                            val picked = if (night) id == "night" else tone == id
                            Box(
                                Modifier
                                    .padding(end = 8.dp)
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(if (picked) Modifier.borderTone() else Modifier)
                                    .clickable {
                                        tone = id
                                        night = id == "night" || id == "ink" || id == "black"
                                        prefs.novelTone = id
                                        prefs.novelNight = night
                                    },
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("翻页", color = muted, fontSize = 13.sp, modifier = Modifier.width(42.dp))
                        listOf("cover" to "覆盖", "slide" to "平移", "vertical" to "上下", "none" to "无动画").forEach { (id, label) ->
                            Text(
                                label,
                                color = if (turn == id) Color.White else fg,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (turn == id) accent else fg.copy(alpha = 0.08f))
                                    .clickable {
                                        turn = id
                                        prefs.novelTurn = id
                                        restored = true
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("间距", color = muted, fontSize = 13.sp, modifier = Modifier.width(42.dp))
                        TrackSlider(
                            gap,
                            1.45f..2.3f,
                            true,
                            {
                                gap = it
                                prefs.novelGap = it
                            },
                            Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.borderTone(): Modifier = this.then(
    Modifier.drawBehind {
        drawCircle(color = Color(0xFFE67A2E), style = Stroke(width = 2.dp.toPx()))
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
    val titled = want.length >= 4 && head.isNotBlank() && (head == want || head.startsWith(want) || head.endsWith(want) || head.contains(want))
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
            if (h <= height) {
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

@Composable
private fun NovelTool(label: String, color: Color, onClick: () -> Unit) {
    Text(
        label,
        color = color,
        fontSize = 13.sp,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 22.dp, vertical = 8.dp),
    )
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
    var modeUse by remember { mutableStateOf("") }
    var rotUse by remember { mutableStateOf("") }
    val mode = if (modeUse.isEmpty()) r.readMode else modeUse
    val rot = if (rotUse.isEmpty()) r.rotation else rotUse
    var hint by remember(chapterId) { mutableStateOf(if (r.modeHint) modeName(mode, more) else "") }
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
    val crop = if (webtoon) r.webtoonCrop else r.cropEdges
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
    var modePick by remember { mutableStateOf(false) }
    var rotPick by remember { mutableStateOf(false) }
    val presetLabel = if (uiLang == "en") "Default" else if (uiLang == "zh-TW") "預設" else "预设"

    BackHandler(enabled = coverAsk || pageMenu || settings || modePick || rotPick) {
        when {
            coverAsk -> coverAsk = false
            pageMenu -> pageMenu = false
            settings -> settings = false
            modePick -> modePick = false
            else -> rotPick = false
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
            "landscape" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            "lock-landscape" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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
                    val p = startPage.coerceIn(0, pages.lastIndex)
                    index = p
                    viewChapterId = ch.id
                    viewCount = pages.size
                    val at = strip.indexOfFirst { it.chapterId == ch.id && it.page == p }.let { if (it < 0) 0 else it }
                    runCatching { listState.scrollToItem(at.coerceAtMost(strip.lastIndex)) }
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
        loading = false
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
                    val p = startPage.coerceIn(0, fresh.lastIndex)
                    index = p
                    val at = strip.indexOfFirst { it.chapterId == ch.id && it.page == p }.let { if (it < 0) 0 else it }
                    runCatching { listState.scrollToItem(at.coerceAtMost(strip.lastIndex)) }
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
    }
    LaunchedEffect(hint) {
        if (hint.isNotEmpty()) {
            delay(1400)
            hint = ""
        }
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
            val filtered = r.skipFiltered && (ch.title.contains("广告") || ch.title.contains("公告") || ch.title.isBlank())
            val seen = r.skipRead && "$workId:${ch.id}" in read
            !filtered && !seen
        }
        val next = candidates.firstOrNull()
        if (next != null) onChapter(next.id) else Toast.makeText(context, copy.emptyCat, Toast.LENGTH_SHORT).show()
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
            if (forward) Toast.makeText(context, copy.emptyCat, Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(context, copy.emptyCat, Toast.LENGTH_SHORT).show()
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
        val p = startPage.coerceIn(0, pages.lastIndex)
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
        val p = startPage.coerceIn(0, pages.lastIndex)
        index = p
        val at = strip.indexOfFirst { it.chapterId == ch.id && it.page == p }.let { if (it < 0) 0 else it }
        listState.scrollToItem(at)
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
            val at = strip.indexOfFirst { it.chapterId == ch && it.page == targetPage }
            if (at >= 0) {
                index = targetPage
                scope.launch { listState.scrollToItem(at) }
            }
            return
        }
        if (pages.isEmpty()) return
        val target = i.coerceIn(0, pages.lastIndex)
        index = target
        scope.launch {
            runCatching {
                if (target < pagerState.pageCount) {
                    if (r.pageAnim) pagerState.animateScrollToPage(target) else pagerState.scrollToPage(target)
                }
            }
        }
    }

    fun onReaderTap(x: Float, y: Float) {
        if (chrome || settings || modePick || rotPick) {
            chrome = false
            return
        }
        val continuous = webtoon && !verticalPage
        if (!r.tapTurn) {
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
            Toast.makeText(context, fail, Toast.LENGTH_SHORT).show()
            return
        }
        val original = cached?.work?.cover.orEmpty()
        Thread {
            val bmp = Crawler.downloadBitmap(src, force = true)
            Handler(Looper.getMainLooper()).post {
                if (bmp == null) {
                    Toast.makeText(context, fail, Toast.LENGTH_SHORT).show()
                    return@post
                }
                val dir = java.io.File(context.filesDir, "covers").apply { mkdirs() }
                val file = java.io.File(dir, "$workId.jpg")
                val wrote = runCatching {
                    file.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it) }
                }.isSuccess && file.exists() && file.length() > 0L
                if (!wrote) {
                    Toast.makeText(context, fail, Toast.LENGTH_SHORT).show()
                    return@post
                }
                prefs.noteCover(workId, file.absolutePath, original)
                val disk = LocalStore.loadWork(workId)
                if (disk != null) {
                    LocalStore.saveWork(disk.copy(work = disk.work.copy(cover = file.absolutePath)))
                }
                onCoverSaved(file.absolutePath)
                Toast.makeText(
                    context,
                    if (uiLang == "en") "Cover updated" else "封面已更新",
                    Toast.LENGTH_SHORT,
                ).show()
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
                    Toast.makeText(context, fail, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, okWord, Toast.LENGTH_SHORT).show()
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
                    val ms = when (r.tapAnim) { "fast" -> 90; "slow" -> 300; else -> 170 }
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
            if (!settings && !modePick && !rotPick && !coverAsk) {
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
                if (source != NestedScrollSource.UserInput || !hasPages.value || loadingNow.value) return Offset.Zero
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
        when {
            loading && pages.isEmpty() && strip.isEmpty() -> Box(Modifier.fillMaxSize().then(tapMod), contentAlignment = Alignment.Center) {
                Text(copy.loading, color = fg.copy(alpha = 0.7f))
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
                    LazyColumn(
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
                                    crop = crop,
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
                        if (src != null) PageImage(src, crop = crop, webtoon = false, colorFilter = pageFilter, tint = pageTint, tintBlend = tintBlend, nonce = reloads[page.toString()] ?: 0, fit = pageFit, align = pageAlign)
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
                        if (src != null) PageImage(src, crop = crop, webtoon = false, colorFilter = pageFilter, tint = pageTint, tintBlend = tintBlend, nonce = reloads[page.toString()] ?: 0, fit = pageFit, align = pageAlign)
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
                onMode = { modePick = !modePick; rotPick = false },
                onRotate = { rotPick = !rotPick; modePick = false },
                onCrop = {
                    patch(if (webtoon) r.copy(webtoonCrop = !r.webtoonCrop) else r.copy(cropEdges = !r.cropEdges))
                },
                onSettings = { settings = true; modePick = false; rotPick = false },
                barInk = MiuixTheme.colorScheme.primary,
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
        if (hint.isNotEmpty()) {
            Text(
                hint,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        if (modePick) {
            FloatMenu(
                alignEnd = false,
                options = listOf(
                    "" to presetLabel,
                    "ltr" to more.ltr,
                    "rtl" to more.rtl,
                    "ttb" to more.ttb,
                    "webtoon" to more.webtoonMode,
                    "ttb-slide" to more.ttbSlide,
                    "ltr-slide" to more.ltrSlide,
                    "rtl-slide" to more.rtlSlide,
                ),
                selected = modeUse,
                onPick = { modeUse = it },
                onDismiss = { modePick = false },
            )
        }
        if (rotPick) {
            FloatMenu(
                alignEnd = true,
                options = listOf(
                    "" to presetLabel,
                    "auto" to more.rotAuto,
                    "portrait" to if (uiLang == "en") "Portrait" else "竖屏",
                    "landscape" to if (uiLang == "en") "Landscape" else "横屏",
                    "lock-portrait" to if (uiLang == "en") "Lock portrait" else if (uiLang == "zh-TW") "鎖定豎屏" else "锁定竖屏",
                    "lock-landscape" to if (uiLang == "en") "Lock landscape" else if (uiLang == "zh-TW") "鎖定橫屏" else "锁定横屏",
                ),
                selected = rotUse,
                onPick = { rotUse = it },
                onDismiss = { rotPick = false },
            )
        }
        if (settings) {
            ReaderSettingsSheet(
                lang = uiLang,
                more = more,
                reading = r,
                modeUse = modeUse,
                rotUse = rotUse,
                onModeUse = { modeUse = it },
                onRotUse = { rotUse = it },
                onChange = { patch(it) },
                onApply = {
                    patch(r.copy(readMode = mode, rotation = rot))
                    modeUse = ""
                    rotUse = ""
                    Toast.makeText(
                        context,
                        if (uiLang == "en") "Saved as the default" else if (uiLang == "zh-TW") "已儲存為預設" else "已保存为预设",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                onDismiss = { settings = false },
            )
        }
        if (pageMenu) {
            val rows = buildList {
                if (prefs.customCover) {
                    add(Triple(Icons.Grid, if (uiLang == "en") "Set as cover" else if (uiLang == "zh-TW") "設定為封面" else "设定为封面", "cover"))
                }
                if (prefs.savePhotos) {
                    add(Triple(Icons.Download, if (uiLang == "en") "Save" else if (uiLang == "zh-TW") "儲存" else "储存", "save"))
                }
                add(Triple(Icons.Rotate, if (uiLang == "en") "Reload image" else if (uiLang == "zh-TW") "重新加載圖片" else "重新加载图片", "reload"))
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
private fun FloatMenu(
    alignEnd: Boolean,
    options: List<Pair<String, String>>,
    selected: String,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(Modifier.fillMaxSize().clickable(onClick = onDismiss)) {
        Column(
            Modifier
                .align(if (alignEnd) Alignment.BottomEnd else Alignment.BottomStart)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .padding(bottom = 118.dp)
                .width(232.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .padding(vertical = 6.dp),
        ) {
            options.forEach { (id, label) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onPick(id); onDismiss() }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        if (selected == id) "✓" else "",
                        modifier = Modifier.width(28.dp),
                        fontSize = 16.sp,
                        color = Color(0xFF1C1C1E),
                    )
                    Text(label, fontSize = 16.sp, color = Color(0xFF1C1C1E))
                }
            }
        }
    }
}

@Composable
private fun ReaderSettingsSheet(
    lang: String,
    more: More,
    reading: ReadingPrefs,
    modeUse: String,
    rotUse: String,
    onModeUse: (String) -> Unit,
    onRotUse: (String) -> Unit,
    onChange: (ReadingPrefs) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun t(cn: String, tw: String, en: String) = when (lang) {
        "en" -> en
        "zh-TW" -> tw
        else -> cn
    }
    val preset = t("预设", "預設", "Default")
    var tab by remember { mutableIntStateOf(0) }
    var pick by remember { mutableStateOf<String?>(null) }
    BackHandler(enabled = pick != null) { pick = null }
    val modes = listOf(
        "" to preset,
        "ltr" to more.ltr,
        "rtl" to more.rtl,
        "ttb" to more.ttb,
        "webtoon" to more.webtoonMode,
        "ttb-slide" to more.ttbSlide,
        "ltr-slide" to more.ltrSlide,
        "rtl-slide" to more.rtlSlide,
    )
    val rots = listOf(
        "" to preset,
        "auto" to more.rotAuto,
        "portrait" to t("竖屏", "豎屏", "Portrait"),
        "landscape" to t("横屏", "橫屏", "Landscape"),
        "lock-portrait" to t("锁定竖屏", "鎖定豎屏", "Lock portrait"),
        "lock-landscape" to t("锁定横屏", "鎖定橫屏", "Lock landscape"),
    )
    fun labelOf(id: String, opts: List<Pair<String, String>>) = opts.find { it.first == id }?.second ?: preset
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().clickable(onClick = onDismiss))
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.74f)
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(Color.White)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .navigationBarsPadding(),
        ) {
            if (pick == null) {
                Row(Modifier.fillMaxWidth()) {
                    listOf(
                        0 to t("阅读模式", "閱讀模式", "Reading"),
                        1 to t("通用", "通用", "General"),
                        2 to t("滤镜", "濾鏡", "Filter"),
                    ).forEach { (i, label) ->
                        Column(
                            Modifier.weight(1f).clickable { tab = i }.padding(top = 16.dp, bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(label, color = if (tab == i) Color(0xFF0088FF) else Color(0xFF8E8E93), fontSize = 15.sp)
                            Spacer(Modifier.height(8.dp))
                            Box(
                                Modifier
                                    .height(2.dp)
                                    .width(64.dp)
                                    .background(if (tab == i) Color(0xFF0088FF) else Color.Transparent),
                            )
                        }
                    }
                }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(bottom = 18.dp)) {
                val currentPick = pick
                if (currentPick != null) {
                    val spec = when (currentPick) {
                        "mode" -> Triple(t("阅读模式", "閱讀模式", "Reading mode"), modes, modeUse)
                        "rot" -> Triple(t("旋转型别", "旋轉型別", "Rotation"), rots, rotUse)
                        "nav" -> Triple(
                            more.navLayout,
                            listOf("default" to preset, "edge" to t("边缘", "邊緣", "Edges"), "l" to "L"),
                            reading.webtoonNav,
                        )
                        "rev" -> Triple(
                            more.reverseTap,
                            listOf("none" to more.none, "h" to t("水平", "水平", "Horizontal"), "v" to t("垂直", "垂直", "Vertical")),
                            reading.webtoonReverseTap,
                        )
                        "pad" -> Triple(
                            more.sidePad,
                            listOf("none" to more.none, "15" to "15%", "s" to t("小", "小", "Small"), "m" to t("中", "中", "Medium")),
                            reading.sidePad,
                        )
                        "bg" -> Triple(
                            more.bgColor,
                            listOf("black" to more.bgBlack, "gray" to more.bgGray, "white" to more.bgWhite),
                            reading.bgColor,
                        )
                        else -> Triple(
                            t("滤镜调和模式", "濾鏡調和模式", "Blend mode"),
                            listOf(
                                "default" to preset,
                                "multiply" to t("正片叠底", "色彩增值", "Multiply"),
                                "screen" to t("滤色", "濾色", "Screen"),
                                "overlay" to t("叠加", "疊加", "Overlay"),
                                "lighten" to t("变亮", "變亮", "Lighten"),
                                "darken" to t("变暗", "變暗", "Darken"),
                            ),
                            reading.filterBlend,
                        )
                    }
                    Text(spec.first, Modifier.padding(horizontal = 20.dp, vertical = 14.dp), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1C1E))
                    spec.second.forEach { (id, label) ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                when (currentPick) {
                                    "mode" -> onModeUse(id)
                                    "rot" -> onRotUse(id)
                                    "nav" -> onChange(reading.copy(webtoonNav = id))
                                    "rev" -> onChange(reading.copy(webtoonReverseTap = id))
                                    "pad" -> onChange(reading.copy(sidePad = id))
                                    "bg" -> onChange(reading.copy(bgColor = id))
                                    else -> onChange(reading.copy(filterBlend = id))
                                }
                                pick = null
                            }.padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(if (spec.third == id) "✓" else "", Modifier.width(28.dp), fontSize = 16.sp, color = Color(0xFF1C1C1E))
                            Text(label, fontSize = 16.sp, color = Color(0xFF1C1C1E))
                        }
                    }
                } else when (tab) {
                    0 -> {
                        SheetLine(t("适用此系列", "適用於此系列", "Use for this series"), onClick = onApply)
                        SheetLine(t("阅读模式", "閱讀模式", "Reading mode"), labelOf(modeUse, modes)) { pick = "mode" }
                        SheetLine(t("旋转型别", "旋轉型別", "Rotation"), labelOf(rotUse, rots)) { pick = "rot" }
                        Text("WEBTOON", Modifier.padding(horizontal = 20.dp, vertical = 12.dp), color = Color(0xFF8E8E93), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        SheetLine(t("导航布局", "導航佈局", "Navigation"), labelOf(reading.webtoonNav, listOf("default" to preset, "edge" to t("边缘", "邊緣", "Edges"), "l" to "L"))) { pick = "nav" }
                        SheetLine(t("反转轻触方向", "反轉輕觸方向", "Invert taps"), labelOf(reading.webtoonReverseTap, listOf("none" to more.none, "h" to t("水平", "水平", "Horizontal"), "v" to t("垂直", "垂直", "Vertical")))) { pick = "rev" }
                        SheetLine(t("侧边填充", "側邊填充", "Side padding"), labelOf(reading.sidePad, listOf("none" to more.none, "15" to "15%", "s" to t("小", "小", "Small"), "m" to t("中", "中", "Medium")))) { pick = "pad" }
                        SheetSwitch(more.cropEdges, reading.webtoonCrop) { onChange(reading.copy(webtoonCrop = it)) }
                        SheetSwitch(more.splitDouble, reading.webtoonSplit) { onChange(reading.copy(webtoonSplit = it)) }
                    }
                    1 -> {
                        SheetLine(
                            more.bgColor,
                            when (reading.bgColor) {
                                "white" -> more.bgWhite
                                "gray" -> more.bgGray
                                else -> more.bgBlack
                            },
                        ) { pick = "bg" }
                        SheetSwitch(more.showPageNum, reading.showPageNum) { onChange(reading.copy(showPageNum = it)) }
                        SheetSwitch(more.fullscreen, reading.fullscreen) { onChange(reading.copy(fullscreen = it)) }
                        SheetSwitch(more.keepScreenOn, reading.keepScreenOn) { onChange(reading.copy(keepScreenOn = it)) }
                        SheetSwitch(more.longPressMenu, reading.longPressMenu) { onChange(reading.copy(longPressMenu = it)) }
                        SheetSwitch(more.chapterTransition, reading.chapterTransition) { onChange(reading.copy(chapterTransition = it)) }
                        SheetSwitch(more.pageAnim, reading.pageAnim) { onChange(reading.copy(pageAnim = it)) }
                    }
                    else -> {
                        SheetSwitch(t("自定义滤镜", "自訂濾鏡", "Custom filter"), reading.filterOn) { onChange(reading.copy(filterOn = it)) }
                        FilterSlider("R", reading.filterR, 0f..255f, reading.filterOn) { onChange(reading.copy(filterR = it)) }
                        FilterSlider("G", reading.filterG, 0f..255f, reading.filterOn) { onChange(reading.copy(filterG = it)) }
                        FilterSlider("B", reading.filterB, 0f..255f, reading.filterOn) { onChange(reading.copy(filterB = it)) }
                        FilterSlider("A", reading.filterA, 0f..255f, reading.filterOn) { onChange(reading.copy(filterA = it)) }
                        SheetLine(
                            t("滤镜调和模式", "濾鏡調和模式", "Blend mode"),
                            labelOf(
                                reading.filterBlend,
                                listOf(
                                    "default" to preset,
                                    "multiply" to t("正片叠底", "色彩增值", "Multiply"),
                                    "screen" to t("滤色", "濾色", "Screen"),
                                    "overlay" to t("叠加", "疊加", "Overlay"),
                                    "lighten" to t("变亮", "變亮", "Lighten"),
                                    "darken" to t("变暗", "變暗", "Darken"),
                                ),
                            ),
                        ) { pick = "blend" }
                        SheetSwitch(t("灰度", "灰階", "Grayscale"), reading.grayscale) { onChange(reading.copy(grayscale = it)) }
                        SheetSwitch(t("反转", "反轉", "Invert"), reading.invert) { onChange(reading.copy(invert = it)) }
                        Spacer(Modifier.height(8.dp))
                        SheetSwitch(t("自定义亮度", "自訂亮度", "Custom brightness"), reading.brightnessOn) { onChange(reading.copy(brightnessOn = it)) }
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Gear, contentDescription = null, tint = if (reading.brightnessOn) Color(0xFF1C1C1E) else Color(0xFFC7C7CC), modifier = Modifier.size(22.dp))
                            TrackSlider(
                                value = reading.brightness,
                                range = -100f..100f,
                                enabled = reading.brightnessOn,
                                onChange = { onChange(reading.copy(brightness = it)) },
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                            )
                            Text(
                                reading.brightness.roundToInt().toString(),
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.End,
                                color = Color(0xFF8E8E93),
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetLine(title: String, value: String? = null, onClick: (() -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFF3A3A3C))
        if (value != null) {
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C1C1E))
            Text("  ⌄", color = Color(0xFF8E8E93), fontSize = 14.sp)
        }
    }
}

@Composable
private fun SheetSwitch(title: String, on: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, modifier = Modifier.weight(1f), fontSize = 15.sp, color = Color(0xFF1C1C1E))
        Box(
            Modifier
                .width(48.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(50))
                .background(if (on) Color(0xFF0088FF) else Color(0xFFE5E5EA))
                .clickable { onChange(!on) }
                .padding(3.dp),
            contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(Modifier.size(22.dp).background(Color.White, CircleShape))
        }
    }
}

@Composable
private fun FilterSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, enabled: Boolean, onChange: (Float) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, Modifier.width(24.dp), fontSize = 15.sp, color = if (enabled) Color(0xFF1C1C1E) else Color(0xFFC7C7CC))
        TrackSlider(value, range, enabled, onChange, Modifier.weight(1f).padding(horizontal = 8.dp))
        Text(value.roundToInt().toString(), Modifier.width(40.dp), textAlign = TextAlign.End, fontSize = 14.sp, color = Color(0xFF8E8E93))
    }
}

@Composable
private fun TrackSlider(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val span = (range.endInclusive - range.start).takeIf { it != 0f } ?: 1f
    val frac = ((value - range.start) / span).coerceIn(0f, 1f)
    BoxWithConstraints(
        modifier
            .height(28.dp)
            .pointerInput(enabled, range) {
                if (!enabled) return@pointerInput
                detectTapGestures { pos ->
                    onChange(range.start + span * (pos.x / size.width).coerceIn(0f, 1f))
                }
            }
            .pointerInput(enabled, range) {
                if (!enabled) return@pointerInput
                detectHorizontalDragGestures { change, _ ->
                    onChange(range.start + span * (change.position.x / size.width).coerceIn(0f, 1f))
                }
            },
    ) {
        Box(Modifier.align(Alignment.CenterStart).fillMaxWidth().height(3.dp).clip(RoundedCornerShape(50)).background(Color(0xFFE3E3E8)))
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .padding(start = (maxWidth - 18.dp) * frac)
                .size(18.dp)
                .background(if (enabled) Color(0xFF9A9AA0) else Color(0xFFD0D0D4), CircleShape),
        )
    }
}

private fun modeName(id: String, more: More) = when (id) {
    "ltr" -> more.ltr
    "rtl" -> more.rtl
    "ttb" -> more.ttb
    "ttb-slide" -> more.ttbSlide
    "ltr-slide" -> more.ltrSlide
    "rtl-slide" -> more.rtlSlide
    else -> more.webtoonMode
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
                modifier = Modifier.clickable(onClick = onNext),
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

@Composable
private fun PageActionSheet(
    items: List<Triple<ImageVector, String, String>>,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().clickable(onClick = onDismiss))
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(Color.White)
                .navigationBarsPadding()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        ) {
            items.forEach { (icon, label, id) ->
                Row(
                    Modifier.fillMaxWidth().clickable { onPick(id) }.padding(horizontal = 22.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(icon, contentDescription = label, tint = Color(0xFF1C1C1E), modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(18.dp))
                    Text(label, fontSize = 16.sp, color = Color(0xFF1C1C1E))
                }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun BoxScope.ReaderChrome(
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
    onMode: () -> Unit,
    onRotate: () -> Unit,
    onCrop: () -> Unit,
    onSettings: () -> Unit,
    barInk: Color,
) {
    val ink = Color(0xFF1C1C1E)
    val bar = Color.White
    Column(Modifier.align(Alignment.TopCenter).fillMaxWidth().background(bar).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Icon(Icons.Back, contentDescription = "返回", tint = ink)
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = ink, maxLines = 1)
                if (chapter.isNotBlank()) {
                    Text(chapter, fontSize = 12.sp, color = Color(0xFF8E8E93), maxLines = 2)
                }
            }
        }
    }
    Column(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(bar)
            .navigationBarsPadding()
            .padding(bottom = 6.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(50)).background(Color(0xFFF2F2F7)).clickable(onClick = onPrev),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.SkipPrev, contentDescription = "上一话", tint = ink) }
            PageSlider(index, count, onSeek, Modifier.weight(1f).padding(horizontal = 10.dp), barInk)
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(50)).background(Color(0xFFF2F2F7)).clickable(onClick = onNext),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.SkipNext, contentDescription = "下一话", tint = ink) }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            ChromeIcon(Icons.Reader, "阅读模式", onMode)
            ChromeIcon(Icons.Rotate, "旋转", onRotate)
            ChromeIcon(Icons.Crop, "裁边", onCrop)
            ChromeIcon(Icons.Gear, "设置", onSettings)
        }
    }
}

@Composable
private fun ChromeIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(Modifier.size(48.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = label, tint = Color(0xFF1C1C1E))
    }
}

@Composable
private fun PageSlider(index: Int, count: Int, onSeek: (Int) -> Unit, modifier: Modifier = Modifier, deep: Color = Color(0xFF0066CC)) {
        val fraction = if (count <= 1) 0f else index / (count - 1f)
        val t = ((fraction - 0.32f) / 0.68f).coerceIn(0f, 1f)
        val depth = t * t * (3f - 2f * t)
        val pale = lerp(Color.White, deep, 0.34f)
        val ink = lerp(pale, deep, depth)
    BoxWithConstraints(
        modifier
            .height(36.dp)
            .pointerInput(count) {
                detectTapGestures { pos ->
                    val f = (pos.x / size.width).coerceIn(0f, 1f)
                    onSeek(((count - 1) * f).roundToInt())
                }
            }
            .pointerInput(count) {
                detectHorizontalDragGestures { change, _ ->
                    val f = (change.position.x / size.width).coerceIn(0f, 1f)
                    onSeek(((count - 1) * f).roundToInt())
                }
            },
    ) {
        Text("${index + 1}", fontSize = 12.sp, color = Color(0xFF666666), modifier = Modifier.align(Alignment.CenterStart))
        Text("$count", fontSize = 12.sp, color = Color(0xFF666666), modifier = Modifier.align(Alignment.CenterEnd))
        val track = Modifier.padding(horizontal = 32.dp).align(Alignment.CenterStart).fillMaxWidth()
        Box(track.height(4.dp).background(Color(0xFFE5E5EA), RoundedCornerShape(50)))
        Box(track.fillMaxWidth(fraction.coerceIn(0.001f, 1f)).height(4.dp).background(ink, RoundedCornerShape(50)))
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp)
                .offset(x = (maxWidth - 82.dp) * fraction)
                .size(16.dp)
                .background(ink, RoundedCornerShape(50)),
        )
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
private fun WebPane(url: String, title: String, onBack: () -> Unit) {
    if(runCatching { WebView.getCurrentWebViewPackage() }.getOrNull()==null) {
        Scaffold(topBar={SubTopBar(title,onBack,BarTitle.End)}) { padding->
            EmptyFace("(´・ω・`)","系统网页组件不可用，请启用或更新系统WebView后重试",Modifier.padding(padding))
        }
        return
    }
    Column(Modifier.fillMaxSize().background(MiuixTheme.colorScheme.surface)) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(48.dp)
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Back, contentDescription = "返回") }
            Spacer(Modifier.weight(1f))
            Text(
                title.ifBlank { url },
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MiuixTheme.colorScheme.onSurface,
            )
        }
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    settings.userAgentString = Crawler.UA
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadUrl(url)
                }
            },
            modifier = Modifier.weight(1f).fillMaxWidth(),
            onRelease = { web ->
                web.stopLoading()
                web.destroy()
            },
        )
    }
}

@Composable
private fun FeedbackPane(copy: Copy, sites: List<WebSite>, onBack: () -> Unit) {
    val context = LocalContext.current
    val saved = "日志已保存"
    val fail = "无法写入日志"
    val createLauncher = rememberLauncherForActivityResult(CreateZipDoc()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        AppLog.add("feedback", "export")
        val text = AppLog.dump(context, sites.toList())
        val ok = writeZip(context, uri, "log.txt", text.toByteArray(Charsets.UTF_8))
        Toast.makeText(context, if (ok) saved else fail, Toast.LENGTH_SHORT).show()
    }
    Scaffold(topBar = { SubTopBar(copy.feedback, onBack, BarTitle.End) }) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
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

private fun writeBackupTo(
    context: Context,
    prefs: Prefs,
    uri: Uri,
    library: List<String>,
    favorites: List<String>,
    sources: List<Source>,
): Boolean {
    val json = prefs.exportJson(library, favorites, sources)
    val ok = writeZip(context, uri, "backup.json", json.toByteArray(Charsets.UTF_8))
    if (ok) {
        prefs.lastBackupAt = System.currentTimeMillis()
        prefs.backupUri = uri.toString()
    }
    return ok
}

@Composable
private fun BackupPane(
    copy: Copy,
    prefs: Prefs,
    library: List<String>,
    favorites: List<String>,
    sources: List<Source>,
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
    val createLauncher = rememberLauncherForActivityResult(CreateZipDoc()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val ok = writeBackupTo(context, prefs, uri, library, favorites, sources)
        Toast.makeText(context, if (ok) saved else failWrite, Toast.LENGTH_SHORT).show()
    }
    val openLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val raw = runCatching { readBackupJson(context, uri) }.getOrNull()
        val data = raw?.let { prefs.importJson(it) }
        if (data != null) {
            onImported(data)
            Toast.makeText(context, restored, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, bad, Toast.LENGTH_SHORT).show()
        }
    }
    Box(Modifier.fillMaxSize()) {
        Scaffold(topBar = { SubTopBar(copy.backup, onBack, BarTitle.End) }) { padding ->
            LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                item {
                    Card(Modifier.padding(12.dp).fillMaxWidth()) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemePane(
    copy: Copy,
    lang: String,
    colorMode: ColorSchemeMode,
    onMode: (ColorSchemeMode) -> Unit,
    iconStyle: String,
    onIcon: (String) -> Unit,
    seed: Int,
    onSeed: (Int) -> Unit,
    palette: String,
    onPalette: (String) -> Unit,
    onBanner: () -> Unit,
    prefs: Prefs,
    onBack: () -> Unit,
) {
    val monetOn = colorMode.name.startsWith("Monet")
    Scaffold(topBar = { SubTopBar(copy.themeSettings, onBack, BarTitle.End) }) { padding ->
        LazyColumn(contentPadding = padding) {
            item { SmallTitle(copy.appearance) }
            item {
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    RadioButtonPreference(
                        title = copy.followSystem,
                        selected = !colorMode.name.contains("Light") && !colorMode.name.contains("Dark"),
                        onClick = { onMode(toneOf(colorMode, "system")) },
                    )
                    RadioButtonPreference(
                        title = copy.light,
                        selected = colorMode.name.contains("Light"),
                        onClick = { onMode(toneOf(colorMode, "light")) },
                    )
                    RadioButtonPreference(
                        title = copy.dark,
                        selected = colorMode.name.contains("Dark"),
                        onClick = { onMode(toneOf(colorMode, "dark")) },
                    )
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
            item {
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    SwitchPreference(
                        checked = monetOn,
                        onCheckedChange = { onMode(monetOf(colorMode, it)) },
                        title = "Monet 取色",
                    )
                    if (monetOn) {
                        Text(
                            if (lang == "en") "Source color" else "主题色",
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                        val presets = listOf(
                            0xFF00A870.toInt() to "青绿",
                            0xFF3482FF.toInt() to "蓝",
                            0xFF00B3A4.toInt() to "青",
                            0xFFFF7A18.toInt() to "橙",
                            0xFFE23B4A.toInt() to "红",
                            0xFFFF4F8B.toInt() to "粉",
                            0xFF34C759.toInt() to "绿",
                        )
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            presets.forEach { (color, _) ->
                                val on = seed == color
                                Box(
                                    Modifier
                                        .size(if (on) 30.dp else 26.dp)
                                        .clip(CircleShape)
                                        .background(Color(color))
                                        .clickable { onSeed(color) },
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
            item { Spacer(Modifier.height(12.dp)) }
            item {
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    ArrowPreference(
                        title = if (lang == "en") "Header background" else if (lang == "zh-TW") "背景欄" else "背景栏",
                        summary = if (lang == "en") "Replace the picture at the top of Mine" else if (lang == "zh-TW") "更換我的頁頂部那張圖" else "更换我的页顶部那张图",
                        onClick = onBanner,
                    )
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

@Composable
private fun GatePane(
    lang: String,
    allowAdult: Boolean,
    onAllowAdult: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val title = when (lang) { "en" -> "Mystery switch"; "zh-TW" -> "神秘開關"; else -> "神秘开关" }
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
                    SwitchPreference(
                        checked = allowAdult,
                        onCheckedChange = onAllowAdult,
                        title = when (lang) { "en" -> "Restricted content (18+)"; "zh-TW" -> "限制級內容（18+）"; else -> "限制级内容（18+）" },
                        summary = when (lang) {
                            "en" -> "Turn on to browse 18+ manga and novel sources"
                            "zh-TW" -> "開啟此功能可瀏覽閱讀18+漫畫和小說源"
                            else -> "开启此功能可浏览阅读18+漫画和小说源"
                        },
                    )
                }
            }
        }
    }
}

private fun toneOf(mode: ColorSchemeMode, tone: String): ColorSchemeMode {
    val monet = mode.name.startsWith("Monet")
    return when (tone) {
        "light" -> if (monet) ColorSchemeMode.MonetLight else ColorSchemeMode.Light
        "dark" -> if (monet) ColorSchemeMode.MonetDark else ColorSchemeMode.Dark
        else -> if (monet) ColorSchemeMode.MonetSystem else ColorSchemeMode.System
    }
}

private fun monetOf(mode: ColorSchemeMode, on: Boolean): ColorSchemeMode {
    val tone = when (mode) {
        ColorSchemeMode.MonetLight, ColorSchemeMode.Light -> "light"
        ColorSchemeMode.MonetDark, ColorSchemeMode.Dark -> "dark"
        else -> "system"
    }
    val forced = when (tone) {
        "light" -> if (on) ColorSchemeMode.MonetLight else ColorSchemeMode.Light
        "dark" -> if (on) ColorSchemeMode.MonetDark else ColorSchemeMode.Dark
        else -> ColorSchemeMode.System
    }
    return if (on && tone == "system") ColorSchemeMode.MonetSystem else forced
}

@Composable
private fun IntroPane(
    copy: Copy,
    onBack: () -> Unit,
    onOss: () -> Unit,
    onNotes: () -> Unit,
) {
    val context = LocalContext.current
    val link = "https://t.me/shumanyu1"
    val body = copy.introBody
    val cut = body.indexOf(link)
    Scaffold(topBar = { SubTopBar(copy.intro, onBack, BarTitle.End) }) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("书漫屿", style = MiuixTheme.textStyles.title2)
                        Text("ShuManYu ${installedVersion(context)}", color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        Spacer(Modifier.height(10.dp))
                        if (cut >= 0) {
                            Text(body.substring(0, cut), fontSize = 14.sp, color = MiuixTheme.colorScheme.onSurfaceSecondary)
                            Text(
                                link,
                                fontSize = 14.sp,
                                color = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    runCatching {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                                        )
                                    }
                                },
                            )
                        } else {
                            Text(body, fontSize = 14.sp, color = MiuixTheme.colorScheme.onSurfaceSecondary)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("项目地址",fontSize=14.sp,color=MiuixTheme.colorScheme.onSurfaceSecondary)
                        Text(
                            "https://github.com/${GitHubUpdates.REPOSITORY}",
                            fontSize=14.sp,
                            color=MiuixTheme.colorScheme.primary,
                            modifier=Modifier.clickable {
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/${GitHubUpdates.REPOSITORY}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                            },
                        )
                    }
                }
            }
            item { SmallTitle("软件功能") }
            item {
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                    ArrowPreference(
                        title = "更新日志",
                        summary = installedVersion(context),
                        onClick = onNotes,
                    )
                    ArrowPreference(
                        title = "开源声明",
                        onClick = onOss,
                    )
                }
            }
            item { Spacer(Modifier.height(28.dp)) }
        }
    }
}

private fun installedVersion(context:Context):String = runCatching {
    context.packageManager.getPackageInfo(context.packageName,0).versionName.orEmpty()
}.getOrDefault("1.0.1")

@Composable
private fun NotesPane(lang: String, onBack: () -> Unit) {
    val context=LocalContext.current
    val title=if(lang=="en")"What's new" else "更新日志"
    val changes=listOf(
        "修复漫画裁边误放大、白色区域误删和长图显示不完整的问题。",
        "修复小说阅读进度恢复、切章位置和分类切换；正文及列表加载失败支持重试。",
        "新增速读谷、阅友小说小说源，以及 GitHub 自动更新提示和项目地址入口。",
        "优化上千章目录及分类分页加载，滑近底部自动加载更多，过滤失效小说链接，移除不可用及仍需验证的书源。",
        "优化右上角功能菜单，取消动画并减少多余空白，恢复“切换小说/漫画源”，统一“网站管理”名称。",
        "精简主题设置，保留Monet取色开关及主题色，移除配色风格、自定义取色、软件图标和进度条颜色设置。",
        "移除网站管理中的“还原内置源”入口及对应弹窗。",
        "删除一些无用功能代码。"
    )
    Scaffold(topBar={SubTopBar(title,onBack,BarTitle.End)}) { padding ->
        LazyColumn(contentPadding=padding) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                        Text("版本 ${installedVersion(context)}",color=MiuixTheme.colorScheme.onSurfaceVariantSummary)
                        Text("更新内容",style=MiuixTheme.textStyles.title2)
                        changes.forEachIndexed { index,text-> Text("${index+1}.$text",fontSize=14.sp,lineHeight=22.sp) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagePane(copy: Copy, lang: String, onLang: (String) -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = { SubTopBar(copy.language, onBack, BarTitle.End) }) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
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
    crop: Boolean = false,
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
    var display by remember(bmp,crop) { mutableStateOf(if(crop)null else bmp) }
    LaunchedEffect(bmp,crop) {
        val original=bmp
        display=if(original==null || !crop)original else withContext(Dispatchers.Default) {
            val rect=WhiteEdgeBounds.find(original.width,original.height,object:WhiteEdgeBounds.Lines {
                override fun row(y:Int,out:IntArray){original.getPixels(out,0,original.width,0,y,original.width,1)}
                override fun column(x:Int,out:IntArray){original.getPixels(out,0,1,x,0,1,original.height)}
            })
            if(rect[0]==0 && rect[1]==0 && rect[2]==original.width && rect[3]==original.height)original
            else android.graphics.Bitmap.createBitmap(original,rect[0],rect[1],rect[2]-rect[0],rect[3]-rect[1])
        }
    }
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

private val coverRam = object : android.util.LruCache<String, android.graphics.Bitmap>(16 * 1024) {
    override fun sizeOf(key: String, value: android.graphics.Bitmap): Int = (value.byteCount / 1024).coerceAtLeast(1)
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

private fun decodeCoverAsset(context: Context, name: String, maxPx: Int = 640): android.graphics.Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.assets.open("covers/$name").use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    val opts = BitmapFactory.Options().apply { inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight, maxPx) }
    val raw = context.assets.open("covers/$name").use { BitmapFactory.decodeStream(it, null, opts) } ?: return null
    return shrinkCover(raw, maxPx)
}

@Composable
private fun SmartCover(
    src: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    plate: Boolean = false,
    onReady: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var bmp by remember(src) { mutableStateOf(coverRam.get(src) ?: Crawler.peekCover(src)) }
    LaunchedEffect(src) {
        val hit = coverRam.get(src) ?: Crawler.peekCover(src)
        if (hit != null) {
            bmp = hit
            return@LaunchedEffect
        }
        val decoded = withContext(Dispatchers.IO) {
            val remote = when {
                src.startsWith("http://") || src.startsWith("https://") -> src
                src.startsWith("//") -> "https:$src"
                src.startsWith("/files/") || src.startsWith("/style/") -> "https://www.bqg5.com$src"
                else -> ""
            }
            val path = when {
                remote.isNotEmpty() -> ""
                src.startsWith("file:") -> src.removePrefix("file://")
                src.startsWith("/") -> src
                else -> ""
            }
            when {
                remote.isNotEmpty() -> Crawler.awaitCover(remote)
                path.isNotEmpty() -> decodeCoverFile(path, 480)
                src.isNotBlank() -> decodeCoverAsset(context.applicationContext, src, 480)
                else -> null
            }
        }
        if (decoded != null) {
            coverRam.put(src, decoded)
            bmp = decoded
        }
    }
    val shot = remember(bmp) { bmp?.asImageBitmap() }
    if (shot != null) {
        Image(bitmap = shot, contentDescription = null, modifier = modifier, contentScale = contentScale)
        if (onReady != null) SideEffect { onReady() }
    } else if (!plate) {
        Box(modifier.background(MiuixTheme.colorScheme.surface))
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
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onCover: (String) -> Unit,
    onDelete: (String) -> Unit,
    onCreate: (String) -> Unit,
) {
    var drop by remember { mutableStateOf<String?>(null) }
    var creating by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }
    val title = if (lang == "en") "Collections" else if (lang == "zh-TW") "收藏夾" else "收藏夹"
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
                    }) { Icon(Icons.Plus, contentDescription = createTitle) }
                },
            )
        },
    ) { padding ->
        if (folders.isEmpty()) {
            EmptyFace(
                "(´・ω・`)",
                if (lang == "en") "No collections yet.\nTap + to create one." else "还没有收藏夹。\n点右上角 + 新建。",
                Modifier.fillMaxSize().padding(padding),
            )
        } else {
            LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                items(folders, key = { it.id }) { folder ->
                    Card(Modifier.padding(horizontal = 12.dp, vertical = 6.dp).fillMaxWidth()) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .combinedClickable(
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
                                    color = MiuixTheme.colorScheme.onSurface,
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
                                                .clickable { onCover(id) },
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
        }
    }
    if (creating) {
        OverlayScrim(onDismiss = { creating = false }) {
            Card(Modifier.padding(24.dp).fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(createTitle, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    TextField(
                        value = draft,
                        onValueChange = { draft = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = nameLabel,
                    )
                    Button(
                        onClick = {
                            if (draft.trim().isNotBlank()) {
                                onCreate(draft)
                                creating = false
                            }
                        },
                        enabled = draft.trim().isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColorsPrimary(),
                    ) { Text(createWord) }
                }
            }
        }
    }
    val id = drop
    if (id != null) {
        ConfirmSheet(
            title = if (lang == "en") "Delete folder?" else if (lang == "zh-TW") "刪除收藏夾？" else "删除收藏夹？",
            message = if (lang == "en") "The books stay on the shelf." else "只删除收藏夹，漫画仍会留在书架。",
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
private fun OssPane(lang: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val title = if (lang == "en") "Open source" else if (lang == "zh-TW") "開源聲明" else "开源声明"
    val libs = listOf(
        Triple("Miuix", "Apache-2.0", "https://github.com/compose-miuix-ui/miuix"),
        Triple("Jetpack Compose", "Apache-2.0", "https://developer.android.com/jetpack/compose"),
        Triple("AndroidX", "Apache-2.0", "https://developer.android.com/jetpack/androidx"),
        Triple("Kotlin", "Apache-2.0", "https://kotlinlang.org"),
        Triple("Kotlin Coroutines", "Apache-2.0", "https://github.com/Kotlin/kotlinx.coroutines"),
    )
    Scaffold(
        topBar = { SubTopBar(title, onBack, BarTitle.End) },
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
                    libs.forEach { (name, license, url) ->
                        ArrowPreference(title = name, summary = license, onClick = {
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                        })
                    }
                }
            }
        }
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
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                Card(Modifier.padding(12.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(if (lang == "en") "Sample" else "示范", fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
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
                Card(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
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

private object Icons {
    val Book: ImageVector = ImageVector.Builder("book", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(6f, 5f); lineTo(11f, 5f); curveTo(12.2f, 5f, 13f, 5.8f, 13f, 7f); lineTo(13f, 20f); lineTo(7f, 18.5f); curveTo(6.4f, 18.3f, 6f, 17.8f, 6f, 17.2f); close()
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(18f, 5f); lineTo(13f, 5f); lineTo(13f, 20f); lineTo(17f, 18.5f); curveTo(17.6f, 18.3f, 18f, 17.8f, 18f, 17.2f); lineTo(18f, 5f); close()
        }
    }.build()

    val Clock: ImageVector = ImageVector.Builder("clock", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(12f, 3.5f)
            curveTo(16.7f, 3.5f, 20.5f, 7.3f, 20.5f, 12f)
            curveTo(20.5f, 16.7f, 16.7f, 20.5f, 12f, 20.5f)
            curveTo(7.3f, 20.5f, 3.5f, 16.7f, 3.5f, 12f)
            curveTo(3.5f, 7.3f, 7.3f, 3.5f, 12f, 3.5f)
            close()
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(12f, 7f); lineTo(12f, 12.5f); lineTo(15.5f, 14.5f)
        }
    }.build()

    val Grid: ImageVector = ImageVector.Builder("grid", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(4.5f, 4.5f); lineTo(10f, 4.5f); lineTo(10f, 10f); lineTo(4.5f, 10f); close()
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(14f, 4.5f); lineTo(19.5f, 4.5f); lineTo(19.5f, 10f); lineTo(14f, 10f); close()
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(4.5f, 14f); lineTo(10f, 14f); lineTo(10f, 19.5f); lineTo(4.5f, 19.5f); close()
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(14f, 14f); lineTo(19.5f, 14f); lineTo(19.5f, 19.5f); lineTo(14f, 19.5f); close()
        }
    }.build()

    val Download: ImageVector = ImageVector.Builder("download", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(12f, 4f); lineTo(12f, 14f)
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(7.5f, 10.5f); lineTo(12f, 15f); lineTo(16.5f, 10.5f)
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(5.5f, 19f); lineTo(18.5f, 19f)
        }
    }.build()

    val Globe: ImageVector = ImageVector.Builder("globe", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(12f, 3.5f)
            curveTo(16.7f, 3.5f, 20.5f, 7.3f, 20.5f, 12f)
            curveTo(20.5f, 16.7f, 16.7f, 20.5f, 12f, 20.5f)
            curveTo(7.3f, 20.5f, 3.5f, 16.7f, 3.5f, 12f)
            curveTo(3.5f, 7.3f, 7.3f, 3.5f, 12f, 3.5f)
            close()
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(3.8f, 12f); lineTo(20.2f, 12f)
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(12f, 3.5f)
            curveTo(14.2f, 6.2f, 15.5f, 9f, 15.5f, 12f)
            curveTo(15.5f, 15f, 14.2f, 17.8f, 12f, 20.5f)
            curveTo(9.8f, 17.8f, 8.5f, 15f, 8.5f, 12f)
            curveTo(8.5f, 9f, 9.8f, 6.2f, 12f, 3.5f)
            close()
        }
    }.build()

    val Store: ImageVector = ImageVector.Builder("store", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(7f, 4.2f)
            lineTo(17f, 4.2f)
            curveTo(17.9f, 4.2f, 18.6f, 4.9f, 18.6f, 5.8f)
            lineTo(18.6f, 19f)
            curveTo(18.6f, 19.9f, 17.9f, 20.6f, 17f, 20.6f)
            lineTo(7f, 20.6f)
            curveTo(6.1f, 20.6f, 5.4f, 19.9f, 5.4f, 19f)
            lineTo(5.4f, 5.8f)
            curveTo(5.4f, 4.9f, 6.1f, 4.2f, 7f, 4.2f)
            close()
        }
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.White)) {
            moveTo(10.1f, 4.2f)
            lineTo(13.9f, 4.2f)
            lineTo(13.9f, 9.4f)
            lineTo(12f, 7.7f)
            lineTo(10.1f, 9.4f)
            close()
        }
    }.build()

    val User: ImageVector = ImageVector.Builder("user", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(12f, 12f)
            curveTo(14.2f, 12f, 16f, 10.2f, 16f, 8f)
            curveTo(16f, 5.8f, 14.2f, 4f, 12f, 4f)
            curveTo(9.8f, 4f, 8f, 5.8f, 8f, 8f)
            curveTo(8f, 10.2f, 9.8f, 12f, 12f, 12f)
            close()
        }
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(5.5f, 20f)
            curveTo(5.5f, 16.4f, 8.4f, 13.5f, 12f, 13.5f)
            curveTo(15.6f, 13.5f, 18.5f, 16.4f, 18.5f, 20f)
        }
    }.build()

    val Back: ImageVector = ImageVector.Builder("back", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f) {
            moveTo(15f, 5f); lineTo(7f, 12f); lineTo(15f, 19f)
        }
    }.build()

    private val stroke = androidx.compose.ui.graphics.SolidColor(Color.White)

    val Plus: ImageVector = ImageVector.Builder("plus", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(12f, 5f); lineTo(12f, 19f)
        }
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(5f, 12f); lineTo(19f, 12f)
        }
    }.build()

    val Bookmark: ImageVector = ImageVector.Builder("bookmark", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(7f, 4f); lineTo(17f, 4f); lineTo(17f, 20f); lineTo(12f, 16.2f); lineTo(7f, 20f); close()
        }
    }.build()

    val BookmarkOn: ImageVector = ImageVector.Builder("bookmarkOn", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = stroke) {
            moveTo(7f, 4f); lineTo(17f, 4f); lineTo(17f, 20f); lineTo(12f, 16.2f); lineTo(7f, 20f); close()
        }
    }.build()

    val SkipPrev: ImageVector = ImageVector.Builder("skipPrev", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(7f, 6f); lineTo(7f, 18f)
            moveTo(17f, 6.5f); lineTo(10f, 12f); lineTo(17f, 17.5f)
        }
    }.build()

    val SkipNext: ImageVector = ImageVector.Builder("skipNext", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(17f, 6f); lineTo(17f, 18f)
            moveTo(7f, 6.5f); lineTo(14f, 12f); lineTo(7f, 17.5f)
        }
    }.build()

    val Reader: ImageVector = ImageVector.Builder("reader", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(8f, 3.5f); lineTo(16f, 3.5f); curveTo(17f, 3.5f, 17.8f, 4.3f, 17.8f, 5.3f)
            lineTo(17.8f, 18.7f); curveTo(17.8f, 19.7f, 17f, 20.5f, 16f, 20.5f); lineTo(8f, 20.5f)
            curveTo(7f, 20.5f, 6.2f, 19.7f, 6.2f, 18.7f); lineTo(6.2f, 5.3f); curveTo(6.2f, 4.3f, 7f, 3.5f, 8f, 3.5f); close()
            moveTo(9f, 8f); lineTo(15f, 8f)
            moveTo(9f, 12f); lineTo(15f, 12f)
        }
    }.build()

    val Rotate: ImageVector = ImageVector.Builder("rotate", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(20f, 12f)
            curveTo(20f, 7.6f, 16.4f, 4f, 12f, 4f)
            curveTo(8.8f, 4f, 6f, 5.8f, 4.6f, 8.4f)
            moveTo(4f, 5f); lineTo(4.6f, 8.6f); lineTo(8.2f, 8f)
            moveTo(4f, 12f)
            curveTo(4f, 16.4f, 7.6f, 20f, 12f, 20f)
            curveTo(15.2f, 20f, 18f, 18.2f, 19.4f, 15.6f)
            moveTo(20f, 19f); lineTo(19.4f, 15.4f); lineTo(15.8f, 16f)
        }
    }.build()

    val Crop: ImageVector = ImageVector.Builder("crop", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(7f, 3.5f); lineTo(7f, 17f); lineTo(20.5f, 17f)
            moveTo(17f, 20.5f); lineTo(17f, 7f); lineTo(3.5f, 7f)
        }
    }.build()

    val Gear: ImageVector = ImageVector.Builder("gear", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(12f, 8.2f)
            curveTo(14.1f, 8.2f, 15.8f, 9.9f, 15.8f, 12f)
            curveTo(15.8f, 14.1f, 14.1f, 15.8f, 12f, 15.8f)
            curveTo(9.9f, 15.8f, 8.2f, 14.1f, 8.2f, 12f)
            curveTo(8.2f, 9.9f, 9.9f, 8.2f, 12f, 8.2f)
            close()
            moveTo(12f, 3.5f); lineTo(13.1f, 6f); lineTo(10.9f, 6f); close()
            moveTo(12f, 20.5f); lineTo(10.9f, 18f); lineTo(13.1f, 18f); close()
            moveTo(3.5f, 12f); lineTo(6f, 10.9f); lineTo(6f, 13.1f); close()
            moveTo(20.5f, 12f); lineTo(18f, 13.1f); lineTo(18f, 10.9f); close()
        }
    }.build()

    val Trash: ImageVector = ImageVector.Builder("trash", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(5f, 7f); lineTo(19f, 7f)
            moveTo(9.2f, 7f); lineTo(9.8f, 4.6f); lineTo(14.2f, 4.6f); lineTo(14.8f, 7f)
            moveTo(7.2f, 7.2f); lineTo(8.2f, 19.4f); lineTo(15.8f, 19.4f); lineTo(16.8f, 7.2f)
            moveTo(10.6f, 10.4f); lineTo(10.9f, 16.2f)
            moveTo(13.4f, 10.4f); lineTo(13.1f, 16.2f)
        }
    }.build()

    val Filter: ImageVector = ImageVector.Builder("filter", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = stroke, strokeLineWidth = 1.8f) {
            moveTo(4f, 7f); lineTo(20f, 7f)
            moveTo(4f, 12f); lineTo(20f, 12f)
            moveTo(4f, 17f); lineTo(20f, 17f)
        }
        path(fill = stroke) {
            moveTo(8f, 7f); curveTo(8f, 8.1f, 7.1f, 9f, 6f, 9f); curveTo(4.9f, 9f, 4f, 8.1f, 4f, 7f); curveTo(4f, 5.9f, 4.9f, 5f, 6f, 5f); curveTo(7.1f, 5f, 8f, 5.9f, 8f, 7f); close()
            moveTo(16f, 12f); curveTo(16f, 13.1f, 15.1f, 14f, 14f, 14f); curveTo(12.9f, 14f, 12f, 13.1f, 12f, 12f); curveTo(12f, 10.9f, 12.9f, 10f, 14f, 10f); curveTo(15.1f, 10f, 16f, 10.9f, 16f, 12f); close()
            moveTo(11f, 17f); curveTo(11f, 18.1f, 10.1f, 19f, 9f, 19f); curveTo(7.9f, 19f, 7f, 18.1f, 7f, 17f); curveTo(7f, 15.9f, 7.9f, 15f, 9f, 15f); curveTo(10.1f, 15f, 11f, 15.9f, 11f, 17f); close()
        }
    }.build()
}
