package com.shumanyu.app

data class Page(val kicker: String? = null, val title: String? = null, val body: List<String>, val talk: List<Pair<String, String>> = emptyList())
data class Chapter(val id: String, val index: Int, val title: String, val pages: List<Page>)
data class Manga(
    val id: String,
    val title: String,
    val author: String,
    val cover: String,
    val genres: String,
    val status: String,
    val source: String,
    val hot: Int,
    val updated: String,
    val synopsis: String,
    val chapters: List<Chapter>,
)

data class Source(val id: String, val name: String, val summary: String, var enabled: Boolean = true, val url: String = "")

fun defaultSources() = emptyList<Source>()

val STORE_CATEGORIES = listOf("全部", "冒险", "百合", "生活", "四格", "伪娘", "悬疑", "后宫", "热血", "耽美", "其他", "恐怖")

val catalog: List<Manga> = emptyList()

fun percent(m: Manga, chapterId: String, page: Int): Int {
    var seen = 0
    val total = m.chapters.sumOf { it.pages.size }.coerceAtLeast(1)
    for (c in m.chapters) {
        if (c.id == chapterId) {
            seen += page + 1
            break
        }
        seen += c.pages.size
    }
    return ((seen * 100f) / total).toInt().coerceIn(0, 100)
}

val NOVEL_IDS = setOf("xingyuan", "qingshi", "wuyin", "shenlan", "xiati", "weiming", "baiwu", "guidao")
