package com.gwax.scryfall

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import mu.KLogging
import org.apache.http.HttpStatus
import org.joda.time.DateTime
import java.io.InputStream
import java.io.Serializable
import java.util.*

data class ScryfallChangelog(val eTag: String, val latestEntry: DateTime) : Serializable {
    constructor() : this("", DateTime(0L))

    companion object : KLogging() {
        const val CHANGELOG_FEED_URL = "https://scryfall.com/changelog/feed"
        private val fuel: FuelManager by lazy { FuelManager() }
    }

    fun fetchLatest(): ScryfallChangelog {
        val request = fuel.request(Method.GET, CHANGELOG_FEED_URL)
        if (eTag.isNotEmpty())
            request.headers["If-None-Match"] = eTag
        val (_, response, result) = request.responseObject(object : ResponseDeserializable<InputStream> {
            override fun deserialize(response: Response): InputStream = response.dataStream
        })
        logger.debug { "<-- ${response.statusCode} (${response.url})" }
        return when (response.statusCode) {
            HttpStatus.SC_NOT_MODIFIED -> this
            HttpStatus.SC_OK -> {
                val newETag = response.headers["Etag"]?.get(0) ?: ""
                val feed = SyndFeedInput().build(XmlReader(result.get()))
                val latestDate = feed.entries.map { it.updatedDate }.max() ?: Date(0L)
                return ScryfallChangelog(newETag, DateTime(latestDate))
            }
            else -> {
                logger.error { "$response" }
                return ScryfallChangelog()
            }
        }
    }
}
