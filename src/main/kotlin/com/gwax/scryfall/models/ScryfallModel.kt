package com.gwax.scryfall.models

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import java.io.Serializable

abstract class ScryfallModel : Serializable {
    companion object {
        val TYPE_ADAPTER_FACTORY: RuntimeTypeAdapterFactory<ScryfallModel> =
            RuntimeTypeAdapterFactory
                .of(ScryfallModel::class.java, "object")
                .registerSubtype(ScryfallRelatedCard::class.java, "related_card")
                .registerSubtype(ScryfallCardFace::class.java, "card_face")
                .registerSubtype(ScryfallCard::class.java, "card")
                .registerSubtype(ScryfallSet::class.java, "set")
                .registerSubtype(ScryfallList::class.java, "list")
    }
}
