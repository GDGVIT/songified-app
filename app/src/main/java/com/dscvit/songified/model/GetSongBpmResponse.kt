package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class GetSongBpmResponse(
    @SerializedName("search")
    val song: ArrayList<Song>
)