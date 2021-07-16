package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class UploadSongResponse(
    @SerializedName("id")
    val songProcessId: String,
    @SerializedName("message")
    val msg: String
)
