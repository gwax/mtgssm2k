package com.gwax.scryfall

import com.gwax.scryfall.models.ScryfallCard
import com.gwax.scryfall.models.ScryfallList
import com.gwax.scryfall.models.ScryfallSet
import java.io.File

class ScryfallData(baseDir: File) {
    private val cache = ScryfallCache(baseDir)

    init {
        val newChangelog = ScryfallChangelog
            .fetch(cache.changelog) ?: throw Exception("Failed to retrieve changelog")
        if (cache.changelog != newChangelog) {
            cache.clear()
            cache.changelog = newChangelog
        }
    }

    val cards: List<ScryfallCard> by lazy {
        cardFetcher().flatMap { (it as ScryfallList).data as List<ScryfallCard> }
    }

    val sets: List<ScryfallSet> by lazy {
        setFetcher().flatMap { (it as ScryfallList).data as List<ScryfallSet> }
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
