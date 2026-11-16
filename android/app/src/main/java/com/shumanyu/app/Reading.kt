package com.shumanyu.app

import com.itos.coui.scroll.couiVerticalScroll

import com.itos.coui.scroll.rememberCouiOverscrollEffect

import com.itos.coui.layout.Group

import androidx.activity.compose.BackHandler
import com.itos.coui.dialog.CouiDialogAction
import com.itos.coui.dialog.CouiDialogActionStyle
import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.dialog.CouiDialogPlacement
import com.itos.coui.theme.CouiTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ReadingPrefs(
    val readMode: String = "webtoon",
    val tapAnim: String = "normal",
    val modeHint: Boolean = false,
    val navOverlay: Boolean = false,
    val pageAnim: Boolean = true,
    val rotation: String = "portrait",
    val bgColor: String = "black",
    val fullscreen: Boolean = true,
    val keepScreenOn: Boolean = true,
    val showPageNum: Boolean = true,
    val skipRead: Boolean = false,
    val chapterTransition: Boolean = true,
    val navLayout: String = "default",
    val reverseTap: String = "none",
    val zoomMode: String = "fit",
    val zoomPos: String = "auto",
    val webtoonNav: String = "default",
    val webtoonReverseTap: String = "none",
    val sidePad: String = "none",
    val hideSens: String = "low",
    val tapTurn: Boolean = true,
    val volumeTurn: Boolean = false,
    val savePageFolder: Boolean = false,
    val filterOn: Boolean = false,
    val filterR: Float = 0f,
    val filterG: Float = 0f,
    val filterB: Float = 0f,
    val filterA: Float = 0f,
    val filterBlend: String = "default",
    val grayscale: Boolean = false,
    val invert: Boolean = false,
    val brightnessOn: Boolean = false,
    val brightness: Float = 0f,
)

data class BackupData(
    val library: List<String>,
    val favorites: List<String>,
    val history: List<String>,
)

data class More(
    val reading: String,
    val createBackup: String,
    val createBackupSum: String,
    val restoreData: String,
    val restoreDataSum: String,
    val refreshing: String,
    val clearAllHistory: String,
    val batchDeleteHistory: String,
    val confirm: String,
    val delete: String,
    val confirmClear: String,
    val confirmBatch: String,
    val readMode: String,
    val tapAnim: String,
    val modeHint: String,
    val modeHintSum: String,
    val navOverlay: String,
    val navOverlaySum: String,
    val pageAnim: String,
    val display: String,
    val rotation: String,
    val bgColor: String,
    val fullscreen: String,
    val keepScreenOn: String,
    val showPageNum: String,
    val readSection: String,
    val skipRead: String,
    val chapterTransition: String,
    val paged: String,
    val navLayout: String,
    val reverseTap: String,
    val zoomMode: String,
    val zoomPos: String,
    val webtoon: String,
    val sidePad: String,
    val hideSens: String,
    val actions: String,
    val tapTurn: String,
    val volumeTurn: String,
    val savePageFolder: String,
    val savePageFolderSum: String,
    val ltr: String,
    val rtl: String,
    val ttb: String,
    val webtoonMode: String,
    val ttbSlide: String,
    val ltrSlide: String,
    val rtlSlide: String,
    val animNormal: String,
    val rotAuto: String,
    val bgBlack: String,
    val bgWhite: String,
    val bgGray: String,
    val none: String,
    val fill: String,
    val fit: String,
    val low: String,
    val done: String,
)

fun moreOf(lang: String) = when (lang) {
    "zh-TW" -> More(
        "閱讀", "建立備份", "儲存為 zip，可用於還原", "還原備份", "從 zip 備份中還原", "正在重新整理…",
        "清空歷史", "批量刪除", "確定", "刪除", "確定清空所有歷史？",
        "確定刪除所選歷史？", "預設閱讀模式", "雙擊動畫速度", "閱讀模式提示", "開始閱讀時，短暫顯示當前閱讀模式", "顯示導航佈局覆蓋層",
        "開啟閱讀器時顯示點按區域", "頁面過渡動畫", "顯示", "預設旋轉類型", "背景顏色", "全螢幕",
        "保持螢幕常亮", "顯示頁碼", "閱讀", "跳過已讀章節", "總是顯示章節間的過渡頁面", "分頁",
        "導航佈局", "反轉輕觸方向", "縮放模式", "縮放位置", "Webtoon 模式",
        "側邊填充", "設定滾動時自動隱藏選單的靈敏度", "操作", "輕觸翻頁", "音量鍵翻頁", "儲存頁面到單獨資料夾",
        "根據漫畫標題建立資料夾", "從左到右", "從右到左", "從上到下", "Webtoon 模式", "從上到下（滑動）",
        "從左到右（滑動）", "從右到左（滑動）", "正常", "自動", "黑色", "白色",
        "灰色", "無", "填充螢幕", "適合螢幕", "低", "完成",
    )
    "en" -> More(
        "Reading", "Create backup", "Save a zip you can restore", "Restore backup", "Restore from a zip backup", "Refreshing…",
        "Clear all history", "Batch delete", "OK", "Delete", "Clear all history?",
        "Delete selected history?", "Default reading mode", "Double-tap animation", "Mode hint", "Briefly show the current mode when reading starts", "Show nav overlay",
        "Show tap zones when the reader opens", "Page transition", "Display", "Default rotation", "Background", "Fullscreen",
        "Keep screen on", "Show page number", "Reading", "Skip read chapters", "Always show chapter transition pages", "Paged",
        "Nav layout", "Reverse tap", "Zoom mode", "Zoom position", "Webtoon",
        "Side padding", "Auto-hide menu sensitivity while scrolling", "Controls", "Tap to turn", "Volume keys turn pages", "Save pages to a folder",
        "Folder named after the title", "Left to right", "Right to left", "Top to bottom", "Webtoon", "Top to bottom (slide)",
        "Left to right (slide)", "Right to left (slide)", "Normal", "Auto", "Black", "White",
        "Gray", "None", "Fill screen", "Fit screen", "Low", "Done",
    )
    else -> More(
        "阅读", "创建备份", "保存为 zip，可用于还原", "恢复备份", "从 zip 备份中还原", "正在刷新...",
        "清空历史", "批量删除", "确定", "删除", "确定清空所有历史？",
        "确定删除所选历史？", "预设阅读模式", "双击动画速度", "阅读模式提示", "开始阅读时，短暂显示当前阅读模式", "显示导航布局覆盖层",
        "开启阅读器时显示点按区域", "页面过渡动画", "显示", "预设旋转类型", "背景颜色", "全屏",
        "保持屏幕常亮", "显示页码", "阅读", "跳过已读章节", "总是显示章节间的过渡页面", "分页",
        "导航布局", "反转轻触方向", "缩放模式", "缩放位置", "Webtoon 模式",
        "侧边填充", "设定滚动时自动隐藏选单的灵敏度", "操作", "轻触翻页", "音量键翻页", "储存页面到单独资料夹",
        "根据漫画标题建立资料夹", "从左到右", "从右到左", "从上到下", "Webtoon 模式", "从上到下（滑动）",
        "从左到右（滑动）", "从右到左（滑动）", "正常", "自动", "黑色", "白色",
        "灰色", "无", "填充屏幕", "适合屏幕", "低", "完成",
    )
}

@Composable
fun ConfirmSheet(
    title: String,
    message: String,
    confirm: String,
    cancel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    /** 底部额外抬高（在系统导航栏边距之外），阅读页用来让开页码。 */
    lift: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(0f),
) {
    val accent = CouiTheme.colorScheme.accent
    Dialog(
        modifier = androidx.compose.ui.Modifier.offset(y = -lift),
        visible = true,
        onDismissRequest = onDismiss,
        title = title,
        message = message,
        confirmAction = CouiDialogAction(
            text = confirm,
            onClick = onConfirm,
            style = CouiDialogActionStyle.Primary,
            colors = CouiDialogDefaults.primaryActionColors(accent),
        ),
        dismissAction = CouiDialogAction(text = cancel, onClick = onDismiss),
        placement = CouiDialogPlacement.Bottom,
    )
}

@Composable
fun ReadingPane(prefs: Prefs, lang: String, onBack: () -> Unit) {
    val more = remember(lang) { moreOf(lang) }
    var r by remember { mutableStateOf(prefs.reading()) }
    fun save(next: ReadingPrefs) {
        r = next
        prefs.setReading(next)
    }
    var pick by remember { mutableStateOf<String?>(null) }
    val preset = if (lang == "en") "Default" else if (lang == "zh-TW") "預設" else "默认"
    fun named(id: String, vararg opts: Pair<String, String>) = opts.find { it.first == id }?.second ?: preset
    Box(Modifier.fillMaxSize()) {
        Scaffold(topBar = { SubTopBar(more.reading, onBack, BarTitle.End) }) { padding ->
            LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
                item {
                    Group {
                        SwitchPreference(checked = r.navOverlay, onCheckedChange = { save(r.copy(navOverlay = it)) }, title = more.navOverlay, summary = more.navOverlaySum)
                        SwitchPreference(checked = r.pageAnim, onCheckedChange = { save(r.copy(pageAnim = it)) }, title = more.pageAnim)
                    }
                }
                item {
                    Group(title = more.display) {
                        ArrowPreference(more.rotation, summary = named(r.rotation, "auto" to more.rotAuto, "portrait" to if (lang == "en") "Portrait" else "竖屏", "landscape" to if (lang == "en") "Landscape" else "横屏"), onClick = { pick = "rot" })
                        ArrowPreference(
                            more.bgColor,
                            summary = when (r.bgColor) {
                                "white" -> more.bgWhite
                                "gray" -> more.bgGray
                                else -> more.bgBlack
                            },
                            onClick = { pick = "bg" },
                        )
                        SwitchPreference(checked = r.fullscreen, onCheckedChange = { save(r.copy(fullscreen = it)) }, title = more.fullscreen)
                        SwitchPreference(checked = r.keepScreenOn, onCheckedChange = { save(r.copy(keepScreenOn = it)) }, title = more.keepScreenOn)
                        SwitchPreference(checked = r.showPageNum, onCheckedChange = { save(r.copy(showPageNum = it)) }, title = more.showPageNum)
                    }
                }
                item {
                    Group(title = more.readSection) {
                        SwitchPreference(checked = r.skipRead, onCheckedChange = { save(r.copy(skipRead = it)) }, title = more.skipRead)
                        SwitchPreference(checked = r.chapterTransition, onCheckedChange = { save(r.copy(chapterTransition = it)) }, title = more.chapterTransition)
                    }
                }
                item {
                    Group(title = more.paged) {
                        ArrowPreference(more.navLayout, summary = named(r.navLayout, "default" to preset, "l" to "L", "edge" to if (lang == "en") "Edges" else "边缘"), onClick = { pick = "nav" })
                        SwitchPreference(checked = r.reverseTap != "none", onCheckedChange = { save(r.copy(reverseTap = if (it) "h" else "none")) }, title = more.reverseTap)
                        ArrowPreference(more.zoomMode, summary = named(r.zoomMode, "fill" to more.fill, "fit" to more.fit), onClick = { pick = "zoom" })
                        ArrowPreference(more.zoomPos, summary = named(r.zoomPos, "auto" to more.rotAuto, "top" to if (lang == "en") "Top" else "顶部", "center" to if (lang == "en") "Center" else "居中"), onClick = { pick = "zpos" })
                    }
                }
                item {
                    Group(title = more.webtoon) {
                        ArrowPreference(more.navLayout, summary = named(r.webtoonNav, "default" to preset, "edge" to if (lang == "en") "Edges" else "边缘"), onClick = { pick = "wnav" })
                        SwitchPreference(checked = r.webtoonReverseTap != "none", onCheckedChange = { save(r.copy(webtoonReverseTap = if (it) "v" else "none")) }, title = more.reverseTap)
                        ArrowPreference(more.sidePad, summary = named(r.sidePad, "none" to more.none, "15" to "15%", "s" to if (lang == "en") "Small" else "小", "m" to if (lang == "en") "Medium" else "中"), onClick = { pick = "pad" })
                        ArrowPreference(more.hideSens, summary = named(r.hideSens, "low" to more.low, "mid" to if (lang == "en") "Medium" else "中", "high" to if (lang == "en") "High" else "高"), onClick = { pick = "sens" })
                    }
                }
                item {
                    Group(title = more.actions) {
                        SwitchPreference(checked = r.tapTurn, onCheckedChange = { save(r.copy(tapTurn = it)) }, title = more.tapTurn)
                        SwitchPreference(checked = r.volumeTurn, onCheckedChange = { save(r.copy(volumeTurn = it)) }, title = more.volumeTurn)
                        SwitchPreference(checked = r.savePageFolder, onCheckedChange = { save(r.copy(savePageFolder = it)) }, title = more.savePageFolder, summary = more.savePageFolderSum)
                    }
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
        when (pick) {
            "rot" -> CouiChoiceDialog(more.rotation, listOf("auto" to more.rotAuto, "portrait" to "竖屏", "landscape" to "横屏"), r.rotation, onPick = { save(r.copy(rotation = it)) }, onDismiss = { pick = null })
            "bg" -> CouiChoiceDialog(more.bgColor, listOf("black" to more.bgBlack, "white" to more.bgWhite, "gray" to more.bgGray), r.bgColor, onPick = { save(r.copy(bgColor = it)) }, onDismiss = { pick = null })
            "nav" -> CouiChoiceDialog(more.navLayout, listOf("default" to preset, "l" to "L 形", "edge" to "边缘"), r.navLayout, onPick = { save(r.copy(navLayout = it)) }, onDismiss = { pick = null })
            "zoom" -> CouiChoiceDialog(more.zoomMode, listOf("fill" to more.fill, "fit" to more.fit), r.zoomMode, onPick = { save(r.copy(zoomMode = it)) }, onDismiss = { pick = null })
            "zpos" -> CouiChoiceDialog(more.zoomPos, listOf("auto" to more.rotAuto, "top" to "顶部", "center" to "居中"), r.zoomPos, onPick = { save(r.copy(zoomPos = it)) }, onDismiss = { pick = null })
            "wnav" -> CouiChoiceDialog(more.navLayout, listOf("default" to preset, "edge" to "边缘"), r.webtoonNav, onPick = { save(r.copy(webtoonNav = it)) }, onDismiss = { pick = null })
            "pad" -> CouiChoiceDialog(more.sidePad, listOf("none" to more.none, "15" to "15%", "s" to "小", "m" to "中"), r.sidePad, onPick = { save(r.copy(sidePad = it)) }, onDismiss = { pick = null })
            "sens" -> CouiChoiceDialog(more.hideSens, listOf("low" to more.low, "mid" to "中", "high" to "高"), r.hideSens, onPick = { save(r.copy(hideSens = it)) }, onDismiss = { pick = null })
        }
    }
}

