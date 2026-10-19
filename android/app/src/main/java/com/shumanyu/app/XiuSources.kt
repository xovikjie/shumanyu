package com.shumanyu.app

import android.content.Context
import com.jayway.jsonpath.JsonPath
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.mozilla.javascript.Context as JsContext
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.Wrapper
import java.net.URL
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/** XIU2 rules exposed as 22 independent native sources, preserving upstream names. */
object XiuSources {
    const val KIND="xiu-native-22"
    private var prefs: android.content.SharedPreferences?=null
    private var rules=emptyList<JSONObject>()
    private val sourceRequests=ConcurrentHashMap<String,java.util.concurrent.locks.ReentrantLock>()
    fun id(s:JSONObject)="native22-"+digest(s.getString("bookSourceUrl"))
    fun init(context:Context) {
        if(rules.isNotEmpty())return
        prefs=context.getSharedPreferences("native22_runtime",Context.MODE_PRIVATE)
        val a=JSONArray(context.assets.open("sources/xiu22.json").bufferedReader().use { it.readText() })
        rules=(0 until a.length()).map { a.getJSONObject(it).also { r->r.put("_smyId",id(r)) } }
    }
    fun sites()=rules.map { WebSite(id(it),it.getString("bookSourceName").removeSuffix(".com"),it.getString("bookSourceUrl"),kind=KIND) }
    fun rule(id:String)=rules.firstOrNull { id(it)==id }
    data class Entry(val type:Int=0,val url:String="")
    fun entry(id:String)=rule(id)?.let { Entry(0,it.getString("bookSourceUrl")) }
    fun typeName(type:Int)="小说"
    fun loginInfo(id:String)="{}"
    private val exploreCache=ConcurrentHashMap<String,List<Pair<String,String>>>()
    private fun plainExplore(source:JSONObject):List<Pair<String,String>> {
        val raw=source.optString("exploreUrl")
        if(raw.isBlank() || raw=="null" || raw.startsWith("@js:"))return emptyList()
        return runCatching {
            if(raw.trim().startsWith("[")) {
                val a=JSONArray(raw);(0 until a.length()).map { a.getJSONObject(it) }.filter { it.optString("url").isNotBlank() && it.optString("title").isNotBlank() }.map { it.optString("title") to it.optString("url") }
            }else raw.lines().mapNotNull { val p=it.split("::",limit=2);if(p.size==2 && p[1].isNotBlank())p[0] to p[1] else null }
        }.getOrDefault(emptyList())
    }
    fun categories(id:String):List<String> {
        val choices=exploreCache[id] ?: rule(id)?.let { plainExplore(it) }.orEmpty()
        if(choices.isEmpty())return listOf("搜索")
        return (listOf("推荐")+choices.map { it.first }.filter { it.isNotBlank() }).distinct()
    }
    private data class ListingCursor(val template:String,val page:Int,val seen:Set<String>)
    private val listingCursors=ConcurrentHashMap<String,ListingCursor>()
    fun hasMore(id:String,category:String)=listingCursors.containsKey("$id:$category")
    private fun rememberNext(id:String,category:String,e:Engine,items:List<CrawledWork>,page:Int,seen:Set<String>) {
        val key="$id:$category"
        val next=if(e.nextListingLink.isBlank())"" else if(e.lastListingRule.contains("{{page}}"))e.lastListingRule else e.nextListingLink
        if(items.isEmpty() || next.isBlank())listingCursors.remove(key)
        else listingCursors[key]=ListingCursor(next,page+1,seen+items.map { it.id })
    }
    fun more(id:String,category:String):Pair<List<CrawledWork>,String?> = runCatching {
        val cursor=listingCursors["$id:$category"] ?: return@runCatching emptyList<CrawledWork>() to null
        val e=Engine(requireNotNull(rule(id)));e.pageNumber=cursor.page
        val items=e.books(cursor.template,"ruleExplore",id).filter { it.id !in cursor.seen }.map { forCategory(it,category) }
        rememberNext(id,category,e,items,cursor.page,cursor.seen)
        rememberBooks(items) to null
    }.getOrElse { emptyList<CrawledWork>() to ("后续内容加载失败，请重试："+message(it)) }
    fun listing(id:String,category:String,onPartial:((List<CrawledWork>)->Unit)?):Pair<List<CrawledWork>,String?> = runCatching {
        val e=Engine(requireNotNull(rule(id)))
        val choices=e.explore();exploreCache[id]=choices
        val target=choices.firstOrNull { it.first==category }?.second
        if(choices.isEmpty())return@runCatching emptyList<CrawledWork>() to "此源仅支持搜索，请点击搜索按钮查找小说"
        require(target!=null || category=="推荐" || category=="推薦") { "未找到该分类，请重新选择分类" }
        val items=(if(target!=null)e.books(target,"ruleExplore",id) else e.browse(id)).map { forCategory(it,category) }
        val previous=listingCursors["$id:$category"]
        if(previous==null)rememberNext(id,category,e,items,1,emptySet())
        else listingCursors["$id:$category"]=previous.copy(seen=previous.seen+items.map { it.id })
        rememberBooks(items)
        onPartial?.invoke(items)
        items to if(items.isEmpty())"该分类暂未获取到小说，请重试或切换分类" else null
    }.getOrElse { emptyList<CrawledWork>() to message(it) }
    internal fun forCategory(work:CrawledWork,category:String)=work.copy(cat=category,listed=(work.listed+category).distinct())
    internal fun inCategory(work:CrawledWork,category:String)=work.cat==category || category in work.listed
    private val catalog=ConcurrentHashMap<String,CrawledWork>()
    private val searchErrors=ConcurrentHashMap<String,String>()
    fun searchError(id:String)=searchErrors[id].orEmpty()
    fun verificationUrl(id:String)=entry(id)?.url.orEmpty().trimEnd('/')+"/modules/article/search.php"
    private fun rememberBooks(items:List<CrawledWork>):List<CrawledWork> { items.forEach { catalog[it.id]=it };return items }
    fun search(id:String,key:String):List<CrawledWork> {
        val local=(catalog.values.filter { it.siteId==id }+LocalStore.loadListing(id)).filter { it.title.contains(key,true) }.distinctBy { it.id }
        searchErrors.remove(id)
        val remote=runCatching {
            val e=Engine(requireNotNull(rule(id)),key)
            e.books(e.requestRule(e.source.optString("searchUrl")),"ruleSearch",id)
        }.getOrElse { val error=message(it);if(!Thread.currentThread().isInterrupted){searchErrors[id]=error;AppLog.add("native22",error)};emptyList() }
        return rememberBooks((local+remote).distinctBy { it.id }.sortedBy { if(it.title==key)0 else if(it.title.contains(key,true))1 else 2 })
    }
    private val availableBooks=ConcurrentHashMap<String,Pair<Long,Boolean>>()
    private val availabilityPool=java.util.concurrent.Executors.newFixedThreadPool(4) { task->Thread(task,"novel-availability").apply { isDaemon=true } }
    private fun availableYueBooks(items:List<CrawledWork>,source:JSONObject):List<CrawledWork> {
        val futures=items.map { item->availabilityPool.submit(java.util.concurrent.Callable {
            val url=originalUrl(item.href);val previous=availableBooks[url]
            val available=if(previous!=null && System.currentTimeMillis()-previous.first<3_600_000)previous.second else {
                // Use the same redirects, cookies, headers, timeout and bounded retries as opening a book.
                // An inconclusive timeout must not turn an unchecked link into a cached valid book.
                runCatching { Engine(source).load(url) }.fold(
                    onSuccess={ availableBooks[url]=System.currentTimeMillis() to true;true },
                    onFailure={ error->
                        if(error is InterruptedException)throw error
                        if(Regex("HTTP (404|410)\\b").containsMatchIn(error.message.orEmpty()))
                            availableBooks[url]=System.currentTimeMillis() to false
                        false
                    },
                )
            }
            item.takeIf { available }
        }) }
        return futures.mapNotNull { it.get() }
    }
    private fun message(e:Throwable):String {
        if(e is InterruptedException)Thread.currentThread().interrupt()
        return (e.message ?: if(e is InterruptedException)"请求已取消" else "小说源请求失败").take(240)
    }
    fun sourceId(href: String)=Regex("[#&]smyNative22=([^&]+)").find(href)?.groupValues?.get(1)
    fun typeOf(href: String)=sourceId(href)?.let { entry(it)?.type } ?: 0
    fun originalUrl(href: String)=raw(href).substringBefore(",{").substringBefore(", {")
    fun home(href: String)=sourceId(href)?.let { entry(it)?.url }?.takeIf { safeSourceUrl(it) } ?: originalUrl(href)
    private fun raw(href: String)=Regex("[&]smyTarget=([^&]+)").find(href)?.groupValues?.get(1)?.let { URLDecoder.decode(it,"UTF-8") } ?: href.substringBefore("#smyNative22=")
    private fun bookUrl(href: String)=Regex("[&]smyBook=([^&]+)").find(href)?.groupValues?.get(1)?.let { URLDecoder.decode(it,"UTF-8") } ?: raw(href)
    internal fun tagged(url: String,id: String,book: String=url): String {
        val target=raw(url)
        val base=if(target.startsWith("data:"))"https://legado-data.invalid/data" else target.substringBefore('#')
        return base.substringBefore(",{")+"#smyNative22=$id&smyBook="+URLEncoder.encode(book,"UTF-8")+"&smyTarget="+URLEncoder.encode(target,"UTF-8")
    }
    internal fun imageUrl(url: String,referer: String): String {
        if(url.isBlank())return ""
        return url.substringBefore(",{").substringBefore(", {")+ (if('#' in url)"&" else "#")+"smyReferer="+URLEncoder.encode(referer,"UTF-8")
    }
    internal fun digest(value:String):String {
        val bytes=MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        val hex="0123456789abcdef"
        return String(CharArray(20){i->val n=bytes[i/2].toInt() and 255;hex[if(i%2==0)n ushr 4 else n and 15]})
    }
    internal fun readableUrl(e:Engine,value:String):String? = runCatching { e.absolute(value) }.getOrNull()?.takeIf { safeSourceUrl(it) }
    internal fun nextPage(e:Engine,page:String,rule:String,visited:Set<String>):String {
        return e.value(page,rule).lineSequence().map { it.trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("javascript:",true) }
            .mapNotNull { runCatching { e.absolute(it).substringBefore('#') }.getOrNull() }
            .firstOrNull { safeSourceUrl(it) && it !in visited }.orEmpty()
    }
    fun detail(href: String): Pair<FetchedWork?,String?> = runCatching {
        val id=requireNotNull(sourceId(href));val meta=requireNotNull(entry(id))
        val e=Engine(requireNotNull(rule(id)),book=raw(href))
        var page=e.load(raw(href));val r=e.source.optJSONObject("ruleBookInfo") ?: JSONObject()
        r.optString("init").takeIf { it.isNotBlank() }?.let { page=e.value(page,it).ifBlank { page } }
        fun info(key:String)=runCatching { e.value(page,r.optString(key)) }.getOrDefault("")
        val title=info("name");val author=info("author");val cover=imageUrl(e.absolute(info("coverUrl")),e.base);val intro=e.clean(info("intro"));val tags=info("kind").split('\n').filter { it.isNotBlank() }
        val toc=info("tocUrl");if(toc.isNotBlank())page=e.load(toc)
        val t=e.source.optJSONObject("ruleToc") ?: JSONObject();val chapters=ArrayList<CachedChapter>();val chapterUrls=HashSet<String>();val visited=HashSet<String>();var round=0
        do {
            visited.add(e.base)
            for(node in e.select(page,t.optString("chapterList"))) {
                val name=e.value(node,t.optString("chapterName"));val link=e.value(node,t.optString("chapterUrl")).ifBlank { if(meta.type!=0)e.base else "" }
                if(name.isNotBlank() && link.isNotBlank() && e.value(node,t.optString("isVolume"))!="true") {
                    val url=readableUrl(e,link) ?: continue
                    if(chapterUrls.add(url))chapters.add(CachedChapter("lg-${digest(url)}",name,tagged(url,id,raw(href)),chapters.size))
                }
            }
            val url=nextPage(e,page,t.optString("nextTocUrl"),visited)
            if(url.isBlank() || url in visited)break
            check(++round<100){"目录分页过多，请在网站查看完整目录"};page=e.load(url)
        }while(true)
        if(chapters.isEmpty() && meta.type!=0)chapters.add(CachedChapter("lg-${digest(href)}",if(meta.type==3)"打开文件" else "打开内容",href,0))
        FetchedWork(author,tags,cover,intro,chapters,title) to if(chapters.isEmpty())"未解析到目录；请检查源设置、登录状态或网站是否失效" else null
    }.getOrElse { null to message(it) }
    fun text(href: String): Pair<String,String?> = runCatching {
        val id=requireNotNull(sourceId(href));val e=Engine(requireNotNull(rule(id)),book=bookUrl(href));val r=e.source.optJSONObject("ruleContent") ?: JSONObject()
        val parts=ArrayList<String>();val visited=HashSet<String>();var url=raw(href);var count=0
        while(url.isNotBlank() && visited.add(url) && count++<20) {
            val page=e.load(url);visited.add(e.base);var text=e.clean(e.value(page,r.optString("content")))
            r.optString("replaceRegex").takeIf { it.isNotBlank() }?.let { text=e.replace(text,it) }
            if(text.isNotBlank())parts.add(text)
            url=nextPage(e,page,r.optString("nextContentUrl"),visited)
        }
        val content=parts.joinToString("\n\n");content to if(content.isBlank())"未解析到正文；该源可能需要登录、验证或专用脚本支持" else null
    }.getOrElse { "" to message(it) }
    class Engine(source: JSONObject, key: String = "", book: String = "") {
        val source: JSONObject = source
        val key: String = key
        val book: String = book
        var pageNumber=1
        var lastListingRule=""
            private set
        var nextListingLink=""
            private set
        var base = source.optString("bookSourceUrl").takeIf { it.startsWith("http://") || it.startsWith("https://") } ?: "https://legado-source.invalid/"
        private var document = ""
        private val vars = ConcurrentHashMap<String, String>()
        private val variablePrefix = "book:${digest(source.optString("bookSourceUrl"))}:${digest(book.ifBlank { source.optString("bookSourceUrl") })}:"
        private fun optional(node: Any, rule: String) = runCatching { value(node,rule) }.getOrDefault("")
        private var scope: org.mozilla.javascript.Scriptable? = null
        private var deadline = 0L
        private var requests = 0
        private var responseHeaders = emptyMap<String,String>()
        private val factory = object : org.mozilla.javascript.ContextFactory() {
            override fun makeContext(): JsContext = super.makeContext().apply {
                optimizationLevel = -1
                languageVersion = JsContext.VERSION_ES6
                instructionObserverThreshold = 10000
                setClassShutter { false }
            }
            override fun observeInstructionCount(cx: JsContext, instructionCount: Int) {
                check(System.currentTimeMillis() < deadline) { "书源脚本运行超时" }
            }
        }
        private fun <T> inJs(block: (JsContext, org.mozilla.javascript.Scriptable) -> T): T {
            deadline = System.currentTimeMillis() + 60_000
            val cx = factory.enterContext()
            try {
                val s = scope ?: cx.initSafeStandardObjects().also { scope = it
                    val bridge = cx.newObject(it)
                    fun expose(name: String, fn: (Array<out Any>) -> Any?) {
                        ScriptableObject.putProperty(bridge, name, object : org.mozilla.javascript.BaseFunction() {
                            override fun call(c: JsContext, sc: org.mozilla.javascript.Scriptable, th: org.mozilla.javascript.Scriptable, args: Array<out Any>): Any? = fn(args)
                        })
                    }
                    fun arg(a: Array<out Any>, i: Int = 0) = a.getOrNull(i)?.let { JsContext.toString(it) }.orEmpty()
                    expose("ajax") { a -> ajax(arg(a)) }
                    expose("put") { a -> put(arg(a),arg(a,1)); "" }
                    expose("get") { a -> get(arg(a)) }
                    expose("getString") { a -> getString(arg(a)) }
                    expose("getElements") { a -> getElements(arg(a)) }
                    expose("setContent") { a -> setContent(arg(a)); "" }
                    expose("getCookie") { a -> getCookie(arg(a)) }
                    expose("setCookie") { a -> setCookie(arg(a),arg(a,1)); "" }
                    expose("base64Decode") { a -> base64Decode(arg(a)) }
                    expose("hexDecodeToString") { a -> hexDecodeToString(arg(a)) }
                    expose("base64Encode") { a -> java.util.Base64.getEncoder().encodeToString(arg(a).toByteArray()) }
                    expose("encodeURI") { a -> URLEncoder.encode(arg(a),arg(a,1).ifBlank { "UTF-8" }) }
                    expose("timeFormat") { a -> java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA).format(java.util.Date(arg(a).toDouble().toLong())) }
                    expose("sourceVariable") { _ -> prefs?.getString("variable-${source.optString("_smyId").ifBlank { digest(source.optString("bookSourceUrl")) }}", "").orEmpty() }
                    expose("setSourceVariable") { a -> prefs?.edit()?.putString("variable-${source.optString("_smyId").ifBlank { digest(source.optString("bookSourceUrl")) }}",arg(a))?.apply(); "" }
                    expose("loginInfo") { _ -> loginInfo(source.optString("_smyId").ifBlank { digest(source.optString("bookSourceUrl")) }) }
                    expose("digest") { a -> MessageDigest.getInstance(arg(a,1).ifBlank { "MD5" }).digest(arg(a).toByteArray()).joinToString("") { "%02x".format(it) } }
                    expose("uuid") { _ -> java.util.UUID.randomUUID().toString() }
                    expose("getStringList") { a -> JSONArray(select(document,arg(a),scalar=true).map { it.toString() }).toString() }
                    expose("base64Bytes") { a -> JSONArray(java.util.Base64.getMimeDecoder().decode(arg(a)).map { it.toInt() }).toString() }
                    expose("hmac") { a -> javax.crypto.Mac.getInstance(arg(a,2).ifBlank { "HmacSHA256" }).run { init(javax.crypto.spec.SecretKeySpec(arg(a,1).toByteArray(),algorithm));doFinal(arg(a).toByteArray()).joinToString("") { "%02x".format(it) } } }
                    expose("aesDecode") { a -> val cipher=javax.crypto.Cipher.getInstance(arg(a,2).ifBlank { "AES/CBC/PKCS5Padding" });val key=javax.crypto.spec.SecretKeySpec(arg(a,1).toByteArray(),"AES");if(arg(a,3).isBlank())cipher.init(javax.crypto.Cipher.DECRYPT_MODE,key) else cipher.init(javax.crypto.Cipher.DECRYPT_MODE,key,javax.crypto.spec.IvParameterSpec(arg(a,3).toByteArray()));String(cipher.doFinal(java.util.Base64.getMimeDecoder().decode(arg(a)))) }
                    expose("attr") { a -> Jsoup.parse(arg(a)).selectFirst("body > *")?.attr(arg(a,1)).orEmpty() }
                    expose("post") { a -> ajax(arg(a)+","+JSONObject().put("method","POST").put("body",arg(a,1)).put("headers",JSONObject(arg(a,2))).toString()) }
                    expose("header") { a -> responseHeaders.entries.firstOrNull { it.key.equals(arg(a),true) }?.value.orEmpty() }
                    ScriptableObject.putProperty(it, "bridge", bridge)
                    cx.evaluateString(it, """
                        var java={ajax:function(u){return String(bridge.ajax(String(u)))}, httpGet:function(u){return {body:function(){return java.ajax(u)}}},
                        put:function(k,v){bridge.put(String(k),String(v));return v},get:function(k,h){if(/^https?:/.test(k)){var b=java.ajax(k+(h?','+JSON.stringify({headers:h}):''));return {body:function(){return b},header:function(n){return String(bridge.header(n))},cookie:function(n){var c=String(bridge.getCookie(k));var m=c.match(new RegExp('(?:^|; *)'+n+'=([^;]*)'));return m?m[1]:''}};}return String(bridge.get(String(k)))},
                        post:function(u,b,h){var text=String(bridge.post(u,b,JSON.stringify(h||{})));return {body:function(){return text},header:function(n){return String(bridge.header(n))}}},
                        md5Encode:function(v){return String(bridge.digest(String(v),'MD5'))},sha256Encode:function(v){return String(bridge.digest(String(v),'SHA-256'))},
                        randomUUID:function(){return String(bridge.uuid())},getStringList:function(r){var a=JSON.parse(String(bridge.getStringList(String(r))));a.toArray=function(){return a};return a},
                        base64DecodeToByteArray:function(v){var a=JSON.parse(String(bridge.base64Bytes(String(v))));a.toArray=function(){return a};return a},
                        HMacHex:function(v,k,a){return String(bridge.hmac(String(v),String(k),a||'HmacSHA256'))},
                        aesBase64DecodeToString:function(v,k,a,iv){return String(bridge.aesDecode(String(v),String(k),a||'AES/CBC/PKCS5Padding',iv||''))},
                        base64Encode:function(v){return String(bridge.base64Encode(String(v)))},encodeURI:function(v,c){return String(bridge.encodeURI(String(v),c||'UTF-8'))},
                        androidId:function(){return 'shumanyu'},timeFormat:function(t){return String(bridge.timeFormat(t))},log:function(v){},
                        getCookie:function(u,n){var c=String(bridge.getCookie(u));var m=c.match(new RegExp('(?:^|; *)'+n+'=([^;]*)'));return m?m[1]:''},
                        getVerificationCode:function(u){throw new Error('此源需要验证码，请打开原网站完成验证后重试')},
                        getString:function(r){return String(bridge.getString(String(r)))},getElements:function(r){var a=JSON.parse(String(bridge.getElements(String(r))));a=a.map(function(x){return {toString:function(){return x},toJSON:function(){return x},attr:function(k){return String(bridge.attr(x,k))}}});a.toArray=function(){return a};return a},
                        getElement:function(r){return java.getElements(r)},setContent:function(v){bridge.setContent(String(v))},
                        toast:function(v){},longToast:function(v){},startBrowserAwait:function(u,t){throw new Error('需要网页验证，请在软件内打开该源网站完成验证：'+u)},
                        base64Decode:function(v){return String(bridge.base64Decode(String(v)))},hexDecodeToString:function(v){return String(bridge.hexDecodeToString(String(v)))}};
                        var cookie={getCookie:function(u){return String(bridge.getCookie(String(u)))},setCookie:function(u,v){bridge.setCookie(String(u),String(v))},removeCookie:function(u){bridge.setCookie(String(u),'')}};
                        var source=JSON.parse(${JSONObject.quote(source.toString())});source.getKey=function(){return source.bookSourceUrl};source.getVariable=function(){return String(bridge.sourceVariable())};source.setVariable=function(v){bridge.setSourceVariable(String(v));return v};source.getLoginInfoMap=function(){return JSON.parse(String(bridge.loginInfo()))};source.getLoginInfo=function(){return JSON.stringify(source.getLoginInfoMap())};
                        var book={origin:source.bookSourceUrl,bookUrl:${JSONObject.quote(book)},durChapterTitle:''};
                        var key=${JSONObject.quote(key)},page=1,result='';
                    """.trimIndent(), "source-bridge", 1, null)
                }
                ScriptableObject.putProperty(s, "baseUrl", base)
                ScriptableObject.putProperty(s, "page", pageNumber)
                return block(cx, s)
            } finally { JsContext.exit() }
        }
        private fun js(code: String, input: Any? = ""): Any? = inJs { cx, s ->
            val v = if (input is List<*>) cx.newArray(s, input.toTypedArray()) else input?.toString().orEmpty()
            ScriptableObject.putProperty(s, "result", v)
            val result = cx.evaluateString(s, code, "source-rule", 1, null)
            unwrap(if (result === org.mozilla.javascript.Undefined.instance) ScriptableObject.getProperty(s,"result") else result)
        }
        private fun unwrap(v: Any?): Any? = when(v) {
            null, org.mozilla.javascript.Undefined.instance -> ""
            is NativeArray -> (0 until v.length.toInt()).map { unwrap(v.get(it, v)) }
            is Wrapper -> unwrap(v.unwrap())
            is org.mozilla.javascript.Scriptable -> inJs { cx, s ->
                ScriptableObject.putProperty(s,"_smyValue",v)
                cx.evaluateString(s,"JSON.stringify(_smyValue)","json",1,null).toString()
            }
            else -> v.toString()
        }
        private fun strings(v: Any?): List<Any> = when(v) { is List<*> -> v.filterNotNull().map { if(it is Map<*,*>) JSONObject(it).toString() else it }; null -> emptyList(); else -> listOf(v) }
        fun absolute(value: String): String = if (value.isBlank()) "" else if (value.startsWith("data:")) value else URL(URL(base), value.trim()).toString()
        fun mediaHeaders(): Map<String,String> {
            val result=linkedMapOf("User-Agent" to Crawler.UA,"Referer" to base)
            runCatching { JSONObject(source.optString("header")) }.getOrNull()?.let { h -> h.keys().forEach { result[it]=h.getString(it) } }
            getCookie(base).takeIf { it.isNotBlank() }?.let { result["Cookie"]=it }
            return result
        }
        private fun serialRequest(url:String,changeBase:Boolean):String {
            val gate=sourceRequests.computeIfAbsent(source.optString("bookSourceUrl")) { java.util.concurrent.locks.ReentrantLock(true) }
            gate.lockInterruptibly()
            try { return request(url,changeBase) }finally { gate.unlock() }
        }
        fun ajax(url: String) = serialRequest(url, false)
        fun load(url: String): String = serialRequest(url, true)
        fun put(k: String, v: String) { vars[k] = v; prefs?.edit()?.putString(variablePrefix+k,v)?.apply() }
        fun get(k: String) = vars[k] ?: prefs?.getString(variablePrefix+k, "").orEmpty()
        fun setContent(value: String) { document = value }
        fun getString(rule: String) = value(document, rule)
        fun getElements(rule: String) = JSONArray(select(document, rule).map { it.toString() }).toString()
        fun getCookie(url: String) = WebCookies.get(url)
        fun setCookie(url: String, cookie: String) { WebCookies.set(url, cookie) }
        fun base64Decode(value: String) = String(java.util.Base64.getMimeDecoder().decode(value))
        fun hexDecodeToString(value: String) = value.chunked(2).map { it.toInt(16).toByte() }.toByteArray().toString(Charsets.UTF_8)
        private fun interpolate(s: String, node: Any = document): String = Regex("\\{\\{([\\s\\S]*?)\\}\\}").replace(s) {
            val expression = it.groupValues[1]
            when {
                expression == "key" -> URLEncoder.encode(key, if (s.contains("gbk",true) || s.contains("gb2312",true)) "GBK" else "UTF-8")
                expression.startsWith("@@") -> value(node, expression.removePrefix("@@"))
                expression.startsWith("$.") -> value(node, expression)
                else -> js(expression, node)?.toString().orEmpty()
            }
        }
        fun requestRule(rule: String): String {
            val input = interpolate(Regex("@get:\\{([^}]+)\\}").replace(rule) { get(it.groupValues[1]) })
            return when {
                input.startsWith("@js:") -> js(input.removePrefix("@js:"))?.toString().orEmpty()
                input.startsWith("<js>") -> {
                    val end = input.indexOf("</js>")
                    val v = js(input.substring(4, end))?.toString().orEmpty()
                    input.substring(end + 5).ifBlank { v }
                }
                else -> input
            }
        }
        private fun request(spec: String, changeBase: Boolean, redirects: Int = 0, retry:Int=0): String {
            check(redirects <= 8) { "网站跳转次数过多" }
            check(!Thread.currentThread().isInterrupted) { "请求已取消" }
            check(++requests <= 160) { "书源请求次数超过上限" }
            val expanded = interpolate(Regex("@get:\\{([^}]+)\\}").replace(spec) { get(it.groupValues[1]) })
            if (expanded.startsWith("data:;base64,")) {
                return java.util.Base64.getDecoder().decode(expanded.removePrefix("data:;base64,").substringBefore(",{")).joinToString("") { "%02x".format(it) }
            }
            val split = expanded.indexOf(",{").takeIf { it >= 0 } ?: expanded.indexOf(", {")
            val path = if (split >= 0) expanded.substring(0, split) else expanded
            val options = if (split >= 0) JSONObject(expanded.substring(split + 1).trim()) else JSONObject()
            val target = absolute(path)
            check(safeSourceUrl(target)) { "书源请求地址不安全" }
            val conn = URL(target).openConnection() as HttpURLConnection
            conn.instanceFollowRedirects = false
            conn.connectTimeout = 12000; conn.readTimeout = 15000
            conn.setRequestProperty("User-Agent", Crawler.UA)
            conn.setRequestProperty("Referer", base)
            getCookie(target).takeIf { it.isNotBlank() }?.let { conn.setRequestProperty("Cookie", it) }
            val defaultHeaders = runCatching { JSONObject(requestRule(source.optString("header"))) }.getOrNull()
            defaultHeaders?.keys()?.forEach { conn.setRequestProperty(it,defaultHeaders.getString(it)) }
            val headers = options.optJSONObject("headers")
            headers?.keys()?.forEach { conn.setRequestProperty(it,headers.getString(it)) }
            conn.requestMethod = options.optString("method", "GET").uppercase()
            val charset = Charset.forName(options.optString("charset", "UTF-8"))
            try {
                if (conn.requestMethod == "POST") {
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=${charset.name()}")
                    conn.outputStream.use { it.write(options.optString("body").toByteArray(charset)) }
                }
                val code = conn.responseCode
                if(code in listOf(301,302,303,307,308)) {
                    val next=URL(URL(target),conn.getHeaderField("Location") ?: error("网站跳转缺少地址")).toString()
                    val before=URL(target);val after=URL(next)
                    val canonical=before.host!=after.host && before.file==after.file
                    val preserve=code==307 || code==308 || (code!=303 && canonical)
                    val nextOptions=JSONObject(options.toString())
                    if(!preserve) { nextOptions.put("method","GET");nextOptions.remove("body") }
                    return request(next+","+nextOptions.toString(),changeBase,redirects+1)
                }
                if(code in listOf(502,503,504) && retry<1) {
                    conn.disconnect()
                    return request(spec,changeBase,redirects,retry+1)
                }
                responseHeaders = conn.headerFields.filterKeys { it != null }.mapValues { it.value.joinToString("; ") }
                check(code in 200..299) { "网站返回 HTTP $code，可能需要验证或源已失效" }
                conn.headerFields.filterKeys { it.equals("Set-Cookie", true) }.values.flatten().forEach { setCookie(target, it) }
                val bytes = conn.inputStream.use { input ->
                    val out = java.io.ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    while (true) {
                        check(!Thread.currentThread().isInterrupted) { "请求已取消" }
                        val n = input.read(buffer)
                        if (n < 0) break
                        check(out.size() + n <= 12_000_000) { "书源响应过大" }
                        out.write(buffer,0,n)
                    }
                    out.toByteArray()
                }
                check(bytes.size <= 12_000_000) { "书源响应过大" }
                val encoding = Regex("charset=[\"']?([\\w-]+)", RegexOption.IGNORE_CASE).find(conn.contentType.orEmpty())?.groupValues?.get(1)
                    ?: Regex("charset=[\"']?([\\w-]+)", RegexOption.IGNORE_CASE).find(String(bytes.take(2048).toByteArray(), Charsets.ISO_8859_1))?.groupValues?.get(1)
                val body = String(bytes, runCatching { Charset.forName(encoding ?: charset.name()) }.getOrDefault(charset))
                if (changeBase) { base = conn.url.toString(); document = body }
                return body
            } catch(error:java.io.IOException) {
                if(retry>=1 || Thread.currentThread().isInterrupted)throw error
                conn.disconnect()
                return request(spec,changeBase,redirects,retry+1)
            } finally { conn.disconnect() }
        }
        fun explore(): List<Pair<String, String>> {
            val raw = source.optString("exploreUrl").takeUnless { it == "null" }.orEmpty()
            if (raw.isBlank()) return emptyList()
            val expanded = if (raw.startsWith("@js:")) js(raw.removePrefix("@js:"))?.toString().orEmpty() else raw
            if (expanded.trim().startsWith("[")) {
                val a = JSONArray(expanded)
                return (0 until a.length()).map { a.getJSONObject(it) }.filter { it.optString("url").isNotBlank() && it.optString("title").isNotBlank() }.map { it.optString("title") to it.optString("url") }
            }
            return expanded.lines().mapNotNull { line -> val p = line.split("::", limit=2); if(p.size==2 && p[1].isNotBlank()) p[0] to p[1] else null }
        }
        fun browse(id: String): List<CrawledWork> {
            val choices=explore().sortedBy { (title,_) -> when {
                title.contains("玄幻") -> 0
                title.contains("推荐") || title.contains("热门") -> 1
                else -> 2
            } }
            var failure:Throwable?=null
            for((_,url) in choices.take(3)) {
                check(!Thread.currentThread().isInterrupted) { "请求已取消" }
                val items=runCatching { books(url,"ruleExplore",id) }.onFailure { failure=it;AppLog.add("native22",it.message ?: "加载失败") }.getOrDefault(emptyList())
                if(items.isNotEmpty())return items
            }
            failure?.let { throw it }
            return emptyList()
        }
        fun books(url: String, ruleName: String, id: String): List<CrawledWork> {
            val page = load(url)
            check(!(page.contains("id=\"cfts\"") && page.contains("turnstile.render"))) { "此搜索需要网站人机验证，请打开网站验证后重试；已加载的书目仍可搜索" }
            lastListingRule=url
            nextListingLink=Jsoup.parse(page,base).select("a[href]").firstOrNull { a ->
                a.attr("rel").equals("next",true) || a.text().trim() in listOf("下一页","下一頁","下页","下頁")
            }?.attr("abs:href")?.takeIf { safeSourceUrl(it) && it!=base }.orEmpty()
            if(nextListingLink.isBlank() && url.contains("{{page}}")) {
                val lastPage=Regex("""maxpage\s*=\s*parseInt\(["'](\d+)["']\)""").find(page)?.groupValues?.get(1)?.toIntOrNull() ?: 1
                if(pageNumber<lastPage)nextListingLink=url
            }
            val r = source.optJSONObject(ruleName)?.takeIf { it.optString("bookList").isNotBlank() } ?: source.optJSONObject("ruleSearch") ?: JSONObject()
            val nodes=select(page,r.optString("bookList"))
            if(nodes.isEmpty() && ruleName=="ruleSearch") {
                val info=source.optJSONObject("ruleBookInfo")
                if(info!=null) {
                    val title=optional(page,info.optString("name"))
                    if(title.isNotBlank())return listOf(CrawledWork("xn-${digest(id+base)}",id,title,imageUrl(absolute(optional(page,info.optString("coverUrl"))),base),tagged(base,id),
                        author=optional(page,info.optString("author")),genres=typeName(source.optInt("bookSourceType")),description=clean(optional(page,info.optString("intro")))))
                }
            }
            val parsed=nodes.mapNotNull { node ->
                val title = value(node, r.optString("name"))
                val link = value(node, r.optString("bookUrl"))
                if (title.isBlank() || link.isBlank()) null else {
                    val href = absolute(link)
                    CrawledWork("xn-${digest(id + href)}", id, title, imageUrl(absolute(optional(node,r.optString("coverUrl"))),base), tagged(href,id),
                        author=optional(node,r.optString("author")), genres=typeName(source.optInt("bookSourceType")), tags=optional(node,r.optString("kind")).split('\n').filter { it.isNotBlank() }, description=clean(optional(node,r.optString("intro"))))
                }
            }.distinctBy { it.id }
            return if(source.optString("bookSourceName")=="阅友小说")availableYueBooks(parsed,source) else parsed
        }
        fun clean(raw: String): String {
            val html = raw.replace(Regex("(?i)<br\\s*/?>|</p>|</div>"), "\n")
            val d = Jsoup.parse(html)
            d.select("script,style").remove()
            return d.wholeText().replace('\u00a0', ' ').lines().map { it.trim() }.filter { it.isNotBlank() }.joinToString("\n")
        }
        fun replace(text: String, spec: String): String {
            val parts = interpolate(spec).split("##")
            val offset = if (parts.firstOrNull().isNullOrBlank()) 1 else 0
            val pattern = parts.getOrNull(offset).orEmpty()
            return if (pattern.isBlank()) text else Regex(pattern).replace(text, parts.getOrNull(offset+1).orEmpty())
        }
        fun value(node: Any, rule: String): String = select(node, rule, scalar = true).joinToString("\n") { it.toString() }.trim()
        fun select(node: Any, input: String, scalar: Boolean = false): List<Any> {
            if (input.isBlank()) return emptyList()
            var rule = input.trim()
            if(rule.startsWith("-")) return select(node,rule.drop(1),scalar).reversed()
            if(rule.startsWith("+")) rule=rule.drop(1)
            if(rule.startsWith("@json:")) rule=rule.removePrefix("@json:")
            var current: Any = node
            // JS blocks may transform a page before a selector, or replace it entirely.
            if (rule.startsWith("<js>")) {
                val end = rule.indexOf("</js>")
                require(end >= 0) { "书源脚本缺少结束标记" }
                current = js(interpolate(rule.substring(4,end),node),node) ?: ""
                rule = rule.substring(end+5)
                if (rule.isBlank()) return strings(current)
            }
            val jsAt = rule.indexOf("@js:")
            if (jsAt >= 0) {
                val before = rule.substring(0,jsAt)
                val selected = if(before.isBlank()) current else select(current,before,scalar).let { if(scalar) it.joinToString("\n") else it }
                var code = rule.substring(jsAt+4)
                val pattern = source.optJSONObject("ruleToc")?.optString("chapterList").orEmpty()
                if (pattern.startsWith(":")) {
                    Regex(pattern.drop(1)).find(node.toString())?.let { match ->
                        code = Regex("\\$(\\d+)").replace(code) { token -> match.groupValues.getOrNull(token.groupValues[1].toInt()).orEmpty() }
                    }
                }
                return strings(js(interpolate(code,node), selected))
            }
            val inline = rule.indexOf("<js>")
            if (inline >= 0) {
                val end = rule.indexOf("</js>",inline)
                val selected = select(current,rule.substring(0,inline),scalar).let { if(scalar) it.joinToString("\n") else it }
                return strings(js(interpolate(rule.substring(inline+4,if(end<0)rule.length else end),node),selected))
            }
            val rep = rule.indexOf("##")
            if(rep>=0) return select(current,rule.substring(0,rep),scalar).map { replace(it.toString(),rule.substring(rep)) }
            if ("&&" in rule) return rule.split("&&").flatMap { select(current,it,scalar) }
            if ("||" in rule) return rule.split("||").asSequence().map { select(current,it,scalar) }.firstOrNull { it.isNotEmpty() } ?: emptyList()
            if (rule.startsWith("@put:")) {
                val end=rule.indexOf('}')
                val p=JSONObject(rule.substring(5,end+1));p.keys().forEach { put(it,value(current,p.getString(it))) }
                return select(current,rule.substring(end+1))
            }
            if ("{{" in rule) return listOf(interpolate(rule,current))
            if (rule.startsWith("http://") || rule.startsWith("https://")) return listOf(interpolate(rule,current))
            if (rule.startsWith("$." ) || rule.startsWith("$[" ) || rule == "$") {
                val json = if(current is Map<*,*> || current is List<*>) current else current.toString()
                val result: Any? = if(json is String) JsonPath.read<Any?>(json,rule) else JsonPath.read<Any?>(json,rule)
                return strings(result).map { if(it is Map<*,*>) JSONObject(it).toString() else it }
            }
            if(rule.startsWith(":")) return Regex(rule.substring(1)).findAll(current.toString()).map { it.value }.toList()
            if(Regex("\\$\\d+").matches(rule)) {
                val regex = source.optJSONObject("ruleToc")?.optString("chapterList").orEmpty().removePrefix(":")
                return listOf(Regex(regex).find(current.toString())?.groupValues?.getOrNull(rule.drop(1).toInt()).orEmpty())
            }
            if (current.toString().trimStart().startsWith("{") && Regex("[a-zA-Z_][\\w.\\[\\]\\*]*").matches(rule)) {
                return select(current, "$." + rule)
            }
            val element = if(current is Element) current else Jsoup.parse(current.toString(),base)
            if (input.startsWith("@") && !input.startsWith("@@") && !input.startsWith("@css:")) {
                val attribute = rule.removePrefix("@")
                val candidate = if (current is Element) current else element.selectFirst("body > *")
                if (candidate != null && candidate.hasAttr(attribute)) return listOf(candidate.attr(attribute))
            }
            if(rule.startsWith("//")) {
                val attr=Regex("/@([\\w:-]+)$").find(rule)
                val xpath=if(attr!=null)rule.substring(0,attr.range.first) else rule
                return element.selectXpath(xpath).map { if(attr!=null)it.attr(attr.groupValues[1]) else it }
            }
            val steps = rule.removePrefix("@css:").removePrefix("@@").split('@').filter { it.isNotBlank() }
            var nodes: List<Element> = listOf(element)
            for ((idx,step) in steps.withIndex()) {
                val last=idx==steps.lastIndex
                if(last && step in listOf("text","html","ownText","textNodes","href","src","content","data-src","onclick")) return nodes.map { when(step) {
                    "text"->it.text();"html"->it.html();"ownText"->it.ownText();"textNodes"->it.textNodes().joinToString("\n") { t->t.text() };else->it.attr(step)
                }}.filter { it.isNotBlank() }
                if(last && !step.startsWith(".") && !step.startsWith("#") && nodes.firstOrNull()?.hasAttr(step) == true) return nodes.map { it.attr(step) }
                nodes = nodes.flatMap { selectElements(it,step) }
            }
            return nodes
        }
        private fun selectElements(node: Element, step: String): List<Element> {
            if(step=="children") return node.children().toList()
            var s=step.replace(Regex("\\[(-?\\d+(?::-?\\d+)?)\\]$"), ".$1")
            var range: String?=null
            val suffix=Regex("\\.(!?-?\\d+(?::-?\\d+)?(?:!-?\\d+)?)$").find(s)
            if(suffix!=null){range=suffix.groupValues[1];s=s.substring(0,suffix.range.first)}
            val exclude=Regex("!(-?\\d+)$").find(s)
            if(exclude!=null){range="!"+exclude.groupValues[1];s=s.substring(0,exclude.range.first)}
            val selected=when {
                s.startsWith("class.")->node.getElementsByClass(s.removePrefix("class.")).toList()
                s.startsWith("id.")->node.select("[id=\"${s.removePrefix("id.")}\"]").toList()
                s.startsWith("tag.")->node.getElementsByTag(s.removePrefix("tag.")).toList()
                s.startsWith("text.")->node.getElementsContainingOwnText(s.removePrefix("text.")).toList()
                else->node.select(s).toList()
            }
            if(range==null)return selected
            fun index(n: String): Int { val i=n.toInt();return if(i<0)selected.size+i else i }
            if(range.startsWith("!")) {
                val excluded = range.drop(1).split(':')
                val a=index(excluded[0]); val b=excluded.getOrNull(1)?.let { index(it) } ?: a
                return selected.filterIndexed { n,_->n !in a..b }
            }
            if(':' in range){val p=range.split(':');val a=index(p[0]);val b=index(p[1]);return (if(a<=b)a..b else a downTo b).mapNotNull { selected.getOrNull(it) }}
            return listOfNotNull(selected.getOrNull(index(range)))
        }
    }
}
