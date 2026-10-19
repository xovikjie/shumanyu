package com.shumanyu.app
import org.json.*
import org.junit.Test
import org.junit.Assume.assumeTrue
import org.junit.Assert.assertTrue
import java.io.File
import java.net.*
import java.util.concurrent.*
class XiuDeepLiveTest {
 @Test fun categoriesPaginationAndRepresentativeWholeBooks() {
  assumeTrue(System.getenv("XIUDEEP")=="1")
  val u=URI(System.getenv("HTTPS_PROXY") ?: System.getenv("https_proxy"));for(p in listOf("http","https")){System.setProperty("$p.proxyHost",u.host);System.setProperty("$p.proxyPort",u.port.toString())}
  System.setProperty("javax.net.ssl.trustStore","/etc/ssl/certs/java/cacerts")
  val a=JSONArray(File("src/main/assets/sources/xiu22.json").readText());val rules=(0 until a.length()).map { a.getJSONObject(it) }
  val field=XiuSources.javaClass.getDeclaredField("rules");field.isAccessible=true;val old=field.get(null);field.set(null,rules)
  val report=JSONArray();val pool=Executors.newFixedThreadPool(4);val books=ConcurrentHashMap<String,CrawledWork>()
  try {
   val tasks=rules.flatMap { source->
    val id=XiuSources.id(source);val cats=XiuSources.categories(id)
    (if(cats==listOf("搜索"))listOf("斗破苍穹","凡人修仙传","诡秘之主") else cats).map { cat->Callable {
     val row=JSONObject().put("source",source.getString("bookSourceName")).put("category",cat)
     try {
      val first=if(cats==listOf("搜索"))XiuSources.search(id,cat) else XiuSources.listing(id,cat,null).let { check(it.first.isNotEmpty()){it.second.orEmpty()};it.first }
      check(first.isNotEmpty()){ "No novels" };val all=first.toMutableList();val seen=first.map { it.id }.toMutableSet();val counts=JSONArray().put(first.size)
      if(cats!=listOf("搜索"))repeat(2){if(XiuSources.hasMore(id,cat)){val next=XiuSources.more(id,cat);check(next.second==null){next.second.orEmpty()};check(next.first.none { it.id in seen }){"Repeated page"};next.first.forEach { seen.add(it.id) };all.addAll(next.first);counts.put(next.first.size)}}
      row.put("pages",counts).put("count",all.size).put("ok",true)
      listOf(all.first(),all.last()).forEach { books[it.id]=it }
     }catch(t:Throwable){row.put("ok",false).put("error",t.message)}
     synchronized(report){report.put(row);File("/workspace/tooling/xiu-deep-list-report.json").writeText(report.toString(2))};row
    } }
   }
   pool.invokeAll(tasks).forEach { it.get() }
   val searching=JSONArray()
   val searchTasks=rules.flatMap { source->books.values.filter { it.siteId==XiuSources.id(source) }.take(3).map { book->Callable {
    val row=JSONObject().put("source",source.getString("bookSourceName")).put("query",book.title)
    try {val found=XiuSources.search(book.siteId,book.title);check(found.any { it.title==book.title || it.id==book.id }){"Known catalog title not found"};row.put("ok",true).put("count",found.size).put("remoteStatus",XiuSources.searchError(book.siteId).ifBlank { "ok" })}catch(t:Throwable){row.put("ok",false).put("error",t.message)}
    synchronized(searching){searching.put(row);File("/workspace/tooling/xiu-deep-search-report.json").writeText(searching.toString(2))};row
   } } }
   pool.invokeAll(searchTasks).forEach { it.get() }
   val reading=JSONArray()
   val readTasks=books.values.map { book->Callable {
    val row=JSONObject().put("title",book.title).put("sourceId",book.siteId).put("href",book.href)
    try {
     val detail=XiuSources.detail(book.href);val work=detail.first ?: error(detail.second.orEmpty());check(work.chapters.isNotEmpty()){ "Empty TOC" }
     val chapters=work.chapters;val selected=listOf(chapters.first(),chapters[chapters.size/2],chapters.last()).distinctBy { it.id };val lengths=JSONArray()
     for(chapter in selected){val body=XiuSources.text(chapter.href);check(body.first.isNotBlank()){"${chapter.title}: ${body.second ?: "Body too short"}"};lengths.put(JSONObject().put("chapter",chapter.title).put("length",body.first.length))}
     row.put("chapters",chapters.size).put("tested",lengths).put("ok",true)
    }catch(t:Throwable){row.put("ok",false).put("error",t.message)}
    synchronized(reading){reading.put(row);File("/workspace/tooling/xiu-deep-reading-report.json").writeText(reading.toString(2))};row
   } }
   pool.invokeAll(readTasks).forEach { it.get() }
   assertTrue("Listing/pagination failure; see report",(0 until report.length()).all {report.getJSONObject(it).optBoolean("ok")})
   assertTrue("Search failure; see report",(0 until searching.length()).all {searching.getJSONObject(it).optBoolean("ok")})
   assertTrue("Reading failure; see report",(0 until reading.length()).all {reading.getJSONObject(it).optBoolean("ok")})
  }finally{pool.shutdownNow();field.set(null,old)}
 }
}
