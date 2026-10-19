package com.shumanyu.app

import org.junit.Assert.*
import org.junit.Test

class CoverHtmlTest {
    @Test fun lazyCoverWinsRegardlessOfAttributeOrder() {
        assertEquals("/real.jpg", coverImageSource("""<img src="/loading.gif" data-original="/real.jpg">"""))
        assertEquals("/real.jpg", coverImageSource("""<img data-original="/real.jpg" src="/loading.gif">"""))
    }

    @Test fun emptyAndInlinePlaceholdersFallBackToRemoteSource() {
        assertEquals("//cdn/cover.webp", coverImageSource("""<img data-original="" data-src="data:image/gif;base64,xxx" data-lazy-src="//cdn/cover.webp">"""))
        assertNull(coverImageSource("""<img src="data:image/png;base64,xxx">"""))
    }

    @Test fun otherAttributeNamesCannotMasqueradeAsSrc() {
        assertNull(coverImageSource("""<img not-src="/wrong.jpg">"""))
        assertEquals("/right.jpg", coverImageSource("""<img SRC = '/right.jpg'>"""))
    }
}
