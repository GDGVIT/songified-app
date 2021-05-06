package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongbookDeleteRequest(
    @SerializedName("songbookId")
    val songbookId: String
)
