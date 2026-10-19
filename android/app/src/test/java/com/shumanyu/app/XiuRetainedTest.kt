package com.shumanyu.app
import org.junit.Test
import org.junit.Assert.*
import org.json.*
import java.io.File
class XiuRetainedTest {
 @Test fun retainedRulesMatchVerifiedNames() {
  val a=JSONArray(File("src/main/assets/sources/xiu22.json").readText())
  val names=(0 until a.length()).map { a.getJSONObject(it).getString("bookSourceName") }
  assertEquals(listOf("速读谷","阅友小说"),names)
  assertEquals(names.size,names.distinct().size)
  assertTrue(names.none { it=="笔趣阁" || it=="笔趣阁二" || it=="笔趣阁2" })
 }
}
