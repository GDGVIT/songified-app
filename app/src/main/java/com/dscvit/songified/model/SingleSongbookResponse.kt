package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SingleSongbookResponse(
    @SerializedName("message")
    val data: SingleSongbookData
)
