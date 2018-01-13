package com.gwax.scryfall.models

import com.google.gson.annotations.SerializedName

enum class ScryfallColor {
    @SerializedName("W")
    WHITE,
    @SerializedName("U")
    BLUE,
    @SerializedName("B")
    BLACK,
    @SerializedName("R")
    RED,
    @SerializedName("G")
    GREEN
}
