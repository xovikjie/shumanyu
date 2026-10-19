package com.shumanyu.app

private val coverAttributes = listOf("data-original", "data-src", "data-lazy-src", "data-lazy", "src").map { name ->
    Regex("""\s$name\s*=\s*["']([^"']*)["']""", RegexOption.IGNORE_CASE)
}

/** Prefer the real lazy-loaded image over a transparent src placeholder. */
internal fun coverImageSource(tag: String): String? {
    for (attribute in coverAttributes) {
        val value = attribute.find(tag)?.groupValues?.get(1)?.trim().orEmpty()
        if (value.isNotEmpty() && !value.startsWith("data:", ignoreCase = true)) return value
    }
    return null
}
