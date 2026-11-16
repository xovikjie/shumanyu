package com.shumanyu.app

import androidx.compose.ui.draw.clip

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.itos.coui.dialog.CouiDialogAction
import com.itos.coui.dialog.CouiDialogPlacement

/**
 * 弹窗/底部面板出现时让悬浮底栏让位：任一弹窗在场（holds>0）底栏就按 COUI 移动曲线滑出屏幕，
 * 全部关闭后再从底部弹回。弹窗统一经由下面的 [Dialog] 包装登记，页面里不用单独处理。
 */
internal object BottomBarVeil {
    var holds by mutableIntStateOf(0)
        private set
    val hidden: Boolean get() = holds > 0
    fun hold() { holds++ }
    fun release() { holds = (holds - 1).coerceAtLeast(0) }
}

/** 在场期间让底栏隐藏。[active] 为 false 时不登记（例如 visible=false 的弹窗）。 */
@Composable
internal fun HoldBottomBar(active: Boolean = true) {
    if (!active) return
    DisposableEffect(Unit) {
        BottomBarVeil.hold()
        onDispose { BottomBarVeil.release() }
    }
}

internal object BarVeilMotion {
    const val HIDE_MS = 300
    const val SHOW_MS = 400
}

/** 底栏外层：按 [BottomBarVeil] 平移出/回屏幕（只动 graphicsLayer，不影响布局），隐藏和动画中吞掉触摸。 */
@Composable
internal fun Modifier.bottomBarVeil(): Modifier {
    val hidden = BottomBarVeil.hidden
    val progress = remember { Animatable(if (hidden) 1f else 0f) }
    LaunchedEffect(hidden) {
        progress.animateTo(
            if (hidden) 1f else 0f,
            tween(if (hidden) BarVeilMotion.HIDE_MS else BarVeilMotion.SHOW_MS, easing = PageMotion.MoveEase),
        )
    }
    val extra = with(LocalDensity.current) { 32.dp.toPx() }
    val blocking = hidden || progress.isRunning
    return this
        .graphicsLayer { translationY = (size.height + extra) * progress.value }
        .then(
            if (blocking) Modifier.pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                }
            } else Modifier,
        )
}

/** 项目内统一弹窗：COUI Dialog + 底栏让位。 */
@Composable
internal fun Dialog(
    onDismissRequest: () -> Unit,
    confirmAction: CouiDialogAction,
    placement: CouiDialogPlacement,
    visible: Boolean = true,
    title: String? = null,
    message: String? = null,
    dismissAction: CouiDialogAction? = null,
    properties: DialogProperties? = null,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    HoldBottomBar(visible)
    if (properties != null) com.itos.coui.dialog.Dialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest, confirmAction = confirmAction, placement = placement, visible = visible,
        title = title, message = message, dismissAction = dismissAction, properties = properties, content = { InSheet { content() } },
    ) else com.itos.coui.dialog.Dialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest, confirmAction = confirmAction, placement = placement, visible = visible,
        title = title, message = message, dismissAction = dismissAction, content = { InSheet { content() } },
    )
}


/** 是否处在底部弹窗/面板里：这里的列表行用 COUI 内缩圆角样式（按压底色不顶到面板边）。 */
internal val LocalSheetRows = androidx.compose.runtime.staticCompositionLocalOf { false }

@Composable
private fun InSheet(content: @Composable () -> Unit) =
    androidx.compose.runtime.CompositionLocalProvider(LocalSheetRows provides true, content = content)

/** 面板里的列表行：裁成 COUI 圆角，行自带的按压/选中底色只铺在自己的圆角范围内。 */
@Composable
internal fun androidx.compose.ui.Modifier.sheetRow(): androidx.compose.ui.Modifier =
    if (LocalSheetRows.current) this.clip(com.itos.coui.shape.CouiShape(14.dp, 3f)) else this
