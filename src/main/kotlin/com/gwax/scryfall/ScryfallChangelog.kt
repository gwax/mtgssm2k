package com.gwax.scryfall

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.joda.time.DateTime
import java.io.Serializable

const val CHANGELOG_FEED_URL = "https://scryfall.com/changelog/feed"

private val logger = KotlinLogging.logger {}

private val defaultFuel: FuelManager by lazy { FuelManager() }

data class ScryfallChangelog(val eTag: String, val latestEntry: DateTime) : Serializable {

    companion object {
        fun fetch(previous: ScryfallChangelog? = null, fuel: FuelManager = defaultFuel): ScryfallChangelog? {
            val request = fuel
                .request(Method.GET, CHANGELOG_FEED_URL)
                .apply { if (previous != null) this.headers["If-None-Match"] = previous.eTag }
            logger.info { "Requesting $CHANGELOG_FEED_URL" }
            val response = fuel.client.executeRequest(request)
            logger.debug { "<-- ${response.statusCode} (${response.url})" }
            return when (response.statusCode) {
                HttpStatus.SC_NOT_MODIFIED -> previous
                HttpStatus.SC_OK -> {
                    val newETag = response.headers["Etag"]?.get(0) ?: throw Exception("Scryfall did not return Etag")
                    val feed = SyndFeedInput().build(XmlReader(response.dataStream))
                    val latestDate = feed.entries
                        .flatMap { entry ->
                            listOf(entry.publishedDate, entry.updatedDate)
                        }
                        .filterNotNull()
                        .max()
                        ?: throw Exception("Scryfall did not provide latest entry date")
                    return ScryfallChangelog(newETag, DateTime(latestDate))
                }
                else -> {
                    logger.error { "$response" }
                    return null
                }
            }
        }
    }
}
