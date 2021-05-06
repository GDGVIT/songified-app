package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongbookSongDeleteRequest(
    @SerializedName("songbookId")
    val songBookId:String,
    @SerializedName("songId")
    val songId:String
)
