package com.shumanyu.app

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text

object ShelfNotes {
    var text by mutableStateOf("")
    var icon by mutableIntStateOf(R.mipmap.ic_launcher)
    private var token by mutableIntStateOf(0)

    fun hold(line: String, iconRes: Int) {
        token++
        icon = iconRes
        text = line
    }

    fun flash(line: String, iconRes: Int) {
        val mine = ++token
        icon = iconRes
        text = line
        Handler(Looper.getMainLooper()).postDelayed({
            if (mine == token) text = ""
        }, 2600)
    }
}

@Composable
fun NoteHost(bottom: androidx.compose.ui.unit.Dp = 92.dp) {
    val msg = ShelfNotes.text
    if (msg.isBlank()) return
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(
            Modifier
                .padding(bottom = bottom)
                .shadow(10.dp, RoundedCornerShape(28.dp))
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(ShelfNotes.icon),
                contentDescription = null,
                modifier = Modifier.size(36.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Text(msg, modifier = Modifier.padding(start = 8.dp), fontSize = 14.sp, color = Color(0xFF1A1A1A))
        }
    }
}

@Composable
fun PullRefreshBox(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val threshold = with(density) { 72.dp.toPx() }
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
                if (busy.value || available.y <= 0f) return Offset.Zero
                offset = (offset + available.y * 0.55f).coerceAtMost(threshold * 1.6f)
                return Offset(0f, available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (busy.value) {
                    offset = 0f
                    return Velocity.Zero
                }
                val fire = offset >= threshold
                val from = offset
                if (from > 1f) {
                    animate(from, 0f, animationSpec = tween(220)) { v, _ -> offset = v }
                } else {
                    offset = 0f
                }
                if (fire) refreshState.value()
                return if (from > 1f) available else Velocity.Zero
            }
        }
    }
    Box(modifier.fillMaxSize().nestedScroll(connection)) {
        Box(Modifier.fillMaxSize().offset { IntOffset(0, offset.roundToInt()) }) { content() }
    }
}
