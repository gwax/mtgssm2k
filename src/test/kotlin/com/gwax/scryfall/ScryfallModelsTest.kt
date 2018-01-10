package com.gwax.scryfall

import com.beust.klaxon.Klaxon
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.net.URI
import java.util.*
import kotlin.reflect.KClass
import kotlin.test.assertEquals

private val gson = gsonBuilder.setPrettyPrinting().create()
private fun String.toPrettyJson(): String =
    gson.toJson(
        JsonParser()
            .parse(this)
            .asJsonObject)

@RunWith(Parameterized::class)
class ScryfallModelsTest(val type: KClass<*>, val jsonString: String, val kotlinValue: Any) {
    @Test
    fun gsonTypeConversionTest() {
        assertEquals(kotlinValue, gson.fromJson(jsonString, type.java))
        assertEquals(jsonString, gson.toJson(kotlinValue))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<out Any?>> = listOf(
            arrayOf(
                UUID::class,
                "\"27907985-b5f6-4098-ab43-15a0c2bf94d5\"",
                UUID.fromString("27907985-b5f6-4098-ab43-15a0c2bf94d5")),
            arrayOf(
                URI::class,
                "\"http://foo.bar.baz\"",
                URI("http://foo.bar.baz")),
            arrayOf(Color::class, "\"W\"", Color.WHITE),
            arrayOf(Color::class, "\"U\"", Color.BLUE),
            arrayOf(Color::class, "\"B\"", Color.BLACK),
            arrayOf(Color::class, "\"R\"", Color.RED),
            arrayOf(Color::class, "\"G\"", Color.GREEN),
            arrayOf(
                RelatedCard::class,
                """
                    |{
                    |  "object": "related_card",
                    |  "id": "27907985-b5f6-4098-ab43-15a0c2bf94d5",
                    |  "name": "Bruna, the Fading Light",
                    |  "uri": "https://api.scryfall.com/cards/emn/15a"
                    |}
                """.trimMargin().toPrettyJson(),
                RelatedCard(
                    id = UUID.fromString("27907985-b5f6-4098-ab43-15a0c2bf94d5"),
                    name = "Bruna, the Fading Light",
                    uri = URI("https://api.scryfall.com/cards/emn/15a")
                )
            ),
            arrayOf(CardFace::class,
                """
                    |{
                    |  "object": "card_face",
                    |  "name": "Delver of Secrets",
                    |  "type_line": "Creature — Human Wizard",
                    |  "oracle_text": "At the beginning of your upkeep, look at the top card of your library. You may reveal that card. If an instant or sorcery card is revealed this way, transform Delver of Secrets.",
                    |  "mana_cost": "{U}",
                    |  "colors": ["U"],
                    |  "power": "1",
                    |  "toughness": "1",
                    |  "illustration_id": "1c2fee9b-89ea-4ab1-a751-451c3cd65a88",
                    |  "image_uris": {
                    |    "small": "https://img.scryfall.com/cards/small/en/isd/51a.jpg?1510051268",
                    |    "normal": "https://img.scryfall.com/cards/normal/en/isd/51a.jpg?1510051268",
                    |    "large": "https://img.scryfall.com/cards/large/en/isd/51a.jpg?1510051268",
                    |    "png": "https://img.scryfall.com/cards/png/en/isd/51a.png?1510051268",
                    |    "art_crop": "https://img.scryfall.com/cards/art_crop/en/isd/51a.jpg?1510051268",
                    |    "border_crop": "https://img.scryfall.com/cards/border_crop/en/isd/51a.jpg?1510051268"
                    |  }
                    |}
                """.trimMargin().toPrettyJson(),
                CardFace(
                    name = "Delver of Secrets",
                    typeLine = "Creature — Human Wizard",
                    oracleText = "At the beginning of your upkeep, look at the top card of your library. You may reveal that card. If an instant or sorcery card is revealed this way, transform Delver of Secrets.",
                    manaCost = "{U}",
                    colors = listOf(Color.BLUE),
                    colorIndicator = null,
                    power = "1",
                    toughness = "1",
                    illustrationId = UUID.fromString("1c2fee9b-89ea-4ab1-a751-451c3cd65a88"),
                    loyalty = null,
                    flavorText = null,
                    imageUris = mapOf(
                        "small" to URI("https://img.scryfall.com/cards/small/en/isd/51a.jpg?1510051268"),
                        "normal" to URI("https://img.scryfall.com/cards/normal/en/isd/51a.jpg?1510051268"),
                        "large" to URI("https://img.scryfall.com/cards/large/en/isd/51a.jpg?1510051268"),
                        "png" to URI("https://img.scryfall.com/cards/png/en/isd/51a.png?1510051268"),
                        "art_crop" to URI("https://img.scryfall.com/cards/art_crop/en/isd/51a.jpg?1510051268"),
                        "border_crop" to URI("https://img.scryfall.com/cards/border_crop/en/isd/51a.jpg?1510051268")
                    )
                ))
        )
    }
}
