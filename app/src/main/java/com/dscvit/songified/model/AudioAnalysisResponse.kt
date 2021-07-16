package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class AudioAnalysisResponse(
    @SerializedName("audioAnalysis")
    val audioAnalysisResponseData: AudioAnalysisResponseData

)
