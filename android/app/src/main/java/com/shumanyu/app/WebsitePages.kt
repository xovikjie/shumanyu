package com.shumanyu.app

import org.jsoup.Jsoup

/** A short-lived snapshot of a page the user actually opened in the in-app browser. */
internal object WebsitePages {
    private data class Page(val html:String,val at:Long)
    private val pages=linkedMapOf<String,Page>()
    @Synchronized fun save(url:String,html:String) {
        if(!Crawler.isSafe(url) || html.length>2_500_000 || html.length<100)return
        val text=Jsoup.parse(html).text().lowercase()
        if(listOf("just a moment","verify you are human","checking your browser","请完成验证").any { text.contains(it) })return
        pages[url.substringBefore('#')]=Page(html,System.currentTimeMillis())
        while(pages.size>8)pages.remove(pages.keys.first())
    }
    @Synchronized fun get(url:String):String? = pages[url.substringBefore('#')]?.takeIf { System.currentTimeMillis()-it.at<10*60*1000 }?.html
    fun parse(html:String,base:String,siteId:String):List<CrawledWork> {
        val doc=Jsoup.parse(html,base)
        return doc.select("a[href]").mapNotNull { a ->
            val img=a.selectFirst("img,source") ?: return@mapNotNull null
            val href=a.absUrl("href").substringBefore('#')
            if(!Crawler.isSafe(href) || href.trimEnd('/')==base.trimEnd('/') || Regex("/(?:login|register|search|category|sort|tag)(?:/|\\?)",RegexOption.IGNORE_CASE).containsMatchIn(href))return@mapNotNull null
            val raw=listOf("data-original","data-src","data-lazy-src","data-lazy","src").map { img.attr(it) }.firstOrNull { it.isNotBlank() && !it.startsWith("data:") }
                ?: img.attr("srcset").split(',').lastOrNull()?.trim()?.substringBefore(' ') ?: return@mapNotNull null
            val cover=runCatching { java.net.URI(base).resolve(raw).toString() }.getOrDefault("")
            if(!Crawler.isSafe(cover) || listOf("logo","avatar","banner","spacer","loading","advert").any { cover.contains(it,true) })return@mapNotNull null
            val name=a.attr("title").ifBlank { img.attr("alt") }.ifBlank { a.text() }.ifBlank { a.parent()?.selectFirst("h2,h3,h4,.title,.bookname")?.text().orEmpty() }.trim()
            if(name.length<2 || name.length>120)return@mapNotNull null
            CrawledWork("w"+href.hashCode().toUInt().toString(16),siteId,name.take(80),cover,href)
        }.distinctBy { it.href }.take(240)
    }
}
