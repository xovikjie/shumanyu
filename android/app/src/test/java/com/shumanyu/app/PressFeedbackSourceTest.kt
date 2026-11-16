package com.shumanyu.app

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * 按压效果统一为 COUI 样式的守护测试：源码里每个 clickable / combinedClickable / toggleable / selectable
 * 都必须显式 indication = null（反馈由 couiClickable / couiButtonFeedback 绘制），
 * 否则会退回 Compose 默认 indication——按下时整块发黑的矩形（用户看到的「黑色描边」）。
 */
class PressFeedbackSourceTest {
    @Test fun noDefaultIndicationAnywhere() {
        val dir = listOf(File("src/main/java/com/shumanyu/app"), File("app/src/main/java/com/shumanyu/app")).first { it.isDirectory }
        val bad = ArrayList<String>()
        val call = Regex("""\.(clickable|combinedClickable|toggleable|selectable)\s*[({]""")
        dir.listFiles { f -> f.extension == "kt" }!!.forEach { f ->
            val text = f.readText()
            call.findAll(text).forEach { m ->
                val tail = text.substring(m.range.first, minOf(text.length, m.range.first + 260))
                val explicitNull = tail.contains("indication = null") || tail.contains("indication=null") ||
                    Regex("""^\.(toggleable|selectable)\([^,]+,\s*\w+,\s*null""").containsMatchIn(tail)
                if (!explicitNull) bad += "${f.name}:${text.substring(0, m.range.first).count { it == '\n' } + 1}"
            }
        }
        assertTrue("这些地方用了默认 indication（会出现黑色按压块）：$bad", bad.isEmpty())
    }
}
