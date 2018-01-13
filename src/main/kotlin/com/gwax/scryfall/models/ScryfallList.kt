package com.gwax.scryfall.models

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import java.net.URI

data class ScryfallList(
    val totalCards: Int? = null,
    val hasMore: Boolean,
    val nextPage: URI? = null,
    val warnings: List<String>? = null,
    val data: List<ScryfallModel>
) : ScryfallModel() {
    companion object {
        val TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactory
            .of(ScryfallList::class.java, "object")
            .registerSubtype(ScryfallList::class.java, "list")
    }
}
