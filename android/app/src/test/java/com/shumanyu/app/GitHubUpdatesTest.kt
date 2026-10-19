package com.shumanyu.app

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class GitHubUpdatesTest {
    private fun release(tag:String="v1.0.2",code:Long?=3):JSONObject = JSONObject()
        .put("tag_name",tag).put("draft",false).put("prerelease",false)
        .put("html_url","https://github.com/${GitHubUpdates.REPOSITORY}/releases/tag/$tag")
        .put("body",(code?.let { "<!-- shumanyu-version-code: $it -->\n" } ?: "")+"修复阅读问题")
        .put("assets",JSONArray().put(JSONObject().put("name","shumanyu.apk").put("size",100)
            .put("browser_download_url","https://github.com/${GitHubUpdates.REPOSITORY}/releases/download/$tag/shumanyu.apk")))
    @Test fun nextCodePromptsAndMetadataIsHidden() {
        val found=requireNotNull(GitHubUpdates.parse(release().toString()))
        assertTrue(GitHubUpdates.isNewer(found,2,"1.0.1"))
        assertEquals("修复阅读问题",found.notes)
        assertEquals(3L,found.code)
    }
    @Test fun sameOrLowerCodeDoesNotPrompt() {
        assertFalse(GitHubUpdates.isNewer(requireNotNull(GitHubUpdates.parse(release(code=2).toString())),2,"1.0.1"))
        assertFalse(GitHubUpdates.isNewer(requireNotNull(GitHubUpdates.parse(release(code=1).toString())),2,"1.0.1"))
    }
    @Test fun sameNameHigherInternalCodePrompts() {
        assertTrue(GitHubUpdates.isNewer(requireNotNull(GitHubUpdates.parse(release("v1.0.1",3).toString())),2,"1.0.1"))
    }
    @Test fun unmarkedVersionsUseNumericComparison() {
        val found=requireNotNull(GitHubUpdates.parse(release("v1.0.10",null).toString()))
        assertTrue(GitHubUpdates.isNewer(found,2,"1.0.9"))
        assertFalse(GitHubUpdates.isNewer(found,2,"1.0.10"))
        assertFalse(GitHubUpdates.isNewer(found,2,"1.1.0"))
    }
    @Test fun draftAndPrereleaseAreIgnored() {
        assertNull(GitHubUpdates.parse(release().put("draft",true).toString()))
        assertNull(GitHubUpdates.parse(release().put("prerelease",true).toString()))
    }
    @Test fun missingApkDoesNotOfferEmptyRelease() {
        assertNull(GitHubUpdates.parse(release().put("assets",JSONArray()).toString()))
        val data=release();data.getJSONArray("assets").getJSONObject(0).put("size",0)
        assertNull(GitHubUpdates.parse(data.toString()))
    }
    @Test fun offsiteAndDifferentRepositoryPagesAreRejected() {
        for(url in listOf("https://github.com.evil.example/${GitHubUpdates.REPOSITORY}/releases/tag/v1.0.2",
            "https://github.com/other/project/releases/tag/v1.0.2","http://github.com/${GitHubUpdates.REPOSITORY}/releases/tag/v1.0.2",
            "https://user@github.com/${GitHubUpdates.REPOSITORY}/releases/tag/v1.0.2")) {
            assertNull(GitHubUpdates.parse(release().put("html_url",url).toString()))
        }
    }
    @Test fun externallyHostedAssetIsRejected() {
        val data=release();data.getJSONArray("assets").getJSONObject(0).put("browser_download_url","https://example.org/book.apk")
        assertNull(GitHubUpdates.parse(data.toString()))
    }
    @Test fun formerRepositoryCannotSupplyAnUpdate() {
        val page=release().put("html_url","https://github.com/bfbkbzc4w5-star/shumanyu/releases/tag/v1.0.2")
        assertNull(GitHubUpdates.parse(page.toString()))
        val asset=release()
        asset.getJSONArray("assets").getJSONObject(0).put("browser_download_url","https://github.com/bfbkbzc4w5-star/shumanyu/releases/download/v1.0.2/app.apk")
        assertNull(GitHubUpdates.parse(asset.toString()))
    }

    private fun feed(updated:String="2026-10-19T08:57:29Z")="""<feed><entry><id>release-one</id><updated>$updated</updated><link rel="alternate" href="https://github.com/${GitHubUpdates.REPOSITORY}/releases/tag/v1.0.2"/><content>Notes</content></entry></feed>"""
    @Test fun releaseFeedDetectsSameTagEditedToNewInternalVersion() {
        val before=GitHubUpdates.parseReleaseRevision(feed())
        assertNotNull(before)
        assertEquals(before,GitHubUpdates.parseReleaseRevision(feed()))
        assertNotEquals(before,GitHubUpdates.parseReleaseRevision(feed("2026-10-19T09:00:00Z")))
    }
    @Test fun invalidOrDifferentRepositoryFeedIsRejected() {
        assertNull(GitHubUpdates.parseReleaseRevision("<html>verification required</html>"))
        assertNull(GitHubUpdates.parseReleaseRevision(feed().replace(GitHubUpdates.REPOSITORY,"other/project")))
    }
}
