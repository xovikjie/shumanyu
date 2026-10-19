package com.shumanyu.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import java.io.File

object AppIcons {
    fun label(style: String, lang: String): String = when (style) {
        "night" -> if (lang == "en") "Night" else "夜色"
        "ink" -> if (lang == "en") "Sticker" else if (lang == "zh-TW") "貼紙" else "贴纸"
        "soft" -> if (lang == "en") "Soft" else "柔彩"
        "custom" -> if (lang == "en") "Photo" else if (lang == "zh-TW") "相簿" else "相册"
        else -> if (lang == "en") "Default" else if (lang == "zh-TW") "預設" else "默认"
    }

    fun mipmap(style: String): Int = when (style) {
        "night" -> R.mipmap.ic_launcher_night
        "ink" -> R.mipmap.ic_launcher_ink
        "soft" -> R.mipmap.ic_launcher_soft
        else -> R.mipmap.ic_launcher
    }

    fun customFile(context: Context) = File(context.filesDir, "custom_icon.png")

    fun loadCustom(context: Context): Bitmap? {
        val file = customFile(context)
        if (!file.exists()) return null
        return android.graphics.BitmapFactory.decodeFile(file.absolutePath)
    }

    fun saveCustom(context: Context, src: Bitmap) {
        val side = minOf(src.width, src.height).coerceAtLeast(1)
        val x = (src.width - side) / 2
        val y = (src.height - side) / 2
        val square = Bitmap.createBitmap(src, x, y, side, side)
        val out = if (side > 432) Bitmap.createScaledBitmap(square, 432, 432, true) else square
        customFile(context).outputStream().use { out.compress(Bitmap.CompressFormat.PNG, 100, it) }
        if (out !== square) out.recycle()
        if (square !== src) square.recycle()
    }

    private fun alias(style: String) = when (style) {
        "night" -> "com.shumanyu.app.IconNight"
        "ink" -> "com.shumanyu.app.IconInk"
        "soft" -> "com.shumanyu.app.IconSoft"
        else -> "com.shumanyu.app.IconDefault"
    }

    fun apply(context: Context, style: String) {
        if (style == "custom") {
            val bmp = loadCustom(context) ?: return
            val sm = context.getSystemService(ShortcutManager::class.java) ?: return
            if (!sm.isRequestPinShortcutSupported) return
            val launch = Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val info = ShortcutInfo.Builder(context, "custom-icon")
                .setShortLabel("书漫屿")
                .setLongLabel("书漫屿")
                .setIcon(Icon.createWithBitmap(bmp))
                .setIntent(launch)
                .build()
            runCatching { sm.requestPinShortcut(info, null) }
            return
        }
        val pm = context.packageManager
        val want = alias(style)
        listOf("IconDefault", "IconNight", "IconInk", "IconSoft").forEach { name ->
            val cn = ComponentName(context.packageName, "com.shumanyu.app.$name")
            val state = if (cn.className == want) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            pm.setComponentEnabledSetting(cn, state, PackageManager.DONT_KILL_APP)
        }
    }
}
