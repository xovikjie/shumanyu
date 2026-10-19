package com.shumanyu.app

import org.junit.Assert.*
import org.junit.Test
import org.junit.Assume.assumeTrue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URI
import java.net.URL

class Biquge365Test {
    @Test fun mobileListingMergesImageAndTextAndKeepsIndependentIds() {
        val html="""<a href='/shu/001/'><img data-src='/cover.jpg' alt='书一章节列表'></a><a href='/shu/001/'>书一</a><a href='/shu/002/'>书二</a><a href='https://evil.example/shu/003/'>假书</a>"""
        val rows=Biquge365.parseList(html,Biquge365.HOME,"玄幻")
        assertEquals(listOf("b365001","b365002"),rows.map { it.id })
        assertEquals(Biquge365.HOME+"/cover.jpg",rows.first().cover)
        assertEquals("书一",rows.first().title)
    }
    @Test fun fullDirectoryExcludesLatestPreviewAndKeepsChapterOrder() {
        val html="""<h2>最新九章</h2><a href='/chapter/123/99.html'>末章</a><h2>全部章节</h2><a href='/chapter/123/1.html'>序章</a><a href='/chapter/123/2.html'>第一章</a><a href='/chapter/123/2.html'>重复</a><a href='/chapter/999/3.html'>其他书</a>"""
        assertEquals(listOf("序章","第一章"),Biquge365.parseToc(html,"123").map { it.title })
    }
    @Test fun genericListingHandlesSlugLinksBeyondSixAndLazyImages() {
        val html=(1..12).joinToString("") { "<a href='/title/book-$it'><picture><source srcset='/images/$it.webp 2x'></picture><h3>作品$it</h3></a>" }
        assertEquals(12,WebsitePages.parse(html,"https://example.org/","custom").size)
    }
    @Test fun emptyNoticeIsNotMistakenForNovelText() {
        assertEquals("",Biquge365.parseText("<div id='txt'><p>一秒记住【笔趣阁】<br>biquge365.net，更新快，无弹窗！</p></div>"))
    }
    @Test fun liveTextOnlySearchResolvesOwnDetailCovers() {
        assumeTrue(System.getenv("B365SEARCHLIVE")=="1")
        val proxy=URI(System.getenv("HTTPS_PROXY"))
        for(p in listOf("http","https")){System.setProperty("$p.proxyHost",proxy.host);System.setProperty("$p.proxyPort",proxy.port.toString())}
        System.setProperty("javax.net.ssl.trustStore","/etc/ssl/certs/java/cacerts")
        val site=WebSite(Sites.BIQUGE,"笔趣阁",Biquge365.HOME,kind=Sites.BIQUGE)
        for(title in listOf("斗破苍穹","大爱仙尊","凡人修仙传")) {
            val raw=Biquge365.search(title)
            assertTrue("$title 搜索结果缺失",raw.any { it.title==title })
            assertTrue("搜索页仅提供书名",raw.first { it.title==title }.cover.isBlank())
            val updates=java.util.concurrent.CopyOnWriteArrayList<List<CrawledWork>>()
            val results=Crawler.search(title,listOf(site)) { updates.add(it) }
            assertTrue("$title 完整搜索结果: ${results.map { it.title }}",results.any { it.title==title })
            val work=results.first { it.title==title }
            assertTrue("$title 未从详情页获取封面",work.cover.startsWith(Biquge365.HOME+"/files/"))
            assertTrue("书名应先于详情封面展示",updates.first().first { it.title==title }.cover.isBlank())
            assertTrue("补齐封面后应通知界面",updates.last().first { it.title==title }.cover.isNotBlank())
            val c=URL(work.cover).openConnection();c.setRequestProperty("User-Agent",Crawler.UA)
            val image=javax.imageio.ImageIO.read(c.getInputStream())
            assertNotNull("$title 不是有效图片",image)
            assertTrue(image.width>=16 && image.height>=16)
            val repeated=Biquge365.search(title)
            assertTrue("$title 再次搜索结果: ${repeated.map { it.title }}",repeated.any { it.title==title })
            val cached=repeated.first { it.title==title }
            assertEquals("再次搜索应直接返回已缓存的详情封面",work.cover,cached.cover)
        }
    }
    @Test fun liveCheck() {
        assumeTrue(System.getenv("B365LIVE")=="1")
        val proxy=URI(System.getenv("HTTPS_PROXY"))
        for(p in listOf("http","https")){System.setProperty("$p.proxyHost",proxy.host);System.setProperty("$p.proxyPort",proxy.port.toString())}
        System.setProperty("javax.net.ssl.trustStore","/etc/ssl/certs/java/cacerts")
        val report=JSONObject();val lists=JSONArray()
        Biquge365.categories.forEach { category ->
            val (rows,error)=Biquge365.listing(category)
            assertNull("$category: $error",error);assertTrue(category,rows.isNotEmpty())
            lists.put(JSONObject().put("category",category).put("count",rows.size))
        }
        report.put("categories",lists)
        val search=Biquge365.search("大梦主");assertTrue("搜索为空",search.any { it.title.contains("大梦主") })
        val books=Biquge365.listing("推荐").first.filter { it.cover.isNotBlank() }.take(3);assertEquals(3,books.size)
        val details=JSONArray()
        books.forEach { work ->
            val (detail,error)=Biquge365.detail(work.href);assertNull(error);assertNotNull(detail)
            val d=detail!!;assertEquals(work.title,d.title);assertTrue(d.chapters.size>20)
            val c=URL(d.cover).openConnection();c.setRequestProperty("User-Agent",Crawler.UA)
            assertTrue(c.getInputStream().use { it.read() }>=0)
            val narrative=d.chapters.filter { Regex("^第.+[章节回]").containsMatchIn(it.title) };assertTrue(narrative.isNotEmpty())
            val chapters=listOf(d.chapters.first(),narrative[narrative.size/2],narrative.last())
            val lengths=chapters.map { chapter -> val (text,err)=Biquge365.text(chapter.href);assertNull(err);assertTrue("${work.title}/${chapter.title}: ${text.length}",text.length>100);text.length }
            details.put(JSONObject().put("title",d.title).put("chapters",d.chapters.size).put("textLengths",lengths))
        }
        report.put("books",details)
        File("/workspace/tooling/102-biquge-live-report.json").writeText(report.toString(2))
    }
}
