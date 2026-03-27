package com.sohaib.kleanbot.data.entities

import com.google.gson.annotations.SerializedName

data class Choice(
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: Message
)