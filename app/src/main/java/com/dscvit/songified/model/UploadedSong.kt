package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class UploadedSong(
    @SerializedName("songId")
    val id: String,
    @SerializedName("songName")
    val name: String,
    @SerializedName("status")
    val status: String

)
