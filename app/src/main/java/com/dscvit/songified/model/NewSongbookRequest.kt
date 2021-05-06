package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class NewSongbookRequest(
    @SerializedName("songbookName")
    val songbookName: String
)
