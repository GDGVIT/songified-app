package com.dscvit.songified.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SingleSongbookSong(

    @SerializedName("songId")
    val songId: String,
    @SerializedName("title")
    val songTitle: String,
    @SerializedName("body")
    val songBody: String,
    @SerializedName("scale")
    val scale: String,
    @SerializedName("tempo")
    val tempo: String,
    @SerializedName("artist")
    val artist: String,
    @SerializedName("timesig")
    val timSig: String,
    @SerializedName("imageurl")
    val coverArt: String
) : Parcelable
