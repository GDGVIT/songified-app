package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class SongAnalysisResponse(

    @SerializedName("status")
    val status: String,
    @SerializedName("_id")
    val analysisId: String,
    @SerializedName("songId")
    val songId: String,
    @SerializedName("data")
    val analysisData: AudioAnalysisResponse,

)
