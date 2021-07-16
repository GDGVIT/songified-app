package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class AddToSongbookResponse(

    @SerializedName("message")
    val response: String
)
