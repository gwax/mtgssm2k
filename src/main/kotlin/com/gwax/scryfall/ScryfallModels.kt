package com.gwax.scryfall

import com.beust.klaxon.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.github.salomonbrys.kotson.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
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
    val uri: URI) {
    @SerializedName("object")
    val objectType = "related_card"
}

data class CardFace(
    val name: String,
    val typeLine: String,
    val oracleText: String?,
    val manaCost: String,
    val colors: Colors,
    val colorIndicator: Colors?,
    val power: String?,
    val toughness: String?,
    val loyalty: String?,
    val flavorText: String?,
    val illustrationId: UUID?,
    val imageUris: Map<String, URI>?) {
    @SerializedName("object")
    val objectType = "card_face"
}

data class Card(
    // Core Card Fields
    val id: UUID,
    val multiverseIds: List<Int>?,
    val mtgoId: Int?,
    val mtgoFoilId: Int?,
    val uri: URI,
    val scryfallUri: URI,
    val printsSearchUri: URI,
    val rulingsUri: URI,
    // Gameplay Fields
    val name: String,
    val layout: String,  // TODO: Enum
    val cmc: Long,
    val typeLine: String,
    val oracleText: String?,
    val manaCost: String,
    val power: String?,  // TODO: Custom type?
    val toughness: String?,  // TODO: Custom type?
    val loyalty: String?,  // TODO: Custom type?
    val lifeModifier: String?,  // TODO: Int?
    val handModifier: String?,  // TODO: Int?
    val colors: Colors,
    val colorIndicator: Colors?,
    val colorIdentity: Colors,
    val allParts: List<RelatedCard>?,
    val cardFaces: List<CardFace>?,
    val legalities: Map<String, String>,  // TODO: Map to Enum
    val reserved: Boolean,
    val edhrecRack: Int?,
    // Print Fields
    val set: String,
    val setName: String,
    val collectorNumber: String,
    val setSetchUri: URI,
    val scryfallSetUri: URI,
    val imageUris: Map<String, URI>?,  // TODO: Map from Enum?
    val highresImage: Boolean,
    val reprint: Boolean,
    val digital: Boolean,
    val rarity: String,  // TODO: Enum
    val flavorText: String?,
    val artist: String?,
    val frame: String,  // TODO: Enum
    val fullArt: Boolean,
    val watermark: String?,
    val borderColor: String,  // TODO: Enum
    val storySpotlightNumber: Int?,
    val storySpotlightUri: URI?,
    val timeshifted: Boolean,
    val colorshifted: Boolean,
    val futureshifted: Boolean
) {
    @SerializedName("object")
    val objectType = "card"
}
