package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class AudioAnalysisResponseData(
    @SerializedName("result")
    val audioAnalysis: AudioAnalysis,
)
