package com.gwax.scryfall

import com.beust.klaxon.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.github.salomonbrys.kotson.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import java.net.URI
import java.util.*

val gsonBuilder: GsonBuilder = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapter<UUID> {
        serialize { JsonPrimitive(it.src.toString()) }
        deserialize { UUID.fromString(it.json.asString) }
    }
    .registerTypeAdapter<URI> {
        serialize { JsonPrimitive(it.src.toString()) }
        deserialize { URI(it.json.asString) }
    }
    .registerTypeAdapter<LocalDate> {
        serialize { JsonPrimitive(ISODateTimeFormat.date().print(it.src)) }
        deserialize { ISODateTimeFormat.date().parseLocalDate(it.json.asString) }
    }
val gson: Gson = gsonBuilder.create()

typealias Colors = List<Color>
enum class Color {
    @SerializedName("W")
    WHITE,
    @SerializedName("U")
    BLUE,
    @SerializedName("B")
    BLACK,
    @SerializedName("R")
    RED,
    @SerializedName("G")
    GREEN
}

data class RelatedCard(
    val id: UUID,
    val name: String,
    val uri: URI
) {
    @SerializedName("object")
    val objectType = "related_card"
}

data class CardFace(
    val name: String,
    val typeLine: String,
    val oracleText: String? = null,
    val manaCost: String,
    val colors: Colors,
    val colorIndicator: Colors? = null,
    val power: String? = null,
    val toughness: String? = null,
    val loyalty: String? = null,
    val flavorText: String? = null,
    val illustrationId: UUID? = null, // TODO: Undocumented
    val imageUris: Map<String, URI>? = null
) {
    @SerializedName("object")
    val objectType = "card_face"
}

data class Card(
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
    val colors: Colors,
    val colorIndicator: Colors? = null,
    val colorIdentity: Colors,
    val allParts: List<RelatedCard>? = null,
    val cardFaces: List<CardFace>? = null,
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
) {
    @SerializedName("object")
    val objectType = "card"
}

data class Set(
    val code: String,
    val mtgoCode: String,
    val name: String,
    val setType: String,  // TODO: Enum
    val releasedAt: LocalDate? = null,
    val blockCode: String? = null,
    val block: String? = null,
    val parentSetCode: String? = null,  // TODO: Not nullable in documentation
    val cardCount: Int,
    val digital: Boolean,
    val foil: Boolean,
    val iconSvgUri: URI,
    val searchUri: URI,
    // Undocumented Fields
    val uri: URI,
    val scryfallUri: URI
) {
    @SerializedName("object")
    val objectType = "set"
}
