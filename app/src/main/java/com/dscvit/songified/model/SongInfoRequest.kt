package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongInfoRequest(
    @SerializedName("songId")
    val songId: String
)
