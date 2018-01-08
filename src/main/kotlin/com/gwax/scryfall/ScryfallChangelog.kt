package com.gwax.scryfall

import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import mu.KLogging
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.joda.time.DateTime
import java.io.Serializable
import java.util.*

data class ScryfallChangelog(val eTag: String, val latestEntry: DateTime) : Serializable {
    constructor() : this("", DateTime(0L))

    companion object : KLogging() {
        const val CHANGELOG_FEED_URL = "https://scryfall.com/changelog/feed"
    }

    fun fetchLatest(): ScryfallChangelog {
        HttpClients.createMinimal().use { client ->
            val request = HttpGet(CHANGELOG_FEED_URL)
            if (eTag.isNotEmpty()) request.addHeader("If-None-Match", eTag)
            client.execute(request).use { response ->
                logger.debug { response.statusLine }
                return when (response.statusLine.statusCode) {
                    HttpStatus.SC_NOT_MODIFIED -> this
                    HttpStatus.SC_OK -> {
                        val eTagHeaders = response.getHeaders("ETag")
                        val newETag = if (eTagHeaders.isEmpty()) {
                            ""
                        } else {
                            eTagHeaders[0].value
                        }
                        response.entity.content.use { stream ->
                            val feed = SyndFeedInput().build(XmlReader(stream))
                            val latestDate = feed.entries.map { it.updatedDate }.max() ?: Date(0L)
                            return ScryfallChangelog(newETag, DateTime(latestDate))
                        }
                    }
                    else -> {
                        logger.error { "Server responded with: ${response.statusLine}" }
                        return ScryfallChangelog()
                    }
                }
            }
        }
    }
}