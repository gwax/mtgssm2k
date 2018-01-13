package com.gwax.scryfall.models

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import java.net.URI
import java.util.*

data class ScryfallCardFace(
    val name: String,
    val typeLine: String,
    val oracleText: String? = null,
    val manaCost: String,
    val colors: List<ScryfallColor>,
    val colorIndicator: List<ScryfallColor>? = null,
    val power: String? = null,
    val toughness: String? = null,
    val loyalty: String? = null,
    val flavorText: String? = null,
    val illustrationId: UUID? = null, // TODO: Undocumented
    val imageUris: Map<String, URI>? = null
) : ScryfallModel() {
    companion object {
        val TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactory
            .of(ScryfallCardFace::class.java, "object")
            .registerSubtype(ScryfallCardFace::class.java, "card_face")
    }
}
