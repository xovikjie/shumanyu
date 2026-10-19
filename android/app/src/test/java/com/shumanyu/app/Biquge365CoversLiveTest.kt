package com.shumanyu.app

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import java.net.URI
import java.net.URL
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Biquge365CoversLiveTest {
    @Test fun everyBookReturnedByAllCategoriesHasItsOwnReadableCover() {
        assumeTrue(System.getenv("B365COVERS")=="1")
        val proxy=URI(System.getenv("HTTPS_PROXY"))
        for(p in listOf("http","https")){System.setProperty("$p.proxyHost",proxy.host);System.setProperty("$p.proxyPort",proxy.port.toString())}
        System.setProperty("javax.net.ssl.trustStore","/etc/ssl/certs/java/cacerts")
        val books=Biquge365.categories.flatMap { val (rows,err)=Biquge365.listing(it);assertNull(err);rows }.distinctBy { it.id }
        assertTrue(books.size>50)
        val pool=Executors.newFixedThreadPool(6)
        val reports=try { pool.invokeAll(books.map { book -> Callable {
            val report=JSONObject().put("id",book.id).put("title",book.title).put("href",book.href)
            try {
                val cover=book.cover.ifBlank { Crawler.peekPageCover(book.href) }
                check(cover.isNotBlank()) { "没有匹配到本书封面" }
                report.put("cover",cover)
                val c=URL(cover).openConnection();c.connectTimeout=10000;c.readTimeout=10000;c.setRequestProperty("User-Agent",Crawler.UA);c.setRequestProperty("Referer",book.href)
                val bytes=c.getInputStream().use { stream -> ByteArray(12).also { check(stream.read(it)>=8) } }
                val valid=(bytes[0].toInt() and 255==255 && bytes[1].toInt() and 255==216) || bytes.take(4)==listOf(137.toByte(),80.toByte(),78.toByte(),71.toByte()) || String(bytes.copyOfRange(0,4)) in listOf("RIFF","GIF8")
                check(valid) { "返回内容不是图片" };report.put("ok",true)
            } catch(t:Throwable) { report.put("ok",false).put("error",t.message ?: t.javaClass.simpleName) }
            report
        } },240,TimeUnit.SECONDS).mapIndexed { i,f -> if(f.isCancelled)JSONObject().put("id",books[i].id).put("ok",false).put("error","检查超时") else f.get() } } finally { pool.shutdownNow() }
        File("/workspace/tooling/102-biquge-covers-report.json").writeText(JSONArray(reports).toString(2))
        val failed=reports.filter { !it.optBoolean("ok") }
        assertEquals("${books.size}本小说封面，失败：${failed.take(10)}",0,failed.size)
    }
}
