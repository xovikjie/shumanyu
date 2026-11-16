package com.shumanyu.app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * 书漫屿自绘图标，COUI-Kit 没有对应字形的部分。按 CouiIcons 线框规范绘制：
 * 24dp 画布、1.4 线宽、圆头圆角、只用单色（由 tint 着色）；选中态（Filled）为实心剪影。
 * 能用官方图标的地方直接用 CouiIcons（返回、添加、更多、设置、网格、搜索、筛选等）。
 */
internal object ShuIcons {
    private const val W = 1.4f

    private fun line(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            paths.forEach { d ->
                addPath(
                    pathData = addPathNodes(d),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = W,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()

    private fun solid(name: String, d: String, evenOdd: Boolean = true): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            addPath(
                pathData = addPathNodes(d),
                fill = SolidColor(Color.Black),
                pathFillType = if (evenOdd) PathFillType.EvenOdd else PathFillType.NonZero,
            )
        }.build()

    // 底栏 / 侧栏
    val BookOpenOutlined = line(
        "bookOpen",
        "M12,6.6C10,5.1 7.2,4.6 3.6,5V18.4C7.2,18 10,18.5 12,20C14,18.5 16.8,18 20.4,18.4V5C16.8,4.6 14,5.1 12,6.6Z",
        "M12,6.6V20",
    )
    val HistoryOutlined = line(
        "history",
        "M4.1,13.2A8.1,8.1 0,1 0,6.3 6.2",
        "M3.6,4.2V7.6H7",
        "M12,7.6V12L15,13.8",
    )
    val HistoryFilled = solid(
        "historyFilled",
        "M12,2.9A9.1,9.1 0,1 1,12 21.1A9.1,9.1 0,1 1,12 2.9Z" +
            "M11.3,7.4L12.7,7.4L12.7,11.6L15.8,13.45L15.1,14.65L11.3,12.4Z",
    )
    val StoreOutlined = line(
        "store",
        "M4.4,10.4L5.6,4.6H18.4L19.6,10.4",
        "M3.6,10.4C3.6,11.8 4.6,12.6 5.8,12.6C7,12.6 7.9,11.8 7.9,10.4C7.9,11.8 8.9,12.6 10,12.6C11.1,12.6 12,11.8 12,10.4C12,11.8 12.9,12.6 14,12.6C15.1,12.6 16.1,11.8 16.1,10.4C16.1,11.8 17,12.6 18.2,12.6C19.4,12.6 20.4,11.8 20.4,10.4",
        "M5.2,12.6V18.6C5.2,19.6 5.9,20.4 7,20.4H17C18.1,20.4 18.8,19.6 18.8,18.6V12.6",
        "M10,20.4V16.4H14V20.4",
    )
    val StoreFilled = solid(
        "storeFilled",
        "M5.1,3.9H18.9L20.6,10.3C20.6,12.1 19.4,13.3 18.2,13.3C17.3,13.3 16.6,12.9 16.1,12.2C15.6,12.9 14.9,13.3 14,13.3C13.1,13.3 12.4,12.9 12,12.2C11.6,12.9 10.9,13.3 10,13.3C9.1,13.3 8.4,12.9 7.9,12.2C7.4,12.9 6.7,13.3 5.8,13.3C4.6,13.3 3.4,12.1 3.4,10.3Z" +
            "M4.6,14.3C5,14.4 5.4,14.5 5.8,14.5C6.6,14.5 7.3,14.2 7.9,13.8C8.5,14.2 9.2,14.5 10,14.5C10.8,14.5 11.5,14.2 12,13.8C12.5,14.2 13.2,14.5 14,14.5C14.8,14.5 15.5,14.2 16.1,13.8C16.7,14.2 17.4,14.5 18.2,14.5C18.6,14.5 19,14.4 19.4,14.3V18.6C19.4,20 18.4,21.1 17,21.1H14.6V16.4C14.6,16 14.4,15.8 14,15.8H10C9.6,15.8 9.4,16 9.4,16.4V21.1H7C5.6,21.1 4.6,20 4.6,18.6Z",
    )
    val UserCircleOutlined = line(
        "userCircle",
        "M12,3A9,9 0,1 1,12 21A9,9 0,1 1,12 3Z",
        "M12,6.6A3.2,3.2 0,1 1,12 13A3.2,3.2 0,1 1,12 6.6Z",
        "M6.2,18.2C7.5,16 9.6,14.9 12,14.9C14.4,14.9 16.5,16 17.8,18.2",
    )

    // 工具栏 / 菜单
    val Refresh = line("refresh", "M19.6,12A7.6,7.6 0,1 1,17.2 6.5", "M17.9,2.9L17.4,6.9L13.4,6.5")
    val SelectMany = line(
        "selectMany",
        "M3.6,7.4L5.6,9.4L9.1,5.9", "M12.4,7.6H20.4",
        "M3.6,15.4L5.6,17.4L9.1,13.9", "M12.4,15.6H20.4",
    )
    val Trash = line(
        "trash",
        "M3.6,6.2H20.4",
        "M9,6.2V4.8C9,4.1 9.5,3.6 10.2,3.6H13.8C14.5,3.6 15,4.1 15,4.8V6.2",
        "M5.6,6.2L6.5,18.9C6.6,19.8 7.3,20.4 8.2,20.4H15.8C16.7,20.4 17.4,19.8 17.5,18.9L18.4,6.2",
        "M10,10.2V16.4", "M14,10.2V16.4",
    )
    val Swap = line("swap", "M4,8.4H18.6", "M15.2,5L18.6,8.4L15.2,11.8", "M20,15.6H5.4", "M8.8,12.2L5.4,15.6L8.8,19")
    val Rows = line(
        "rows",
        "M5.6,4.4H18.4C19.3,4.4 20,5.1 20,6V9C20,9.9 19.3,10.6 18.4,10.6H5.6C4.7,10.6 4,9.9 4,9V6C4,5.1 4.7,4.4 5.6,4.4Z",
        "M5.6,13.4H18.4C19.3,13.4 20,14.1 20,15V18C20,18.9 19.3,19.6 18.4,19.6H5.6C4.7,19.6 4,18.9 4,18V15C4,14.1 4.7,13.4 5.6,13.4Z",
    )
    val Download = line("download", "M12,3.6V15", "M7.4,10.6L12,15.2L16.6,10.6", "M4,15.6V18.4C4,19.5 4.9,20.4 6,20.4H18C19.1,20.4 20,19.5 20,18.4V15.6")
    val Globe = line(
        "globe",
        "M12,3A9,9 0,1 1,12 21A9,9 0,1 1,12 3Z",
        "M12,3C9.4,5.6 8.4,8.8 8.4,12C8.4,15.2 9.4,18.4 12,21",
        "M12,3C14.6,5.6 15.6,8.8 15.6,12C15.6,15.2 14.6,18.4 12,21",
        "M3.6,9H20.4", "M3.6,15H20.4",
    )

    val Edit = line("edit", "M5,19L5.7,15.4L15.9,5.2C16.6,4.5 17.8,4.5 18.5,5.2C19.2,5.9 19.2,7.1 18.5,7.8L8.3,18Z", "M14.3,6.8L16.9,9.4", "M12.6,19.6H19.4")

    // 阅读器
    val SkipPrev = line("skipPrev", "M17,5.8L10.4,12L17,18.2", "M7,5.8V18.2")
    val SkipNext = line("skipNext", "M7,5.8L13.6,12L7,18.2", "M17,5.8V18.2")
    val Rotate = line(
        "rotate",
        "M4.6,12A7.4,7.4 0,1 0,6.8 6.8",
        "M7.1,2.9L6.8,6.8L10.7,7.1",
        "M10.4,9.4H13.6C14.2,9.4 14.6,9.8 14.6,10.4V14.6C14.6,15.2 14.2,15.6 13.6,15.6H10.4C9.8,15.6 9.4,15.2 9.4,14.6V10.4C9.4,9.8 9.8,9.4 10.4,9.4Z",
    )
    val ReadMode = line(
        "readMode",
        "M6,3.6H16.4L19.4,6.6V19C19.4,19.8 18.8,20.4 18,20.4H6C5.2,20.4 4.6,19.8 4.6,19V5C4.6,4.2 5.2,3.6 6,3.6Z",
        "M8.2,9.2H15.8", "M8.2,12.6H15.8", "M8.2,16H12.6",
    )

    val Brightness = line(
        "brightness",
        "M12,8.2A3.8,3.8 0,1 1,12 15.8A3.8,3.8 0,1 1,12 8.2Z",
        "M12,3.4V5", "M12,19V20.6", "M3.4,12H5", "M19,12H20.6",
        "M5.9,5.9L7.05,7.05", "M16.95,16.95L18.1,18.1", "M5.9,18.1L7.05,16.95", "M16.95,7.05L18.1,5.9",
    )

    /** 图标总览（截图用）。 */
    val all: List<Pair<String, ImageVector>> get() = listOf(
        "书架" to BookOpenOutlined, "历史" to HistoryOutlined, "历史·选中" to HistoryFilled,
        "书城" to StoreOutlined, "书城·选中" to StoreFilled, "我的" to UserCircleOutlined,
        "刷新" to Refresh, "多选" to SelectMany, "删除" to Trash, "切换书源" to Swap, "改名" to Edit, "横排" to Rows,
        "下载" to Download, "地球" to Globe, "上一话" to SkipPrev, "下一话" to SkipNext,
        "旋转" to Rotate, "阅读模式" to ReadMode, "亮度" to Brightness,
    )

}
