package com.shumanyu.app

data class ChapterBump(
    val id: String,
    val title: String,
    val index: Int,
    val chapterTitle: String,
    val added: Int,
)

internal fun isIdTitle(title: String?): Boolean {
    val t = title?.trim().orEmpty()
    if (t.isEmpty()) return true
    return Regex("""^(?:jm|ik|jj|ch)?\d+$""", RegexOption.IGNORE_CASE).matches(t)
}

internal fun realTitle(vararg opts: String?): String {
    val idLike = Regex("""^(?:jm|ik)?\d+$""", RegexOption.IGNORE_CASE)
    var fallback = ""
    for (o in opts) {
        val t = o?.trim()?.replace(Regex("\\s+"), " ").orEmpty()
        if (t.isEmpty()) continue
        if (fallback.isEmpty()) fallback = t.take(80)
        if (idLike.matches(t)) continue
        return t.take(80)
    }
    return fallback
}

internal fun repairChapters(list: List<CachedChapter>): List<CachedChapter> {
    val used = HashSet<String>()
    val seenHref = HashSet<String>()
    val out = ArrayList<CachedChapter>(list.size)
    for (c in list) {
        val href = c.href.trim()
        if (href.isNotBlank() && !seenHref.add(href)) continue
        var id = c.id.trim()
        if (id.isBlank() || !used.add(id)) {
            var n = out.size + 1
            var next = "ch$n"
            while (!used.add(next)) {
                n++
                next = "ch$n"
            }
            id = next
        }
        out.add(c.copy(id = id, index = out.size + 1))
    }
    return out
}

object Updates {
    fun refresh(id: String, href: String, seed: CrawledWork? = null): ChapterBump? {
        if (href.isBlank()) return null
        val old = LocalStore.loadWork(id)
        val (fresh, err) = Crawler.fetchWork(href)
        if (fresh == null || fresh.chapters.isEmpty()) {
            if (err != null) AppLog.add("update", err)
            return null
        }
        val oldCh = old?.chapters ?: emptyList()
        val byHref = LinkedHashMap<String, CachedChapter>()
        for (c in oldCh) {
            if (c.href.isBlank()) continue
            byHref[c.href] = c
        }
        for (n in fresh.chapters) {
            if (n.href.isBlank()) continue
            val prev = byHref[n.href]
            byHref[n.href] = if (prev == null) n else prev.copy(title = n.title.ifBlank { prev.title })
        }
        val merged = repairChapters(if (byHref.isEmpty()) fresh.chapters else byHref.values.toList())
        val base = old?.work ?: seed ?: CrawledWork(id, seed?.siteId.orEmpty(), "", seed?.cover.orEmpty(), href)
        val title = realTitle(fresh.title, seed?.title, base.title, id)
        val w = base.copy(
            title = title.ifBlank { id },
            siteId = base.siteId.ifBlank { seed?.siteId.orEmpty() },
            href = base.href.ifBlank { href },
            author = fresh.author.ifBlank { base.author.ifBlank { seed?.author.orEmpty() } },
            cover = run {
                val kept = base.cover
                val file = kept.removePrefix("file://")
                if ((kept.startsWith("/") || kept.startsWith("file:")) && java.io.File(file).exists()) kept
                else fresh.cover.ifBlank { kept.ifBlank { seed?.cover.orEmpty() } }
            },
            tags = fresh.tags.ifEmpty { base.tags.ifEmpty { seed?.tags ?: emptyList() } },
            genres = fresh.tags.joinToString(" ").ifBlank { base.genres.ifBlank { seed?.genres.orEmpty() } },
            cat = base.cat.ifBlank { seed?.cat.orEmpty() },
            description = fresh.description.ifBlank { base.description },
            described = true,
        )
        LocalStore.saveWork(CachedWork(w, merged, System.currentTimeMillis()))
        val added = (merged.size - oldCh.size).coerceAtLeast(0)
        val latest = merged.maxByOrNull { it.index } ?: merged.last()
        return ChapterBump(w.id, w.title.ifBlank { id }, latest.index, latest.title, added)
    }
}
