package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class ScaleAnalysisResponse(
    @SerializedName("result")
    val scaleAnalysis:ScaleAnalysis,
)
