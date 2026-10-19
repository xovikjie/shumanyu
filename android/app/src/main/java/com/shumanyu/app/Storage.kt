package com.shumanyu.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.File

@Composable
fun StoragePane(lang: String, onBack: () -> Unit, onOpen: (String) -> Unit, generation: Int = 0) {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    val cacheBytes = remember(tick, generation) { LocalStore.cacheBytes() }
    val downBytes = remember(tick, generation) { LocalStore.downloadBytes(context) }
    val cachePath = LocalStore.cachePath().ifBlank { LocalStore.defaultCache(context).absolutePath }
    val downPath = LocalStore.downloadRoot(context).absolutePath
    val pickCache = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) adoptTree(context, uri, download = false) { tick++ }
    }
    val pickDown = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) adoptTree(context, uri, download = true) { tick++ }
    }
    val title = when (lang) { "en" -> "Storage"; "zh-TW" -> "儲存空間"; else -> "存储空间" }
    Scaffold(topBar = { SubTopBar(title, onBack, BarTitle.End) }) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item {
                StorageCard(
                    title = when (lang) { "en" -> "Cached comics"; "zh-TW" -> "已緩存漫畫或小說"; else -> "已缓存漫画或小说" },
                    bytes = cacheBytes,
                    summary = when (lang) {
                        "en" -> "Covers and chapters saved while reading. Open to delete."
                        "zh-TW" -> "閱讀時自動保存的封面和章節。點進去可以刪除。"
                        else -> "阅读时自动保存的封面和章节。点进去可以删除。"
                    },
                    action = when (lang) { "en" -> "Manage"; "zh-TW" -> "管理"; else -> "管理" },
                    onAction = { onOpen("cache") },
                )
            }
            item {
                StorageCard(
                    title = when (lang) { "en" -> "Downloaded comics"; "zh-TW" -> "已下載漫畫或小說"; else -> "已下载漫画或小说" },
                    bytes = downBytes,
                    summary = when (lang) {
                        "en" -> "Titles you downloaded from the detail page. Open to delete."
                        "zh-TW" -> "在作品頁下載的漫畫或小說。點進去可以刪除。"
                        else -> "在作品页下载的漫画或小说。点进去可以删除。"
                    },
                    action = when (lang) { "en" -> "Manage"; "zh-TW" -> "管理"; else -> "管理" },
                    onAction = { onOpen("download") },
                )
            }
            item {
                Card(Modifier.padding(horizontal = 12.dp, vertical = 6.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(when (lang) { "en" -> "Folders"; "zh-TW" -> "目錄位置"; else -> "目录位置" }, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            when (lang) {
                                "en" -> "Where cached and downloaded titles are stored. You can change either folder."
                                "zh-TW" -> "已緩存和已下載的漫畫、小說所在的系統目錄，方便查找。也可以分別更改位置。"
                                else -> "已缓存和已下载的漫画、小说所在的系统目录，方便查找。也可以分别更改位置。"
                            },
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
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
    }
}

@Composable
fun StorageListPane(kind: String, lang: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    var ask by remember { mutableIntStateOf(-1) }
    var picking by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var askAll by remember { mutableStateOf(false) }
    var askBatch by remember { mutableStateOf(false) }
    val items = remember(tick, kind) {
        if (kind == "download") LocalStore.listDownloads(context) else LocalStore.listCached()
    }
    val title = if (kind == "download") {
        when (lang) { "en" -> "Downloaded"; "zh-TW" -> "已下載"; else -> "已下载" }
    } else {
        when (lang) { "en" -> "Cached"; "zh-TW" -> "已緩存"; else -> "已缓存" }
    }
    val empty = when (lang) { "en" -> "Nothing here"; "zh-TW" -> "這裡是空的"; else -> "这里是空的" }
    val batch = when (lang) { "en" -> "Delete selected"; "zh-TW" -> "批量刪除"; else -> "批量删除" }
    val all = when (lang) { "en" -> "Delete all"; "zh-TW" -> "全部刪除"; else -> "全部删除" }
    fun wipe(ids: Collection<String>) {
        ids.forEach { id ->
            if (kind == "download") LocalStore.deleteDownload(context, id) else LocalStore.deleteCached(id)
        }
    }
    Scaffold(topBar = { SubTopBar(title, onBack) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (items.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Text(batch, color = MiuixTheme.colorScheme.primary, modifier = Modifier.clickable {
                        if (selected.isEmpty()) picking = true else askBatch = true
                    })
                    Text(all, color = Color(0xFFFF3B30), modifier = Modifier.clickable { askAll = true })
                }
            }
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(empty, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                    items(items.size) { i ->
                        val item = items[i]
                        val on = item.id in selected
                        Card(Modifier.padding(horizontal = 12.dp, vertical = 4.dp).fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .pointerInput(item.id, picking) {
                                        detectTapGestures(
                                            onLongPress = {
                                                picking = true
                                                selected = if (on) selected - item.id else selected + item.id
                                            },
                                            onTap = {
                                                if (picking) {
                                                    selected = if (on) selected - item.id else selected + item.id
                                                }
                                            },
                                        )
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (picking) {
                                    Box(
                                        Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(if (on) MiuixTheme.colorScheme.primary else Color.Transparent)
                                            .padding(1.dp),
                                    )
                                    Spacer(Modifier.width(10.dp))
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(item.title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text(formatBytes(item.bytes), fontSize = 12.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
                                }
                                if (!picking) {
                                    Text(
                                        when (lang) { "en" -> "Delete"; "zh-TW" -> "刪除"; else -> "删除" },
                                        color = Color(0xFFFF3B30),
                                        modifier = Modifier.padding(start = 12.dp).clickable { ask = i },
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
        if (ask in items.indices) {
            val item = items[ask]
            ConfirmSheet(
                title = when (lang) { "en" -> "Delete?"; "zh-TW" -> "確定刪除嗎"; else -> "确定删除吗" },
                message = item.title,
                confirm = when (lang) { "en" -> "Delete"; "zh-TW" -> "刪除"; else -> "删除" },
                cancel = when (lang) { "en" -> "Cancel"; "zh-TW" -> "取消"; else -> "取消" },
                onConfirm = {
                    wipe(listOf(item.id))
                    ask = -1
                    tick++
                },
                onDismiss = { ask = -1 },
            )
        }
        if (askBatch && selected.isNotEmpty()) {
            ConfirmSheet(
                title = batch,
                message = "${selected.size}",
                confirm = when (lang) { "en" -> "Delete"; "zh-TW" -> "刪除"; else -> "删除" },
                cancel = when (lang) { "en" -> "Cancel"; "zh-TW" -> "取消"; else -> "取消" },
                onConfirm = {
                    wipe(selected)
                    selected = emptySet()
                    picking = false
                    askBatch = false
                    tick++
                },
                onDismiss = { askBatch = false },
            )
        }
        if (askAll) {
            ConfirmSheet(
                title = all,
                message = "${items.size}",
                confirm = when (lang) { "en" -> "Delete"; "zh-TW" -> "刪除"; else -> "删除" },
                cancel = when (lang) { "en" -> "Cancel"; "zh-TW" -> "取消"; else -> "取消" },
                onConfirm = {
                    if (kind == "download") LocalStore.clearDownloads(context) else LocalStore.clearCached()
                    selected = emptySet()
                    picking = false
                    askAll = false
                    tick++
                },
                onDismiss = { askAll = false },
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
                        .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        .clickable(onClick = onAction)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) { Text(action, fontSize = 14.sp) }
            }
            Spacer(Modifier.height(8.dp))
            Text(formatBytes(bytes), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(summary, fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        }
    }
}

@Composable
private fun PathRow(label: String, path: String, change: String, onCopy: () -> Unit, onChange: () -> Unit) {
    Column(Modifier.padding(top = 14.dp)) {
        Text(label, fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        Text(path, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp).clickable(onClick = onCopy))
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(change, color = MiuixTheme.colorScheme.primary, modifier = Modifier.clickable(onClick = onChange))
            Text("复制", color = MiuixTheme.colorScheme.primary, modifier = Modifier.clickable(onClick = onCopy))
        }
    }
}

@Composable
private fun Legend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
    }
}

@Composable
private fun StorageRing(app: Long, used: Long, total: Long) {
    val other = (used - app).coerceAtLeast(0L)
    val free = (total - used).coerceAtLeast(0L)
    Canvas(Modifier.fillMaxSize().padding(10.dp)) {
        val stroke = 22.dp.toPx()
        val arc = Size(size.width - stroke, size.height - stroke)
        val top = Offset(stroke / 2, stroke / 2)
        val style = Stroke(width = stroke, cap = StrokeCap.Butt)
        val sum = (app + other + free).coerceAtLeast(1L).toFloat()
        var start = -90f
        fun sweep(part: Long) = 360f * part / sum
        drawArc(Color(0xFFE6E6EB), start, 360f, false, top, arc, style = style)
        val appSweep = sweep(app)
        if (appSweep > 0.4f) {
            drawArc(Color(0xFF3482FF), start, appSweep, false, top, arc, style = style)
            start += appSweep
        }
        val otherSweep = sweep(other)
        if (otherSweep > 0.4f) drawArc(Color(0xFFC56BFF), start, otherSweep, false, top, arc, style = style)
    }
}

private fun storageLine(lang: String, app: Long, total: Long): String {
    val pct = if (total <= 0) 0.0 else app * 100.0 / total
    val text = if (pct < 0.1) "0.1" else String.format("%.1f", pct)
    return when (lang) {
        "en" -> "Using $text% of phone storage"
        "zh-TW" -> "書漫嶼占手機儲存$text%"
        else -> "书漫屿占手机存储$text%"
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
    Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
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

private fun adoptTree(context: Context, uri: Uri, download: Boolean, onOk: () -> Unit) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
        )
    }
    val path = treePath(uri)
    if (path == null) {
        Toast.makeText(context, "无法识别这个文件夹", Toast.LENGTH_SHORT).show()
        return
    }
    val dir = File(path)
    val ok = if (download) LocalStore.useDownload(context, dir) else LocalStore.useCache(context, dir)
    if (ok) {
        Toast.makeText(context, "目录已更改", Toast.LENGTH_SHORT).show()
        onOk()
        return
    }
    if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
        Toast.makeText(context, "请允许所有文件访问后再选一次目录", Toast.LENGTH_LONG).show()
        val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    } else {
        Toast.makeText(context, "这个文件夹无法写入", Toast.LENGTH_SHORT).show()
    }
}
