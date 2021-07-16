package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class Song(
//    val details: Details,
//    val userComments: List<Any>
    @SerializedName("id")
    val song_id: String,
    @SerializedName("title")
    val song_title: String,
    @SerializedName("uri")
    val song_uri: String,
    @SerializedName("artist")
    val artist: Artist
)
