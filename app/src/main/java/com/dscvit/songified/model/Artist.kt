package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class Artist(

    @SerializedName("id")
    val artist_id: String,
    @SerializedName("name")
    val artist_title: String,
    @SerializedName("uri")
    val artist_uri: String,
    @SerializedName("img")
    val img: String

)
