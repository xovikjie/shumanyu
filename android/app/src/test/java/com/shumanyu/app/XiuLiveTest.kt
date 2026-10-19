package com.shumanyu.app
import org.json.*
import org.junit.Test
import org.junit.Assume.assumeTrue
import java.io.File
import java.net.*
import java.util.concurrent.*
class XiuLiveTest {
 @Test fun checkEachSource() {
  assumeTrue(System.getenv("XIULIVE")=="1")
  val u=URI(System.getenv("HTTPS_PROXY") ?: System.getenv("https_proxy"));for(p in listOf("http","https")){System.setProperty("$p.proxyHost",u.host);System.setProperty("$p.proxyPort",u.port.toString())}
  System.setProperty("javax.net.ssl.trustStore","/etc/ssl/certs/java/cacerts")
  val rules=JSONArray(File("/workspace/tooling/bitbucket-shuyuan.json").readText())
  val pool=Executors.newFixedThreadPool(3)
  try {
   val tasks=(0 until rules.length()).map { i->Callable {
    val source=rules.getJSONObject(i);val report=JSONObject().put("name",source.getString("bookSourceName")).put("url",source.getString("bookSourceUrl"));var stage="列表/搜索"
    try {
     val id=XiuSources.id(source);val e=XiuSources.Engine(source,"斗破苍穹")
     var works=e.browse(id)
     if(works.isEmpty())works=e.books(e.requestRule(source.optString("searchUrl")),"ruleSearch",id)
     check(works.isNotEmpty()){ "未解析到小说" };report.put("count",works.size)
     val work=works.first();report.put("title",work.title).put("href",work.href)
     stage="详情";val page=e.load(XiuSources.originalUrl(work.href));val info=source.optJSONObject("ruleBookInfo") ?: JSONObject()
     val cover=work.cover.ifBlank { e.absolute(e.value(page,info.optString("coverUrl"))) }
     stage="封面";check(cover.isNotBlank()){ "封面地址为空" }
     val c=URL(cover).openConnection() as HttpURLConnection;c.connectTimeout=12000;c.readTimeout=15000;c.setRequestProperty("User-Agent",Crawler.UA);c.setRequestProperty("Referer",e.base)
     val bytes=try{check(c.responseCode==200){"HTTP ${c.responseCode}"};c.inputStream.use { it.readNBytes(2000000) }}finally{c.disconnect()}
     val image=javax.imageio.ImageIO.read(bytes.inputStream());check(image!=null){"图片无法解码"};report.put("cover",cover).put("imageSize","${image.width}x${image.height}")
     stage="目录";val tocUrl=e.value(page,info.optString("tocUrl"));val tocPage=if(tocUrl.isBlank())page else e.load(tocUrl)
     val toc=source.optJSONObject("ruleToc") ?: JSONObject();val chapters=e.select(tocPage,toc.optString("chapterList"));check(chapters.isNotEmpty()){ "目录为空" };report.put("chapters",chapters.size)
     stage="正文";val chapter=e.value(chapters.first(),toc.optString("chapterUrl"));val chapterPage=e.load(e.absolute(chapter));val content=e.clean(e.value(chapterPage,source.optJSONObject("ruleContent")?.optString("content").orEmpty()));check(content.length>100){"正文为空或过短"};report.put("textLength",content.length).put("ok",true)
    }catch(t:Throwable){report.put("ok",false).put("stage",stage).put("error",(t.message ?: t.javaClass.simpleName).take(200))}
    println("SOURCE_CHECK: $report");report
   } }
   val futures=pool.invokeAll(tasks,240,TimeUnit.SECONDS)
   val reports=JSONArray();futures.forEachIndexed { i,f->reports.put(if(f.isCancelled)JSONObject().put("name",rules.getJSONObject(i).getString("bookSourceName")).put("ok",false).put("error","总测试超时") else f.get()) }
   File("/workspace/tooling/xiu22-live-report.json").writeText(reports.toString(2))
  }finally{pool.shutdownNow()}
 }
}
