package com.shumanyu.app

import java.net.URL

internal fun safeSourceUrl(raw: String): Boolean {
        return try {
            val u = URL(raw.trim())
            if (u.protocol != "http" && u.protocol != "https") return false
            val host = u.host.lowercase()
            if (host == "localhost" || host.endsWith(".localhost") || host == "0.0.0.0") return false
            if (host.startsWith("127.") || host.startsWith("10.") || host.startsWith("192.168.") || host.startsWith("169.254.")) return false
            val m = Regex("^172\\.(\\d+)\\.").find(host)
            if (m != null) {
                val n = m.groupValues[1].toInt()
                if (n in 16..31) return false
            }
            true
        } catch (_: Exception) {
            false
        }
    }
