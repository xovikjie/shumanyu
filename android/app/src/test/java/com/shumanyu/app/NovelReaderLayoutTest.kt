package com.shumanyu.app
import org.junit.Test
import org.junit.Assert.*
class NovelReaderLayoutTest {
    @Test fun missingTitleAndDuplicateNumberHaveReadableHeading() {
        assertEquals("第1章",normalizedNovelChapterTitle("",0))
        assertEquals("第525章",normalizedNovelChapterTitle("525 第525章",524))
        assertEquals("序章",normalizedNovelChapterTitle("序章",0))
        assertEquals("第1章 老乡见老乡",normalizedNovelChapterTitle("1 第1章",0,"第1章 老乡见老乡\n\n正文"))
    }
    @Test fun favoriteGenresAreDeduplicatedWithoutSourceOrListingMarkers() {
        val work=CrawledWork("id","source","书名","","https://example.org/",genres="玄幻 魔法",tags=listOf("魔法","推荐","__private"))
        assertEquals(listOf("魔法","玄幻"),favoriteTags(work))
    }
}
