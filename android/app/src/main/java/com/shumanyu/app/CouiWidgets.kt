package com.shumanyu.app

import androidx.compose.runtime.mutableStateOf

import com.itos.coui.text.rememberCouiTextStyle

import com.itos.coui.backdrop.couiBackdrop

import com.itos.coui.effect.couiButtonFeedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import kotlin.math.roundToInt
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.ColumnScope as FoundationColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.ui.platform.LocalDensity
import com.itos.coui.backdrop.CouiBackdrop
import com.itos.coui.icons.CouiIcons
import com.itos.coui.topappbar.TopAppBarSurface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.layout.Item
import com.itos.coui.shape.CouiShape
import com.itos.coui.layout.Scaffold as CouiScaffold
import com.itos.coui.radiobutton.RadioButton
import com.itos.coui.switch.Switch
import com.itos.coui.theme.CouiPresets
import com.itos.coui.theme.CouiTheme
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text as CouiText
import com.itos.coui.topappbar.TopAppBar as CouiTopAppBar
import com.itos.coui.topappbar.TopAppBarButton
import com.itos.coui.topappbar.TopAppBarButtonGroup

/** Test hook only. Shipping UI always uses COUI floating chrome on device (minSdk 34). */
internal var useFloatingChrome: Boolean = true

val LocalContentColor = compositionLocalOf { Color.Unspecified }
internal val LocalInCouiToolbar = staticCompositionLocalOf { false }

enum class ColorSchemeMode {
    System, Light, Dark
}

data class AppColorScheme(
    val primary: Color,
    val onSurface: Color,
    val onSurfaceVariantSummary: Color,
    val onSurfaceSecondary: Color,
    val surface: Color,
    val surfaceContainer: Color,
)

data class AppTextStyles(
    val title1: TextStyle,
    val title2: TextStyle,
    val main: TextStyle,
    val button: TextStyle,
)

object AppTheme {
    val colorScheme: AppColorScheme
        @Composable get() {
            val c = CouiTheme.colorScheme
            return AppColorScheme(
                primary = c.accent,
                onSurface = c.onSurface,
                onSurfaceVariantSummary = c.onSurfaceVariant,
                onSurfaceSecondary = c.onSurfaceVariant,
                surface = c.background,
                surfaceContainer = c.container,
            )
        }
    val textStyles: AppTextStyles
        @Composable get() {
            val body = CouiTheme.typography.body
            return AppTextStyles(
                title1 = body.copy(fontSize = 24.sp, fontWeight = FontWeight.Normal),
                title2 = body.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                main = body,
                button = body.copy(fontWeight = FontWeight.Medium),
            )
        }
}

@Composable
fun couiScheme(dark: Boolean) =
    // 主题色即 COUI-Kit 默认主色（CouiColor.Day/Night.BLUE）；勾选框（含菜单内勾选）跟随主题主色。
    (if (dark) CouiPresets.Dark else CouiPresets.Light).let { it.copy(checkboxAccent = it.accent, menuCheckboxAccent = it.accent) }

/** 顶栏是否处在 COUI Scaffold 的 topBar 槽里（那里有 backdrop 作用域）。 */
internal val LocalInCouiScaffold = androidx.compose.runtime.staticCompositionLocalOf { false }

@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    containerColor: Color = CouiTheme.colorScheme.background,
    content: @Composable (PaddingValues) -> Unit,
) {
    CouiScaffold(
        modifier = modifier,
        topBar = { CompositionLocalProvider(LocalInCouiScaffold provides true) { topBar() } },
        bottomBar = bottomBar,
        containerColor = containerColor,
        content = content,
    )
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = AppTheme.textStyles.main,
) {
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current.takeOrElse { CouiTheme.colorScheme.onSurface } } }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign ?: TextAlign.Unspecified,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
            textDecoration = textDecoration,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing,
        ),
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = AppTheme.textStyles.main,
) {
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current.takeOrElse { CouiTheme.colorScheme.onSurface } } }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign ?: TextAlign.Unspecified,
            lineHeight = lineHeight,
            fontFamily = fontFamily,
            textDecoration = textDecoration,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing,
        ),
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/**
 * COUI 卡片表面：平滑圆角（CouiShape，权重 3）、主题卡片色；可点时带 COUI 列表按压反馈
 * （按下变暗 + 松手淡出），长按同样生效。
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    insideMargin: PaddingValues = PaddingValues(0.dp),
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = CouiShape(cornerRadius, 3f)
    val clickable = onClick != null || onLongPress != null
    val source = remember { MutableInteractionSource() }
    val click = modifier
        .expandSource(cornerRadius)
        .clip(shape)
        .background(CouiTheme.colorScheme.card)
        .then(
            if (clickable) Modifier
                .couiButtonFeedback(CouiTheme.isDark, source, true, list = true)
                .combinedClickable(interactionSource = source, indication = null, onClick = { onClick?.invoke() }, onLongClick = onLongPress)
            else Modifier
        )
        .padding(insideMargin)
    Column(click, content = content)
}

@Composable
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.takeOrElse { CouiTheme.colorScheme.onSurface },
) {
    Image(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint),
    )
}

@Composable
fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.takeOrElse { CouiTheme.colorScheme.onSurface },
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint),
    )
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (useFloatingChrome && LocalInCouiToolbar.current) {
        TopAppBarButton(onClick = onClick, modifier = modifier.expandSource(20.dp), enabled = enabled) {
            content()
        }
    } else if (LocalInCouiToolbar.current) {
        // Non-device preview of the 40dp COUI floating circle (Robolectric cannot run COUI AGSL).
        Box(
            modifier
                .size(40.dp)
                .expandSource(20.dp)
                .shadow(6.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.16f), spotColor = Color.Black.copy(alpha = 0.16f))
                .clip(CircleShape)
                .background(Color(0xF2F7F7F7))
                .couiClickable(enabled = enabled, press = true, list = false, shape = CircleShape, role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center,
            content = { content() },
        )
    } else {
        Box(
            modifier
                .size(44.dp)
                .expandSource(22.dp)
                .couiClickable(enabled = enabled, press = true, list = false, shape = CircleShape, role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center,
            content = { content() },
        )
    }
}

@Composable
fun SmallTitle(text: String, modifier: Modifier = Modifier) {
    CouiText(
        text = text,
        kind = CouiTextKind.GroupTitle,
        color = CouiTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp),
        textAlign = TextAlign.Start,
    )
}

@Composable
fun SmallTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    titleColor: Color = Color.Unspecified,
    subtitle: String = "",
    subtitleColor: Color = Color.Unspecified,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: Any? = null,
    defaultWindowInsetsPadding: Boolean = true,
    titlePadding: Dp = 0.dp,
    navigationIconPadding: Dp = 0.dp,
    actionIconPadding: Dp = 0.dp,
    bottomContent: @Composable () -> Unit = {},
    largeTitle: String = title,
    largeTitleColor: Color = Color.Unspecified,
) {
    AppTopBar(
        title = title,
        modifier = modifier,
        titleColor = titleColor,
        navigationIcon = navigationIcon,
        actions = actions,
        large = false,
    )
}

@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    titleColor: Color = Color.Unspecified,
    largeTitle: String = title,
    largeTitleColor: Color = Color.Unspecified,
    subtitle: String = "",
    subtitleColor: Color = Color.Unspecified,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: Any? = null,
    defaultWindowInsetsPadding: Boolean = true,
    titlePadding: Dp = 0.dp,
    navigationIconPadding: Dp = 0.dp,
    actionIconPadding: Dp = 0.dp,
    bottomContent: @Composable () -> Unit = {},
) {
    AppTopBar(
        title = title,
        modifier = modifier,
        titleColor = titleColor,
        navigationIcon = navigationIcon,
        actions = actions,
        large = true,
    )
}

@Composable
private fun AppTopBar(
    title: String,
    modifier: Modifier,
    titleColor: Color,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    large: Boolean,
) {
    val ink = titleColor.takeOrElse { CouiTheme.colorScheme.onSurface }
    val titleBlock = @Composable {
        Text(
            title,
            fontSize = if (large) 22.sp else 17.sp,
            fontWeight = if (large) FontWeight.Normal else FontWeight.Medium,
            color = ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    // COUI TopAppBar 没有 backdrop 作用域时会直接抛 IllegalStateException（1.0.2 切书源闪退即此）；
    // COUI Scaffold 会为顶栏提供 backdrop 作用域；不在我们的 Scaffold 里时退回普通顶栏，绝不抛异常。
    if (useFloatingChrome && LocalInCouiScaffold.current) {
        CompositionLocalProvider(LocalInCouiToolbar provides true) {
            CouiTopAppBar(
                modifier = modifier,
                title = titleBlock,
                navigationIcon = {
                    TopAppBarButtonGroup { navigationIcon() }
                },
                actions = {
                    TopAppBarButtonGroup { actions() }
                },
            )
        }
    } else {
        Row(
            modifier
                .fillMaxWidth()
                .background(CouiTheme.colorScheme.background)
                .statusBarsPadding()
                .heightIn(min = 56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navigationIcon()
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) { titleBlock() }
            Row(verticalAlignment = Alignment.CenterVertically, content = actions)
        }
    }
}

@Composable
fun SwitchPreference(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    val colors = com.itos.coui.layout.CouiPageDefaults.themeColors()
    val source = remember { MutableInteractionSource() }
    Item(
        modifier = modifier.sheetRow().toggleable(checked, source, null, enabled, Role.Switch, onValueChange = onCheckedChange),
        summary = summary?.let {
            { CouiText(text = it, kind = CouiTextKind.PreferenceSummary, color = colors.summary, textAlign = TextAlign.Start) }
        },
        trailing = {
            Switch(checked = checked, onCheckedChange = null, enabled = enabled)
        },
        enabled = enabled,
    ) {
        CouiText(text = title, kind = CouiTextKind.PreferenceTitle, color = colors.title, textAlign = TextAlign.Start)
    }
}

@Composable
fun ArrowPreference(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val colors = com.itos.coui.layout.CouiPageDefaults.themeColors()
    Item(
        modifier = modifier.sheetRow().expandSource(12.dp),
        summary = summary?.let {
            { CouiText(text = it, kind = CouiTextKind.PreferenceSummary, color = colors.summary, textAlign = TextAlign.Start) }
        },
        onClick = if (enabled) onClick else null,
        enabled = enabled,
    ) {
        CouiText(text = title, kind = CouiTextKind.PreferenceTitle, color = colors.title, textAlign = TextAlign.Start)
    }
}

@Composable
fun RadioButtonPreference(
    title: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
) {
    val colors = com.itos.coui.layout.CouiPageDefaults.themeColors()
    // 整行点击交给 COUI Item 自带的列表按压反馈（与 ArrowPreference 一致）。
    Item(
        modifier = modifier.sheetRow().semantics { role = Role.RadioButton; this.selected = selected },
        onClick = if (enabled) onClick else null,
        icon = { RadioButton(selected = selected, enabled = enabled) },
        summary = summary?.let {
            { CouiText(text = it, kind = CouiTextKind.PreferenceSummary, color = colors.summary, textAlign = TextAlign.Start) }
        },
        enabled = enabled,
    ) {
        CouiText(text = title, kind = CouiTextKind.PreferenceTitle, color = colors.title, textAlign = TextAlign.Start)
    }
}

/** 多选行：COUI Item + 官方 Checkbox，整行可切换。 */
@Composable
fun CheckboxPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    checkboxAtEnd: Boolean = false,
) {
    val colors = com.itos.coui.layout.CouiPageDefaults.themeColors()
    val source = remember { MutableInteractionSource() }
    val box: @Composable () -> Unit = { com.itos.coui.checkbox.Checkbox(checked = checked, enabled = enabled) }
    Item(
        modifier = modifier.sheetRow()
            .couiButtonFeedback(CouiTheme.isDark, source, enabled, list = true)
            .toggleable(checked, source, null, enabled, Role.Checkbox, onCheckedChange),
        icon = if (checkboxAtEnd) null else box,
        trailing = if (checkboxAtEnd) box else ({}),
        summary = summary?.let {
            { CouiText(text = it, kind = CouiTextKind.PreferenceSummary, color = colors.summary, textAlign = TextAlign.Start) }
        },
        enabled = enabled,
    ) {
        CouiText(text = title, kind = CouiTextKind.PreferenceTitle, color = colors.title, textAlign = TextAlign.Start)
    }
}


/** Gap between separate COUI floating toolbar circles. */
@Composable
fun RowScope.ToolbarGap() = Spacer(Modifier.width(8.dp))

/** 悬浮标签页顶栏的背景层；顶栏里的分类标签直接采样它，不再自己垫底色。 */
internal val LocalHeaderBackdrop = androidx.compose.runtime.staticCompositionLocalOf<CouiBackdrop?> { null }

/**
 * Tab page header: title + COUI floating TopAppBarButtons (TopAppBarSurface over [backdrop]),
 * with optional [below] rows (search pill, category strip) sharing the same blurred surface.
 */
@Composable
fun FloatingTabHeader(
    backdrop: CouiBackdrop?,
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    below: @Composable FoundationColumnScope.() -> Unit = {},
) {
    val body: @Composable () -> Unit = {
        CompositionLocalProvider(LocalInCouiToolbar provides true, LocalHeaderBackdrop provides (if (useFloatingChrome) backdrop else null)) {
            Column(Modifier.fillMaxWidth().statusBarsPadding()) {
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 60.dp).padding(start = 24.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        title,
                        modifier = Modifier.weight(1f),
                        color = CouiTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (useFloatingChrome) {
                        TopAppBarButtonGroup { actions() }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) { actions() }
                    }
                }
                below()
            }
        }
    }
    if (useFloatingChrome && backdrop != null) {
        TopAppBarSurface(modifier = modifier.fillMaxWidth(), backdrop = backdrop) { body() }
    } else {
        Box(modifier.fillMaxWidth().background(CouiTheme.colorScheme.background)) { body() }
    }
}

/**
 * COUI 搜索框（官方 SearchBar）。[backdrop] 为空时取所在顶栏（TopAppBarSurface）的背景；
 * 测试渲染无法运行 AGSL 时退回同尺寸的胶囊外观。
 */
@Composable
fun CouiSearchField(
    query: String,
    onQuery: (String) -> Unit,
    hint: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    cancel: String = "取消",
    backdrop: CouiBackdrop? = null,
) {
    if (useFloatingChrome) {
        val state = com.itos.coui.search.rememberCouiSearchBarState(expanded)
        if (backdrop != null) com.itos.coui.search.SearchBar(
            modifier = modifier.fillMaxWidth(), state = state, query = query, onQueryChange = onQuery,
            onExpandedChange = onExpandedChange, placeholder = hint, cancelText = cancel, clearDescription = "清除", backdrop = backdrop,
        ) else com.itos.coui.search.SearchBar(
            modifier = modifier.fillMaxWidth(), state = state, query = query, onQueryChange = onQuery,
            onExpandedChange = onExpandedChange, placeholder = hint, cancelText = cancel, clearDescription = "清除",
        )
        return
    }
    // 非真机替身：与 SearchBar 同高的悬浮胶囊 + 取消。
    val scheme = CouiTheme.colorScheme
    val focus = remember { FocusRequester() }
    if (expanded) LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        CouiFloatingCapsule(
            backdrop = null,
            modifier = Modifier.weight(1f).height(com.itos.coui.search.CouiSearchBarDefaults.surfaceHeight)
                .then(if (!expanded) Modifier.couiClickable { onExpandedChange(true) } else Modifier),
            contentPadding = PaddingValues(horizontal = 14.dp),
        ) {
            Icon(CouiIcons.Outlined.Search, contentDescription = null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                val style = rememberCouiTextStyle(CouiTextKind.Search)
                if (query.isEmpty()) BasicText(hint, style = style.copy(color = scheme.onSurfaceVariant), maxLines = 1)
                if (expanded) BasicTextField(
                    value = query, onValueChange = onQuery, singleLine = true,
                    textStyle = style.copy(color = scheme.onSurface),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth().focusRequester(focus),
                )
            }
        }
        if (expanded) CouiText(
            text = cancel, kind = CouiTextKind.SearchCancel, color = scheme.accent,
            modifier = Modifier.couiClickable { onExpandedChange(false) }.padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        )
    }
}

/** COUI floating text capsule for toolbar actions (取消 / 全选). */
@Composable
fun ToolbarTextButton(label: String, accent: Boolean = false, onClick: () -> Unit) {
    val color = if (accent) CouiTheme.colorScheme.accent else CouiTheme.colorScheme.onSurface
    if (useFloatingChrome && LocalInCouiToolbar.current) {
        TopAppBarButton(onClick = onClick, text = true) {
            Text(label, color = color, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    } else {
        CouiFloatingCapsule(
            backdrop = null,
            modifier = Modifier.height(40.dp).widthIn(min = 60.dp).couiClickable(press = true, list = false, shape = com.itos.coui.shape.CouiCapsuleShape, onClick = onClick),
            contentPadding = PaddingValues(horizontal = 14.dp),
        ) {
            Text(label, color = color, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    }
}


/**
 * 独立搜索页（书城 / 书架 / 历史）：结果层作为背景源，顶部 COUI TopAppBarSurface 内放展开态 SearchBar，
 * 进入时从收起态展开（官方动画），点「取消」或返回即离开。
 */
@Composable
fun SearchPage(
    query: String,
    onQuery: (String) -> Unit,
    hint: String,
    cancel: String,
    onCancel: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    var headPx by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(!useFloatingChrome) }
    LaunchedEffect(Unit) { expanded = true }
    val density = LocalDensity.current
    val backdrop = com.itos.coui.backdrop.rememberCouiBackdrop()
    Box(Modifier.fillMaxSize().background(CouiTheme.colorScheme.background)) {
        val top = with(density) { headPx.toDp() }
        Box(Modifier.fillMaxSize().then(if (useFloatingChrome) Modifier.couiBackdrop(backdrop) else Modifier).background(CouiTheme.colorScheme.background)) {
            content(PaddingValues(top = top))
        }
        val bar: @Composable () -> Unit = {
            Box(Modifier.fillMaxWidth().onSizeChanged { headPx = it.height }.statusBarsPadding().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
                CouiSearchField(query, onQuery, hint, expanded, { if (!it) onCancel() }, cancel = cancel)
            }
        }
        if (useFloatingChrome) TopAppBarSurface(modifier = Modifier.fillMaxWidth(), backdrop = backdrop) { bar() }
        else Box(Modifier.fillMaxWidth().background(CouiTheme.colorScheme.background)) { bar() }
    }
}

/** 滑杆行：COUI 官方 Slider，左侧短标签/图标，右侧数值，行距与 COUI Item 一致。 */
@Composable
fun SliderPreference(
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    valueText: String? = value.roundToInt().toString(),
    steps: Int = 0,
) {
    val scheme = CouiTheme.colorScheme
    Row(
        modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = if (enabled) scheme.onSurface else scheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
        } else if (label != null) {
            CouiText(text = label, kind = CouiTextKind.PreferenceTitle, color = if (enabled) scheme.onSurface else scheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.widthIn(min = 24.dp))
            Spacer(Modifier.width(12.dp))
        }
        com.itos.coui.slider.Slider(
            modifier = Modifier.weight(1f),
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            enabled = enabled,
        )
        if (valueText != null) {
            Spacer(Modifier.width(12.dp))
            CouiText(text = valueText, kind = CouiTextKind.PreferenceValue, color = scheme.onSurfaceVariant, textAlign = TextAlign.End, modifier = Modifier.widthIn(min = 36.dp))
        }
    }
}
