package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class AddSongInfoRequest(

    @SerializedName("songId")
    val songId: String,
    @SerializedName("songName")
    val sonName: String,
    @SerializedName("detail")
    val songDetails: String
)
