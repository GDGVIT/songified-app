package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class UpdateSongInSongbookRequest(

    @SerializedName("songbookId")
    val songbookId: String,
    @SerializedName("songId")
    val songId: String,
    @SerializedName("title")
    val songTitle: String,
    @SerializedName("body")
    val songBody: String,
    @SerializedName("scale")
    val scale: String,
    @SerializedName("tempo")
    val tempo: Int,
    @SerializedName("artist")
    val artist: String
)
