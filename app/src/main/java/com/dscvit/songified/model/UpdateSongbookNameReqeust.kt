package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class UpdateSongbookNameReqeust(
    @SerializedName("songbookId")
    val songbookId: String,
    @SerializedName("songbookName")
    val songbookName: String
)
