package com.gwax.util

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

val gson: Gson = GsonBuilder()
        .registerTypeAdapter<DateTime> {
            serialize {
                ISODateTimeFormat
                        .dateTime()
                        .print(it.src)
                        .toJson()
            }

            deserialize {
                when (it.json.nullString) {
                    null -> null
                    else -> ISODateTimeFormat
                            .dateTime()
                            .parseDateTime(it.json.nullString)
                }
            }
        }
        .create()