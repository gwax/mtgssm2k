package com.gwax.scryfall.models

import com.gwax.scryfall.util.gsonBuilder
import com.gwax.scryfall.util.parseJson
import com.gwax.scryfall.util.sortedKeys
import com.gwax.scryfall.util.toString
import org.joda.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.test.assertEquals

abstract class ModelTestBase() {
    protected val gson = gsonBuilder.setPrettyPrinting().create()
    protected fun String.reJson() =
        gson.toJson(this.parseJson().sortedKeys())
}

@RunWith(Parameterized::class)
class ScryfallColorTest(private val scryfallString: String, private val enumValue: ScryfallColor) : ModelTestBase() {
    @Test
    fun scryfallColorTest() {
        assertEquals(
            enumValue,
            gson.fromJson(scryfallString, ScryfallColor::class.java))
        assertEquals(
            scryfallString.parseJson().sortedKeys().toString(gson),
            gson.toJson(enumValue, ScryfallColor::class.java).reJson())
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<out Any>> = listOf(
            arrayOf("W", ScryfallColor.WHITE),
            arrayOf("U", ScryfallColor.BLUE),
            arrayOf("B", ScryfallColor.BLACK),
            arrayOf("R", ScryfallColor.RED),
            arrayOf("G", ScryfallColor.GREEN)
        )
    }
}

@RunWith(Parameterized::class)
class ScryfallModelsTest(private val stringValue: String, private val kotlinValue: ScryfallModel) : ModelTestBase() {
    @Test
    fun scryfallModelRoundTripTest() {
        assertEquals(
            kotlinValue,
            gson.fromJson(stringValue, ScryfallModel::class.java))
        assertEquals(
            stringValue.parseJson().sortedKeys().toString(gson),
            gson.toJson(kotlinValue).reJson())
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<out Any>> = listOf(
            arrayOf(
                """
                    |{
                    |  "object": "related_card",
                    |  "id": "27907985-b5f6-4098-ab43-15a0c2bf94d5",
                    |  "name": "Bruna, the Fading Light",
                    |  "uri": "https://api.scryfall.com/cards/emn/15a"
                    |}
                """.trimMargin(),
                ScryfallRelatedCard(
                    id = UUID.fromString("27907985-b5f6-4098-ab43-15a0c2bf94d5"),
                    name = "Bruna, the Fading Light",
                    uri = URI("https://api.scryfall.com/cards/emn/15a")
                )
            ),
            arrayOf(
                """
                    |{
                    |    "object": "card_face",
                    |    "name": "Delver of Secrets",
                    |    "mana_cost": "{U}",
                    |    "type_line": "Creature — Human Wizard",
                    |    "oracle_text": "At the beginning of your upkeep, look at the top card of your library. You may reveal that card. If an instant or sorcery card is revealed this way, transform Delver of Secrets.",
                    |    "colors": ["U"],
                    |    "power": "1",
                    |    "toughness": "1",
                    |    "illustration_id": "1c2fee9b-89ea-4ab1-a751-451c3cd65a88",
                    |    "image_uris": {
                    |        "small": "https://img.scryfall.com/cards/small/en/isd/51a.jpg?1510051268",
                    |        "normal": "https://img.scryfall.com/cards/normal/en/isd/51a.jpg?1510051268",
                    |        "large": "https://img.scryfall.com/cards/large/en/isd/51a.jpg?1510051268",
                    |        "png": "https://img.scryfall.com/cards/png/en/isd/51a.png?1510051268",
                    |        "art_crop": "https://img.scryfall.com/cards/art_crop/en/isd/51a.jpg?1510051268",
                    |        "border_crop": "https://img.scryfall.com/cards/border_crop/en/isd/51a.jpg?1510051268"
                    |    }
                    |}
                """.trimMargin(),
                ScryfallCardFace(
                    name = "Delver of Secrets",
                    typeLine = "Creature — Human Wizard",
                    oracleText = "At the beginning of your upkeep, look at the top card of your library. You may reveal that card. If an instant or sorcery card is revealed this way, transform Delver of Secrets.",
                    manaCost = "{U}",
                    colors = listOf(ScryfallColor.BLUE),
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
                )
            ),
            arrayOf(
                """
                    |{
                    |    "object": "card",
                    |    "id": "27907985-b5f6-4098-ab43-15a0c2bf94d5",
                    |    "multiverse_ids": [414304],
                    |    "mtgo_id": 61098,
                    |    "name": "Bruna, the Fading Light",
                    |    "uri": "https://api.scryfall.com/cards/emn/15a",
                    |    "scryfall_uri": "https://scryfall.com/card/emn/15a?utm_source=api",
                    |    "layout": "meld",
                    |    "highres_image": true,
                    |    "image_uris": {
                    |        "small": "https://img.scryfall.com/cards/small/en/emn/15a.jpg?1509987682",
                    |        "normal": "https://img.scryfall.com/cards/normal/en/emn/15a.jpg?1509987682",
                    |        "large": "https://img.scryfall.com/cards/large/en/emn/15a.jpg?1509987682",
                    |        "png": "https://img.scryfall.com/cards/png/en/emn/15a.png?1509987682",
                    |        "art_crop": "https://img.scryfall.com/cards/art_crop/en/emn/15a.jpg?1509987682",
                    |        "border_crop": "https://img.scryfall.com/cards/border_crop/en/emn/15a.jpg?1509987682"
                    |    },
                    |    "cmc": 7.0,
                    |    "type_line": "Legendary Creature — Angel Horror",
                    |    "oracle_text": "When you cast Bruna, the Fading Light, you may return target Angel or Human creature card from your graveyard to the battlefield.\nFlying, vigilance\n(Melds with Gisela, the Broken Blade.)",
                    |    "mana_cost": "{5}{W}{W}",
                    |    "power": "5",
                    |    "toughness": "7",
                    |    "colors": ["W"],
                    |    "color_identity": ["W"],
                    |    "all_parts": [{
                    |        "object": "related_card",
                    |        "id": "27907985-b5f6-4098-ab43-15a0c2bf94d5",
                    |        "name": "Bruna, the Fading Light",
                    |        "uri": "https://api.scryfall.com/cards/emn/15a"
                    |    }, {
                    |        "object": "related_card",
                    |        "id": "5a7a212e-e0b6-4f12-a95c-173cae023f93",
                    |        "name": "Brisela, Voice of Nightmares",
                    |        "uri": "https://api.scryfall.com/cards/emn/15b"
                    |    }, {
                    |        "object": "related_card",
                    |        "id": "c75c035a-7da9-4b36-982d-fca8220b1797",
                    |        "name": "Gisela, the Broken Blade",
                    |        "uri": "https://api.scryfall.com/cards/emn/28a"
                    |    }],
                    |    "legalities": {
                    |        "standard": "not_legal",
                    |        "frontier": "legal",
                    |        "modern": "legal",
                    |        "pauper": "not_legal",
                    |        "legacy": "legal",
                    |        "penny": "legal",
                    |        "vintage": "legal",
                    |        "duel": "legal",
                    |        "commander": "legal",
                    |        "1v1": "legal",
                    |        "future": "not_legal"
                    |    },
                    |    "reserved": false,
                    |    "reprint": false,
                    |    "set": "emn",
                    |    "set_name": "Eldritch Moon",
                    |    "set_uri": "https://api.scryfall.com/cards/search?q=%2B%2Be%3Aemn",
                    |    "set_search_uri": "https://api.scryfall.com/cards/search?q=%2B%2Be%3Aemn",
                    |    "scryfall_set_uri": "https://scryfall.com/sets/emn?utm_source=api",
                    |    "rulings_uri": "https://api.scryfall.com/cards/emn/15a/rulings",
                    |    "prints_search_uri": "https://api.scryfall.com/cards/search?order=set\u0026q=%2B%2B%21%22Bruna%2C+the+Fading+Light%22",
                    |    "collector_number": "15a",
                    |    "digital": false,
                    |    "rarity": "rare",
                    |    "flavor_text": "She now sees only Emrakul's visions.",
                    |    "illustration_id": "4c8cee4a-a9a4-42eb-9cbf-fcc6c6344d00",
                    |    "artist": "Clint Cearley",
                    |    "frame": "2015",
                    |    "full_art": false,
                    |    "border_color": "black",
                    |    "timeshifted": false,
                    |    "colorshifted": false,
                    |    "futureshifted": false,
                    |    "edhrec_rank": 1098,
                    |    "usd": "0.79",
                    |    "eur": "0.39",
                    |    "related_uris": {
                    |        "gatherer": "http://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=414304",
                    |        "tcgplayer_decks": "http://decks.tcgplayer.com/magic/deck/search?contains=Bruna%2C+the+Fading+Light\u0026page=1\u0026partner=Scryfall",
                    |        "edhrec": "http://edhrec.com/route/?cc=Bruna%2C+the+Fading+Light",
                    |        "mtgtop8": "http://mtgtop8.com/search?MD_check=1\u0026SB_check=1\u0026cards=Bruna%2C+the+Fading+Light"
                    |    },
                    |    "purchase_uris": {
                    |        "amazon": "https://www.amazon.com/gp/search?ie=UTF8\u0026index=toys-and-games\u0026keywords=Bruna%2C+the+Fading+Light\u0026tag=scryfall-20",
                    |        "ebay": "http://rover.ebay.com/rover/1/711-53200-19255-0/1?campid=5337966903\u0026icep_catId=19107\u0026icep_ff3=10\u0026icep_sortBy=12\u0026icep_uq=Bruna%2C+the+Fading+Light\u0026icep_vectorid=229466\u0026ipn=psmain\u0026kw=lg\u0026kwid=902099\u0026mtid=824\u0026pub=5575230669\u0026toolid=10001",
                    |        "tcgplayer": "http://store.tcgplayer.com/magic/eldritch-moon/bruna-the-fading-light?partner=Scryfall",
                    |        "magiccardmarket": "https://www.cardmarket.com/Magic/Products/Singles/Eldritch+Moon/Bruna%2C+the+Fading+Light+%2F+Brisela%2C+Voice+of+Nightmares?referrer=scryfall",
                    |        "cardhoarder": "https://www.cardhoarder.com/cards/61098?affiliate_id=scryfall\u0026ref=card-profile\u0026utm_campaign=affiliate\u0026utm_medium=card\u0026utm_source=scryfall",
                    |        "card_kingdom": "https://www.cardkingdom.com/catalog/item/207487?partner=scryfall\u0026utm_campaign=affiliate\u0026utm_medium=scryfall\u0026utm_source=scryfall",
                    |        "mtgo_traders": "http://www.mtgotraders.com/deck/ref.php?id=61098\u0026referral=scryfall",
                    |        "coolstuffinc": "http://www.coolstuffinc.com/p/Magic%3A+The+Gathering/Bruna%2C+the+Fading+Light?utm_source=scryfall"
                    |    }
                    |}
                """.trimMargin(),
                ScryfallCard(
                    id = UUID.fromString("27907985-b5f6-4098-ab43-15a0c2bf94d5"),
                    multiverseIds = listOf(414304),
                    mtgoId = 61098,
                    name = "Bruna, the Fading Light",
                    uri = URI("https://api.scryfall.com/cards/emn/15a"),
                    scryfallUri = URI("https://scryfall.com/card/emn/15a?utm_source=api"),
                    layout = "meld",
                    highresImage = true,
                    imageUris = mapOf(
                        "small" to URI("https://img.scryfall.com/cards/small/en/emn/15a.jpg?1509987682"),
                        "normal" to URI("https://img.scryfall.com/cards/normal/en/emn/15a.jpg?1509987682"),
                        "large" to URI("https://img.scryfall.com/cards/large/en/emn/15a.jpg?1509987682"),
                        "png" to URI("https://img.scryfall.com/cards/png/en/emn/15a.png?1509987682"),
                        "art_crop" to URI("https://img.scryfall.com/cards/art_crop/en/emn/15a.jpg?1509987682"),
                        "border_crop" to URI("https://img.scryfall.com/cards/border_crop/en/emn/15a.jpg?1509987682")
                    ),
                    cmc = 7.0,
                    typeLine = "Legendary Creature — Angel Horror",
                    oracleText = "When you cast Bruna, the Fading Light, you may return target Angel or Human creature card from your graveyard to the battlefield.\nFlying, vigilance\n(Melds with Gisela, the Broken Blade.)",
                    manaCost = "{5}{W}{W}",
                    power = "5",
                    toughness = "7",
                    colors = listOf(ScryfallColor.WHITE),
                    colorIdentity = listOf(ScryfallColor.WHITE),
                    allParts = listOf(
                        ScryfallRelatedCard(
                            id = UUID.fromString("27907985-b5f6-4098-ab43-15a0c2bf94d5"),
                            name = "Bruna, the Fading Light",
                            uri = URI("https://api.scryfall.com/cards/emn/15a")),
                        ScryfallRelatedCard(
                            id = UUID.fromString("5a7a212e-e0b6-4f12-a95c-173cae023f93"),
                            name = "Brisela, Voice of Nightmares",
                            uri = URI("https://api.scryfall.com/cards/emn/15b")),
                        ScryfallRelatedCard(
                            id = UUID.fromString("c75c035a-7da9-4b36-982d-fca8220b1797"),
                            name = "Gisela, the Broken Blade",
                            uri = URI("https://api.scryfall.com/cards/emn/28a"))
                    ),
                    legalities = mapOf(
                        "standard" to "not_legal",
                        "frontier" to "legal",
                        "modern" to "legal",
                        "pauper" to "not_legal",
                        "legacy" to "legal",
                        "penny" to "legal",
                        "vintage" to "legal",
                        "duel" to "legal",
                        "commander" to "legal",
                        "1v1" to "legal",
                        "future" to "not_legal"
                    ),
                    reserved = false,
                    reprint = false,
                    set = "emn",
                    setName = "Eldritch Moon",
                    setUri = URI("https://api.scryfall.com/cards/search?q=%2B%2Be%3Aemn"),
                    setSearchUri = URI("https://api.scryfall.com/cards/search?q=%2B%2Be%3Aemn"),
                    scryfallSetUri = URI("https://scryfall.com/sets/emn?utm_source=api"),
                    rulingsUri = URI("https://api.scryfall.com/cards/emn/15a/rulings"),
                    printsSearchUri = URI("https://api.scryfall.com/cards/search?order=set\u0026q=%2B%2B%21%22Bruna%2C+the+Fading+Light%22"),
                    collectorNumber = "15a",
                    digital = false,
                    rarity = "rare",
                    flavorText = "She now sees only Emrakul's visions.",
                    illustrationId = UUID.fromString("4c8cee4a-a9a4-42eb-9cbf-fcc6c6344d00"),
                    artist = "Clint Cearley",
                    frame = "2015",
                    fullArt = false,
                    borderColor = "black",
                    timeshifted = false,
                    colorshifted = false,
                    futureshifted = false,
                    edhrecRank = 1098,
                    usd = "0.79",
                    eur = "0.39",
                    relatedUris = mapOf(
                        "gatherer" to URI("http://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=414304"),
                        "tcgplayer_decks" to URI("http://decks.tcgplayer.com/magic/deck/search?contains=Bruna%2C+the+Fading+Light\u0026page=1\u0026partner=Scryfall"),
                        "edhrec" to URI("http://edhrec.com/route/?cc=Bruna%2C+the+Fading+Light"),
                        "mtgtop8" to URI("http://mtgtop8.com/search?MD_check=1\u0026SB_check=1\u0026cards=Bruna%2C+the+Fading+Light")
                    ),
                    purchaseUris = mapOf(
                        "amazon" to URI("https://www.amazon.com/gp/search?ie=UTF8\u0026index=toys-and-games\u0026keywords=Bruna%2C+the+Fading+Light\u0026tag=scryfall-20"),
                        "ebay" to URI("http://rover.ebay.com/rover/1/711-53200-19255-0/1?campid=5337966903\u0026icep_catId=19107\u0026icep_ff3=10\u0026icep_sortBy=12\u0026icep_uq=Bruna%2C+the+Fading+Light\u0026icep_vectorid=229466\u0026ipn=psmain\u0026kw=lg\u0026kwid=902099\u0026mtid=824\u0026pub=5575230669\u0026toolid=10001"),
                        "tcgplayer" to URI("http://store.tcgplayer.com/magic/eldritch-moon/bruna-the-fading-light?partner=Scryfall"),
                        "magiccardmarket" to URI("https://www.cardmarket.com/Magic/Products/Singles/Eldritch+Moon/Bruna%2C+the+Fading+Light+%2F+Brisela%2C+Voice+of+Nightmares?referrer=scryfall"),
                        "cardhoarder" to URI("https://www.cardhoarder.com/cards/61098?affiliate_id=scryfall\u0026ref=card-profile\u0026utm_campaign=affiliate\u0026utm_medium=card\u0026utm_source=scryfall"),
                        "card_kingdom" to URI("https://www.cardkingdom.com/catalog/item/207487?partner=scryfall\u0026utm_campaign=affiliate\u0026utm_medium=scryfall\u0026utm_source=scryfall"),
                        "mtgo_traders" to URI("http://www.mtgotraders.com/deck/ref.php?id=61098\u0026referral=scryfall"),
                        "coolstuffinc" to URI("http://www.coolstuffinc.com/p/Magic%3A+The+Gathering/Bruna%2C+the+Fading+Light?utm_source=scryfall")
                    )
                )
            ),
            arrayOf(
                """
                    |{
                    |    "object": "set",
                    |    "code": "rix",
                    |    "mtgo_code": "rix",
                    |    "name": "Rivals of Ixalan",
                    |    "uri": "https://api.scryfall.com/sets/rix",
                    |    "scryfall_uri": "https://scryfall.com/sets/rix",
                    |    "search_uri": "https://api.scryfall.com/cards/search?order=set&q=%2B%2Be%3Arix",
                    |    "released_at": "2018-01-19",
                    |    "set_type": "expansion",
                    |    "card_count": 205,
                    |    "digital": false,
                    |    "foil": false,
                    |    "block_code": "xln",
                    |    "block": "Ixalan",
                    |    "icon_svg_uri": "https://assets.scryfall.com/assets/sets/rix.svg"
                    |}
                """.trimMargin(),
                ScryfallSet(
                    code = "rix",
                    mtgoCode = "rix",
                    name = "Rivals of Ixalan",
                    uri = URI("https://api.scryfall.com/sets/rix"),
                    scryfallUri = URI("https://scryfall.com/sets/rix"),
                    searchUri = URI("https://api.scryfall.com/cards/search?order=set&q=%2B%2Be%3Arix"),
                    releasedAt = LocalDate(2018, 1, 19),
                    setType = "expansion",
                    cardCount = 205,
                    digital = false,
                    foil = false,
                    blockCode = "xln",
                    block = "Ixalan",
                    iconSvgUri = URI("https://assets.scryfall.com/assets/sets/rix.svg")
                )
            ),
            arrayOf(
                """
                    |{
                    |    "object": "list",
                    |    "has_more": true,
                    |    "next_page": "https://foo.bar/baz",
                    |    "data": [{
                    |        "object": "related_card",
                    |        "id": "27907985-b5f6-4098-ab43-15a0c2bf94d5",
                    |        "name": "Bruna, the Fading Light",
                    |        "uri": "https://api.scryfall.com/cards/emn/15a"
                    |    }, {
                    |        "object": "related_card",
                    |        "id": "5a7a212e-e0b6-4f12-a95c-173cae023f93",
                    |        "name": "Brisela, Voice of Nightmares",
                    |        "uri": "https://api.scryfall.com/cards/emn/15b"
                    |    }, {
                    |        "object": "related_card",
                    |        "id": "c75c035a-7da9-4b36-982d-fca8220b1797",
                    |        "name": "Gisela, the Broken Blade",
                    |        "uri": "https://api.scryfall.com/cards/emn/28a"
                    |    }]
                    |}
                """.trimMargin(),
                ScryfallList(
                    hasMore = true,
                    nextPage = URL("https://foo.bar/baz"),
                    data = listOf(
                        ScryfallRelatedCard(
                            id = UUID.fromString("27907985-b5f6-4098-ab43-15a0c2bf94d5"),
                            name = "Bruna, the Fading Light",
                            uri = URI("https://api.scryfall.com/cards/emn/15a")),
                        ScryfallRelatedCard(
                            id = UUID.fromString("5a7a212e-e0b6-4f12-a95c-173cae023f93"),
                            name = "Brisela, Voice of Nightmares",
                            uri = URI("https://api.scryfall.com/cards/emn/15b")),
                        ScryfallRelatedCard(
                            id = UUID.fromString("c75c035a-7da9-4b36-982d-fca8220b1797"),
                            name = "Gisela, the Broken Blade",
                            uri = URI("https://api.scryfall.com/cards/emn/28a"))
                    )
                )
            )
        )
    }
}
