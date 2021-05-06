package com.dscvit.songified.model

import com.google.gson.annotations.SerializedName

data class PreviousUploadsResponse(

    @SerializedName("songs")
    val songAnalysisResponse: ArrayList<UploadedSong>
)
