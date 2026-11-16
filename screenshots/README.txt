Stage files are prefixed (p1- = 阶段1). Unprefixed PNGs are older rounds.

All app screens are Robolectric / non-device captures (no KVM on this box).
They do NOT show the real 凝光 / Mesh floating material: Robolectric cannot run COUI AGSL shaders.
- Top-right toolbar buttons: on device they are COUI TopAppBarButton over TopAppBarSurface (floating material);
  shots show a layout-accurate 40dp circle stand-in.
- Bottom bar: on device com.itos.coui.navigation.NavigationBar + CouiFloatingRenderer, scaled 1.1x (RoomyNavScale);
  shots show a layout-accurate capsule at the same scale.
- 更多 menus (p1-menu-*): on device COUI Menu (floating material + COUI expand motion);
  shots show a layout-accurate card (28dp radius, 40dp rows, 20dp icons).
- p1-launcher-icon-preview.png: PIL simulation of adaptive icon masks (circle / squircle), not a launcher screenshot.
- Search result shots use sample rows; real pages reuse ShelfPane / HistoryPane / store search results.

p2- = 阶段2 (P2ScreenshotTest, Robolectric, non-device, same limits as above: no 凝光/Mesh material).
- These render the real app panes (DiscoverPane, SourcePickPane, ThemePane, IntroPane, StoragePane,
  ReadingPane, MinePane, PermissionDialog); only the bottom bar is the capsule stand-in.
- p2-bookstore-copy: live 拷贝漫画「推荐」data and covers fetched from the API at test time.
- p2-permission-first-launch: COUI Dialog sits at the top in Robolectric; on device it is centred.
- p2-storage / p2-storage-after-clear: test fills covers, listings, cacheDir and a half-downloaded page,
  then taps 清理 → 确认; the test asserts the remaining cache is < 1 KB and shelf data survives.
- p2-reading-settings: rendered on a 2000dp-tall surface so the whole list fits in one image.
- Version text shows 1.0.2 because Robolectric has no package versionName (fallback; becomes 0.1.0 in 阶段4).

== 阶段 2.5（p25-）==
全部为 Robolectric 非真机渲染（useFloatingChrome=false）：凝光/Mesh 悬浮材质、模糊、AGSL 按压高光都无法在此渲染，
图中悬浮胶囊/底部工具卡/轻提示/顶栏用的是同尺寸、同圆角、同投影的不透明替身；真机上走 couiFloatingSurface / TopAppBarSurface。
弹窗（p25-dialog-*, p25-permission-centered）：Robolectric 会忽略 Window gravity，把弹窗窗口画在 (0,0)。
测试先截无弹窗的底图，再截带弹窗的画面，把弹窗窗口区域内与底图不同的像素（卡片+投影）
按代码里显式的 placement 放到真机位置：Center → 水平垂直居中（授权提示），Bottom → 底部居中；
系统遮罩 FLAG_DIM_BEHIND 不渲染，以 COUI 浅色同强度（黑 20%）的遮罩层代替。
p25-compare-*.png：左为改前（p1/p2 截图），右为 p2.5。

== 阶段 3（p3-）==
p3-expand-book / p3-collapse-book / p3-expand-setting / p3-predictive-back：
在 Robolectric 里运行完整 ShuManYuApp，用测试时钟逐帧推进后按时间点截图（原始帧在 p3-frames/）。
动画就是 App 内的真实代码路径（Expand.kt），只有悬浮材质/底栏是不透明替身；非真机，帧时间为测试时钟时间。
测试环境里网络状态检测为离线，顶部会出现「当前没有网络」提示，与动画无关。
p3-theme-bookstore / p3-theme-dialog：主题色改为 COUI-Kit 默认蓝后的书城与弹窗。
