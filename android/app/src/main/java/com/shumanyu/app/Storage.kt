package com.shumanyu.app

import com.itos.coui.scroll.rememberCouiOverscrollEffect

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import kotlinx.coroutines.launch

@Composable
fun StoragePane(lang: String, keep: Set<String>, onBack: () -> Unit) {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    var askClear by remember { mutableStateOf(false) }
    var askDown by remember { mutableStateOf(false) }
    var clearing by remember { mutableStateOf(false) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    // 目录遍历放到 IO 线程：缓存多时（外部存储走 FUSE）在主线程统计/删除会卡住界面直至被系统杀掉。
    val sizes by androidx.compose.runtime.produceState(0L to 0L, tick) {
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            runCatching { LocalStore.clearableBytes(context, keep) to LocalStore.downloadBytes(context) }.getOrDefault(0L to 0L)
        }
    }
    val (clearable, downBytes) = sizes
    val cachePath = LocalStore.cachePath().ifBlank { LocalStore.defaultCache(context).absolutePath }
    val downPath = LocalStore.downloadRoot(context).absolutePath
    val pickCache = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) adoptTree(context, uri, download = false, onOk = { tick++ }, onNeedAllFiles = { openAllFilesSettings(context) })
    }
    val pickDown = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) adoptTree(context, uri, download = true, onOk = { tick++ }, onNeedAllFiles = { openAllFilesSettings(context) })
    }
    val title = when (lang) { "en" -> "Storage"; "zh-TW" -> "儲存空間"; else -> "存储空间" }
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
        LazyColumn(overscrollEffect = rememberCouiOverscrollEffect(), contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                StorageCard(
                    title = when (lang) { "en" -> "Cache"; "zh-TW" -> "快取"; else -> "缓存" },
                    bytes = clearable,
                    summary = when (lang) {
                        "en" -> "Images, store lists, temporary and half-downloaded files. Shelf, history and favorites are kept."
                        "zh-TW" -> "圖片快取、書城列表、暫存檔和未下載完的檔案。書架、歷史和收藏不受影響。"
                        else -> "图片缓存、书城列表、临时文件和未下完的文件。书架、历史和收藏不受影响。"
                    },
                    action = when (lang) { "en" -> "Clear"; "zh-TW" -> "清理"; else -> "清理" },
                    onAction = { askClear = true },
                )
            }
            item {
                StorageCard(
                    title = when (lang) { "en" -> "Downloads"; "zh-TW" -> "下載"; else -> "下载" },
                    bytes = downBytes,
                    summary = when (lang) {
                        "en" -> "Downloaded comics and novels. You'll need to download them again after clearing. Shelf, history and favorites are kept."
                        "zh-TW" -> "已下載的漫畫或小說，清理後需重新下載。書架、歷史和收藏不受影響。"
                        else -> "已下载的漫画或小说，清理后需重新下载。书架、历史和收藏不受影响。"
                    },
                    action = when (lang) { "en" -> "Clear"; "zh-TW" -> "清理"; else -> "清理" },
                    onAction = { askDown = true },
                )
            }
            item {
                Card(Modifier.padding(horizontal = 12.dp, vertical = 6.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(when (lang) { "en" -> "Folders"; "zh-TW" -> "目錄位置"; else -> "目录位置" }, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        PathRow(
                            label = when (lang) { "en" -> "Cache"; "zh-TW" -> "緩存目錄"; else -> "缓存目录" },
                            path = cachePath,
                            change = when (lang) { "en" -> "Change"; "zh-TW" -> "更改"; else -> "更改" },
                            onCopy = { copyPath(context, cachePath) },
                            onChange = { pickCache.launch(null) },
                        )
                        PathRow(
                            label = when (lang) { "en" -> "Downloads"; "zh-TW" -> "下載目錄"; else -> "下载目录" },
                            path = downPath,
                            change = when (lang) { "en" -> "Change"; "zh-TW" -> "更改"; else -> "更改" },
                            onCopy = { copyPath(context, downPath) },
                            onChange = { pickDown.launch(null) },
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
        if (askClear) {
            ConfirmSheet(
                title = when (lang) { "en" -> "Clear cache?"; "zh-TW" -> "確定清理快取嗎"; else -> "确定清理缓存吗" },
                message = formatBytes(clearable),
                confirm = when (lang) { "en" -> "Clear"; "zh-TW" -> "清理"; else -> "清理" },
                cancel = when (lang) { "en" -> "Cancel"; "zh-TW" -> "取消"; else -> "取消" },
                onConfirm = {
                    // 先关弹窗，再在后台线程清理；弹窗退场动画结束后才刷新数字、弹轻提示，
                    // 不让轻提示的背景采样和弹窗退场挤在同一帧。
                    askClear = false
                    if (!clearing) {
                        clearing = true
                        val app = context.applicationContext
                        scope.launch {
                            val started = android.os.SystemClock.uptimeMillis()
                            val left = LocalStore.clearCacheAsync(app, keep)
                            val wait = 450L - (android.os.SystemClock.uptimeMillis() - started)
                            if (wait > 0) kotlinx.coroutines.delay(wait)
                            clearing = false
                            tick++
                            CouiToast.show((when (lang) { "en" -> "Cleared, "; "zh-TW" -> "已清理，剩餘 "; else -> "已清理，剩余 " }) + formatBytes(left))
                        }
                    }
                },
                onDismiss = { askClear = false },
            )
        }
        if (askDown) {
            ConfirmSheet(
                title = when (lang) { "en" -> "Clear downloads?"; "zh-TW" -> "確定清理下載嗎"; else -> "确定清理下载吗" },
                message = formatBytes(downBytes),
                confirm = when (lang) { "en" -> "Clear"; "zh-TW" -> "清理"; else -> "清理" },
                cancel = when (lang) { "en" -> "Cancel"; "zh-TW" -> "取消"; else -> "取消" },
                onConfirm = {
                    askDown = false
                    if (Downloader.running) {
                        CouiToast.show(when (lang) { "en" -> "A download is running, try again later"; "zh-TW" -> "正在下載，請稍後再清理"; else -> "正在下载，请稍后再清理" })
                    } else if (!clearing) {
                        clearing = true
                        val app = context.applicationContext
                        scope.launch {
                            val started = android.os.SystemClock.uptimeMillis()
                            val left = LocalStore.clearDownloadsAsync(app)
                            val wait = 450L - (android.os.SystemClock.uptimeMillis() - started)
                            if (wait > 0) kotlinx.coroutines.delay(wait)
                            clearing = false
                            tick++
                            CouiToast.show((when (lang) { "en" -> "Cleared, "; "zh-TW" -> "已清理，剩餘 "; else -> "已清理，剩余 " }) + formatBytes(left))
                        }
                    }
                },
                onDismiss = { askDown = false },
            )
        }
    }
}

@Composable
private fun StorageCard(title: String, bytes: Long, summary: String, action: String, onAction: () -> Unit) {
    Card(Modifier.padding(horizontal = 12.dp, vertical = 6.dp).fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AppTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        .couiClickable(onClick = onAction)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) { Text(action, fontSize = 14.sp) }
            }
            Spacer(Modifier.height(8.dp))
            Text(formatBytes(bytes), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            if(summary.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(summary, fontSize = 13.sp, color = AppTheme.colorScheme.onSurfaceVariantSummary)
            }
        }
    }
}

@Composable
private fun PathRow(label: String, path: String, change: String, onCopy: () -> Unit, onChange: () -> Unit) {
    Column(Modifier.padding(top = 14.dp)) {
        Text(label, fontSize = 13.sp, color = AppTheme.colorScheme.onSurfaceVariantSummary)
        Text(path, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp).couiClickable(onClick = onCopy))
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(change, color = AppTheme.colorScheme.primary, modifier = Modifier.couiClickable(onClick = onChange))
            Text("复制", color = AppTheme.colorScheme.primary, modifier = Modifier.couiClickable(onClick = onCopy))
        }
    }
}

fun formatBytes(n: Long): String {
    if (n < 1024) return "$n B"
    val kb = n / 1024.0
    if (kb < 1024) return if (kb < 10) String.format("%.1f KB", kb) else "${kb.toInt()} KB"
    val mb = kb / 1024.0
    if (mb < 1024) return if (mb < 10) String.format("%.1f MB", mb) else "${mb.toInt()} MB"
    return String.format("%.1f GB", mb / 1024.0)
}

private fun copyPath(context: Context, path: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("path", path))
    CouiToast.show("已复制")
}

private fun treePath(uri: Uri): String? {
    val id = runCatching { DocumentsContract.getTreeDocumentId(uri) }.getOrNull() ?: return null
    val parts = id.split(":", limit = 2)
    val rel = parts.getOrElse(1) { "" }.trim('/')
    val root = when (parts[0]) {
        "primary" -> "/storage/emulated/0"
        else -> "/storage/${parts[0]}"
    }
    return if (rel.isBlank()) root else "$root/$rel"
}

private fun adoptTree(context: Context, uri: Uri, download: Boolean, onOk: () -> Unit, onNeedAllFiles: () -> Unit = {}) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
        )
    }
    val path = treePath(uri)
    if (path == null) {
        CouiToast.show("无法识别这个文件夹")
        return
    }
    val dir = File(path)
    val ok = if (download) LocalStore.useDownload(context, dir) else LocalStore.useCache(context, dir)
    if (ok) {
        CouiToast.show("目录已更改")
        onOk()
        return
    }
    if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
        CouiToast.show("需要所有文件访问才能写入这个目录", long = true)
        onNeedAllFiles()
    } else {
        CouiToast.show("这个文件夹无法写入")
    }
}
