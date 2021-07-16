package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SingleSongbookData(
    @SerializedName("data")
    val songbookSongs: ArrayList<SingleSongbookSong>
)
