package com.shumanyu.app

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

internal data class GitHubRelease(val version:String,val code:Long?,val notes:String,val page:String)

internal object GitHubUpdates {
    const val REPOSITORY="xovikjie/shumanyu"
    const val RELEASES="https://github.com/$REPOSITORY/releases"
    private const val API="https://api.github.com/repos/$REPOSITORY/releases/latest"

    fun latest():GitHubRelease? {
        val connection=URL(API).openConnection() as HttpURLConnection
        connection.connectTimeout=10_000
        connection.readTimeout=10_000
        connection.setRequestProperty("Accept","application/vnd.github+json")
        connection.setRequestProperty("X-GitHub-Api-Version","2022-11-28")
        connection.setRequestProperty("User-Agent","ShuManYu-Android")
        try {
            if(connection.responseCode==404)return null
            check(connection.responseCode==200){"暂时无法连接 GitHub，请稍后重试"}
            val bytes=connection.inputStream.use { it.readBytesLimited(1_000_000) }
            return parse(String(bytes,Charsets.UTF_8))
        }finally { connection.disconnect() }
    }
    private fun java.io.InputStream.readBytesLimited(limit:Int):ByteArray {
        val output=java.io.ByteArrayOutputStream()
        val buffer=ByteArray(8192)
        while(true){val count=read(buffer);if(count<0)break;check(output.size()+count<=limit){"更新信息过大"};output.write(buffer,0,count)}
        return output.toByteArray()
    }
    fun parse(json:String):GitHubRelease? {
        val item=JSONObject(json)
        if(item.optBoolean("draft") || item.optBoolean("prerelease"))return null
        val page=item.optString("html_url")
        val uri=runCatching { URI(page) }.getOrNull() ?: return null
        if(uri.scheme!="https" || uri.host!="github.com" || uri.userInfo!=null || uri.port!=-1 ||
            !uri.path.orEmpty().startsWith("/$REPOSITORY/releases/tag/"))return null
        val assets=item.optJSONArray("assets") ?: return null
        val hasApk=(0 until assets.length()).any { index->
            val asset=assets.getJSONObject(index)
            asset.optString("name").endsWith(".apk",true) && asset.optLong("size")>0 &&
                asset.optString("browser_download_url").startsWith("https://github.com/$REPOSITORY/releases/download/")
        }
        if(!hasApk)return null
        val tag=item.optString("tag_name").removePrefix("v").removePrefix("V")
        if(!Regex("\\d+(?:\\.\\d+){1,3}").matches(tag))return null
        val body=item.optString("body")
        val code=Regex("<!--\\s*shumanyu-version-code:\\s*(\\d+)\\s*-->").find(body)?.groupValues?.get(1)?.toLongOrNull()
        val notes=Regex("<!--[\\s\\S]*?-->").replace(body,"").trim().take(2000)
        return GitHubRelease(tag,code,notes,page)
    }
    fun isNewer(release:GitHubRelease,currentCode:Long,currentVersion:String):Boolean {
        release.code?.let { return it>currentCode }
        val local=currentVersion.removePrefix("v").removePrefix("V").split('.').map { it.toIntOrNull() ?: return false }
        val remote=release.version.split('.').map { it.toIntOrNull() ?: return false }
        for(i in 0 until maxOf(local.size,remote.size)) {
            val comparison=remote.getOrElse(i){0}.compareTo(local.getOrElse(i){0})
            if(comparison!=0)return comparison>0
        }
        return false
    }
}
