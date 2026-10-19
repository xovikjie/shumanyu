package com.shumanyu.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AppUpdateController(private val context:Context) {
    private val settings=context.getSharedPreferences("github_updates_${GitHubUpdates.REPOSITORY.replace('/','_')}",Context.MODE_PRIVATE)
    private var checking=false
    var available by mutableStateOf<GitHubRelease?>(null)
        private set

    suspend fun check() {
        if(checking)return
        if(!Net.online(context))return
        val now=System.currentTimeMillis()
        if(now-settings.getLong("lastAttempt",0)<15*60_000L || now-settings.getLong("lastSuccess",0)<12*60*60_000L)return
        checking=true
        settings.edit().putLong("lastAttempt",now).apply()
        try {
            val release=withContext(Dispatchers.IO){GitHubUpdates.latest()}
            settings.edit().putLong("lastSuccess",now).apply()
            val info=context.packageManager.getPackageInfo(context.packageName,0)
            val code=if(Build.VERSION.SDK_INT>=28)info.longVersionCode else @Suppress("DEPRECATION") info.versionCode.toLong()
            if(release!=null && GitHubUpdates.isNewer(release,code,info.versionName.orEmpty())) {
                if(now>=settings.getLong("snooze:${release.version}:${release.code}",0))available=release
            }
        }catch(cancelled:CancellationException){throw cancelled}
        catch(error:Exception){AppLog.add("app-update",error.javaClass.simpleName)}
        finally{checking=false}
    }
    fun later() {
        available?.let { settings.edit().putLong("snooze:${it.version}:${it.code}",System.currentTimeMillis()+24*60*60_000L).apply() }
        available=null
    }
    fun download() {
        val page=available?.page ?: return
        val opened=runCatching { context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(page)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }.isSuccess
        if(opened)later() else message("无法打开浏览器，请安装浏览器后重试")
    }
    private fun message(text:String){Toast.makeText(context,text,Toast.LENGTH_LONG).show()}
}
