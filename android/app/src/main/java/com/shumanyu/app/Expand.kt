package com.shumanyu.app

import android.os.SystemClock
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.util.lerp

/**
 * 一镜到底展开 / 收回（应用内实现，不依赖系统能力）。
 *
 * 可点元素用 [expandSource] 标记：按下的瞬间（不消费事件）记下它在根布局里的真实矩形和圆角。
 * 紧接着发生的「进入下一级页面」就从这个矩形展开到全屏；返回时收回到同一位置。
 * 元素已滑出屏幕、太小、按下过久之前，或根本没有标记时，退回普通转场（淡入 + 轻微缩放）。
 */
internal data class Origin(val rect: Rect, val radius: Float, val underToken: String = "")

internal object ExpandOrigin {
    private var rect: Rect? = null
    private var radius = 0f
    private var at = 0L

    /** 最近一次按下的有效期：超过就视为与本次跳转无关。 */
    private const val FRESH_MS = 1500L

    fun arm(r: Rect, radiusPx: Float) {
        rect = r
        radius = radiusPx
        at = SystemClock.uptimeMillis()
    }

    /** 取出并清空待用的起点；[screen] 为根布局尺寸，用于判断元素是否仍在屏幕内。 */
    fun take(screen: Size): Origin? {
        val r = rect ?: return null
        rect = null
        if (SystemClock.uptimeMillis() - at > FRESH_MS) return null
        return Origin(r, radius).takeIf { it.usable(screen) }
    }

    fun clear() { rect = null }
}

/** 起点至少有一半面积在屏幕内、且不是零尺寸，才做一镜到底；否则走普通转场。 */
internal fun Origin.usable(screen: Size): Boolean {
    if (screen.width <= 0f || screen.height <= 0f) return false
    if (rect.width < 8f || rect.height < 8f) return false
    val vis = rect.intersect(Rect(0f, 0f, screen.width, screen.height))
    if (vis.width <= 0f || vis.height <= 0f) return false
    return vis.width * vis.height >= rect.width * rect.height * 0.5f
}

/** 标记一个点按后会打开下一级页面的元素。[radius] 为元素自身的圆角。 */
internal fun Modifier.expandSource(radius: Dp = 16.dp): Modifier = composed {
    val density = LocalDensity.current
    val radiusPx = with(density) { radius.toPx() }
    val holder = remember { arrayOfNulls<LayoutCoordinates>(1) }
    this
        .onPlaced { holder[0] = it }
        .pointerInput(radiusPx) {
            awaitPointerEventScope {
                while (true) {
                    val e = awaitPointerEvent(PointerEventPass.Initial)
                    if (e.changes.any { it.changedToDownIgnoreConsumed() }) {
                        val c = holder[0]
                        if (c != null && c.isAttached) {
                            val p = c.positionInRoot()
                            ExpandOrigin.arm(Rect(p.x, p.y, p.x + c.size.width, p.y + c.size.height), radiusPx)
                        }
                    }
                }
            }
        }
}

/** COUI 动效规范：位移/形变用 COUI Move Ease (0.3, 0, 0.1, 1)，透明度用 COUI Ease (0.33, 0, 0.67, 1)。 */
internal object PageMotion {
    val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
    val FadeEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)
    const val OPEN_MS = 380
    const val CLOSE_MS = 340
    const val FALLBACK_MS = 260
    /** 预测性返回手势拖到底时页面收回的比例（剩余部分在松手后完成）。 */
    const val GESTURE_SHRINK = 0.55f
}

private fun lerpRect(a: Rect, b: Rect, t: Float) = Rect(
    lerp(a.left, b.left, t), lerp(a.top, b.top, t), lerp(a.right, b.right, t), lerp(a.bottom, b.bottom, t),
)

private class ClipShape(val height: Float, val corner: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Rounded(RoundRect(0f, 0f, size.width, height.coerceAtMost(size.height), CornerRadius(corner)))
}

/**
 * 页面层的一镜到底形变。[progress] 0 = 收在起点元素处，1 = 全屏；只在图层/绘制阶段读取，
 * 动画过程中二级页不重组、不重新测量。页面按起点宽度等比缩放、裁成起点的高度与圆角，
 * 内容在前段从容器色中淡入。[origin] 为空时是普通转场：淡入 + 0.92→1 缩放。
 */
internal fun Modifier.expandTransform(
    origin: Origin?,
    containerColor: Color,
    fallbackScale: Float = 1f,
    fallbackShift: Float = 0f,
    screenCorner: Float = 0f,
    progress: () -> Float,
): Modifier =
    if (origin == null) {
        graphicsLayer {
            val v = progress().coerceIn(0f, 1f)
            val s = lerp(0.92f, fallbackScale, v)
            scaleX = s
            scaleY = s
            translationX = fallbackShift * v
            alpha = PageMotion.FadeEase.transform(v)
            transformOrigin = TransformOrigin.Center
            roundedPage(v < 1f || s < 0.999f, screenCorner / s)
        }
    } else {
        val paint = Paint()
        graphicsLayer {
            val v = progress().coerceIn(0f, 1f)
            val full = Rect(0f, 0f, size.width, size.height)
            val r = lerpRect(origin.rect, full, v)
            val s = (r.width / size.width).coerceAtLeast(0.01f)
            scaleX = s
            scaleY = s
            transformOrigin = TransformOrigin(0f, 0f)
            translationX = r.left
            translationY = r.top
            // 圆角从起点元素的圆角过渡到屏幕圆角，全程不变直角；只在动画到终点（v = 1）时才取消裁切。
            shape = ClipShape(r.height / s, (if (v >= 1f) 0f else lerp(origin.radius, screenCorner, v)) / s)
            clip = v < 1f
            shadowElevation = if (v < 1f) PAGE_SHADOW * density else 0f
            // 起点刚开始展开时让下层元素透出一点，避免一帧之内被纯色块盖住。
            alpha = (v / 0.12f).coerceIn(0f, 1f)
        }.drawWithContent {
            val v = progress().coerceIn(0f, 1f)
            drawRect(containerColor)
            val a = PageMotion.FadeEase.transform(((v - 0.1f) / 0.4f).coerceIn(0f, 1f))
            if (a >= 0.999f) {
                drawContent()
            } else if (a > 0.001f) {
                paint.alpha = a
                drawIntoCanvas { canvas ->
                    canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
                    drawContent()
                    canvas.restore()
                }
            }
        }
    }

/** 屏幕圆角半径（px）：API 31+ 读 RoundedCorner，取不到时用 24dp。 */
@androidx.compose.runtime.Composable
internal fun rememberScreenCornerPx(): Float {
    val view = androidx.compose.ui.platform.LocalView.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    return androidx.compose.runtime.remember(view, density) {
        val insets = view.rootWindowInsets
        val r = listOf(
            android.view.RoundedCorner.POSITION_TOP_LEFT,
            android.view.RoundedCorner.POSITION_TOP_RIGHT,
        ).maxOfOrNull { insets?.getRoundedCorner(it)?.radius ?: 0 } ?: 0
        if (r > 0) r.toFloat() else with(density) { 24.dp.toPx() }
    }
}

private const val PAGE_SHADOW = 6f

/** 转场中的页面图层：圆角裁切 + 轻阴影（只改图层参数）；[on] 为假时恢复全屏直角。 */
internal fun androidx.compose.ui.graphics.GraphicsLayerScope.roundedPage(on: Boolean, corner: Float) {
    if (on) {
        shape = androidx.compose.foundation.shape.RoundedCornerShape(corner)
        clip = true
        shadowElevation = PAGE_SHADOW * density
    } else {
        clip = false
        shadowElevation = 0f
    }
}

internal enum class TransitKind { Open, Close }

/**
 * 一次页面切换。[page] 是在动的那一层（打开时是新页，关闭时是正在退出的页），[under] 是它下面那层。
 * [from] 为起始进度；[fallbackScale]/[fallbackShift] 是普通转场在预测性返回松手时的起始缩放/平移，
 * 让松手后的退出与手势中的画面连续。
 */
internal class Transit<S>(
    val id: Int,
    val kind: TransitKind,
    val page: S,
    val under: S,
    val underToken: String,
    val origin: Origin?,
    val from: Float,
    val fallbackScale: Float = 1f,
    val fallbackShift: Float = 0f,
)

/**
 * 根据页面层级链的变化判断是「进入下一级」还是「返回」，并为每个打开的页面保存它的展开起点，
 * 返回时收回到同一处。纯计算，在组合阶段调用；同一条链重复调用是幂等的。
 */
internal class PageNav<S>(initial: List<S>, private val token: (S) -> String) {
    private var prev: List<S> = initial
    private val origins = HashMap<String, Origin>()
    private var seq = 0
    var current: Transit<S>? = null
        private set

    fun update(now: List<S>, screen: Size, gestureV: Float, fallbackScale: Float, fallbackShift: Float): Transit<S>? {
        val pt = prev.map(token)
        val nt = now.map(token)
        if (pt == nt || now.isEmpty() || prev.isEmpty()) return current
        val oldTop = prev.last()
        val newTop = now.last()
        val openedFrom = nt.indexOf(pt.last())
        val closedTo = pt.indexOf(nt.last())
        current = when {
            openedFrom in 0 until nt.lastIndex -> {
                val o = ExpandOrigin.take(screen)?.copy(underToken = pt.last())
                if (o != null) origins[nt.last()] = o else origins.remove(nt.last())
                Transit(++seq, TransitKind.Open, newTop, oldTop, pt.last(), o, 0f)
            }
            closedTo in 0 until pt.lastIndex -> {
                ExpandOrigin.clear()
                val o = origins[pt.last()]?.takeIf { it.underToken == nt.last() && it.usable(screen) }
                pt.drop(closedTo + 1).forEach { origins.remove(it) }
                Transit(
                    ++seq, TransitKind.Close, oldTop, newTop, nt.last(), o,
                    from = if (o != null) gestureV else 1f,
                    fallbackScale = fallbackScale,
                    fallbackShift = fallbackShift,
                )
            }
            else -> {
                // 同级替换（如阅读器换章）：不做转场。
                ExpandOrigin.clear()
                pt.filter { it !in nt }.forEach { origins.remove(it) }
                null
            }
        }
        prev = now
        return current
    }

    fun finish(id: Int) {
        if (current?.id == id) current = null
    }

    /** 当前顶层页面（[top]）若是从 [under] 里的元素展开的，返回其起点，用于预测性返回跟手收回。 */
    fun originOf(top: String, under: String, screen: Size): Origin? =
        origins[top]?.takeIf { it.underToken == under && it.usable(screen) }
}
