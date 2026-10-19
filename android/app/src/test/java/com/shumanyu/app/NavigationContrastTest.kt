package com.shumanyu.app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationContrastTest {
    @Test fun lightAndDarkCustomAccentsKeepReadableText() {
        for(background in listOf(Color(0xFFF3E8F4),Color(0xFFCCD9CC),Color(0xFF303030),Color(0xFF57505F))) {
            for(accent in listOf(Color(0xFF448AFF),Color(0xFF00A870),Color(0xFFFFCC00),Color(0xFFE8B8FF))) {
                val ink=readableNavigationTint(accent,background)
                val a=ink.luminance();val b=background.luminance()
                assertTrue((maxOf(a,b)+0.05f)/(minOf(a,b)+0.05f)>=4.5f)
            }
        }
    }
}
