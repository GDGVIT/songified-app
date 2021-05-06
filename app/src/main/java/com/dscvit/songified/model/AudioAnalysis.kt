package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class AudioAnalysis(


    @SerializedName("moodTags")
    val moodTags: ArrayList<String>,
    @SerializedName("genreTags")
    val genreTags: ArrayList<String>,
    @SerializedName("emotionalProfile")
    val emotionalProfile: String,
    @SerializedName("musicalEraTag")
    val musicalEraTag: String,
    @SerializedName("data")
    val analysisData: String,
    @SerializedName("energyLevel")
    val energy: String,
)
