package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongDetailsResponse(
    @SerializedName("song")
    val song: SongDetail
)
