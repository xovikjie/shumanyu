package com.shumanyu.app

import android.os.Build
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text

/** Telegram-style floating navigation: a gently blurred capsule and a calm tinted selection. */
@Composable
fun FloatingBottomBar(
    backdrop: Backdrop,
    selected: Int,
    labels: List<String>,
    icons: List<ImageVector>,
    onSelect: (Int) -> Unit,
    accent: Color,
    dark: Boolean,
    modifier: Modifier = Modifier,
) {
    require(labels.isNotEmpty() && labels.size == icons.size)
    val index = selected.coerceIn(labels.indices)
    val shape = RoundedCornerShape(32.dp)
    val chipShape = RoundedCornerShape(28.dp)
    val surface = if (dark) Color(0xFF202329) else Color(0xFFECEEF2)
    val idle = if (dark) Color(0xFFE9EDF1) else Color(0xFF20262C)
    val chip = accent.copy(alpha = if (dark) 0.20f else 0.12f)
    val selectedTint = remember(accent, surface, dark) {
        val worstCase = if (dark) surface.copy(alpha = 0.82f).compositeOver(Color.White)
            else surface.copy(alpha = 0.76f).compositeOver(Color.Black)
        readableNavigationTint(accent, chip.compositeOver(worstCase))
    }
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val position by animateFloatAsState(
        targetValue = index.toFloat(),
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 500f),
        label = "floatingTabPosition",
    )
    val interactions = remember(labels.size) { List(labels.size) { MutableInteractionSource() } }

    val background = if (Build.VERSION.SDK_INT >= 31) {
        Modifier.drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = { blur(8.dp.toPx()) },
            highlight = null,
            shadow = null,
            innerShadow = null,
            onDrawSurface = { drawRect(surface.copy(alpha = if (dark) 0.82f else 0.76f)) },
        )
    } else Modifier.background(surface.copy(alpha = 0.96f), shape)
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(8.dp, shape, clip = false)
            .then(background)
            .border(0.5.dp, if (dark) Color.White.copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.045f), shape)
            .clip(shape),
        contentAlignment = AbsoluteAlignment.CenterLeft,
    ) {
        val tabWidth = (maxWidth - 8.dp) / labels.size
        Box(
            Modifier
                .padding(4.dp)
                .width(tabWidth)
                .fillMaxHeight()
                .graphicsLayer {
                    val slot = if (isLtr) position else labels.lastIndex - position
                    translationX = slot * tabWidth.toPx()
                }
                .background(chip, chipShape),
        )
        Row(Modifier.fillMaxSize().padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            labels.forEachIndexed { item, label ->
                val active = item == index
                val tint by animateColorAsState(
                    targetValue = if (active) selectedTint else idle,
                    animationSpec = spring(dampingRatio = 1f, stiffness = 500f),
                    label = "floatingTabColor",
                )
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(chipShape)
                        .selectable(
                            selected = active,
                            role = Role.Tab,
                            interactionSource = interactions[item],
                            indication = null,
                            onClick = { onSelect(item) },
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(icons[item], contentDescription = null, tint = tint, modifier = Modifier.size(23.dp))
                    Text(
                        label,
                        color = tint,
                        fontSize = 12.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

/** Keep custom theme accents legible on the selected background with the high-opacity readability layer. */
internal fun readableNavigationTint(accent: Color, background: Color): Color {
    val base = background.luminance()
    val endpoint = if (base > 0.179f) Color.Black else Color.White
    var tint = accent.copy(alpha = 1f)
    repeat(24) {
        val foreground = tint.luminance()
        val contrast = (maxOf(base, foreground) + 0.05f) / (minOf(base, foreground) + 0.05f)
        if (contrast >= 4.5f) return tint
        tint = lerp(tint, endpoint, 0.15f)
    }
    return endpoint
}
