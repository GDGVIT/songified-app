package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class AnalysedDataRequest(
    @SerializedName("songId")
    val songId: String
)
