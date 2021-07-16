package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class ScaleAnalysis(
    @SerializedName("bpm")
    val bpm: String,
    @SerializedName("key")
    val key: Key,

)
