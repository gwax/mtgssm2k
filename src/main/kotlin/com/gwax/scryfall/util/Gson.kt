package com.gwax.scryfall.util

import com.github.salomonbrys.kotson.RegistrationBuilder
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.*
import com.gwax.scryfall.models.*
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import java.net.URI
import java.util.*

fun JsonElement.sortedKeys(): JsonElement =
    if (this !is JsonObject)
        this
    else
        this.entrySet()
            .sortedBy { it.key }
            .fold(JsonObject(), { obj, (key, value) ->
                obj.add(key, value.sortedKeys())
                obj
            })

val uuidTypeRegister: RegistrationBuilder<UUID, UUID>.() -> Unit = {
    serialize { JsonPrimitive(it.src.toString()) }
    deserialize { UUID.fromString(it.json.asString) }
}

val uriTypeRegister: RegistrationBuilder<URI, URI>.() -> Unit = {
    serialize { JsonPrimitive(it.src.toString()) }
    deserialize { URI(it.json.asString) }
}

val localDateRegister: RegistrationBuilder<LocalDate, LocalDate>.() -> Unit = {
    serialize { JsonPrimitive(ISODateTimeFormat.date().print(it.src)) }
    deserialize { ISODateTimeFormat.date().parseLocalDate(it.json.asString) }
}

val gsonBuilder = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapterFactory(ScryfallModel.TYPE_ADAPTER_FACTORY)
    .registerTypeAdapterFactory(ScryfallCard.TYPE_ADAPTER_FACTORY)
    .registerTypeAdapterFactory(ScryfallCardFace.TYPE_ADAPTER_FACTORY)
    .registerTypeAdapterFactory(ScryfallList.TYPE_ADAPTER_FACTORY)
    .registerTypeAdapterFactory(ScryfallRelatedCard.TYPE_ADAPTER_FACTORY)
    .registerTypeAdapterFactory(ScryfallSet.TYPE_ADAPTER_FACTORY)
    .registerTypeAdapter(uuidTypeRegister)
    .registerTypeAdapter(uriTypeRegister)
    .registerTypeAdapter(localDateRegister)

val gson = gsonBuilder.create()
