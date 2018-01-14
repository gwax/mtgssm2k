package com.gwax.scryfall

import com.gwax.scryfall.models.ScryfallCard
import com.gwax.scryfall.models.ScryfallList
import com.gwax.scryfall.models.ScryfallSet
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

class ScryfallData(baseDir: File) {
    private val cache = ScryfallCache(baseDir)

    init {
        val newChangelog = ScryfallChangelog
            .fetch(cache.changelog) ?: throw Exception("Failed to retrieve changelog")
        if (cache.changelog != newChangelog) {
            logger.info { "Changelog does not match cache, clearing cache." }
            cache.clear()
            cache.changelog = newChangelog
        }
    }

    val cards: List<ScryfallCard> by lazy {
        val cachedCards = cache.allCards
        if (cachedCards != null) {
            logger.trace { "Using cached value for all cards" }
            cachedCards
        } else {
            val allCards = cardFetcher().flatMap { (it as ScryfallList).data as List<ScryfallCard> }
            cache.allCards = allCards
            allCards
        }
    }

    val sets: List<ScryfallSet> by lazy {
        val cachedSets = cache.allSets
        if (cachedSets != null) {
            logger.trace { "Using cached value for all sets" }
            cachedSets
        } else {
            val allSets = setFetcher().flatMap { (it as ScryfallList).data as List<ScryfallSet> }
            cache.allSets = allSets
            allSets
        }

    }

    private fun cardFetcher() =
        ScryfallFetcher(Endpoint.CARDS, cache)

    private fun setFetcher() =
        ScryfallFetcher(Endpoint.SETS, cache)
}

fun main(args: Array<String>) {
    val baseDir = File(System.getProperty("user.home")).resolve(".mtg_ssm")
    val scryfallData = ScryfallData(baseDir)
    val setNames = scryfallData.sets.map { it.name }
    println(setNames)
    val cardNames = scryfallData.cards.map { it.name }
    println(cardNames)
}
