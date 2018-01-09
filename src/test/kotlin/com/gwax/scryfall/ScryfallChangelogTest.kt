package com.gwax.scryfall

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.SyndFeedOutput
import org.apache.http.HttpStatus
import org.joda.time.DateTime
import org.junit.Test
import java.io.InputStream
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertSame

class ScryfallChangelogTest {
    private fun testManager(responseBlock: (Request) -> Response): FuelManager =
        FuelManager().apply {
            client = object : Client {
                override fun executeRequest(request: Request): Response = responseBlock(request)
            }
        }

    private fun feedStream(entries: List<SyndEntry>): InputStream {
        val feed: SyndFeed = SyndFeedImpl().apply {
            this.feedType = "atom_1.0"
            this.entries = entries
        }
        val writer = StringWriter()
        SyndFeedOutput().output(feed, writer)
        return writer.toString().byteInputStream()
    }


    @Test
    fun testFetchOK() {
        val inputStream = feedStream(listOf(
            SyndEntryImpl().apply {
                this.title = "Entry 1"
                this.publishedDate = DateTime(2016, 1, 1, 0, 0).toDate()
                this.updatedDate = DateTime(2017, 1, 1, 0, 0).toDate()
            },
            SyndEntryImpl().apply {
                this.title = "Entry 2"
                this.publishedDate = DateTime(2014, 1, 1, 0, 0).toDate()
                this.updatedDate = DateTime(2015, 1, 1, 0, 0).toDate()
            }))
        val testFuel = testManager { request ->
            Response(
                url = request.url,
                statusCode = HttpStatus.SC_OK,
                headers = mapOf("Etag" to listOf("ghi")),
                dataStream = inputStream)
        }
        assertEquals(
            ScryfallChangelog("ghi", DateTime(2017, 1, 1, 0, 0)),
            ScryfallChangelog.fetch(fuel = testFuel))
    }

    @Test
    fun testFetchOKNoDates() {
        val inputStream = feedStream(listOf())
        val testFuel = testManager { request ->
            Response(
                url = request.url,
                statusCode = HttpStatus.SC_OK,
                headers = mapOf("Etag" to listOf("jkl")),
                dataStream = inputStream)
        }
        assertFails { ScryfallChangelog.fetch(fuel = testFuel) }
    }

    @Test
    fun testFetchOKNoEtag() {
        val testFuel = testManager { request ->
            Response(
                url = request.url,
                statusCode = HttpStatus.SC_OK)
        }
        assertFails { ScryfallChangelog.fetch(fuel = testFuel) }
    }

    @Test
    fun testFetchNotModified() {
        val previous = ScryfallChangelog("abc", DateTime(2017, 1, 2, 3, 4, 5))
        val testFuel = testManager { request ->
            assertEquals("abc", request.headers["If-None-Match"])
            Response(
                url = request.url,
                statusCode = HttpStatus.SC_NOT_MODIFIED)
        }

        assertSame(previous, ScryfallChangelog.fetch(previous, testFuel))
    }

    @Test
    fun testFetchServerError() {
        val testFuel = testManager { request ->
            Response(
                url = request.url,
                statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }

        assertNull(ScryfallChangelog.fetch(fuel = testFuel))
    }
}
