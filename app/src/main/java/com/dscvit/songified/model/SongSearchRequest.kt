package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongSearchRequest(

//    @SerializedName("song_name")
//    val songName: String
    @SerializedName("type ")
    val type: String,
    @SerializedName("lookup")
    val lookup: String,

    )
