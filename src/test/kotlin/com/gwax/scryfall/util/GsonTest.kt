package com.gwax.scryfall.util

import com.github.salomonbrys.kotson.RegistrationBuilder
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.joda.time.LocalDate
import org.junit.Test
import java.net.URI
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

fun String.parseJson(): JsonElement = JsonParser().parse(this)
fun JsonElement.toString(gson: Gson): String = gson.toJson(this)

class GsonTest {
    private fun gson(modifiers: GsonBuilder.() -> Unit) =
        GsonBuilder()
            .setPrettyPrinting()
            .apply(modifiers)
            .create()

    @Test
    fun sortedKeysTest() {
        val gson = gson {}
        val unsorted = "{\"b\": 1, \"a\": 2}".parseJson()
        val sorted = "{\"a\": 2, \"b\": 1}".parseJson()
        assertNotEquals(
            sorted.toString(gson),
            unsorted.toString(gson))
        assertEquals(
            sorted.sortedKeys().toString(gson),
            unsorted.sortedKeys().toString(gson))
    }

    private inline fun <reified T> gsonRoundTrip(
        gson: Gson,
        stringValue: String,
        kotlinValue: T) {
        assertEquals(
            kotlinValue,
            gson.fromJson(stringValue, T::class.java))
        assertEquals(
            stringValue.parseJson().toString(gson),
            gson.toJson(kotlinValue))
    }

    @Test
    fun uuidTypeRegisterTest() = gsonRoundTrip(
        gson { this.registerTypeAdapter(uuidTypeRegister) },
        "\"27907985-b5f6-4098-ab43-15a0c2bf94d5\"",
        UUID.fromString("27907985-b5f6-4098-ab43-15a0c2bf94d5"))

    @Test
    fun uriTypeRegisterTest() = gsonRoundTrip(
        gson { this.registerTypeAdapter(uriTypeRegister) },
        "\"http://foo.bar.baz\"",
        URI("http://foo.bar.baz"))

    @Test
    fun localDateRegisterTest() = gsonRoundTrip(
        gson { this.registerTypeAdapter(localDateRegister) },
        "2018-01-02",
        LocalDate(2018, 1, 2))
}
