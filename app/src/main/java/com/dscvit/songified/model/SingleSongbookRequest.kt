package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SingleSongbookRequest(

    @SerializedName("songbookId")
    val songbookId: String
)
