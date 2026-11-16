package com.shumanyu.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

internal class AppUpdateController(private val context:Context) {
    private val settings=context.getSharedPreferences("github_updates_${GitHubUpdates.REPOSITORY.replace('/','_')}",Context.MODE_PRIVATE)
    private var probedAt:Long?=null
    private var seenRevision:String?=null
    private var attemptedAt:Long?=null
    private var lastFailed=false
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Main.immediate)
    private var downloadJob:Job?=null
    private var pendingFile:File?=null

    var available by mutableStateOf<GitHubRelease?>(null)
        private set
    var checking by mutableStateOf(false)
        private set
    var downloading by mutableStateOf(false)
        private set
    var progress by mutableStateOf<Float?>(null)
        private set
    var downloadError by mutableStateOf("")
        private set

    suspend fun check(immediate:Boolean=false) {
        if(checking || available!=null)return
        if(!Net.online(context))return
        val elapsed=SystemClock.elapsedRealtime()
        if(!immediate && !updateProbeDue(elapsed,probedAt))return
        val now=System.currentTimeMillis()
        checking=true
        probedAt=elapsed
        try {
            // Frequent conditional feed requests avoid exhausting GitHub's
            // anonymous API quota. Fetch full metadata as soon as releases change.
            val revision=try { withContext(Dispatchers.IO) { GitHubUpdates.releaseRevision() } }
                catch(cancelled:CancellationException){throw cancelled}
                catch(error:Exception){null}
            if(!updateCheckDue(elapsed,attemptedAt,lastFailed,revision!=null && revision!=seenRevision))return
            attemptedAt=elapsed
            lastFailed=true
            val release=withContext(Dispatchers.IO){GitHubUpdates.latest()}
            lastFailed=false
            if(revision!=null)seenRevision=revision
            val info=context.packageManager.getPackageInfo(context.packageName,0)
            val code=if(Build.VERSION.SDK_INT>=28)info.longVersionCode else @Suppress("DEPRECATION") info.versionCode.toLong()
            val newer=release!=null && GitHubUpdates.isNewer(release,code,info.versionName.orEmpty())
            val snoozed=release!=null && now<settings.getLong("snooze:${release.version}:${release.code}",0)
            AppLog.add("app-update","installed=$code remote=${release?.code ?: release?.version} newer=$newer snoozed=$snoozed")
            if(newer && !snoozed)available=release
        }catch(cancelled:CancellationException){throw cancelled}
        catch(error:Exception){AppLog.add("app-update","check failed: ${error.message.orEmpty().take(160)}; retry later")}
        finally{checking=false}
    }

    fun later() {
        available?.let { settings.edit().putLong("snooze:${it.version}:${it.code}",System.currentTimeMillis()+24*60*60_000L).apply() }
        available=null
    }

    /** 已在本地下载好、但还差「允许安装未知应用」的包，重试时直接装，不再重下。 */
    fun startDownload() {
        if(downloading)return
        downloadError=""
        val ready=pendingFile
        if(ready!=null && ready.exists()) {
            scope.launch { install(ready) }
            return
        }
        val release=available ?: return
        downloadJob=scope.launch { downloadInternal(release) }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob=null
        downloading=false
        progress=null
    }

    fun openPage() {
        val page=available?.page ?: return
        val opened=runCatching { context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(page)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }.isSuccess
        if(opened)later() else message("无法打开浏览器，请安装浏览器后重试")
    }

    private suspend fun downloadInternal(release:GitHubRelease) {
        downloading=true
        progress=null
        try {
            val file=withContext(Dispatchers.IO){downloadApk(release)}
            if(file==null) {
                downloadError="下载失败，请检查网络后重试"
                return
            }
            progress=1f
            val problem=withContext(Dispatchers.IO){verifyApk(file,release)}
            if(problem!=null) {
                file.delete()
                downloadError=problem
                return
            }
            pendingFile=file
            install(file)
        }catch(cancelled:CancellationException){throw cancelled}
        catch(error:Exception){
            downloadError=error.message?.take(80)?.ifBlank { "下载失败" } ?: "下载失败"
            AppLog.add("app-update","download failed: ${error.message.orEmpty().take(160)}")
        }finally {
            downloading=false
            downloadJob=null
        }
    }

    private fun downloadApk(release:GitHubRelease):File? {
        val url=release.apkUrl
        if(url.isBlank())return null
        val dir=File(context.cacheDir,"updates").apply{mkdirs()}
        val target=File(dir,"app-${release.version}.apk")
        val part=File(dir,"download.part")
        dir.listFiles()?.forEach { if(it!=part && it!=target) it.delete() }
        var connection:HttpURLConnection?=null
        return try {
            connection=(URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout=15_000
                readTimeout=30_000
                instanceFollowRedirects=true
                setRequestProperty("User-Agent","ShuManYu-Android")
                setRequestProperty("Accept","application/octet-stream")
            }
            if(connection.responseCode !in 200..299)return null
            val total=connection.contentLengthLong
            part.delete()
            connection.inputStream.use { input ->
                part.outputStream().use { out ->
                    val buffer=ByteArray(64*1024)
                    var read=0L
                    while(true) {
                        val count=input.read(buffer)
                        if(count<0)break
                        read+=count
                        if(read>300L*1024*1024) {
                            part.delete()
                            return null
                        }
                        out.write(buffer,0,count)
                        if(total>0)progress=(read.toFloat()/total).coerceIn(0f,1f)
                    }
                }
            }
            if(total>0 && part.length()!=total) {
                part.delete()
                return null
            }
            target.delete()
            if(!part.renameTo(target)) {
                part.delete()
                return null
            }
            target
        }finally { runCatching { connection?.disconnect() } }
    }

    private fun verifyApk(file:File,release:GitHubRelease):String? {
        val pm=context.packageManager
        val archive=pm.getPackageArchiveInfo(file.absolutePath,PackageManager.GET_SIGNING_CERTIFICATES)
            ?: return "安装包损坏"
        val newCode=if(Build.VERSION.SDK_INT>=28)archive.longVersionCode else @Suppress("DEPRECATION") archive.versionCode.toLong()
        if(newCode<=0L)return "安装包版本号无效"
        release.code?.let { if(newCode!=it)return "安装包版本与发布信息不一致" }
        val installed=pm.getPackageInfo(context.packageName,PackageManager.GET_SIGNING_CERTIFICATES)
        val currentCode=if(Build.VERSION.SDK_INT>=28)installed.longVersionCode else @Suppress("DEPRECATION") installed.versionCode.toLong()
        if(newCode<=currentCode)return "安装包版本不高于当前版本"
        val fresh=archive.signingInfo?.apkContentsSigners?.map { sha256(it.toByteArray()) }?.toSet().orEmpty()
        val local=installed.signingInfo?.apkContentsSigners?.map { sha256(it.toByteArray()) }?.toSet().orEmpty()
        if(fresh.isEmpty() || local.isEmpty() || fresh!=local)return "安装包签名与当前应用不一致"
        return null
    }

    private fun install(file:File) {
        if(!context.packageManager.canRequestPackageInstalls()) {
            downloadError="请先允许「安装未知应用」，然后点重试"
            message("请允许安装未知应用后重试")
            runCatching {
                context.startActivity(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.parse("package:${context.packageName}"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            }
            return
        }
        val uri=runCatching {
            FileProvider.getUriForFile(context,"${context.packageName}.updates",file)
        }.getOrNull()
        if(uri==null) {
            downloadError="无法准备安装文件"
            return
        }
        val intent=Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri,"application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        val opened=runCatching { context.startActivity(intent) }.isSuccess
        if(opened) {
            pendingFile=null
            available=null
            progress=null
            downloadError=""
        } else {
            downloadError="无法拉起系统安装器"
        }
    }

    private fun sha256(bytes:ByteArray):String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun message(text:String){CouiToast.show(text, long = true)}
}
