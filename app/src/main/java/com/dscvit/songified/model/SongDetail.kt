package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongDetail(

    @SerializedName("id")
    val songId: String,
    @SerializedName("title")
    val songTitle: String,
    @SerializedName("uri")
    val songUri: String,
    @SerializedName("artist")
    val artist: Artist,
    @SerializedName("tempo")
    val tempo: String,
    @SerializedName("time_sig")
    val timeSig: String,
    @SerializedName("key_of")
    val keyOf: String,
    @SerializedName("camelot")
    val camelot: String,


    )