package com.gwax.scryfall.models

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import java.net.URI
import java.util.*

data class ScryfallCard(
    // Core Card Fields
    val id: UUID,
    val multiverseIds: List<Int>? = null,
    val mtgoId: Int? = null,
    val mtgoFoilId: Int? = null,
    val uri: URI,
    val scryfallUri: URI,
    val printsSearchUri: URI,
    val rulingsUri: URI,
    // Gameplay Fields
    val name: String,
    val layout: String,  // TODO: Enum
    val cmc: Double,
    val typeLine: String,
    val oracleText: String? = null,
    val manaCost: String,
    val power: String? = null,  // TODO: Custom type?
    val toughness: String? = null,  // TODO: Custom type?
    val loyalty: String? = null,  // TODO: Custom type?
    val lifeModifier: String? = null,  // TODO: Int?
    val handModifier: String? = null,  // TODO: Int?
    val colors: List<ScryfallColor>,
    val colorIndicator: List<ScryfallColor>? = null,
    val colorIdentity: List<ScryfallColor>,
    val allParts: List<ScryfallRelatedCard>? = null,
    val cardFaces: List<ScryfallCardFace>? = null,
    val legalities: Map<String, String>,  // TODO: Map to Enum
    val reserved: Boolean,
    val edhrecRank: Int? = null,
    // Print Fields
    val set: String,
    val setName: String,
    val collectorNumber: String,
    val setSearchUri: URI,  // TODO: Misspelled in the documentation as set_setch_api
    val scryfallSetUri: URI,
    val imageUris: Map<String, URI>? = null,  // TODO: Map from Enum?
    val highresImage: Boolean,
    val reprint: Boolean,
    val digital: Boolean,
    val rarity: String,  // TODO: Enum
    val flavorText: String? = null,
    val artist: String? = null,
    val frame: String,  // TODO: Enum
    val fullArt: Boolean,
    val watermark: String? = null,
    val borderColor: String,  // TODO: Enum
    val storySpotlightNumber: Int? = null,
    val storySpotlightUri: URI? = null,
    val timeshifted: Boolean,
    val colorshifted: Boolean,
    val futureshifted: Boolean,
    // Undocumented Fields
    val setUri: URI,
    val illustrationId: UUID? = null,
    val usd: String? = null,
    val eur: String? = null,
    val relatedUris: Map<String, URI>? = null,
    val purchaseUris: Map<String, URI>? = null
) : ScryfallModel() {
    companion object {
        val TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactory
            .of(ScryfallCard::class.java, "object")
            .registerSubtype(ScryfallCard::class.java, "card")
    }
}
