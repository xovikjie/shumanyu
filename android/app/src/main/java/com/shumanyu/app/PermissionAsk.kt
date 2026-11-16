package com.shumanyu.app





import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.itos.coui.dialog.CouiDialogAction
import com.itos.coui.dialog.CouiDialogActionStyle
import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.dialog.CouiDialogPlacement
import com.itos.coui.theme.CouiTheme

private const val ONBOARD_KEY = "permissionOnboard010"

internal fun notificationsAllowed(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

internal fun allFilesAllowed(): Boolean = Environment.isExternalStorageManager()

internal data class PermissionCopy(val title: String, val body: String, val allow: String, val later: String)

internal fun permissionCopy(lang: String) = when (lang) {
    "en" -> PermissionCopy(
        "Permissions",
        "ShuManYu asks for these once, now:\n\n" +
            "• Notifications — tell you when a book on your shelf has new chapters.\n" +
            "• All files access — keep cache and downloads in a folder you choose (optional).\n\n" +
            "You won't be asked again while reading. You can change them later in system settings.",
        "Allow",
        "Not now",
    )
    "zh-TW" -> PermissionCopy(
        "授權提示",
        "書漫嶼會在首次開啟時一次性申請以下權限：\n\n" +
            "• 通知：書架上的書有新章節時提醒你。\n" +
            "• 所有檔案存取：把快取和下載放到你指定的資料夾（可選）。\n\n" +
            "使用過程中不會再彈出授權提示，之後也可以在系統設定中修改。",
        "去授權",
        "暫不",
    )
    else -> PermissionCopy(
        "授权提示",
        "书漫屿会在首次打开时一次性申请以下权限：\n\n" +
            "• 通知：书架上的书有新章节时提醒你。\n" +
            "• 所有文件访问：把缓存和下载放到你指定的文件夹（可选）。\n\n" +
            "使用过程中不会再弹出授权提示，之后也可以在系统设置中修改。",
        "去授权",
        "暂不",
    )
}

/**
 * First-launch permission prompt. Shown once; on 去授权 it requests notifications and then
 * opens the all-files access page. Features never ask again on their own.
 */
@Composable
internal fun FirstLaunchPermissions(lang: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("shumanyu", Context.MODE_PRIVATE) }
    var visible by remember {
        mutableStateOf(!prefs.getBoolean(ONBOARD_KEY, false) && (!notificationsAllowed(context) || !allFilesAllowed()))
    }
    fun done() {
        visible = false
        prefs.edit().putBoolean(ONBOARD_KEY, true).apply()
    }
    val notifyLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!allFilesAllowed()) openAllFilesSettings(context)
    }
    if (!visible) return
    val copy = remember(lang) { permissionCopy(lang) }
    PermissionDialog(
        copy = copy,
        onAllow = {
            done()
            if (!notificationsAllowed(context)) notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            else if (!allFilesAllowed()) openAllFilesSettings(context)
        },
        onLater = { done() },
    )
}

@Composable
internal fun PermissionDialog(copy: PermissionCopy, onAllow: () -> Unit, onLater: () -> Unit) {
    Dialog(
        visible = true,
        onDismissRequest = onLater,
        title = copy.title,
        message = copy.body,
        confirmAction = CouiDialogAction(
            text = copy.allow,
            onClick = onAllow,
            style = CouiDialogActionStyle.Primary,
            colors = CouiDialogDefaults.primaryActionColors(CouiTheme.colorScheme.accent),
        ),
        dismissAction = CouiDialogAction(text = copy.later, onClick = onLater),
        // 首次授权提示必须屏幕正中：显式指定 Center（COUI 会把弹窗窗口 gravity 设为 CENTER，
        // 水平、垂直都居中，并使用居中缩放的进出场动画），不依赖默认的底部弹出。
        placement = CouiDialogPlacement.Center,
    )
}

internal fun openAllFilesSettings(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        data = Uri.parse("package:${context.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}
