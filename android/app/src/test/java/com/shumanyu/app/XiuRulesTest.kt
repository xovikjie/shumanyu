package com.shumanyu.app

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class XiuRulesTest {
    private fun engine(extra: String = "") = XiuSources.Engine(JSONObject("""{"bookSourceUrl":"https://example.org"$extra}"""))
    @Test fun legacySelectorsPreserveOrderAndNegativeIndices() {
        val e=engine()
        val html="""<div id="list"><a href="/1">一</a><a href="/2">二</a><a href="/3">三</a></div>"""
        assertEquals("三",e.value(html,"id.list.0@tag.a.-1@text"))
        assertEquals("二\n三",e.value(html,"id.list.0@tag.a!0@text"))
        assertEquals("一\n二",e.value(html,"#list@a.0:1@text"))
        assertEquals("三",e.value(html,"#list@a.!0:1@text"))
        assertEquals("一\n二",e.value(html,"#list@a[0:1]@text"))
        assertEquals("123",e.value("<a data-bid='123'>书</a>","a@data-bid"))
    }
    @Test fun xpathAndJsonRulesExtractActualMetadata() {
        val e=engine()
        assertEquals("作者甲",e.value("""<meta property="og:novel:author" content="作者甲">""","//meta[@property='og:novel:author']/@content"))
        assertEquals("书名",e.value("""{"data":[{"name":"书名"}]}""","$.data[0].name"))
        assertEquals("一\n二",e.value("""{"data":[{"name":"一"},{"name":"二"}]}""","$.data[*].name"))
    }
    @Test fun javascriptCanTransformRulesAndKeepSourceVariables() {
        val e=engine()
        assertEquals("https://example.org/find?q=test",XiuSources.Engine(JSONObject("""{"bookSourceUrl":"https://example.org"}"""),"test").requestRule("@js:baseUrl+'/find?q={{key}}'"))
        assertEquals("abc",e.value("ABC","@js:result.toLowerCase()"))
        assertEquals("a\nb",e.value("", "@js:var a=['a','b']; a"))
        assertEquals("x",e.value("", "<js>java.put('name','x'); java.get('name');</js>"))
        assertEquals("/book/5",e.value("""<a onclick="open('5')">书</a>""","a@onclick@js:'/book/'+result.match(/\\d+/)[0]"))
    }
    @Test fun replacementsTemplatesAndHtmlParagraphsWork() {
        val e=engine()
        assertEquals("作者",e.value("<span>作者：作者</span>","span@text##作者："))
        assertEquals("https://example.org/123",e.value("""{"id":123}""","https://example.org/{{$.id}}"))
        assertEquals("第一段\n第二段",e.clean("<p>第一段</p><p>第二段</p><script>ad()</script>"))
    }
    @Test fun taggedChapterUrlsRetainSourceAcrossStorage() {
        val href=XiuSources.tagged("https://example.org/ch/1","xiu2-a","https://example.org/book/1")
        assertEquals("xiu2-a",XiuSources.sourceId(href))
        assertTrue(href.contains("smyBook="))
    }
    @Test fun paginationSkipsDisabledLinksAndAlreadyReadPages() {
        val e=XiuSources.Engine(JSONObject("""{"bookSourceUrl":"https://example.org"}"""))
        val html="""<div class="page"><a href="javascript:void(0)">上一页</a><a href="/chapter/1.html">1</a><a href="/chapter/1_2.html">2</a></div>"""
        assertEquals("https://example.org/chapter/1_2.html",XiuSources.nextPage(e,html,".page@a@href",setOf("https://example.org/chapter/1.html")))
        assertEquals("",XiuSources.nextPage(e,"<a href='javascript:void(0)'>下一页</a>","a@href",emptySet()))
    }
    @Test fun sourceIdentifiersRemainStableAfterDisplayNameCleanup() {
        assertEquals("native22-6d9f04cd94ef3d7c32b0",XiuSources.id(JSONObject("""{"bookSourceUrl":"https://www.69shuba.com","bookSourceName":"69书吧"}""")))
    }
    @Test fun chapterActionLinksAreNotTreatedAsReadableChapters() {
        val e=XiuSources.Engine(JSONObject("""{"bookSourceUrl":"https://example.org"}"""))
        assertNull(XiuSources.readableUrl(e,"javascript:void(0)"))
        assertEquals("https://example.org/book/1.html",XiuSources.readableUrl(e,"/book/1.html"))
    }
    @Test fun categoryMembershipSurvivesReturningToRecommendation() {
        val book=CrawledWork("same","native22-test","书名","","https://example.org/book",genres="小说",tags=listOf("历史"))
        val recommended=XiuSources.forCategory(book,"推荐")
        val military=XiuSources.forCategory(book,"军事历史")
        val merged=mergeListed(recommended,military,"军事历史")
        assertTrue(XiuSources.inCategory(merged,"军事历史"))
        assertTrue(XiuSources.inCategory(merged,"推荐"))
        assertFalse(XiuSources.inCategory(merged,"科幻末世"))
        val revisited=mergeListed(merged,recommended,"推荐")
        assertTrue(XiuSources.inCategory(revisited,"军事历史"))
        assertTrue(XiuSources.inCategory(revisited,"推荐"))
    }
    @Test fun upstreamCategoryListingDoesNotRequireMatchingBookGenre() {
        val book=CrawledWork("one","native22-test","书名","","https://example.org/book",genres="小说")
        val listed=XiuSources.forCategory(book,"最近更新")
        assertTrue(XiuSources.inCategory(listed,"最近更新"))
        assertFalse(XiuSources.inCategory(listed,"总排行榜"))
    }
    @Test fun paginationUsesRequestedPageInsteadOfRepeatingFirstPage() {
        val e=engine()
        e.pageNumber=2
        assertEquals("/list/2.html",e.requestRule("/list/{{page}}.html"))
        e.pageNumber=3
        assertEquals("/list/3.html",e.requestRule("/list/{{page}}.html"))
    }
}
