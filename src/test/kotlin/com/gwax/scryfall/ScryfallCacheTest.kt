package com.gwax.scryfall

import org.joda.time.DateTime
import org.junit.Test
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScryfallCacheTest {
    private val cacheDir = createTempDir()

    init {
        cacheDir.deleteOnExit()
    }

    @Test
    fun cacheChangelogTest() {
        val cache1 = ScryfallCache(cacheDir)
        assertNull(cache1.changelog)
        cache1.changelog = ScryfallChangelog("abc", DateTime(17L))
        assertEquals(
            ScryfallChangelog("abc", DateTime(17L)),
            cache1.changelog)
        cache1.close()

        val cache2 = ScryfallCache(cacheDir)
        assertEquals(
            ScryfallChangelog("abc", DateTime(17L)),
            cache2.changelog)
        cache2.clear()
        assertNull(cache2.changelog)
    }

    @Test
    fun cacheUrlsTest() {
        val url1 = URL("http://foo.bar")
        val url2 = URL("http:/baz.quux")
        val cache1 = ScryfallCache(cacheDir)
        assertNull(cache1.urls[url1])
        assertNull(cache1.urls[url2])
        cache1.urls[url1] = "foobar"
        assertEquals("foobar", cache1.urls[url1])
        assertNull(cache1.urls[url2])
        cache1.close()

        val cache2 = ScryfallCache(cacheDir)
        assertEquals("foobar", cache2.urls[url1])
        assertNull(cache2.urls[url2])
        cache2.clear()
        assertNull(cache2.urls[url1])
    }
}
