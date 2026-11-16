package com.shumanyu.app

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.itos.coui.theme.CouiTheme
import kotlin.math.roundToInt

/**
 * 下拉刷新。内容随手指带阻尼下移；顶部出现 COUI 风格的悬浮圆形指示器，
 * 拉动时环形进度跟随距离，松手触发后保持在刷新位并无限旋转，结束后弹回。
 */
@Composable
fun PullRefreshBox(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val threshold = with(density) { 72.dp.toPx() }
    val holdAt = with(density) { 60.dp.toPx() }
    var offset by remember { mutableFloatStateOf(0f) }
    val refreshState = rememberUpdatedState(onRefresh)
    val busy = rememberUpdatedState(refreshing)
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (busy.value) return Offset.Zero
                if (available.y >= 0f || offset <= 0f) return Offset.Zero
                val consume = available.y.coerceAtLeast(-offset)
                offset = (offset + consume).coerceAtLeast(0f)
                return Offset(0f, consume)
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (busy.value || available.y <= 0f || source != NestedScrollSource.UserInput) return Offset.Zero
                offset = (offset + available.y * 0.5f).coerceAtMost(threshold * 1.6f)
                return Offset(0f, available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (busy.value) return Velocity.Zero
                val fire = offset >= threshold
                val from = offset
                if (fire) refreshState.value()
                if (from > 1f) animate(from, 0f, animationSpec = tween(260)) { v, _ -> offset = v } else offset = 0f
                return if (from > 1f) available else Velocity.Zero
            }
        }
    }
    val held by animateFloatAsState(if (refreshing) holdAt else 0f, spring(dampingRatio = 0.85f, stiffness = 380f), label = "hold")
    val shown = maxOf(offset, held)
    Box(modifier.fillMaxSize().nestedScroll(connection)) {
        Box(Modifier.fillMaxSize().offset { IntOffset(0, shown.roundToInt()) }) { content() }
        if (shown > 1f) {
            val pull = (offset / threshold).coerceIn(0f, 1f)
            Box(
                Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, (shown - holdAt * 0.85f).roundToInt()) }
                    .padding(top = 8.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    Modifier
                        .graphicsLayer {
                            val s = if (refreshing) 1f else (0.6f + 0.4f * pull)
                            scaleX = s; scaleY = s; alpha = if (refreshing) 1f else pull.coerceAtLeast(0.2f)
                        }
                        .size(40.dp)
                        .shadow(8.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.12f), spotColor = Color.Black.copy(alpha = 0.12f))
                        .background(CouiTheme.colorScheme.card, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    CouiCircularProgress(Modifier.size(22.dp), progress = if (refreshing) null else pull * 0.85f, strokeWidth = 2.dp)
                }
            }
        }
    }
}
