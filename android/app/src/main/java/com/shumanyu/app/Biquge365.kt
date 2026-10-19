package com.shumanyu.app

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URI
import java.net.URLEncoder

/** The existing 笔趣阁 entry; IDs deliberately differ from the retired website. */
internal object Biquge365 {
    const val HOME = "https://m.biquge365.net"
    val categories = listOf("推荐", "玄幻", "仙侠", "都市", "网游", "科幻", "灵异", "历史", "其他", "完本", "排行")
    fun accepts(url: String) = runCatching { URI(url).host?.lowercase() in setOf("m.biquge365.net", "www.biquge365.net", "biquge365.net") }.getOrDefault(false)
    private fun bookId(url: String) = Regex("/(?:shu|book|newbook|chapter)/(\\d+)").find(url)?.groupValues?.get(1)
    private fun meta(doc: Document, key: String) = doc.selectFirst("meta[property='$key'],meta[name='$key']")?.attr("content").orEmpty()
    fun listing(category: String): Pair<List<CrawledWork>, String?> {
        val index = categories.indexOf(category)
        val path = when(category) { "完本" -> "/full/"; "排行" -> "/top/"; else -> if(index in 1..8) "/sort/${index}_1/" else "/" }
        val (html,error,url)=Crawler.webPage(HOME+path)
        if(error!=null)return emptyList<CrawledWork>() to error
        val books=parseList(html,url,category)
        return books to if(books.isEmpty())"未获取到小说，请重试" else null
    }
    private val recentSearches=object : LinkedHashMap<String,Pair<Long,List<CrawledWork>>>(32,0.75f,true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String,Pair<Long,List<CrawledWork>>>?)=size>32
    }
    fun search(query: String): List<CrawledWork> {
        val key=query.trim()
        val cached=synchronized(recentSearches) { recentSearches[key] }
        if(cached!=null && System.currentTimeMillis()-cached.first<300_000) {
            return cached.second.map { work -> if(work.cover.isBlank())work.copy(cover=Crawler.cachedPageCover(work.href)) else work }
        }
        val (html,error,url)=Crawler.postForm("$HOME/waps.php",mapOf("s" to key))
        val rows=if(error==null)parseList(html,url,"搜索") else emptyList()
        if(rows.isNotEmpty())synchronized(recentSearches) { recentSearches[key]=System.currentTimeMillis() to rows }
        return rows
    }
    fun parseList(html: String, base: String, category: String): List<CrawledWork> {
        val doc=Jsoup.parse(html,base)
        val map=linkedMapOf<String,CrawledWork>()
        for(a in doc.select("a[href]")) {
            val href=a.absUrl("href");val id=Regex("/shu/(\\d+)/?$").find(URIish(href))?.groupValues?.get(1) ?: continue
            if(!accepts(href))continue
            val image=a.selectFirst("img")
            val title=a.text().ifBlank { image?.attr("alt").orEmpty().removeSuffix("章节列表") }.trim()
            if(title.length<2)continue
            val src=image?.let { it.attr("data-original").ifBlank { it.attr("data-src") }.ifBlank { it.attr("src") } }.orEmpty()
            val cover=if(src.isBlank())"" else URI(base).resolve(src).toString()
            val prev=map[id]
            val tag=category.takeUnless { it in listOf("推荐","搜索","排行") }
            map[id]=CrawledWork("b365$id",Sites.BIQUGE,title,cover.ifBlank { prev?.cover.orEmpty() }.ifBlank { Crawler.cachedPageCover("$HOME/shu/$id/") },"$HOME/shu/$id/",genres=tag ?: "小说",cat=category,tags=listOfNotNull(tag))
        }
        return map.values.toList()
    }
    private fun URIish(url: String)=url.substringBefore('?').substringBefore('#')
    fun detail(href: String): Pair<FetchedWork?,String?> {
        val id=bookId(href) ?: return null to "小说链接无效"
        val (html,error,_)=Crawler.webPage("$HOME/shu/$id/")
        if(error!=null)return null to error
        val doc=Jsoup.parse(html,HOME)
        // Same website's full directory avoids dozens of mobile directory-page requests.
        val (toc,tocError,_)=Crawler.webPage("https://www.biquge365.net/newbook/$id/")
        if(tocError!=null)return null to tocError
        val chapters=parseToc(toc,id)
        if(chapters.isEmpty())return null to "未获取到完整目录，请重试"
        val title=meta(doc,"og:novel:book_name").ifBlank { meta(doc,"og:title") }
        return FetchedWork(meta(doc,"og:novel:author"),listOf(meta(doc,"og:novel:category"),meta(doc,"og:novel:status")).filter { it.isNotBlank() },meta(doc,"og:image"),meta(doc,"og:description"),chapters,title) to null
    }
    fun parseToc(html: String, id: String): List<CachedChapter> {
        val doc=Jsoup.parse(html,HOME)
        // The full directory starts after this heading; exclude the latest-chapter preview.
        val start=html.indexOf("全部章节").takeIf { it>=0 } ?: html.indexOf("正文").takeIf { it>=0 } ?: 0
        val region=if(start>0)Jsoup.parse(html.substring(start),HOME) else doc
        return region.select("a[href]").mapNotNull { a ->
            val url=a.absUrl("href");val match=Regex("/chapter/$id/(\\d+)\\.html").find(url) ?: return@mapNotNull null
            if(!accepts(url))return@mapNotNull null
            a.text().takeIf { it.isNotBlank() }?.let { Triple(match.groupValues[1],it,"$HOME${match.value}") }
        }.distinctBy { it.first }.mapIndexed { index,x -> CachedChapter("b365c${id}_${x.first}",x.second,x.third,index) }
    }
    fun parseText(html:String):String {
        val node=Jsoup.parse(html).selectFirst("#txt,#content,#chaptercontent") ?: return ""
        node.select("script,style,.ad,iframe").remove()
        node.select("p").filter { it.text().startsWith("一秒记住【笔趣阁】") }.forEach { it.remove() }
        node.select("br").forEach { it.before("\n") }
        return node.wholeText().lines().map { it.replace('\u00a0',' ').trim() }.filter { it.isNotBlank() }.joinToString("\n\n") { "　　$it" }
    }
    fun text(href: String): Pair<String,String?> {
        var target=href
        val chunks=mutableListOf<String>()
        val seen=hashSetOf<String>()
        val chapter=Regex("/chapter/(\\d+)/(\\d+)").find(href)?.value ?: return "" to "章节链接无效"
        repeat(12) {
            if(!seen.add(target))return "" to "章节分页异常，请重试"
            val (html,error,_)=Crawler.webPage(target)
            if(error!=null)return "" to error
            val text=parseText(html)
            if(text.length<20)return "" to "该章节暂无正文，请重试或查看下一章"
            chunks.add(text)
            val next=Jsoup.parse(html,target).select("a[href]").firstOrNull { it.text().trim()=="下一页" }?.absUrl("href")
            if(next==null || !accepts(next) || !next.substringBefore('?').startsWith(chapter.let { HOME+it }+"_"))return chunks.joinToString("\n\n") to null
            target=next
        }
        return "" to "章节分页过多，请打开网站查看"
    }
}
