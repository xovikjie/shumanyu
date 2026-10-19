package com.shumanyu.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object Notify {
    const val CHANNEL = "shumanyu_updates"
    private const val LIVE = "shumanyu_live"
    private const val PROGRESS_ID = 42

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        if (nm.getNotificationChannel(CHANNEL) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "章节更新", NotificationManager.IMPORTANCE_DEFAULT),
            )
        }
        if (nm.getNotificationChannel(LIVE) == null) {
            nm.createNotificationChannel(
                NotificationChannel(LIVE, "实时活动", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "更新章节时显示在状态栏的实时活动"
                    setShowBadge(false)
                },
            )
        }
    }

    private fun canNotify(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true
        val ok = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        return ok == PackageManager.PERMISSION_GRANTED
    }

    private fun openIntent(context: Context, request: Int): PendingIntent =
        PendingIntent.getActivity(
            context,
            request,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    fun live(context: Context, current: Int, total: Int, label: String) {
        ensureChannel(context)
        if (!canNotify(context)) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val safe = label.ifBlank { "更新中" }
        val title = "正在更新章节"
        val max = total.coerceAtLeast(1)
        val now = current.coerceIn(0, max)
        val n = if (Build.VERSION.SDK_INT >= 36) {
            val style = Notification.ProgressStyle()
                .setStyledByProgress(true)
                .addProgressSegment(Notification.ProgressStyle.Segment(max).setColor(0xFF3482FF.toInt()))
                .setProgress(now)
            if (total <= 0) style.setProgressIndeterminate(true)
            Notification.Builder(context, LIVE)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle(title)
                .setContentText(safe)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setRequestPromotedOngoing(true)
                .setShortCriticalText("更新中")
                .setStyle(style)
                .setContentIntent(openIntent(context, 1))

                .build()
        } else {
            NotificationCompat.Builder(context, LIVE)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle(title)
                .setContentText(safe)
                .setProgress(max, now, total <= 0)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(openIntent(context, 1))
                .setPriority(NotificationCompat.PRIORITY_LOW)

                .build()
        }
        nm.notify(PROGRESS_ID, n)
    }

    fun liveClear(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        nm.cancel(PROGRESS_ID)
    }

    fun chapter(context: Context, title: String, index: Int, chapterTitle: String) {
        ensureChannel(context)
        if (!canNotify(context)) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val spoken = Regex("""(\d+)""").find(chapterTitle)?.groupValues?.get(1)?.toIntOrNull() ?: index
        val line = "${title.ifBlank { "漫画" }}已更新${spoken}话"
        val head = line
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setContentTitle(head)
            .setContentText(line)
            .setStyle(NotificationCompat.BigTextStyle().bigText(line))
            .setAutoCancel(true)
            .setContentIntent(openIntent(context, title.hashCode()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            .build()
        val id = (title.hashCode() and 0x7fffffff) + 100
        nm.notify(id, n)
        AppLog.add("update", line)
    }
}
