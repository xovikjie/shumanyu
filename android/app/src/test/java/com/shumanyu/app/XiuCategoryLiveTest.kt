package com.shumanyu.app
import org.json.*
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import java.io.File
import java.net.URI
import java.util.concurrent.*
class XiuCategoryLiveTest {
 @Test fun checkEveryCategory() {
  assumeTrue(System.getenv("XIUCATS")=="1")
  val u=URI(System.getenv("HTTPS_PROXY") ?: System.getenv("https_proxy"));for(p in listOf("http","https")){System.setProperty("$p.proxyHost",u.host);System.setProperty("$p.proxyPort",u.port.toString())}
  System.setProperty("javax.net.ssl.trustStore","/etc/ssl/certs/java/cacerts")
  val a=JSONArray(File("src/main/assets/sources/xiu22.json").readText());val tasks=ArrayList<Callable<JSONObject>>()
  for(i in 0 until a.length()) {
   val source=a.getJSONObject(i);val id=XiuSources.id(source)
   for((label,url) in XiuSources.Engine(source).explore())tasks.add(Callable {
    val row=JSONObject().put("source",source.getString("bookSourceName")).put("category",label).put("url",url)
    try {val e=XiuSources.Engine(source);val books=e.books(url,"ruleExplore",id);row.put("count",books.size).put("ok",books.isNotEmpty()).put("first",books.firstOrNull()?.title.orEmpty());if(books.isNotEmpty())row.put("cover",books.first().cover)}catch(t:Throwable){row.put("ok",false).put("error",t.message)}
    println("CATEGORY_CHECK: $row");row
   })
  }
  val pool=Executors.newFixedThreadPool(3)
  try {val rows=JSONArray();pool.invokeAll(tasks).forEach { rows.put(it.get()) };File("/workspace/tooling/xiu-category-report.json").writeText(rows.toString(2));assertTrue("Retained category failed; see category report",(0 until rows.length()).all { rows.getJSONObject(it).optBoolean("ok") })}finally{pool.shutdownNow()}
 }
}
