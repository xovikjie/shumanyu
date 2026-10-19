package com.shumanyu.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    private var shield: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalStore.init(this)
        AppLog.init(this)
        XiuSources.init(this)
        Notify.ensureChannel(this)
        requestStartupPerms()
        applyPeek(peekOn())
        window.decorView.post { followSystemRefresh() }
        enableEdgeToEdge()
        setContent {
            ShuManYuApp()
        }
    }

    fun applyPeek(on: Boolean) {
        val bit = WindowManager.LayoutParams.FLAG_SECURE
        if (on) window.addFlags(bit) else window.clearFlags(bit)
        val attrs = window.attributes
        attrs.flags = if (on) attrs.flags or bit else attrs.flags and bit.inv()
        window.attributes = attrs
        if (Build.VERSION.SDK_INT >= 33) setRecentsScreenshotEnabled(!on)
        if (!on) shield?.visibility = View.GONE
    }

    override fun onPause() {
        val on = peekOn()
        applyPeek(on)
        if (on) {
            ensureShield()
            shield?.bringToFront()
            shield?.visibility = View.VISIBLE
        }
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        applyPeek(peekOn())
    }

    override fun onResume() {
        super.onResume()
        shield?.visibility = View.GONE
        applyPeek(peekOn())
        followSystemRefresh()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            shield?.visibility = View.GONE
            applyPeek(peekOn())
            followSystemRefresh()
        }
    }

    private fun peekOn(): Boolean =
        getSharedPreferences("shumanyu", MODE_PRIVATE).getBoolean("peekGuard", false)

    @Suppress("DEPRECATION")
    private fun currentDisplay(): Display? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) display else windowManager.defaultDisplay
    }

    private fun readSystemHz(): Float {
        val cr = contentResolver
        val names = arrayOf(
            "peak_refresh_rate",
            "user_refresh_rate",
            "oplus_customize_screen_refresh_rate",
            "coloros_screen_refresh_rate",
            "miui_refresh_rate",
        )
        for (name in names) {
            val asFloat = runCatching { Settings.System.getFloat(cr, name) }.getOrNull()
            if (asFloat != null && asFloat >= 59f) return asFloat
            val asInt = runCatching { Settings.System.getInt(cr, name) }.getOrNull()
            if (asInt != null && asInt >= 59) return asInt.toFloat()
            val secure = runCatching { Settings.Secure.getInt(cr, name) }.getOrNull()
            if (secure != null && secure >= 59) return secure.toFloat()
        }
        return 0f
    }

    private fun followSystemRefresh() {
        val display = currentDisplay() ?: return
        val modes = display.supportedModes
        if (modes.isEmpty()) return
        val maxHz = modes.maxOf { it.refreshRate }
        val high = modes.filter { it.refreshRate in 119f..150f }
        val systemHz = readSystemHz()
        val current = display.refreshRate
        val target = when {
            maxHz < 90f -> current.coerceIn(59f, maxHz.coerceAtLeast(59f))
            systemHz >= 59f -> systemHz.coerceAtMost(maxHz)
            current >= 90f -> current
            high.isNotEmpty() -> high.maxOf { it.refreshRate }
            else -> maxHz
        }
        val active = display.mode
        val pool = modes.filter {
            it.physicalWidth == active.physicalWidth && it.physicalHeight == active.physicalHeight
        }.ifEmpty { modes.toList() }
        val mode = pool.minByOrNull { abs(it.refreshRate - target) } ?: return
        val lp = window.attributes
        if (lp.preferredDisplayModeId != mode.modeId || abs(lp.preferredRefreshRate - mode.refreshRate) > 0.5f) {
            lp.preferredDisplayModeId = mode.modeId
            lp.preferredRefreshRate = mode.refreshRate
            window.attributes = lp
        }
        if (Build.VERSION.SDK_INT >= 31) {
            window.setFrameRateBoostOnTouchEnabled(true)
        }
        if (Build.VERSION.SDK_INT >= 35) {
            window.setFrameRatePowerSavingsBalanced(mode.refreshRate < 90f)
            window.decorView.setRequestedFrameRate(mode.refreshRate)
        }
    }

    private fun ensureShield() {
        if (shield != null) return
        val v = View(this)
        v.setBackgroundColor(Color.BLACK)
        v.visibility = View.GONE
        addContentView(
            v,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT),
        )
        shield = v
    }

    private fun requestStartupPerms() {
        val prefs = getSharedPreferences("shumanyu", MODE_PRIVATE)
        if (prefs.getBoolean("permsAskedV22", false)) return
        prefs.edit().putBoolean("permsAskedV22", true).apply()
        val needed = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= 33) {
            needed += Manifest.permission.POST_NOTIFICATIONS
            needed += Manifest.permission.READ_MEDIA_IMAGES
        } else {
            needed += Manifest.permission.READ_EXTERNAL_STORAGE
            needed += Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 22)
        }
    }
}
