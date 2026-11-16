package com.shumanyu.app

import kotlin.math.roundToInt
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.window.DialogProperties
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.backdrop.couiBackdrop
import com.itos.coui.dialog.CouiDialogAction
import com.itos.coui.dialog.CouiDialogActionStyle
import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.dialog.CouiDialogPlacement
import com.itos.coui.effect.CouiFloatingRenderer
import com.itos.coui.effect.CouiFloatingSurface
import com.itos.coui.effect.LocalCouiContourGlowEnabled
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.effect.couiFloatingSurface
import com.itos.coui.motion.couiPressMotion
import com.itos.coui.motion.rememberCouiPressMotion
import com.itos.coui.shape.CouiCapsuleShape
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.rememberCouiTextStyle
import com.itos.coui.theme.CouiColor
import com.itos.coui.theme.CouiTheme
import com.itos.coui.topappbar.CouiTopAppBarButtonDefaults
import com.itos.coui.text.Text as CouiText

/*
 * 书漫屿自家的 COUI 风格组件：COUI-Kit 没有对应控件的地方（通用按钮、多字段输入、悬浮提示、
 * 进度、空状态等）按 COUI-Kit 的设计语言绘制——平滑胶囊/项目圆角、主题色 token、CouiTextKind
 * 字体、COUI 按压弹簧（couiPressMotion）与列表/按钮反馈（couiButtonFeedback）、悬浮材质。
 */

// ---------------------------------------------------------------- 按压

/** COUI 普通按压：按下时按面积与长宽比缩放，松手弹回（与 COUI 弹窗按钮、顶栏按钮同一弹簧）。 */
@Composable
fun Modifier.couiPress(enabled: Boolean = true): Modifier {
    val press = rememberCouiPressMotion()
    return graphicsLayer {
        val s = press.scale.value
        scaleX = s
        scaleY = s
    }.couiPressMotion(press, enabled, cancelOnConsumedMove = true)
}

/**
 * 全应用统一的 COUI 按压反馈：COUI-Kit 的 couiButtonFeedback（列表/按钮按压底色，跟随 [shape] 圆角），
 * [press] 时再叠加 couiPressMotion 缩放弹簧。始终 indication = null —— 不用 Compose 默认的
 * 调试 indication（按下时整块发黑的矩形，即「黑色描边/黑框」），也没有 Material 水波纹和焦点框。
 */
@Composable
fun Modifier.couiClickable(
    enabled: Boolean = true,
    press: Boolean = false,
    list: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape? = com.itos.coui.shape.CouiShape(12.dp, 3f),
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier {
    val source = remember { MutableInteractionSource() }
    return this
        .then(if (press) Modifier.couiPress(enabled) else Modifier)
        .then(if (shape != null) Modifier.clip(shape) else Modifier)
        .couiButtonFeedback(CouiTheme.isDark, source, enabled, list = list)
        .combinedClickable(interactionSource = source, indication = null, enabled = enabled, role = role, onLongClick = onLongClick, onClick = onClick)
}

/** 兜底：主题根部把默认 indication 换成空实现，漏网的 clickable 也不会再画黑色按压块。 */
internal object NoPressIndication : androidx.compose.foundation.IndicationNodeFactory {
    override fun create(interactionSource: androidx.compose.foundation.interaction.InteractionSource): androidx.compose.ui.node.DelegatableNode =
        object : Modifier.Node() {}
    override fun equals(other: Any?) = other === this
    override fun hashCode() = 0x5348
}

// ---------------------------------------------------------------- 按钮

enum class CouiButtonKind { Primary, Secondary, Text, Danger }

/**
 * 通用按钮。几何与配色对齐 COUI 弹窗按钮：平滑胶囊、44dp 高（compact 36dp）、
 * 主按钮为主题色 + 白字，次按钮为中性填色，文字按钮无底色，危险操作用 COUI 红。
 */
@Composable
fun CouiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    kind: CouiButtonKind = CouiButtonKind.Secondary,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    compact: Boolean = false,
) {
    val dark = CouiTheme.isDark
    val accent = CouiTheme.colorScheme.accent
    val neutral = Color(if (dark) 0xFF353535 else 0xFFEBEBEB)
    val (bg, fg) = when (kind) {
        CouiButtonKind.Primary -> accent to Color.White
        CouiButtonKind.Secondary -> neutral to Color(if (dark) 0xE6FFFFFF else 0xE6000000)
        CouiButtonKind.Text -> Color.Transparent to accent
        CouiButtonKind.Danger -> neutral to (if (dark) CouiColor.Night.RED else CouiColor.Day.RED)
    }
    val surface = if (enabled) bg else bg.copy(alpha = bg.alpha * 0.4f)
    val content = if (enabled) fg else fg.copy(alpha = 0.3f)
    val source = remember { MutableInteractionSource() }
    val style = rememberCouiTextStyle(if (compact) CouiTextKind.PreferenceValue else CouiTextKind.PreferenceTitle)
    Row(
        modifier
            .heightIn(min = if (compact) 36.dp else 44.dp)
            .expandSource(if (compact) 18.dp else 22.dp)
            .couiPress(enabled)
            .clip(CouiCapsuleShape)
            .background(surface)
            .couiButtonFeedback(dark, source, enabled)
            .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = if (kind == CouiButtonKind.Text) 12.dp else if (compact) 16.dp else 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(if (compact) 16.dp else 18.dp))
            Spacer(Modifier.width(6.dp))
        }
        BasicText(text, style = style.copy(color = content, textAlign = TextAlign.Center), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ---------------------------------------------------------------- 输入

/**
 * 页面/表单内的输入框，样式取自 COUI InputDialog（COUIInputView）：无底色、0.33dp 分隔线，
 * 获焦时主题色 1dp 焦点线从起始侧展开，提示色与正文字号一致。
 */
@Composable
fun CouiInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    autoFocus: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val dark = CouiTheme.isDark
    val accent = CouiTheme.colorScheme.accent
    val hint = Color(if (dark) 0x4DFFFFFF else 0x42000000)
    val divider = Color(if (dark) 0x33FFFFFF else 0x1F000000)
    var focused by remember { mutableStateOf(false) }
    val line = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(focused) {
        line.snapTo(0f)
        if (focused) line.animateTo(1f, tween(250, easing = androidx.compose.animation.core.CubicBezierEasing(0f, 0f, .1f, 1f)))
    }
    val focus = remember { FocusRequester() }
    if (autoFocus) LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    val body = rememberCouiTextStyle(CouiTextKind.Body).copy(fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp))
    Column(modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            CouiText(text = label, kind = CouiTextKind.GroupTitle, color = if (focused) accent else CouiTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    val normal = 0.33.dp.toPx().coerceAtLeast(1f)
                    val active = 1.dp.toPx().coerceAtLeast(1f)
                    drawRect(divider, Offset(0f, size.height - normal), Size(size.width, normal))
                    if (focused) drawRect(accent, Offset(0f, size.height - active), Size(size.width * line.value, active))
                }
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) BasicText(placeholder, style = body.copy(color = hint))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                minLines = minLines,
                textStyle = body.copy(color = CouiTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(accent),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier.fillMaxWidth().heightIn(min = 22.dp).focusRequester(focus).onFocusChanged { focused = it.isFocused },
            )
        }
    }
}

/** 选项弹窗：COUI Dialog + Item/RadioButton 行（替代旧的遮罩 + 卡片 ChoiceSheet）。 */
@Composable
fun <T> CouiChoiceDialog(
    title: String,
    options: List<Pair<T, String>>,
    selected: T?,
    onPick: (T) -> Unit,
    onDismiss: () -> Unit,
    cancel: String = "取消",
) {
    Dialog(
        onDismissRequest = onDismiss,
        title = title,
        confirmAction = CouiDialogAction(text = cancel, onClick = onDismiss),
        placement = CouiDialogPlacement.Bottom,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            options.forEach { (id, label) ->
                RadioButtonPreference(title = label, selected = id == selected, onClick = { onPick(id); onDismiss() })
            }
        }
    }
}

// ---------------------------------------------------------------- 悬浮材质

/**
 * COUI 悬浮胶囊表面。真机上与 COUI 顶栏胶囊按钮同一材质/投影（couiFloatingSurface，
 * 对 [backdrop] 采样模糊）；[backdrop] 为空或在测试渲染时退回同尺寸的不透明胶囊 + 投影。
 */
@Composable
fun CouiFloatingCapsule(
    backdrop: CouiBackdrop?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp? = null,
    blur: Boolean = true,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val dark = CouiTheme.isDark
    val capsule = cornerRadius == null
    val shape: Shape = if (cornerRadius == null) CouiCapsuleShape else com.itos.coui.shape.CouiShape(cornerRadius, 3f)
    // 玻璃材质的着色器在个别机型上可能建不起来：建不起来就退回下面的纯色胶囊，不让整页崩掉。
    val renderer = if (useFloatingChrome && backdrop != null) remember { runCatching { CouiFloatingRenderer.standalone() }.getOrNull() } else null
    if (renderer != null && backdrop != null) {
        val density = LocalDensity.current.density
        val radiusPx = cornerRadius?.let { it.value * density }
        val contour = LocalCouiContourGlowEnabled.current
        val material = remember(dark, blur, capsule) {
            CouiTopAppBarButtonDefaults.material(dark, capsule = capsule, search = true).let { if (blur) it.copy(blurRadius = 25f) else it }
        }
        val shadow = remember(dark, density, contour, capsule) { CouiTopAppBarButtonDefaults.shadow(dark, capsule, density, contour, search = true) }
        val layer = rememberGraphicsLayer()
        var size by remember { mutableStateOf(IntSize.Zero) }
        Box(
            modifier
                .onSizeChanged { size = it }
                .couiFloatingSurface(backdrop, renderer, material, shadow, shape, layer, contour, foregroundComposition = true) {
                    CouiFloatingSurface(
                        bounds = Rect(Offset.Zero, Size(size.width.toFloat(), size.height.toFloat())),
                        cornerWeight = if (capsule) 1f else 2f,
                        cornerRadius = radiusPx,
                    )
                },
        ) {
            Row(
                Modifier.drawWithContent { layer.record { this@drawWithContent.drawContent() } }.clip(shape).padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    } else {
        Row(
            modifier
                .shadow(12.dp, shape, ambientColor = Color.Black.copy(alpha = 0.14f), spotColor = Color.Black.copy(alpha = 0.14f))
                .clip(shape)
                .background(if (dark) Color(0xF22A2A2A) else Color(0xF5FAFAFA))
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

// ---------------------------------------------------------------- 轻提示

/**
 * 应用内轻提示（替换 android.widget.Toast）：底部居中的 COUI 悬浮胶囊。
 * 任意线程可调用；[hold] 保持显示直到下一次 [show]/[hide]。
 */
object CouiToast {
    internal var message by mutableStateOf<String?>(null)
        private set
    private var token = 0
    private val main = Handler(Looper.getMainLooper())

    fun show(text: String, long: Boolean = false) = post {
        val mine = ++token
        message = text
        main.postDelayed({ if (mine == token) clear() }, if (long) 3500L else 2000L)
    }

    fun hold(text: String) = post {
        ++token
        message = text
    }

    fun hide() = post { ++token; clear() }

    private fun clear() {
        message = null
    }

    private fun post(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block() else main.post(block)
    }
}

@Composable
fun CouiToastHost(backdrop: CouiBackdrop?, bottom: Dp = 104.dp) {
    val msg = CouiToast.message
    var last by remember { mutableStateOf("") }
    if (msg != null) last = msg
    Box(Modifier.fillMaxSize().navigationBarsPadding().padding(bottom = bottom), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = msg != null,
            enter = fadeIn(tween(180)) + scaleIn(spring(dampingRatio = 0.8f, stiffness = 500f), initialScale = 0.86f),
            exit = fadeOut(tween(160)) + scaleOut(tween(160), targetScale = 0.92f),
        ) {
            CouiFloatingCapsule(
                backdrop = backdrop,
                modifier = Modifier.padding(horizontal = 32.dp).widthIn(max = 360.dp).heightIn(min = 44.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 11.dp),
            ) {
                CouiText(text = last, kind = CouiTextKind.Body, color = CouiTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
            }
        }
    }
}

// ---------------------------------------------------------------- 进度

/** COUI 风格环形进度：主题色圆头弧线 + 浅色轨道；[progress] 为空时无限旋转。 */
@Composable
fun CouiCircularProgress(
    modifier: Modifier = Modifier.size(28.dp),
    progress: Float? = null,
    strokeWidth: Dp = 2.5.dp,
    color: Color = CouiTheme.colorScheme.accent,
) {
    val track = CouiTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val spin = if (progress == null) {
        val t = rememberInfiniteTransition(label = "spin")
        val rot by t.animateFloat(0f, 360f, infiniteRepeatable(tween(900, easing = LinearEasing)), label = "rot")
        val sweep by t.animateFloat(36f, 250f, infiniteRepeatable(tween(760, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "sweep")
        rot to sweep
    } else null
    Canvas(modifier) {
        val w = strokeWidth.toPx()
        val inset = w / 2f
        val arcSize = Size(size.width - w, size.height - w)
        drawArc(track, 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(w))
        val (start, sweep) = spin ?: (-90f to 360f * progress!!.coerceIn(0f, 1f))
        if (sweep > 0f) drawArc(color, start - 90f, sweep, false, Offset(inset, inset), arcSize, style = Stroke(w, cap = StrokeCap.Round))
    }
}

/** COUI 风格线形进度：4dp 胶囊轨道，确定进度填充，未知进度时一段胶囊往返滑动。 */
@Composable
fun CouiLinearProgress(progress: Float?, modifier: Modifier = Modifier.fillMaxWidth()) {
    val accent = CouiTheme.colorScheme.accent
    val track = CouiTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val slide = if (progress == null) {
        val t = rememberInfiniteTransition(label = "slide")
        val x by t.animateFloat(0f, 1f, infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "x")
        x
    } else null
    Canvas(modifier.height(4.dp)) {
        val r = size.height / 2f
        drawRoundRect(track, cornerRadius = androidx.compose.ui.geometry.CornerRadius(r))
        if (slide != null) {
            val seg = size.width * 0.32f
            drawRoundRect(accent, Offset((size.width - seg) * slide, 0f), Size(seg, size.height), androidx.compose.ui.geometry.CornerRadius(r))
        } else {
            val w = size.width * progress!!.coerceIn(0f, 1f)
            if (w > 0f) drawRoundRect(accent, Offset.Zero, Size(w.coerceAtLeast(size.height), size.height), androidx.compose.ui.geometry.CornerRadius(r))
        }
    }
}

// ---------------------------------------------------------------- 空状态

/**
 * 空状态/加载/出错的统一版式：颜文字（或环形进度）+ COUI 正文说明 + 可选次按钮，
 * 居中，按 COUI 页面边距留白。
 */
@Composable
internal fun EmptyState(
    face: String?,
    message: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 40.dp)) {
            if (loading) CouiCircularProgress(Modifier.size(32.dp))
            else if (face != null) BasicText(
                face,
                style = rememberCouiTextStyle(CouiTextKind.HomeTitle).copy(
                    color = CouiTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(Modifier.height(16.dp))
            CouiText(text = message, kind = CouiTextKind.Body, color = CouiTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            if (actionText != null && onAction != null) {
                Spacer(Modifier.height(20.dp))
                CouiButton(actionText, onAction, kind = CouiButtonKind.Secondary, compact = true)
            }
        }
    }
}

/** 单字段命名/改名：直接使用 COUI InputDialog，主按钮在内容为空时禁用。 */
@Composable
fun CouiNameDialog(
    title: String,
    initial: String,
    placeholder: String,
    confirm: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    cancel: String = "取消",
) {
    var value by remember { mutableStateOf(androidx.compose.ui.text.input.TextFieldValue(initial, androidx.compose.ui.text.TextRange(initial.length))) }
    val ok = value.text.trim().isNotEmpty()
    HoldBottomBar()
    com.itos.coui.dialog.InputDialog(
        value = value,
        onValueChange = { value = it },
        onDismissRequest = onDismiss,
        confirmAction = CouiDialogAction(
            text = confirm,
            onClick = { if (ok) onConfirm(value.text.trim()) },
            enabled = ok,
            style = CouiDialogActionStyle.Primary,
            colors = CouiDialogDefaults.primaryActionColors(CouiTheme.colorScheme.accent),
        ),
        dismissAction = CouiDialogAction(text = cancel, onClick = onDismiss),
        clearContentDescription = "清除",
        title = title,
        placeholder = placeholder,
    )
}

/**
 * 分段/分类标签：真机直接用 COUI ChipTabRow（背景取自同尺寸的不透明兄弟层 [background]）；
 * 测试渲染无法跑 AGSL 时退回同尺寸的胶囊标签。
 */
@Composable
fun <K : Any> CouiTabs(
    items: List<Pair<K, String>>,
    selected: K,
    onSelect: (K) -> Unit,
    modifier: Modifier = Modifier,
    background: Color = CouiTheme.colorScheme.background,
    contentPadding: Dp = 16.dp,
    centered: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
) {
    if (useFloatingChrome) {
        val bd = com.itos.coui.backdrop.rememberCouiBackdrop()
        // ChipTabRow 内部总是从左排起；居中时按它的尺寸规则（左右各 16dp、标签 12dp 内边距、间距 8dp）
        // 先量出整行宽度，再把整行收窄到这个宽度放在中间。放不下时退回铺满、照常滑动。
        val measurer = androidx.compose.ui.text.rememberTextMeasurer()
        val density = androidx.compose.ui.platform.LocalDensity.current
        val rowWidth = if (!centered) Dp.Unspecified else remember(items, density) {
            val style = com.itos.coui.chip.CouiChipTabDefaults.textStyle
            val text = items.sumOf { (_, l) -> measurer.measure(l, style).size.width }
            with(density) { text.toDp() } + 16.dp * 2 + 24.dp * items.size + 8.dp * (items.size - 1).coerceAtLeast(0) + 4.dp
        }
        // 放在悬浮顶栏（TopAppBarSurface）里时直接采样顶栏的背景，自己不再垫一层方形底色；
        // 只有弹窗等没有顶栏背景的地方才用同色兄弟层当背景。
        val toolbar = LocalHeaderBackdrop.current
        Box(modifier.fillMaxWidth().clipToBounds(), contentAlignment = if (centered) Alignment.Center else Alignment.CenterStart) {
            if (toolbar == null) Box(Modifier.matchParentSize().couiBackdrop(bd).background(background))
            com.itos.coui.chip.ChipTabRow(
                modifier = (if (rowWidth == Dp.Unspecified) Modifier.fillMaxWidth() else Modifier.widthIn(max = rowWidth).fillMaxWidth()).padding(vertical = 6.dp),
                items = items.map { (k, l) -> com.itos.coui.chip.CouiChipTabItem(k, l) },
                selectedKey = selected,
                onSelectedKeyChange = { @Suppress("UNCHECKED_CAST") onSelect(it as K) },
                backdrop = toolbar ?: bd,
                leadingAction = leading,
                leadingActionVisible = leading != null,
            )
        }
    } else {
        val dark = CouiTheme.isDark
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (leading != null) Box(Modifier.padding(start = 8.dp)) { leading() }
        androidx.compose.foundation.lazy.LazyRow(
            Modifier.weight(1f).padding(vertical = 6.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = contentPadding),
            horizontalArrangement = if (centered) Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally) else Arrangement.spacedBy(8.dp),
        ) {
            items(items.size) { i ->
                val (k, l) = items[i]
                val on = k == selected
                Box(
                    Modifier
                        .height(36.dp)
                        .couiPress()
                        .then(if (on) Modifier.shadow(6.dp, CouiCapsuleShape, ambientColor = Color.Black.copy(alpha = 0.1f), spotColor = Color.Black.copy(alpha = 0.1f)) else Modifier)
                        .clip(CouiCapsuleShape)
                        .background(if (on) (if (dark) Color(0xFF3A3A3A) else Color.White) else CouiTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .couiClickable { onSelect(k) }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CouiText(text = l, kind = CouiTextKind.PreferenceValue, color = if (on) CouiTheme.colorScheme.accent else CouiTheme.colorScheme.onSurface)
                }
            }
        }
        }
    }
}


// ---------------------------------------------------------------- 阅读器工具条

/**
 * 阅读器顶栏：COUI TopAppBarSurface（透明底、渐隐模糊）里的悬浮圆形返回按钮 + 标题 + 胶囊按钮组。
 * [backdrop] 是阅读内容层；为空（测试渲染）时退回同尺寸的悬浮按钮外观。
 */
@Composable
fun ReaderTopBar(
    backdrop: CouiBackdrop?,
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val body: @Composable () -> Unit = {
        androidx.compose.runtime.CompositionLocalProvider(LocalInCouiToolbar provides (backdrop != null || !useFloatingChrome)) {
            Row(
                Modifier.fillMaxWidth().statusBarsPaddingCompat().heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) { Icon(com.itos.coui.icons.CouiIcons.Outlined.Back, contentDescription = "返回", tint = CouiTheme.colorScheme.onSurface) }
                Spacer(Modifier.width(12.dp))
                CouiText(
                    text = title,
                    kind = CouiTextKind.Toolbar,
                    color = CouiTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
                if (useFloatingChrome && backdrop != null) com.itos.coui.topappbar.TopAppBarButtonGroup { actions() }
                else Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { actions() }
            }
        }
    }
    if (useFloatingChrome && backdrop != null) {
        // 与各页顶栏同一 COUI 顶栏材质（模糊 + 渐隐），标题压在漫画画面上也保持可读。
        com.itos.coui.topappbar.TopAppBarSurface(modifier = modifier.fillMaxWidth(), backdrop = backdrop) { body() }
    } else {
        // 测试/无模糊替身：COUI 顶栏的底色渐隐（不透明 → 透明）。
        val bg = CouiTheme.colorScheme.background
        Box(
            modifier.fillMaxWidth().background(
                androidx.compose.ui.graphics.Brush.verticalGradient(0f to bg.copy(alpha = 0.96f), 0.75f to bg.copy(alpha = 0.9f), 1f to bg.copy(alpha = 0f)),
            ),
        ) { body() }
    }
}

/** 阅读器底部悬浮面板：COUI 悬浮材质的大圆角卡片，左右与底部（导航栏之上）各留 16dp，内容自定。 */
@Composable
fun ReaderBottomPanel(
    backdrop: CouiBackdrop?,
    modifier: Modifier = Modifier,
    onPanelTop: (Int) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier.fillMaxWidth().navigationBarsPadding().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        CouiFloatingCapsule(
            backdrop = backdrop,
            // 面板（可见卡片本身）顶边的窗口坐标，供工具栏弹出菜单放在面板之上。
            modifier = Modifier.fillMaxWidth().onGloballyPositioned { onPanelTop(it.boundsInWindow().top.roundToInt()) },
            cornerRadius = 28.dp,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        ) {
            Column(Modifier.fillMaxWidth(), content = content)
        }
    }
}

/** 阅读器面板里的图标+文字工具按钮（COUI 按压缩放 + 列表反馈）。 */
@Composable
fun ReaderTool(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, tint: Color = CouiTheme.colorScheme.onSurface) {
    val source = remember { MutableInteractionSource() }
    val c = if (enabled) tint else tint.copy(alpha = 0.3f)
    Column(
        modifier
            .couiPress(enabled)
            .clip(com.itos.coui.shape.CouiShape(16.dp, 3f))
            .couiButtonFeedback(CouiTheme.isDark, source, enabled, list = true)
            .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = c, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(2.dp))
        CouiText(text = label, kind = CouiTextKind.Description, color = c, textAlign = TextAlign.Center, maxLines = 1)
    }
}

/** 圆形图标按钮（阅读器上一话/下一话等），COUI 按压 + 反馈。 */
@Composable
fun CouiRoundIcon(icon: ImageVector, label: String, onClick: () -> Unit, enabled: Boolean = true, tint: Color = CouiTheme.colorScheme.onSurface) {
    val source = remember { MutableInteractionSource() }
    Box(
        Modifier
            .size(40.dp)
            .couiPress(enabled)
            .clip(CouiCapsuleShape)
            .couiButtonFeedback(CouiTheme.isDark, source, enabled)
            .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = label, tint = if (enabled) tint else tint.copy(alpha = 0.3f), modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun Modifier.statusBarsPaddingCompat(): Modifier = this.then(Modifier.windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.statusBars))


/** 危险操作悬浮按钮（批量删除）：56dp 圆形 COUI 红底 + 白色线框图标，COUI 按压缩放与投影。 */
@Composable
fun CouiDangerFab(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val dark = CouiTheme.isDark
    val red = if (dark) CouiColor.Night.RED else CouiColor.Day.RED
    val source = remember { MutableInteractionSource() }
    Box(
        modifier
            .size(56.dp)
            .couiPress()
            .shadow(12.dp, CouiCapsuleShape, ambientColor = red.copy(alpha = 0.35f), spotColor = red.copy(alpha = 0.35f))
            .clip(CouiCapsuleShape)
            .background(red)
            .couiButtonFeedback(dark, source)
            .clickable(source, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(26.dp))
    }
}

/**
 * 无 AGSL 环境（测试渲染）下底部导航栏的替身：与 COUI NavigationBar 同尺寸的不透明胶囊 + 投影。
 * 真机始终使用 COUI NavigationBar。
 */
@Composable
internal fun NavigationBarStandIn(items: List<Triple<String, ImageVector, ImageVector>>, selectedIndex: Int, onSelection: (Int) -> Unit) {
    Box(Modifier.zIndex(1f).fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 22.dp), contentAlignment = Alignment.Center) {
        Row(
            Modifier.widthIn(max = 520.dp).fillMaxWidth().shadow(16.dp, CouiCapsuleShape).clip(CouiCapsuleShape).background(Color(if (CouiTheme.isDark) 0xE62A2A2A else 0xE6F0F0F0))
                .height(58.dp).fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, (title, outlined, filled) ->
                val on = index == selectedIndex
                val tint = CouiTheme.colorScheme.onSurface.copy(alpha = if (on) 0.92f else 0.66f)
                Column(
                    Modifier.weight(1f).height(48.dp).couiClickable { onSelection(index) }.padding(top = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(if (on) filled else outlined, contentDescription = title, tint = tint, modifier = Modifier.size(25.dp))
                    Spacer(Modifier.height(2.dp))
                    BasicText(title, style = androidx.compose.ui.text.TextStyle(color = tint, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium), maxLines = 1)
                }
            }
        }
    }
}
