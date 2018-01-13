package com.gwax.scryfall.models

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import java.net.URI
import java.util.*

data class ScryfallRelatedCard(
    val id: UUID,
    val name: String,
    val uri: URI
) : ScryfallModel() {
    companion object {
        val TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactory
            .of(ScryfallRelatedCard::class.java, "object")
            .registerSubtype(ScryfallRelatedCard::class.java, "related_card")
    }
}
