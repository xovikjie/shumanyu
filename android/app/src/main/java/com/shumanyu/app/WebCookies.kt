package com.shumanyu.app

import android.webkit.CookieManager
import android.webkit.WebView

/** Public pages must still load when the system WebView provider is absent. */
internal object WebCookies {
    private val manager: CookieManager? by lazy {
        runCatching {
            if (WebView.getCurrentWebViewPackage() == null) null else CookieManager.getInstance()
        }.getOrNull()
    }
    fun get(url: String): String = runCatching { manager?.getCookie(url).orEmpty() }.getOrDefault("")
    fun set(url: String, cookie: String) { runCatching { manager?.setCookie(url, cookie) } }
}
