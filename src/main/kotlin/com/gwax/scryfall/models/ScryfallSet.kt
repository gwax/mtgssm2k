package com.gwax.scryfall.models

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import org.joda.time.LocalDate
import java.net.URI

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
) : ScryfallModel() {
    companion object {
        val TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactory
            .of(ScryfallSet::class.java, "object")
            .registerSubtype(ScryfallSet::class.java, "set")
    }
}
