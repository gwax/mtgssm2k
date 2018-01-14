package com.gwax.scryfall

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.gwax.scryfall.models.ScryfallList
import com.gwax.scryfall.models.ScryfallModel
import com.gwax.scryfall.util.gson
import mu.KotlinLogging
import org.apache.http.HttpStatus
import java.net.URL

const val MIN_FETCH_WAIT_MILLIS = 100L

private val logger = KotlinLogging.logger {}
private val defaultFuel: FuelManager by lazy { FuelManager() }

enum class Endpoint(val url: URL) {
    CARDS(URL("https://api.scryfall.com/cards")),
    SETS(URL("https://api.scryfall.com/sets"))
}

class ScryfallFetcher(
    private val endpoint: Endpoint,
    private val cache: ScryfallCache,
    private val fuel: FuelManager = defaultFuel
) : Iterable<ScryfallModel> {
    override fun iterator(): Iterator<ScryfallModel> =
        ScryfallFetcherIterator(endpoint, cache, fuel)
}

class ScryfallFetcherIterator(
    endpoint: Endpoint,
    private val cache: ScryfallCache,
    private val fuel: FuelManager = defaultFuel
) : Iterator<ScryfallModel> {
    private var lastCall: Long = 0L
    private var hasMore = true
    private var nextPage: URL? = endpoint.url

    override fun hasNext(): Boolean = hasMore
    override fun next(): ScryfallList {
        val target = nextPage ?: throw Exception("No next_page")
        val pageData = fetchResultString(target)
        val scryfallList = gson.fromJson(pageData, ScryfallList::class.java)
        hasMore = scryfallList.hasMore
        nextPage = scryfallList.nextPage
        return scryfallList
    }

    private fun fetchResultString(target: URL): String? =
        if (cache.urls.containsKey(target)) {
            logger.debug { "Retrieving cached $target" }
            cache.urls[target]
        } else {
            val request = fuel.request(Method.GET, nextPage.toString())
            while (lastCall + MIN_FETCH_WAIT_MILLIS > System.currentTimeMillis()) {
                logger.trace { "Sleeping until rate limit passed" }
                Thread.sleep(System.currentTimeMillis() - lastCall)
            }
            logger.info { "Requesting $target" }
            val response = fuel.client.executeRequest(request)
            logger.debug { "<-- ${response.statusCode} (${response.url})" }
            lastCall = System.currentTimeMillis()
            when (response.statusCode) {
                HttpStatus.SC_OK -> {
                    val responseString = String(response.data)
                    cache.urls[target] = responseString
                    responseString
                }
                else -> {
                    logger.error { "$response" }
                    throw Exception("Scryfall did not return a result")
                }
            }
        }
}


fun main(args: Array<String>) {
    val cacheDir = createTempDir()
    cacheDir.deleteOnExit()
    val cache = ScryfallCache(cacheDir)
    val fetcher = ScryfallFetcherIterator(Endpoint.CARDS, cache)
    println(fetcher.next().data.size)
    println(fetcher.next().data.size)
    println(fetcher.next().data.size)
    println("try2")
    val fetcher2 = ScryfallFetcherIterator(Endpoint.CARDS, cache)
    println(fetcher2.next().data.size)
    println(fetcher2.next().data.size)
}
