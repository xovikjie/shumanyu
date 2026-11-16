# 书漫屿 COUI 接入排查

- 排查时间：2026-11-11（Asia/Shanghai）
- 项目：`/workspace/shumanyu-coui/android`（Compose，包 `com.shumanyu.app`）
- 对照库：ItosEO/COUI-Kit，依赖 `io.github.itoseo:coui:0.0.1-alpha9`、`coui-icons:0.0.1-alpha9`；源码与文档在 `/tmp/COUI-Kit`
- 方式：只读对照，未改任何应用源码
- 结论先说：工程已去掉 Material / Material3 / Miuix 依赖，主题、底栏、顶栏悬浮按钮、确认弹窗、开关/单选设置行、右上角 Menu 已接到 COUI。未接入的主要是自绘控件（Card、Button、TextField、搜索条、滑条、复选框、底部表单、Toast）以及滚动回弹、折叠顶栏、ChipTab、InputDialog、图标替换和页面转场。

## 1. COUI-Kit 实际提供的组件与效果

来自 `docs/components/README.md`、`docs/effects/README.md` 与 `coui` / `coui-icons` 源码公开 API。

### 组件

| 组件 | 包 / 入口 | 用途 |
| --- | --- | --- |
| 页面容器 | `com.itos.coui.layout.Scaffold` | 统一顶栏、底栏、正文 Padding 与安全区 |
| 顶栏 | `TopAppBar`、`TopAppBarSurface`、`TopAppBarButton`、`TopAppBarButtonGroup` | 标题与悬浮操作按钮 |
| 折叠顶栏 | `LargeTopAppBar`、`CollapsingTopAppBar` | 大标题折叠；可与常驻搜索联动 |
| 顶栏搜索 | `TopAppBarSearch` 等 | 搜索按钮与输入框之间的转场 |
| 常驻搜索 | `com.itos.coui.search.SearchBar` + `rememberCouiSearchBarState` | 搜索胶囊与独立取消按钮、展开动画 |
| Group | `com.itos.coui.layout.Group` | 分组圆角卡片、标题操作与说明 |
| Item | `com.itos.coui.layout.Item` | 设置行：图标、主文案、摘要、尾部控件 |
| Divider | `com.itos.coui.layout.Divider` | 组内分隔线 |
| Footer | `com.itos.coui.layout.Footer` | 分组辅助说明 |
| 底栏 | `com.itos.coui.navigation.NavigationBar` | 页面选择与融合表面 |
| FAB | `com.itos.coui.floatingactionbutton.Fab` | 独立浮动操作 |
| 菜单 | `com.itos.coui.menu.Menu` | 选择、勾选、分组与子菜单 |
| 滑动条 | `com.itos.coui.slider.Slider` | 连续、分段与隐藏点位 |
| 复选框 | `com.itos.coui.checkbox.Checkbox` | 复选状态与切换动画 |
| 单选框 | `com.itos.coui.radiobutton.RadioButton` | 单选状态与切换动画 |
| 开关 | `com.itos.coui.switch.Switch` | 二值切换、拖动吸附与加载状态 |
| ChipTab | `ChipTab`、`ChipTabRow`、`ChipTabButton` | 单选分类、横向滚动 |
| 弹窗 | `Dialog` | 底部 / 居中窗口、受控操作 |
| 输入弹窗 | `InputDialog` | 带输入框的弹窗预设 |
| 选择器 | `WheelPicker`、`CalendarPicker` | 整数滚轮与月历 |
| 文字 | `com.itos.coui.text.Text` + `CouiTextKind` | 标题、摘要等标准字号 |
| 图标 | `CouiIcons.Filled` / `Outlined` / `Bitmap` | 官方矢量和位图 |
| 主题 | `CouiTheme`、`CouiPresets`、`CouiShape` | 配色、深浅色、平滑圆角 |

### 效果与动效（不是完整页面控件）

| 效果 | 入口 | 用途 |
| --- | --- | --- |
| 背景采样 | `CouiBackdrop`、`rememberCouiBackdrop`、`couiBackdrop` | 模糊材质采样 |
| 普通阴影 | `Modifier.couiShadow` | 替代 Foundation `shadow` |
| 模糊与材质 | `rememberCouiMaterialEffect` 等 | 均匀模糊、材质混色 |
| 浮动表面 | `couiFloatingSurface` / `couiFloatingSurfaces` | 组合采样、材质、投影、光效 |
| 轮廓与跟手光 | `rememberCouiSpotlight`、状态反馈 | 光效与按压反馈 |
| 运动 | `couiPressMotion`、`couiTouchMotion`、跟随回弹 | 按压缩放、触摸形变 |
| 边界回弹 | `couiVerticalScroll`、`rememberCouiOverscrollEffect` | 列表惯性与边缘回弹 |
| 横向融合 | Metaball / `CouiSwipeRevealDefaults` | 侧滑露出动作的参数与跟随；**没有完整 SwipeReveal 组件** |

### COUI 明确没有的控件

SnackBar / Toast、Circular / Linear Progress、通用 Button、通用 TextField（仅出现在 `SearchBar` / `InputDialog` 内部）、独立 BottomSheet 组件（底部形态需用 `Dialog(placement = Bottom)`）、Preference 专用组件（用 `Group` + `Item` 拼）、空状态插画、下拉刷新、整页「一镜到底」共享元素转场、侧边导航栏。

---

## 2. 已经接入、不必再报的部分

这些已经接到 COUI，或只是薄封装，不列入待替换清单（除非旁边还有自绘替代物）。

| 能力 | 现状 |
| --- | --- |
| 主题 | `App.kt` 用 `CouiTheme` + `CouiPresets` |
| 页面容器 | `CouiWidgets.kt` 的 `Scaffold` 转调 `com.itos.coui.layout.Scaffold` |
| 二级页顶栏 | `SubTopBar` → `TopAppBar` / `SmallTopAppBar` → `CouiTopAppBar` + `TopAppBarButtonGroup` |
| 主页顶栏按钮 | `FloatingTabHeader` 使用 `TopAppBarSurface` + `TopAppBarButton`；`IconButton` 在工具栏内走 `TopAppBarButton` |
| 底栏 | `App.kt` `NavigationBar` + `CouiNavigationItem`；`RoomyNavScale` 只是放大密度 |
| 右上角更多菜单 | `CornerMenu`、`FloatMenu` 使用 `Menu` / `CouiMenuItem` / `couiMenuAnchor` |
| 确认弹窗、权限说明 | `ConfirmSheet`、`PermissionAsk` 使用 `Dialog` |
| 设置行 | `SwitchPreference` / `ArrowPreference` / `RadioButtonPreference` 使用 `Item` + `Switch` / `RadioButton` + `CouiText` |
| 部分图标 | 底栏选中态 `CouiIcons.Filled.BookOpen` / `UserCircle`；筛选 `ListFilter`；书城网格 `LayoutSpliced`；网站管理 `Setting`；添加 `Add`；搜索图标 `Outlined.Search` |
| 主页顶栏采样 | 书架 / 历史顶栏用 `rememberCouiBackdrop` + `couiBackdrop` |

残留库：全工程未发现 Miuix、Material、Material3、`androidx.compose.material.icons` 引用。`app/build.gradle.kts` 只有 Compose Foundation / UI / Animation + COUI。

---

## 3. 按页面 / 功能分组的未接入清单

格式：页面或功能 / 当前控件 / 文件:行号 / 可替换的 COUI 组件。没有对应组件的标「COUI 无对应」。

### 3.1 公共封装（影响全应用）

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 通用正文文字 | 自封装 `Text`，底层 `BasicText`，未走 `CouiTextKind` | `CouiWidgets.kt:161`、`202` | `com.itos.coui.text.Text`（设置标题/摘要已在用；正文、按钮、空状态仍是自绘 Text） |
| 通用按钮 | 自绘 `Button` + `ButtonDefaults`（圆角矩形 + `clickable`，无 COUI 按压反馈） | `CouiWidgets.kt:242-305`；调用约 21 处，见书城空态、详情页、表单、网站管理等 | **COUI 无对应**通用 Button。出现在弹窗里的应改为 `Dialog` / `InputDialog` 的 `CouiDialogAction`；工具栏文字按钮已用 `TopAppBarButton` |
| 通用卡片 / 分组 | 自绘 `Card`（`CouiShape` + `card` 色），不用 `Group`。工程内约 49 处 | `CouiWidgets.kt:308-328`；设置页、详情、书城列表、存储、我的 等 | `Group`（分组卡片）；组内行用 `Item`，分隔用 `Divider`，底部说明用 `Footer` |
| 通用输入框 | 自绘 `TextField`（标签 + `BasicTextField` + 圆角背景） | `CouiWidgets.kt:408-454`；调用见 3.8 | 单字段弹窗用 `InputDialog`。页面内多行/多字段表单 **COUI 无对应**通用 TextField，可继续自绘，但样式应对齐 SearchBar / InputDialog |
| 分组小标题 | `SmallTitle` 已用 `CouiTextKind.GroupTitle`，但分组本身仍是 `Card` 而不是 `Group(title = …)` | `CouiWidgets.kt:395` | `Group` 的 `title` 槽，可去掉外层 SmallTitle + Card |
| 大标题顶栏 | `TopAppBar(large = true)` 只把字号改为 22sp，仍是普通 `CouiTopAppBar`，没有折叠 | `CouiWidgets.kt:485-511`、`515-546` | `LargeTopAppBar` + `CouiTopAppBarDefaults.exitUntilCollapsedScrollBehavior`。当前 `SubTopBar` 调用几乎都是 `BarTitle.End`（小标题），大标题路径基本闲置 |
| `AlignTitleBar` | 纯 Row 自绘标题栏，未用 COUI 顶栏 | `App.kt:1960` | `TopAppBar` / `LargeTopAppBar`（若确认已无调用可删除，本次只记录定义） |

### 3.2 搜索（书城 / 书架 / 历史 / 我的）

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 常驻搜索胶囊 | 自绘 `PillSearchField`：圆角矩形 + 自配背景 + `BasicTextField` 或 `clickable` 入口 | 定义 `CouiWidgets.kt:685`；历史入口 `App.kt:1177`；书架入口 `App.kt:1232`；书城入口 `App.kt:3902`；我的页内筛选 `App.kt:4627` | `SearchBar` + `rememberCouiSearchBarState`。主页顶栏下的搜索可再挂到 `CollapsingTopAppBar` |
| 独立搜索页 | 自绘 `SearchPage`：普通背景条 + `PillSearchField` + 文字「取消」 | 定义 `CouiWidgets.kt:764`；本地搜 `App.kt:1430`；书城搜 `App.kt:4431` | `SearchBar`（展开态自带取消按钮与弹簧动画）。从入口胶囊到搜索页的转场应对 `docs/components/search.md` 的顶栏搜索，或 SearchBar 自身展开 |
| 我的页搜索 | 顶栏下方常驻 `PillSearchField`，输入后原地过滤设置项，没有取消按钮和展开动效 | `App.kt:4627-4632` | `SearchBar`（文档明确适用于设置页列表搜索） |

### 3.3 主页：书架、历史、书城、我的、底栏

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 书架 / 历史顶栏结构 | `FloatingTabHeader` 自拼大标题 Row，搜索条放在 `below` 槽，未用折叠顶栏 | `CouiWidgets.kt:643-680` | `CollapsingTopAppBar` 或 `LargeTopAppBar`，搜索用 `SearchBar` 联动 |
| 书城分类 Tab | 自绘 `SourceTab`（文字 + 底部 2dp 指示条） | `App.kt:4295-4313` | `ChipTab` / `ChipTabRow` |
| 书城标签横滑 | 自绘 `SourceTagStrip`（`horizontalScroll` + 文字点击） | `App.kt:4243-4292` | `ChipTabRow` |
| 书架筛选表单内的 Tab | `ShelfSheet` 顶部「筛选 / 排序 / 显示」同样是文字 + 下划线 | `App.kt:3282-3301` | `ChipTab` / `ChipTabRow` |
| 阅读器设置表单 Tab | `ReaderSettingsSheet`「阅读模式 / 通用 / 滤镜」同样是文字 + 下划线 | `App.kt:7141-7160` | `ChipTab` / `ChipTabRow` |
| 书架 / 历史 / 书城书籍卡片 | 自绘 `Card` + 封面（`ShelfGridTile` `App.kt:2507`、`ShelfListRow` `App.kt:2547`、`DiscoverListRow` `App.kt:4101`） | 同上 | 封面网格 **COUI 无对应**专用书封组件；若改成设置式列表行可用 `Group` + `Item`，网格封面建议保留自绘，仅统一圆角（`CouiShape`）和按压（`couiPressMotion`） |
| 空状态 | 自绘 `EmptyFace`（颜文字 + 文案） | 定义 `App.kt:2026`；书架 `2156`、`2160`；历史 `3424`；书城加载/离线/空列表 `3804-3823`；搜索无结果 `1443`、`4442`；收藏 `8587`；文件夹 `2875`；WebView 不可用 `7761` | **COUI 无对应**空状态组件。可继续自绘，文案与间距对齐 COUI 字体 |
| 加载提示 | 书城底部 `NativeListingFooter` 用纯文字「正在加载…」；按钮在忙碌时显示「…」 | `App.kt:2013-2018`、`3805`、`4746` | **COUI 无对应** ProgressIndicator。开关的 loading 态不适用这里 |
| 批量删除悬浮按钮 | 自绘红色圆形 Box + 自绘垃圾桶图标，不是 FAB | 书架 `App.kt:2311-2323`；同类还有历史 `3532`、文件夹 `2945` | `Fab`（颜色需自定义；默认强调色可能不适合破坏性操作）。图标见第 4 节，垃圾桶 **COUI 无对应** |
| 平板侧栏 | 自绘 `TabletRail` | `App.kt:3117` | **COUI 无对应**侧边导航；只有底部 `NavigationBar` |
| 下拉刷新 | 自绘 `PullRefreshBox`，且没有刷新指示器，只是把内容下移 | `PullRefresh.kt:86-133` | **COUI 无对应**下拉刷新。列表回弹可改用 `rememberCouiOverscrollEffect` / `couiVerticalScroll`，不能替代刷新手势 |
| 列表滚动 | 各页 `LazyColumn` / `verticalScroll` 均未加 COUI 回弹 | 例如书架、历史 `App.kt:3427`，阅读设置 `Reading.kt:340`，存储 `Storage.kt:74`，我的 `App.kt:4565`，阅读器设置 `App.kt:7163` 等，全应用多处 | `Modifier.couiVerticalScroll` 或 `rememberCouiOverscrollEffect` 接到 Lazy 列表 |
| 我的页头图与统计 | 自绘 `Card` + 图片 + `MineStat` | `App.kt:4573-4603`、`4484` | 头图 **COUI 无对应**；下方设置分组应改为 `Group` + `Item`（`App.kt:4609-4614` 仍是 `Card` 包 `ArrowPreference`） |

### 3.4 设置类二级页（我的子页）

这些页大多已经是 `Scaffold` + `SubTopBar` + `SwitchPreference` / `RadioButtonPreference` / `ArrowPreference`，但分组外壳仍是 `Card`，滚动无 COUI 回弹，也没有 `Group` / `Divider` / `Footer`。

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 阅读设置 | `Card` 分组 + Preference；选项弹出 `ChoiceSheet` | `Reading.kt:342-410`，选择器 `415-426` | 分组改 `Group`；`ChoiceSheet` 改 `Dialog`（底部，内容用 `RadioButton` + `Item`） |
| 主题设置 | `Card` + `RadioButtonPreference`；仍含 Monet 开关 | `App.kt:7976` 起，卡片约 `7994`、`8014`、`8058` | `Group`；Monet 是业务项，与组件库无关 |
| 更多设置 | `Card` + Preference | `App.kt:3031`、`3062`、`3089` | `Group` |
| 语言 | `Card` + `RadioButtonPreference` | `App.kt:8200-8204` | `Group` |
| 字体大小 | `Card` + `RadioButtonPreference` | `App.kt:8700`、`8715-8719` | `Group`；若改为连续调节可用 `Slider`（当前是四档单选，不一定要改） |
| 软件介绍 / 更新日志 / 开源声明 | `Card` 包正文 | `App.kt:8108`、`8145`、`8183`、`8677` | 长文 **COUI 无对应**文章组件；外层可用 `Group`，或普通 Column + COUI 文字样式 |
| 备份恢复 | `Card` + 自绘 `Button` + `Toast` | `App.kt:7957` 一带 | `Group` + `Item`；结果反馈见 3.9 |
| 意见反馈 | `Card` + `Button` + `Toast` | `App.kt:7835`、`7830` | 同上 |
| 实验室 | `Card` + Preference | `App.kt:2676`、`2692` | `Group` |
| 横幅设置 | `SubTopBar` + 自绘 `Button` | `App.kt:2729`、`2743-2744` | 主按钮 **COUI 无对应**；可保留自绘并加 `couiPressMotion` / `couiButtonFeedback` |
| 存储空间 | `StorageCard` / `Card` 自绘；环形用量图 `Canvas`；路径行自绘 | `Storage.kt:57-117`、`StorageCard 266`、`StorageRing 311`、`PathRow 290` | 列表分组用 `Group` + `Item`。环形图、路径复制 **COUI 无对应** |
| 存储列表 | `Card` 列表 + `ConfirmSheet`（已是 Dialog）+ 批量选择 | `Storage.kt:121` 起，卡片约 `167` | 行可用 `Group`/`Item`；多选框见 3.5 |

### 3.5 选择控件：复选、单选、滑条（自绘，未用 COUI）

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 多选圆点 | 自绘圆形 + 「✓」`CheckDot` | 定义 `Reading.kt:432`；收藏/历史多选 `App.kt` 约 `2949` 前的选中态、`3463`、`3495` 等 | `Checkbox`（样式是方框勾选，不是圆点；若必须保持圆点则 **COUI 无对应**） |
| 书架筛选复选 | 自绘方框 + 「✓」`ShelfCheck` | `App.kt:3367-3383`，调用 `3306-3308`、`3348-3358` | `Checkbox`，建议再包进 `Item` |
| 书架显示模式单选 | 自绘圆点 `ShelfRadio` | `App.kt:3386-3402`，调用 `3340` | `RadioButton` + `Item`（设置页已有 `RadioButtonPreference`，这里没复用） |
| 阅读器亮度 / 字号等 | 自绘 `TrackSlider`（细轨 + 灰圆点，手势自处理） | 定义 `App.kt:7354-7388`；小说工具栏 `5707`、`5800`；滤镜 `7348`；自定义亮度 `7280` | `Slider` |
| 阅读器滤镜 RGBA | `FilterSlider` → `TrackSlider` | `App.kt:7342-7350`，调用 `7253-7256` | `Slider` |
| 漫画阅读进度 | 自绘 `PageSlider` | 定义 `App.kt:7694`；工具栏 `7671`；小说分页 `5601` | `Slider`（可设 `steps` 做整页吸附） |
| 阅读器设置里的开关 | 自绘胶囊 `SheetSwitch`（固定蓝/灰，不是 COUI `Switch`） | `App.kt:7320-7338`，调用 `7232-7274` | `Switch`，建议用 `SwitchPreference` / `Item` |
| 阅读器设置里的箭头行 | 自绘 `SheetLine` | `App.kt:7303` | `Item`（`ArrowPreference`） |

### 3.6 弹窗、底部表单、菜单（未用 COUI Dialog / Menu / InputDialog）

已用 `Dialog` 的：`ConfirmSheet`、权限说明。下面这些仍是自绘遮罩 + `Card` / 底部白底面板。

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 通用遮罩 | `OverlayScrim`（黑色半透明 + `clickable`） | `Reading.kt:211` | 应随内容一起换成 `Dialog`，不要单独挡一层 |
| 阅读设置选项弹出 | `ChoiceSheet`：遮罩 + `Card` + `RadioButtonPreference` + 文字取消 | `Reading.kt:281-315` | `Dialog(placement = Bottom 或 Center)`，选项继续用 `RadioButton` + `Item` |
| 收藏夹选择 / 创建 | `FolderPickSheet`：遮罩 + `Card` + `TextField` + `Button` + `ArrowPreference` | `App.kt:2750-2778` | 创建用 `InputDialog`；选择列表用 `Dialog` + `Item` |
| 文件夹重命名、添加网站、重命名网站、新建收藏夹、编辑收藏书目 | 同样是 `OverlayScrim` + `Card` + `TextField` + `Button` | `App.kt:2964`、`3940`、`4768`、`8619`、`8608` | 单行输入统一 `InputDialog`；多字段（添加网站两个输入框 `3944`、`3950`；书源三个输入框 `4664-4666`）**COUI 无对应**多输入弹窗，可用一个 `Dialog` 自定义内容，输入样式对齐 `InputDialog` |
| 右上角旧菜单残留 | `MenuSheet`：遮罩 + `Card` + 纯文字行（书城等已改 `CornerMenu`，这里仍在用） | 定义 `Reading.kt:250`；调用 `App.kt:2949`（文件夹页） | `Menu`（与 `CornerMenu` 相同） |
| 书架筛选 / 排序 / 显示 | 自绘底部面板 `ShelfSheet`（圆角白底、拖动条、自绘 Tab/Check/Radio） | `App.kt:3256-3364` | 整体可用 `Dialog(placement = Bottom)`；内部控件见 3.5、3.3 |
| 阅读器设置面板 | 自绘底部 74% 高白面板 `ReaderSettingsSheet` | `App.kt:7088-7163` 起 | 可用 `Dialog(placement = Bottom)`；内部 Switch/Slider/Tab 见上。若必须是可拖高度的驻留面板，**COUI 无对应**独立 BottomSheet |
| 长按图片操作 | 自绘底部列表 `PageActionSheet` | `App.kt:7590-7618` | `Dialog(placement = Bottom)` + `Item`，或 `Menu` |
| 阅读器模式 / 旋转等浮动菜单 | `FloatMenu` 已用 COUI `Menu` | `App.kt:7067` | 已接入，不列入待办 |

### 3.7 阅读器与详情页

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 作品详情顶栏 | 部分用 `IconButton` + 自绘 `Icons.Back` / `Download` / `Globe`，未全部走工具栏悬浮按钮逻辑 | `App.kt:4928-4957` | 顶栏按钮用 `TopAppBar` + `TopAppBarButton`；返回图标用 `CouiIcons.Outlined.Back`。下载、地球图标见第 4 节 |
| 详情操作按钮 | 自绘 `Button`（加入书架、收藏夹、阅读等） | `App.kt:5054-5065` | **COUI 无对应**通用 Button；可自绘并加 `couiButtonFeedback` / `couiPressMotion`，或主操作考虑 `Fab` |
| 章节列表 | `Card` + 文字行 + 自绘下载图标 | `App.kt:5087`、`5148` | 行可用 `Item`；下载图标 **COUI 无对应** |
| 漫画阅读器顶栏 / 底栏 | 自绘白底 `ReaderChrome`，不是 `TopAppBar` / `NavigationBar` | `App.kt:7623-7681` | 顶栏可试 `TopAppBar`（阅读沉浸场景不一定合适）。底栏上一话/下一话/设置是阅读专用工具条，**COUI 无对应**阅读器铬栏；进度条改 `Slider`，返回图标改 `CouiIcons.Outlined.Back`，设置改 `CouiIcons.Outlined.Setting` |
| 小说阅读工具 | `NovelTool`、`TrackSlider`、`PageSlider` | `App.kt:5890`、`5601`、`5707`、`5800` | 滑条改 `Slider`；其余为阅读专用，**COUI 无对应** |
| 章节分隔 | 自绘 `ChapterDivider`（读完 / 下一章文案） | `App.kt:7401` | **COUI 无对应**（`Divider` 只是设置组细线，语义不同） |
| 内置浏览器 | `WebPane`：`SubTopBar` + `AndroidView(WebView)` | `App.kt:7758-7786` | WebView **COUI 无对应**；顶栏可维持 `SubTopBar` |

### 3.8 输入框调用点（对应 3.1 / 3.6）

自绘 `TextField` 出现在：

- `App.kt:2764` 创建收藏夹
- `App.kt:2968` 重命名文件夹
- `App.kt:3944`、`3950` 添加网站（名称、网址）
- `App.kt:4664-4666` 书源名称 / 网址 / 备注（页面内表单，不是弹窗）
- `App.kt:4772` 重命名网站
- `App.kt:8623` 新建收藏夹

单字段弹窗优先 `InputDialog`；书源页三个字段留在页面里，**COUI 无对应**多行表单输入。

### 3.9 Toast / 提示

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 系统 Toast | `android.widget.Toast`，全应用约 42 处 | 例如 `App.kt:593`、`684`、`753`、`922-966`、`1726`、`3101`、`4702-4722`、`4934-4947`、`5437`、`6147`、`6532-6579`、`7830`、`7940-7950`；`Storage.kt:355`、`378`、`384`、`389`、`392`；`AppUpdateController.kt:65` | **COUI 无对应** Snackbar / Toast |
| 应用内气泡 | 自绘 `NoteHost` + `ShelfNotes`（白胶囊 + 图标 + 阴影） | `PullRefresh.kt:43-83`；挂载 `App.kt:1924` | **COUI 无对应**。视觉上可改用浮动材质（`couiFloatingSurface` + `couiShadow`），但没有现成提示组件 |

### 3.10 页面切换与动效

| 页面 / 功能 | 当前用的控件 | 文件:行号 | COUI 对应 |
| --- | --- | --- | --- |
| 二级页进入 | 直接切换 `screen`，没有从点击处展开的入场动画 | `App.kt` 中 `Draw` / `layerChain`，约 `1132-1888` | **COUI 无对应**整页共享元素 /「一镜到底」转场。库里的运动是控件级：搜索展开、菜单弹出、按压形变、滚动回弹 |
| 系统返回手势 | 自写缩放 + 平移（`graphicsLayer` scale 0.9–1.0，按边缘平移） | `App.kt:1074-1088`、`1842-1866` | **COUI 无对应**预测性返回模板。可保留现有逻辑 |
| 可点击卡片 / 按钮按压 | 普通 `clickable` / `combinedClickable`，未用 COUI 按压 | 全书封、自绘 Button、列表行等 | `couiPressMotion`、`couiButtonFeedback`（`Item.onClick` 已自带列表反馈；自绘卡片没有） |
| 书架长按拖拽排序 | 自写拖拽幽灵层 + `shadow` | `App.kt:2287-2309` | 拖拽排序 **COUI 无对应**。阴影可改 `couiShadow`；若以后做侧滑删除，只有 `CouiSwipeRevealDefaults` 参数，**没有完整侧滑组件** |

---

## 4. 自绘图标对照

两处自绘图标库：

- `TabIcons`：`TabIcons.kt` 全文（约 21-265 行），底栏未选中态和工具栏图标
- `Icons`：`App.kt:8727` 起，返回、加号、下载、地球、书签、上一话/下一话、阅读、旋转、裁边、齿轮、垃圾桶、网格等

### 4.1 CouiIcons 里有可替换项

| 当前图标 | 位置（主要调用） | 建议替换 |
| --- | --- | --- |
| `Icons.Back` | `SubTopBar` `App.kt:1944`、`1952`，以及 `2838`、`4928`、`5531`、`5565`、`7645`、`7774` | `CouiIcons.Outlined.Back`（Filled 同源；支持 RTL 镜像） |
| `Icons.Plus` | 收藏夹创建 `App.kt:8519` | `CouiIcons.Outlined.Add` |
| `Icons.Grid` | 长按设为封面 `App.kt:6991` | `CouiIcons.Filled.Grid` |
| `Icons.Gear` | 阅读器设置、亮度 `App.kt:7681`、`7279` | `CouiIcons.Outlined.Setting` 或 `Filled.Setting` / `Filled.GridSliders` / `Outlined.SliderCustom`（按语义选） |
| `TabIcons.More` | 右上角三点 `App.kt:1987` | `CouiIcons.Outlined.More`（用户已要求重绘三点，应直接换官方图标） |
| `TabIcons.Swap` | 书城「切换书源」`App.kt:3895` | 可近似用 `CouiIcons.Filled.Transfer`（系统「传输」图标，不是专门的换源符号） |
| `TabIcons.Rows` | 历史「切换横竖排」`App.kt:1160` | 可近似用 `CouiIcons.Outlined.LayoutSpliced`（书城网格按钮已在用） |
| `TabIcons.BookOpenOutlined` | 底栏 / 侧栏未选中「书架」`App.kt:1266`、`3121` | **仅有** `CouiIcons.Filled.BookOpen`，没有 Outlined 书本。未选中态 **COUI 无对应**，可继续自绘轮廓，或两态都用 Filled 靠透明度区分（效果一般） |
| `TabIcons.UserCircleOutlined` | 底栏 / 侧栏未选中「我的」`App.kt:1269`、`3124` | **仅有** `CouiIcons.Filled.UserCircle`，Outlined **COUI 无对应** |

### 4.2 CouiIcons 里没有对应（须自绘保留）

| 当前图标 | 用途 | 说明 |
| --- | --- | --- |
| `Icons.Download` / 详情与长按保存 | 下载、保存图片 | 无下载箭头 |
| `Icons.Globe` | 打开网站 | 无地球/浏览器 |
| `Icons.Trash`、`TabIcons.Trash` | 删除、清空、批量移除 FAB | 无垃圾桶 |
| `Icons.Bookmark`、`Icons.BookmarkOn` | 书签（若仍引用） | 无书签 |
| `Icons.SkipPrev`、`Icons.SkipNext` | 上一话 / 下一话 | 无章节跳转 |
| `Icons.Reader` | 阅读模式 | 可用 `Filled.BookOpen` 近似，不是同一字形 |
| `Icons.Rotate` | 旋转、重新加载图片 | 无旋转 |
| `Icons.Crop` | 裁边 | 无裁剪 |
| `TabIcons.Refresh` | 书架刷新 | 无刷新 |
| `TabIcons.SelectMany` | 批量删除 | 无多选 |
| `TabIcons.HistoryOutlined` / `HistoryFilled` | 底栏「历史」 | 无历史时钟 |
| `TabIcons.StoreOutlined` / `StoreFilled` | 底栏「书城」 | 无商店/书城 |

`CouiIcons` 里还有大量系统设置类图标（Wifi、Lock、Lab、Album、Photo 等），当前应用几乎没用到；与阅读器场景匹配的只有 Search、Back、Add、More、Setting、ListFilter、LayoutSpliced、BookOpen、UserCircle、Grid、Transfer 等一小撮。

---

## 5. 其它库残留

- **Miuix**：未发现 import、依赖或字符串残留。
- **Material / Material3**：未发现依赖与 import。自绘组件名字（`Button`、`Card`、`TextField`、`IconButton`、`SmallTopAppBar`）是应用自己的封装，不是 androidx.compose.material。
- **其它 UI 库**：无 Accompanist、无 material-icons。提示用的是 Android `Toast` 和自绘 `NoteHost`。通知进度在 `Notify.kt`，属于系统 Notification，不是界面控件。

---

## 6. 按优先级的替换建议

### P0 — 用户能直接看出「还不像 COUI」的控件

1. **搜索全面换成 `SearchBar`**  
   替换 `PillSearchField` / `SearchPage`。书城、书架、历史：收起胶囊点击后展开（可进入独立结果层，空查询不显示结果）。「我的」用同一组件做设置项过滤。这是库里现成、动效完整的能力，和当前自绘胶囊差别最大。

2. **设置页分组从 `Card` 换成 `Group` + `Divider` + `Footer`**  
   覆盖：我的、阅读设置、主题、更多、语言、字体、备份、反馈、实验室、介绍、存储、书源列表等所有 Preference 卡片。行本身已是 `Item`，只差外壳。这是 COUI 设置页最基本的结构。

3. **自绘弹窗改 `Dialog` / `InputDialog`**  
   `ChoiceSheet`、`FolderPickSheet`、重命名、添加网站（能拆成单字段的）、`ShelfSheet`、`PageActionSheet`、文件夹 `MenuSheet`。确认框已经在用 `Dialog`，其它还在用遮罩 + `Card`，风格不统一。  
   阅读器 `ReaderSettingsSheet` 若需半屏驻留，可先把内部 Switch/Slider/Tab 换掉，外壳能套 `Dialog(Bottom)` 再套；硬要拖拽高度则只能自绘（COUI 无 BottomSheet）。

4. **阅读器与筛选里的自绘 Switch / Radio / Checkbox / Slider 换官方组件**  
   `SheetSwitch` → `Switch`；`ShelfCheck` / 可选的 `CheckDot` → `Checkbox`；`ShelfRadio` → `RadioButton`；`TrackSlider` / `FilterSlider` / `PageSlider` → `Slider`。这是用户天天碰到的控件，现在完全是另一套外观。

### P1 — 结构与动效对齐

5. **分类 Tab 换 `ChipTab` / `ChipTabRow`**  
   书城 `SourceTab`、`SourceTagStrip`，以及书架筛选表和阅读器设置表顶部的三段 Tab。

6. **列表加上 COUI 滚动回弹**  
   所有 `LazyColumn` / `verticalScroll` 接 `rememberCouiOverscrollEffect` 或 `couiVerticalScroll`。工作量大但改动局部、风险低。

7. **图标能换的先换**  
   至少：`Back`、`Add`、`More`、`Setting`/`Gear`、`Grid`。`Transfer` / `LayoutSpliced` 仅作近似，需产品确认。没有对应的（历史、书城、刷新、删除、下载、地球、上一话/下一话、裁边、旋转、多选）继续自绘，不要硬用不相干图标。底栏未选中的书本、头像因只有 Filled，建议保留自绘轮廓。

8. **可点击表面补 COUI 按压**  
   书封、自绘按钮、非 `Item` 的列表行加 `couiPressMotion` 或 `couiButtonFeedback`。

9. **二级页顶栏如需大标题折叠，改用 `LargeTopAppBar`**  
   当前 `SubTopBar(..., BarTitle.End)` 都是小标题，先保持也可以；若要设置页大标题下滑折叠，不要只改字号，应接到官方折叠顶栏。

### P2 — 没有官方组件、只能近似或维持自绘

10. **通用 `Button`、页面内多字段 `TextField`、封面网格、阅读器铬栏、WebView、环形存储图、空状态、Toast、下拉刷新、平板侧栏、拖拽排序、整页一镜到底转场**  
    均为 **COUI 无对应**。建议：按钮和输入框在视觉上向 Dialog / SearchBar 靠拢并加上按压反馈；Toast 可暂时保留，或把 `NoteHost` 做成浮动材质；空状态只统一字体和间距；一镜到底需要应用自己做共享元素或圆形揭示，库帮不上整页转场。预测性返回可继续用现有缩放平移。

11. **破坏性 FAB**  
    批量删除红圆可试 `Fab`，但库默认强调色和垃圾桶图标都不匹配，需要自定义颜色并保留自绘图标，收益一般，可放后。

### 建议动手顺序

先做 P0 的搜索、Group、弹窗、阅读器滑条/开关，界面会立刻整齐；再做 ChipTab、overscroll、图标和按压；无对应的能力不要为了「全换完」而硬套。本次排查未改代码，另一个流程若已开始改同一批文件，合入时以那边为准重新对一下行号。
