package com.gwax.util

import org.joda.time.DateTime
import org.junit.Test
import kotlin.test.assertEquals

class GsonTest {
    private val dt = DateTime(2017, 1, 2, 3, 4, 5)
    private val dtStr = "\"2017-01-02T03:04:05.000-08:00\""

    @Test
    fun dateTimeToJsonTest() {
        assertEquals(dtStr, gson.toJson(dt))
    }

    @Test
    fun dateTimeFromJsonTest() {
        assertEquals(
                dt,
                gson.fromJson(dtStr, DateTime::class.java))
    }
}