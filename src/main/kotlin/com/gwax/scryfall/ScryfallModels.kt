package com.gwax.scryfall

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import java.net.URI
import java.util.*

val gsonBuilder: GsonBuilder = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory
            .of(ScryfallModel::class.java, "object")
            .registerSubtype(ScryfallRelatedCard::class.java, "related_card")
            .registerSubtype(ScryfallCardFace::class.java, "card_face")
            .registerSubtype(ScryfallCard::class.java, "card")
            .registerSubtype(ScryfallSet::class.java, "set")
            .registerSubtype(ScryfallList::class.java, "list")
    )
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory
            .of(ScryfallRelatedCard::class.java, "object")
            .registerSubtype(ScryfallRelatedCard::class.java, "related_card")
    )
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory
            .of(ScryfallCardFace::class.java, "object")
            .registerSubtype(ScryfallCardFace::class.java, "card_face")
    )
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory
            .of(ScryfallCard::class.java, "object")
            .registerSubtype(ScryfallCard::class.java, "card")
    )
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory
            .of(ScryfallSet::class.java, "object")
            .registerSubtype(ScryfallSet::class.java, "set")
    )
    .registerTypeAdapterFactory(
        RuntimeTypeAdapterFactory
            .of(ScryfallList::class.java, "object")
            .registerSubtype(ScryfallList::class.java, "list")
    )
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
val gson = gsonBuilder.create()

typealias ScryfallColors = List<ScryfallColor>
enum class ScryfallColor {
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

abstract class ScryfallModel

data class ScryfallRelatedCard(
    val id: UUID,
    val name: String,
    val uri: URI
) : ScryfallModel()

data class ScryfallCardFace(
    val name: String,
    val typeLine: String,
    val oracleText: String? = null,
    val manaCost: String,
    val colors: ScryfallColors,
    val colorIndicator: ScryfallColors? = null,
    val power: String? = null,
    val toughness: String? = null,
    val loyalty: String? = null,
    val flavorText: String? = null,
    val illustrationId: UUID? = null, // TODO: Undocumented
    val imageUris: Map<String, URI>? = null
) : ScryfallModel()

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
    val colors: ScryfallColors,
    val colorIndicator: ScryfallColors? = null,
    val colorIdentity: ScryfallColors,
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
) : ScryfallModel()

data class ScryfallSet(
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
) : ScryfallModel()

data class ScryfallList(
    val totalCards: Int? = null,
    val hasMore: Boolean,
    val nextPage: URI? = null,
    val warnings: List<String>? = null,
    val data: List<ScryfallModel>
) : ScryfallModel()
