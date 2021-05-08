package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class AddToSongbookRequest(

    @SerializedName("songbookId")
    val songbookId: String,
    @SerializedName("title")
    val songTitle: String,
    @SerializedName("body")
    val songBody: String,
    @SerializedName("scale")
    val scale: String,
    @SerializedName("tempo")
    val tempo: Int,
    @SerializedName("artist")
    val artist: String,
    @SerializedName("timesig")
    val timSig: String,
    @SerializedName("imageurl")
    val coverArt: String


)